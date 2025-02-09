package claon.post.service;

import claon.center.domain.Center;
import claon.center.repository.HoldInfoRepository;
import claon.common.domain.Pagination;
import claon.common.domain.PaginationFactory;
import claon.common.exception.BadRequestException;
import claon.common.exception.ErrorCode;
import claon.common.exception.InternalServerErrorException;
import claon.common.exception.NotFoundException;
import claon.common.exception.UnauthorizedException;
import claon.common.validator.IdEqualValidator;
import claon.common.validator.IsExistUrlValidator;
import claon.common.validator.IsImageValidator;
import claon.common.validator.IsPrivateValidator;
import claon.post.domain.ClimbingHistory;
import claon.post.domain.Post;
import claon.post.domain.PostReport;
import claon.post.dto.PostContentsUrlDto;
import claon.post.dto.PostCreateRequestDto;
import claon.post.dto.PostDetailResponseDto;
import claon.post.dto.PostReportRequestDto;
import claon.user.domain.User;
import claon.user.repository.BlockUserRepository;
import claon.center.repository.CenterRepository;
import claon.post.domain.PostContents;
import claon.post.dto.PostReportResponseDto;
import claon.post.dto.PostResponseDto;
import claon.post.dto.PostUpdateRequestDto;
import claon.post.infra.PostContentsImageManager;
import claon.post.repository.ClimbingHistoryRepository;
import claon.post.repository.PostLikeRepository;
import claon.post.repository.PostReportRepository;
import claon.post.repository.PostRepository;
import claon.post.repository.PostRepositorySupport;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final BlockUserRepository blockUserRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final HoldInfoRepository holdInfoRepository;
    private final CenterRepository centerRepository;
    private final ClimbingHistoryRepository climbingHistoryRepository;
    private final PostRepositorySupport postRepositorySupport;
    private final PostReportRepository postReportRepository;
    private final PostContentsImageManager postContentsImageManager;
    private final PaginationFactory paginationFactory;

    @Transactional(readOnly = true)
    public Pagination<PostDetailResponseDto> findUserPostsByCenterAndYearMonth(
            User user,
            String nickname,
            String centerId,
            Integer year,
            Integer month,
            Pageable pageable
    ) {
        return this.paginationFactory.create(
                postRepositorySupport.findByNicknameAndCenterAndYearMonth(user.getId(), nickname, centerId, year, month, pageable).map(
                        post -> PostDetailResponseDto.from(
                                post,
                                post.getWriter().getNickname().equals(user.getNickname()),
                                postLikeRepository.findByLikerAndPost(user, post).isPresent(),
                                postLikeRepository.countByPost(post)))
        );
    }

    @Transactional(readOnly = true)
    public Pagination<PostDetailResponseDto> findPosts(
            User user,
            Pageable pageable
    ) {
        return this.paginationFactory.create(
                postRepositorySupport.findExceptLaonUserAndBlockUser(user.getId(), pageable).map(
                        post -> PostDetailResponseDto.from(
                                post,
                                post.getWriter().getNickname().equals(user.getNickname()),
                                postLikeRepository.findByLikerAndPost(user, post).isPresent(),
                                postLikeRepository.countByPost(post)))
        );
    }

    @Transactional(readOnly = true)
    public PostDetailResponseDto findPost(
            User user,
            String postId
    ) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        if (!post.getWriter().getNickname().equals(user.getNickname())) {
            IsPrivateValidator.of(post.getWriter().getNickname(), post.getWriter().getIsPrivate()).validate();

            if (!blockUserRepository.findBlock(user.getId(), post.getWriter().getId()).isEmpty()) {
                throw new UnauthorizedException(
                        ErrorCode.NOT_ACCESSIBLE,
                        String.format("%s을 찾을 수 없습니다.", post.getWriter().getNickname())
                );
            }
        }

        return PostDetailResponseDto.from(
                post,
                post.getWriter().getNickname().equals(user.getNickname()),
                postLikeRepository.findByLikerAndPost(user, post).isPresent(),
                postLikeRepository.countByPost(post)
        );
    }

    @Transactional
    public PostResponseDto createPost(
            User user,
            PostCreateRequestDto postCreateRequestDto
    ) {
        Center center = centerRepository.findById(postCreateRequestDto.getCenterId()).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "암장을 찾을 수 없습니다."
                )
        );

        Post post = this.postRepository.save(
                Post.of(
                        center,
                        postCreateRequestDto.getContent(),
                        postCreateRequestDto.getContentsList().stream()
                                .map(contents -> PostContents.of(
                                        contents.getUrl()
                                ))
                                .collect(Collectors.toList()),
                        user
                )
        );

        List<ClimbingHistory> climbingHistoryList = Optional.ofNullable(postCreateRequestDto.getClimbingHistories())
                .orElse(Collections.emptyList())
                .stream().map(history ->
                        climbingHistoryRepository.save(ClimbingHistory.of(
                                post,
                                holdInfoRepository.findById(history.getHoldId()).orElseThrow(
                                        () -> new InternalServerErrorException(
                                                ErrorCode.INTERNAL_SERVER_ERROR,
                                                "홀드 정보를 찾을 수 없습니다."
                                        )),
                                history.getClimbingCount())))
                .collect(Collectors.toList());

        return PostResponseDto.from(post, climbingHistoryList);
    }

    @Transactional
    public PostResponseDto updatePost(
            User user,
            String postId,
            PostUpdateRequestDto postUpdateRequestDto
    ) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        IdEqualValidator.of(post.getWriter().getId(), user.getId()).validate();

        climbingHistoryRepository.deleteAllByPost(postId);

        List<ClimbingHistory> climbingHistoryList = Optional.ofNullable(postUpdateRequestDto.getClimbingHistories())
                .orElse(Collections.emptyList())
                .stream().map(history ->
                        climbingHistoryRepository.save(ClimbingHistory.of(
                                post,
                                holdInfoRepository.findById(history.getHoldId()).orElseThrow(
                                        () -> new InternalServerErrorException(
                                                ErrorCode.INTERNAL_SERVER_ERROR,
                                                "홀드 정보를 찾을 수 없습니다."
                                        )),
                                history.getClimbingCount())))
                .collect(Collectors.toList());

        post.update(
                postUpdateRequestDto.getContent(),
                postUpdateRequestDto.getContentsList().stream()
                        .map(contents -> PostContents.of(
                                contents.getUrl()
                        ))
                        .collect(Collectors.toList())
        );

        return PostResponseDto.from(postRepository.save(post), climbingHistoryList);
    }

    @Transactional
    public PostResponseDto deletePost(User user, String postId) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        IdEqualValidator.of(post.getWriter().getId(), user.getId()).validate();

        post.delete();

        return PostResponseDto.from(this.postRepository.save(post));
    }

    @Transactional
    public PostReportResponseDto createReport(
            User user,
            String postId,
            PostReportRequestDto postReportRequestDto
    ) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        postReportRepository.findByReporterAndPost(user, post).ifPresent(
                like -> {
                    throw new BadRequestException(
                            ErrorCode.ROW_ALREADY_EXIST,
                            "이미 신고한 게시글입니다."
                    );
                }
        );

        return PostReportResponseDto.from(
                postReportRepository.save(
                        PostReport.of(
                                user,
                                post,
                                postReportRequestDto.getReportType(),
                                postReportRequestDto.getContent()
                        )
                )
        );
    }

    public String uploadContents(MultipartFile image) {
        IsImageValidator.of(image).validate();

        return this.postContentsImageManager.uploadContents(image);
    }

    public void deleteContents(User user, String postId, PostContentsUrlDto postContentsUrlDto) {
        Post post = this.postRepository.findById(postId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        String imagePath = postContentsUrlDto.getUrl();
        IdEqualValidator.of(post.getWriter().getId(), user.getId())
                .linkWith(IsExistUrlValidator.of(post.getContentList(), imagePath)).validate();

        this.postContentsImageManager.deleteContents(imagePath);
    }
}