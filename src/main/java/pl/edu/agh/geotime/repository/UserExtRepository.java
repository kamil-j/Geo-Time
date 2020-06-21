package pl.edu.agh.geotime.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.domain.Authority;
import pl.edu.agh.geotime.domain.Department;
import pl.edu.agh.geotime.domain.Subdepartment;
import pl.edu.agh.geotime.domain.UserExt;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface UserExtRepository extends JpaRepository<UserExt, Long> {
    String USERS_EXT_BY_LOGIN_CACHE = "usersExtByLogin";

    Optional<UserExt> findOneByIdAndSubdepartment_Department_Id(Long id, Long departmentId);
    Optional<UserExt> findOneByIdAndSubdepartment_Id(Long id, Long subdepartmentId);
    Optional<UserExt> findOneByUser_Login(String login);
    Optional<UserExt> findOneByUser_LoginAndSubdepartment_Department_Id(String login, Long departmentId);
    Optional<UserExt> findOneByUser_LoginAndSubdepartment_Id(String login, Long subdepartmentId);
    Optional<UserExt> findOneByUser_EmailIgnoreCase(String email);
    List<UserExt> findAllByUser_ActivatedIsFalseAndUser_CreatedDateBefore(Instant dateTime);

    @EntityGraph(attributePaths = {"user.authorities"})
    Page<UserExt> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"user.authorities"})
    Page<UserExt> findAllBySubdepartment_DepartmentAndUser_AuthoritiesContains(Pageable pageable, Department department, Authority authority);

    @EntityGraph(attributePaths = {"user.authorities"})
    Page<UserExt> findAllBySubdepartmentAndUser_AuthoritiesContains(Pageable pageable, Subdepartment subdepartment, Authority authority);

    @EntityGraph(attributePaths = {"user.authorities"})
    @Cacheable(cacheNames = USERS_EXT_BY_LOGIN_CACHE)
    Optional<UserExt> findOneWithUser_AuthoritiesByUser_Login(String login);

    @EntityGraph(attributePaths = {"user.authorities"})
    Optional<UserExt> findOneWithUser_AuthoritiesByIdAndSubdepartment_Department_Id(Long id, Long departmentId);

    @EntityGraph(attributePaths = {"user.authorities"})
    Optional<UserExt> findOneWithUser_AuthoritiesByIdAndSubdepartment_Id(Long id, Long subdepartmentId);

    @EntityGraph(attributePaths = {"user.authorities"})
    Optional<UserExt> findOneWithUser_AuthoritiesByUser_LoginAndSubdepartment_Department_Id(String login, Long departmentId);

    @EntityGraph(attributePaths = {"user.authorities"})
    Optional<UserExt> findOneWithUser_AuthoritiesByUser_LoginAndSubdepartment_Id(String login, Long subdepartmentId);
}
