package claon.repository;

import claon.center.domain.CenterImg;
import claon.center.domain.Charge;
import claon.center.domain.ChargeElement;
import claon.center.domain.OperatingTime;
import claon.center.repository.HoldInfoRepository;
import claon.center.domain.Center;
import claon.center.domain.HoldInfo;
import claon.center.repository.CenterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class HoldInfoRepositoryTest {
    @Autowired
    private CenterRepository centerRepository;
    @Autowired
    private HoldInfoRepository holdInfoRepository;

    private Center center;
    private HoldInfo holdInfo;

    @BeforeEach
    void setUp() {
        this.center = centerRepository.save(Center.of(
                "test",
                "test",
                "010-1234-1234",
                "https://test.com",
                "https://instagram.com/test",
                "https://youtube.com/channel/test",
                List.of(new CenterImg("img test")),
                List.of(new OperatingTime("매일", "10:00", "23:00")),
                "facilities test",
                List.of(new Charge(List.of(new ChargeElement("자유 패키지", "330,000")), "charge image")),
                "hold info img test"
        ));

        this.holdInfo = holdInfoRepository.save(HoldInfo.of(
                "test",
                "test.jpg",
                this.center
        ));
    }

    @Test
    public void successFindAllByCenter() {
        // when
        List<HoldInfo> holdInfoList = holdInfoRepository.findAllByCenter(this.center);

        // then
        assertThat(holdInfoList.size()).isEqualTo(1);
    }

    @Test
    public void successFindByIdAndCenter() {
        // given
        String holdInfoId = this.holdInfo.getId();

        // when
        Optional<HoldInfo> holdInfo = holdInfoRepository.findByIdAndCenter(holdInfoId, this.center);

        // then
        assertThat(holdInfo.isPresent()).isTrue();
    }
}
