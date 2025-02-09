package claon.user.web;

import claon.common.domain.Pagination;
import claon.user.domain.UserDetails;
import claon.user.dto.LaonFindResponseDto;
import claon.user.dto.UserPostDetailResponseDto;
import claon.user.service.LaonService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/laon")
public class LaonController {
    private final LaonService laonService;

    @PostMapping(value = "/{laonNickname}")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void createLaon(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String laonNickname
    ) {
        this.laonService.createLaon(userDetails.getUser(), laonNickname);
    }

    @DeleteMapping(value = "/{laonNickname}")
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteLaon(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String laonNickname
    ) {
        this.laonService.deleteLaon(userDetails.getUser(), laonNickname);
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<LaonFindResponseDto> findAllLaon(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) final Pageable pageable
    ) {
        return this.laonService.findAllLaon(userDetails.getUser(), pageable);
    }

    @GetMapping("/posts")
    @ResponseStatus(value = HttpStatus.OK)
    public Pagination<UserPostDetailResponseDto> getLaonPost(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return this.laonService.findLaonPost(userDetails.getUser(), pageable);
    }
}
