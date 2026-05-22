package AlerteServer.repository;

import AlerteServer.entity.Ot;
import AlerteServer.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtRepository extends JpaRepository<Ot, String>{
}
