package claon.center.service;

import claon.common.domain.Pagination;
import claon.center.dto.CenterPostThumbnailResponseDto;
import org.springframework.data.domain.Pageable;

public interface PostPort {
    Pagination<CenterPostThumbnailResponseDto> findByCenterAndHoldExceptBlockUser(String centerId, String holdId, String userId, Pageable pageable);

    Pagination<CenterPostThumbnailResponseDto> findByCenterExceptBlockUser(String centerId, String userId, Pageable pageable);

    Integer countByCenterExceptBlockUser(String centerId, String userId);
}
