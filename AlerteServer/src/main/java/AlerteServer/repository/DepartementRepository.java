package AlerteServer.repository;

import AlerteServer.entity.Departement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface DepartementRepository extends JpaRepository<Departement, String>{
}
