package coLaon.ClaonBack.center.web;

import coLaon.ClaonBack.center.dto.CenterBookmarkResponseDto;
import coLaon.ClaonBack.center.dto.CenterCreateRequestDto;
import coLaon.ClaonBack.center.dto.CenterDetailResponseDto;
import coLaon.ClaonBack.center.dto.CenterReportCreateRequestDto;
import coLaon.ClaonBack.center.dto.CenterReportResponseDto;
import coLaon.ClaonBack.center.dto.CenterResponseDto;
import coLaon.ClaonBack.center.dto.CenterSearchResponseDto;
import coLaon.ClaonBack.center.dto.HoldInfoResponseDto;
import coLaon.ClaonBack.center.dto.ReviewCreateRequestDto;
import coLaon.ClaonBack.center.dto.ReviewListFindResponseDto;
import coLaon.ClaonBack.center.dto.ReviewResponseDto;
import coLaon.ClaonBack.center.dto.ReviewUpdateRequestDto;
import coLaon.ClaonBack.center.dto.CenterPreviewResponseDto;
import coLaon.ClaonBack.center.dto.CenterSearchOption;
import coLaon.ClaonBack.center.service.CenterBookmarkService;
import coLaon.ClaonBack.center.service.CenterReviewService;
import coLaon.ClaonBack.center.service.CenterService;
import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.config.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/centers")
public class CenterController {
    private final CenterService centerService;
    private final CenterReviewService centerReviewService;
    private final CenterBookmarkService centerBookmarkService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CenterResponseDto create(
            @AuthenticationPrincipal UserAccount userAccount,
            @RequestBody CenterCreateRequestDto centerCreateRequestDto
    ) {
        return this.centerService.create(userAccount.getUser(), centerCreateRequestDto);
    }

    @GetMapping(value = "/{centerId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CenterDetailResponseDto findCenter(
            @AuthenticationPrincipal UserAccount userAccount,
            @PathVariable String centerId
    ) {
        return this.centerService.findCenter(userAccount.getUser(), centerId);
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<CenterPreviewResponseDto> getCenterList(
            @AuthenticationPrincipal UserAccount userAccount,
            @RequestParam("option") CenterSearchOption option,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return this.centerService.findCenterListByOption(userAccount.getUser(), option, pageable);
    }

    @GetMapping(value = "/name/{keyword}")
    @ResponseStatus(value = HttpStatus.OK)
    public List<CenterSearchResponseDto> searchCenter(
            @PathVariable String keyword
    ) {
        return this.centerService.searchCenter(keyword);
    }

    @GetMapping(value = "/{centerId}/hold")
    @ResponseStatus(value = HttpStatus.OK)
    public List<HoldInfoResponseDto> findHoldInfoByCenter(
            @PathVariable String centerId
    ) {
        return this.centerService.findHoldInfoByCenterId(centerId);
    }

    @PostMapping("/{centerId}/review")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ReviewResponseDto createReview(
            @AuthenticationPrincipal UserAccount userAccount,
            @PathVariable String centerId,
            @RequestBody @Valid ReviewCreateRequestDto reviewCreateRequestDto
    ) {
        return this.centerReviewService.createReview(userAccount.getUser(), centerId, reviewCreateRequestDto);
    }

    @PutMapping(value = "/review/{reviewId}")
    @ResponseStatus(value = HttpStatus.OK)
    public ReviewResponseDto updateReview(
            @AuthenticationPrincipal UserAccount userAccount,
            @PathVariable String reviewId,
            @RequestBody @Valid ReviewUpdateRequestDto updateRequestDto
    ) {
        return this.centerReviewService.updateReview(userAccount.getUser(), reviewId, updateRequestDto);
    }

    @DeleteMapping(value = "/review/{reviewId}")
    @ResponseStatus(value = HttpStatus.OK)
    public ReviewResponseDto deleteReview(
            @AuthenticationPrincipal UserAccount userAccount,
            @PathVariable String reviewId
    ) {
        return this.centerReviewService.deleteReview(userAccount.getUser(), reviewId);
    }

    @GetMapping(value = "/{centerId}/review")
    @ResponseStatus(value = HttpStatus.OK)
    public ReviewListFindResponseDto findReviewByCenter(
            @AuthenticationPrincipal UserAccount userAccount,
            @PathVariable String centerId,
            @PageableDefault(size = 5) final Pageable pageable
    ) {
        return this.centerReviewService.findReview(userAccount.getUser(), centerId, pageable);
    }

    @PostMapping(value = "/{centerId}/bookmark")
    @ResponseStatus(value = HttpStatus.CREATED)
    public CenterBookmarkResponseDto create(
            @AuthenticationPrincipal UserAccount userAccount,
            @PathVariable String centerId
    ) {
        return this.centerBookmarkService.create(userAccount.getUser(), centerId);
    }

    @DeleteMapping(value = "/{centerId}/bookmark")
    @ResponseStatus(value = HttpStatus.OK)
    public void delete(
            @AuthenticationPrincipal UserAccount userAccount,
            @PathVariable String centerId
    ) {
        this.centerBookmarkService.delete(userAccount.getUser(), centerId);
    }

    @PostMapping(value = "/{centerId}/report")
    @ResponseStatus(value = HttpStatus.CREATED)
    public CenterReportResponseDto createReport(
            @AuthenticationPrincipal UserAccount userAccount,
            @PathVariable String centerId,
            @RequestBody CenterReportCreateRequestDto centerReportCreateRequestDto
    ) {
        return this.centerService.createReport(userAccount.getUser(), centerId, centerReportCreateRequestDto);
    }
}
