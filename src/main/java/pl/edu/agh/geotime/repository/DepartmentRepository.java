package pl.edu.agh.geotime.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.domain.Department;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findById(Long id);

    @EntityGraph(attributePaths = {"studyFields"})
    Optional<Department> findOneByIdAndFunctionalIsFalse(Long id);

    @EntityGraph(attributePaths = {"studyFields"})
    List<Department> findAllByFunctionalIsFalse();
}
