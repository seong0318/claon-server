package claon.center.repository;

import claon.center.domain.CenterReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<CenterReview, String> {
    @Query(value = "SELECT * "
            + "FROM TB_CENTER_REVIEW AS r "
            + "WHERE r.user_id = :userId AND r.center_id = :centerId", nativeQuery = true)
    Optional<CenterReview> findByUserIdAndCenterId(@Param("userId") String userId, @Param("centerId") String centerId);
}

