package claon.repository;

import claon.center.domain.CenterImg;
import claon.center.domain.Charge;
import claon.center.domain.ChargeElement;
import claon.center.domain.OperatingTime;
import claon.post.domain.PostReport;
import claon.user.domain.User;
import claon.user.repository.UserRepository;
import claon.center.domain.Center;
import claon.center.repository.CenterRepository;
import claon.post.domain.Post;
import claon.post.domain.enums.PostReportType;
import claon.post.repository.PostReportRepository;
import claon.post.repository.PostRepository;
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
public class PostReportRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CenterRepository centerRepository;
    @Autowired
    private PostReportRepository postReportRepository;

    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        this.user = userRepository.save(User.of(
                "test@gmail.com",
                "1234567890",
                "test",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId"
        ));

        Center center = Center.of(
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
        );
        centerRepository.save(center);

        this.post = postRepository.save(Post.of(
                center,
                "testContent1",
                user,
                List.of(),
                List.of()
        ));

        postReportRepository.save(PostReport.of(
                this.user, this.post, PostReportType.INAPPROPRIATE_POST, "testContent"
        ));
    }

    @Test
    public void successFindByReporterAndPost() {
        // when
        Optional<PostReport> postReport = postReportRepository.findByReporterAndPost(this.user, this.post);

        // then
        assertThat(postReport).isPresent();
    }
}
