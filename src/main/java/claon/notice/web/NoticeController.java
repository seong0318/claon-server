package claon.notice.web;

import claon.common.domain.Pagination;
import claon.notice.dto.NoticeResponseDto;
import claon.notice.service.NoticeService;
import claon.user.domain.UserDetails;
import claon.notice.dto.NoticeCreateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notices")
public class NoticeController {
    private final NoticeService noticeService;

    @GetMapping
    public Pagination<NoticeResponseDto> getNoticeList(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return noticeService.getNoticeList(pageable);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.OK)
    public NoticeResponseDto createNotice(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid NoticeCreateRequestDto dto
    ) {
        return noticeService.createNotice(userDetails.getUser(), dto);
    }
}
