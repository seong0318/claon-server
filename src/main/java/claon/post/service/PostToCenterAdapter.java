package claon.post.service;

import claon.center.dto.CenterPostThumbnailResponseDto;
import claon.center.service.PostPort;
import claon.common.domain.Pagination;
import claon.common.domain.PaginationFactory;
import claon.post.repository.PostRepositorySupport;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostToCenterAdapter implements PostPort {
    private final PostRepositorySupport postRepositorySupport;
    private final PaginationFactory paginationFactory;

    @Override
    public Pagination<CenterPostThumbnailResponseDto> findByCenterAndHoldExceptBlockUser(String centerId, String holdId, String userId, Pageable pageable) {
        return this.paginationFactory.create(
                postRepositorySupport.findByCenterAndHoldExceptBlockUser(centerId, holdId, userId, pageable)
                            .map((post) -> CenterPostThumbnailResponseDto.from(post.getId(), post.getThumbnailUrl()))
            );
    }

    @Override
    public Pagination<CenterPostThumbnailResponseDto> findByCenterExceptBlockUser(String centerId, String userId, Pageable pageable) {
        return this.paginationFactory.create(
                postRepositorySupport.findByCenterExceptBlockUser(centerId, userId, pageable)
                        .map((post) -> CenterPostThumbnailResponseDto.from(post.getId(), post.getThumbnailUrl()))
        );
    }

    @Override
    public Integer countByCenterExceptBlockUser(String centerId, String userId) {
        return postRepositorySupport.countByCenterExceptBlockUser(centerId, userId);
    }
}
