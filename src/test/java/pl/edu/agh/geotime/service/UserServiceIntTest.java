package pl.edu.agh.geotime.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.GeoTimeApp;
import pl.edu.agh.geotime.domain.User;
import pl.edu.agh.geotime.domain.UserExt;
import pl.edu.agh.geotime.repository.UserExtRepository;
import pl.edu.agh.geotime.repository.UserRepository;
import pl.edu.agh.geotime.web.rest.UserResourceIntTest;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GeoTimeApp.class)
@Transactional
public class UserServiceIntTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserExtRepository userExtRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EntityManager em;

    private UserExt userExt;

    @Before
    public void init() {
        userExt = UserResourceIntTest.createEntity(em);
        userExtRepository.saveAndFlush(userExt);
    }

    @Test
    @Transactional
    public void testFindNotActivatedUsersByCreationDateBefore() {
        User user = userExt.getUser();
        user.setActivated(false);
        user.setCreatedDate(Instant.now().minus(65, ChronoUnit.DAYS));

        userExtRepository.saveAndFlush(userExt);

        List<User> users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(
            Instant.now().minus(3, ChronoUnit.DAYS)
        );
        assertThat(users).isNotEmpty();

        userService.removeNotActivatedUsers();

        users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(
            Instant.now().minus(3, ChronoUnit.DAYS)
        );
        assertThat(users).isEmpty();
    }

    @Test
    @Transactional
    public void testRemoveNotActivatedUsers() {
        User user = userExt.getUser();
        user.setActivated(false);
        user.setCreatedDate(Instant.now().minus(65, ChronoUnit.DAYS));
        userExtRepository.saveAndFlush(userExt);

        assertThat(userRepository.findOneByLogin(user.getLogin())).isPresent();
        assertThat(userExtRepository.findOne(user.getId())).isNotNull();

        userService.removeNotActivatedUsers();

        assertThat(userRepository.findOneByLogin(user.getLogin())).isNotPresent();
        assertThat(userExtRepository.findOne(user.getId())).isNull();
    }
}
