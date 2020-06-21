package pl.edu.agh.geotime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.agh.geotime.domain.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
