package claon.center.dto;

import claon.center.domain.SectorInfo;
import claon.center.domain.Center;
import claon.center.domain.HoldInfo;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class CenterDetailResponseDto {
    private String id;
    private String name;
    private String address;
    private String tel;
    private String webUrl;
    private String instagramUrl;
    private String youtubeUrl;
    private List<CenterImgDto> imgList;
    private List<OperatingTimeDto> operatingTimeList;
    private String facilities;
    private List<ChargeDto> chargeList;
    private List<CenterHoldInfoResponseDto> holdInfoList;
    private String holdInfoImg;
    private List<SectorInfoResponseDto> sectorInfoList;
    private Boolean isBookmarked;
    private Integer postCount;
    private Integer reviewCount;

    private CenterDetailResponseDto(
            String id,
            String name,
            String address,
            String tel,
            String webUrl,
            String instagramUrl,
            String youtubeUrl,
            List<CenterImgDto> imgList,
            List<OperatingTimeDto> operatingTimeList,
            String facilities,
            List<ChargeDto> chargeList,
            List<CenterHoldInfoResponseDto> holdInfoList,
            String holdInfoImg,
            List<SectorInfoResponseDto> sectorInfoList,
            Boolean isBookmarked,
            Integer postCount,
            Integer reviewCount
    ) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.tel = tel;
        this.webUrl = webUrl;
        this.instagramUrl = instagramUrl;
        this.youtubeUrl = youtubeUrl;
        this.imgList = imgList;
        this.operatingTimeList = operatingTimeList;
        this.facilities = facilities;
        this.chargeList = chargeList;
        this.holdInfoList = holdInfoList;
        this.holdInfoImg = holdInfoImg;
        this.sectorInfoList = sectorInfoList;
        this.isBookmarked = isBookmarked;
        this.postCount = postCount;
        this.reviewCount = reviewCount;
    }

    public static CenterDetailResponseDto from(
            Center center,
            List<HoldInfo> holdInfoList,
            List<SectorInfo> sectorInfoList,
            Boolean isBookmarked,
            Integer postCount,
            Integer reviewCount
    ) {
        return new CenterDetailResponseDto(
                center.getId(),
                center.getName(),
                center.getAddress(),
                center.getTel(),
                center.getWebUrl(),
                center.getInstagramUrl(),
                center.getYoutubeUrl(),
                center.getImgList().stream().map(CenterImgDto::from).collect(Collectors.toList()),
                center.getOperatingTime().stream().map(OperatingTimeDto::from).collect(Collectors.toList()),
                center.getFacilities(),
                center.getCharge().stream().map(ChargeDto::from).collect(Collectors.toList()),
                holdInfoList.stream().map(CenterHoldInfoResponseDto::from).collect(Collectors.toList()),
                center.getHoldInfoImg(),
                sectorInfoList.stream().map(SectorInfoResponseDto::from).collect(Collectors.toList()),
                isBookmarked,
                postCount,
                reviewCount
        );
    }
}
