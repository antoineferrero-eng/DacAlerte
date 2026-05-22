package AlerteServer.repository;

import AlerteServer.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteRepository extends JpaRepository<Site, String>{
}
