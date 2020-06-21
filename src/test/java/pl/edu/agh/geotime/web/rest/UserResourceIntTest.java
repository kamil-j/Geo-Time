package pl.edu.agh.geotime.web.rest;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.GeoTimeApp;
import pl.edu.agh.geotime.domain.*;
import pl.edu.agh.geotime.repository.DepartmentRepository;
import pl.edu.agh.geotime.repository.UserExtRepository;
import pl.edu.agh.geotime.repository.UserRepository;
import pl.edu.agh.geotime.security.AuthoritiesConstants;
import pl.edu.agh.geotime.service.MailService;
import pl.edu.agh.geotime.service.UserService;
import pl.edu.agh.geotime.web.rest.dto.ext.UserExtDTO;
import pl.edu.agh.geotime.web.rest.mapper.UserMapper;
import pl.edu.agh.geotime.web.rest.errors.ExceptionTranslator;
import pl.edu.agh.geotime.web.rest.helper.UserUploadHelper;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.edu.agh.geotime.web.rest.TestUtil.createFormattingConversionService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GeoTimeApp.class)
public class UserResourceIntTest {

    private static final String DEFAULT_LOGIN = "johndoe";
    private static final String UPDATED_LOGIN = "jhipster";

    private static final Long DEFAULT_ID = 1L;

    private static final String DEFAULT_EMAIL = "johndoe@localhost";
    private static final String UPDATED_EMAIL = "jhipster@localhost";

    private static final String DEFAULT_FIRSTNAME = "john";
    private static final String UPDATED_FIRSTNAME = "jhipsterFirstName";

    private static final String DEFAULT_LASTNAME = "doe";
    private static final String UPDATED_LASTNAME = "jhipsterLastName";

    private static final String DEFAULT_LANGKEY = "en";
    private static final String UPDATED_LANGKEY = "fr";

    private static final Long DEFAULT_SUBDEPARTMENT_ID = 123L;
    private static final String DEFAULT_SUBDEPARTMENT_SHORT_NAME = "GeoTime";
    private static final Subdepartment DEFAULT_SUBDEPARTMENT_ENTITY = new Subdepartment("GeoTime Dep", DEFAULT_SUBDEPARTMENT_SHORT_NAME);

    private static final String TEST_USER_LOGIN = "system";

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserExtRepository userExtRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserUploadHelper userUploadHelper;

    @Autowired
    private MailService mailService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CacheManager cacheManager;

    private MockMvc restUserExtMockMvc;

    private UserExt userExt;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        cacheManager.getCache(userExtRepository.USERS_EXT_BY_LOGIN_CACHE).clear();
        final UserResource userResource = new UserResource(userExtRepository, userMapper, userService, mailService, userUploadHelper);
        this.restUserExtMockMvc = MockMvcBuilders.standaloneSetup(userResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    public static UserExt createEntity(EntityManager em) {
        UserExt userExt = new UserExt();
        // Add required entity
        User user = createUserEntity();
        em.persist(user);
        em.flush();
        userExt.setUser(user);
        userExt.setId(user.getId());

        // Add required entity
        UserGroup userGroup = UserGroupResourceIntTest.createEntity(em);
        em.persist(userGroup);
        em.flush();
        userExt.setUserGroup(userGroup);

        Department department = DepartmentResourceIntTest.createEntity(em);
        em.persist(department);
        em.flush();

        Subdepartment subdepartment = SubdepartmentResourceIntTest.createEntity(em);
        subdepartment.setDepartment(department);
        em.persist(subdepartment);
        em.flush();

        userExt.setSubdepartment(subdepartment);

        return userExt;
    }

    private static User createUserEntity() {
        User user = new User();
        user.setLogin(DEFAULT_LOGIN + RandomStringUtils.randomAlphabetic(5));
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);
        user.setEmail(RandomStringUtils.randomAlphabetic(5) + DEFAULT_EMAIL);
        user.setFirstName(DEFAULT_FIRSTNAME);
        user.setLastName(DEFAULT_LASTNAME);
        user.setLangKey(DEFAULT_LANGKEY);
        Authority authority = new Authority();
        authority.setName(AuthoritiesConstants.USER);
        user.setAuthorities(Collections.singleton(authority));
        return user;
    }

    @Before
    public void initTest() {
        userExt = createEntity(em);
        userExt.getUser().setLogin(DEFAULT_LOGIN);
        userExt.getUser().setEmail(DEFAULT_EMAIL);

        Department department = departmentRepository.findOne(1L);
        Subdepartment subdepartment = userExt.getSubdepartment();
        subdepartment.setDepartment(department);
        em.persist(subdepartment);
        em.flush();
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void createUserExt() throws Exception {
        int databaseSizeBeforeCreate = userExtRepository.findAll().size();

        // Create the User
        UserExtDTO userDTO = new UserExtDTO();
        userDTO.setLogin(DEFAULT_LOGIN+"1");
        userDTO.setFirstName(DEFAULT_FIRSTNAME+"1");
        userDTO.setLastName(DEFAULT_LASTNAME+"1");
        userDTO.setEmail(DEFAULT_EMAIL+"1");
        userDTO.setActivated(true);
        userDTO.setLangKey(DEFAULT_LANGKEY);
        userDTO.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));
        userDTO.setSubdepartmentId(userExt.getSubdepartment().getId());
        userDTO.setUserGroupId(userExt.getUserGroup().getId());

        restUserExtMockMvc.perform(post("/api/users")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userDTO)))
            .andExpect(status().isCreated());

        // Validate the User in the database
        List<UserExt> userList = userExtRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeCreate + 1);
        UserExt testUser = userList.get(userList.size() - 1);
        assertThat(testUser.getUser().getLogin()).isEqualTo(DEFAULT_LOGIN+"1");
        assertThat(testUser.getUser().getFirstName()).isEqualTo(DEFAULT_FIRSTNAME+"1");
        assertThat(testUser.getUser().getLastName()).isEqualTo(DEFAULT_LASTNAME+"1");
        assertThat(testUser.getUser().getEmail()).isEqualTo(DEFAULT_EMAIL+"1");
        assertThat(testUser.getUser().getLangKey()).isEqualTo(DEFAULT_LANGKEY);
        assertThat(testUser.getSubdepartment()).isEqualTo(userExt.getSubdepartment());
        assertThat(testUser.getUserGroup()).isEqualTo(userExt.getUserGroup());
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void createUserExtWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = userExtRepository.findAll().size();

        UserExtDTO userDTO = new UserExtDTO();
        userDTO.setId(1L);
        userDTO.setLogin(DEFAULT_LOGIN);
        userDTO.setFirstName(DEFAULT_FIRSTNAME);
        userDTO.setLastName(DEFAULT_LASTNAME);
        userDTO.setEmail(DEFAULT_EMAIL);
        userDTO.setActivated(true);
        userDTO.setLangKey(DEFAULT_LANGKEY);
        userDTO.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));
        userDTO.setUserGroupId(userExt.getUserGroup().getId());

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserExtMockMvc.perform(post("/api/users")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userDTO)))
            .andExpect(status().isBadRequest());

        // Validate the User in the database
        List<UserExt> userList = userExtRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void createUserWithExistingLogin() throws Exception {
        // Initialize the database
        userExtRepository.saveAndFlush(userExt);
        int databaseSizeBeforeCreate = userExtRepository.findAll().size();

        UserExtDTO userDTO = new UserExtDTO();
        userDTO.setLogin(DEFAULT_LOGIN);// this login should already be used
        userDTO.setFirstName(DEFAULT_FIRSTNAME);
        userDTO.setLastName(DEFAULT_LASTNAME);
        userDTO.setEmail("anothermail@localhost");
        userDTO.setActivated(true);
        userDTO.setLangKey(DEFAULT_LANGKEY);
        userDTO.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));
        userDTO.setUserGroupId(userExt.getUserGroup().getId());

        // Create the User
        restUserExtMockMvc.perform(post("/api/users")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userDTO)))
            .andExpect(status().isBadRequest());

        // Validate the User in the database
        List<UserExt> userList = userExtRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void createUserWithExistingEmail() throws Exception {
        // Initialize the database
        userExtRepository.saveAndFlush(userExt);
        int databaseSizeBeforeCreate = userExtRepository.findAll().size();

        UserExtDTO userDTO = new UserExtDTO();
        userDTO.setLogin("anotherlogin");
        userDTO.setFirstName(DEFAULT_FIRSTNAME);
        userDTO.setLastName(DEFAULT_LASTNAME);
        userDTO.setEmail(DEFAULT_EMAIL);// this email should already be used
        userDTO.setActivated(true);
        userDTO.setLangKey(DEFAULT_LANGKEY);
        userDTO.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));
        userDTO.setUserGroupId(userExt.getUserGroup().getId());

        // Create the User
        restUserExtMockMvc.perform(post("/api/users")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userDTO)))
            .andExpect(status().isBadRequest());

        // Validate the User in the database
        List<UserExt> userList = userExtRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllUsersExt() throws Exception {
        // Initialize the database
        userExtRepository.saveAndFlush(userExt);

        // Get all the users
        restUserExtMockMvc.perform(get("/api/users?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].login").value(hasItem(DEFAULT_LOGIN)))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRSTNAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LASTNAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].langKey").value(hasItem(DEFAULT_LANGKEY)))
            .andExpect(jsonPath("$.[*].subdepartmentId").value(hasItem(userExt.getSubdepartment().getId().intValue())))
            .andExpect(jsonPath("$.[*].userGroupId").value(hasItem(userExt.getUserGroup().getId().intValue())));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getUserExt() throws Exception {
        // Initialize the database
        userExtRepository.saveAndFlush(userExt);
        String userLogin = userExt.getUser().getLogin();

        assertThat(cacheManager.getCache(UserExtRepository.USERS_EXT_BY_LOGIN_CACHE).get(TEST_USER_LOGIN)).isNull();

        // Get the user
        restUserExtMockMvc.perform(get("/api/users/{login}", userExt.getUser().getLogin()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.login").value(userLogin))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRSTNAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LASTNAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.langKey").value(DEFAULT_LANGKEY))
            .andExpect(jsonPath("$.subdepartmentId").value(userExt.getSubdepartment().getId().intValue()))
            .andExpect(jsonPath("$.userGroupId").value(userExt.getUserGroup().getId().intValue()));

        assertThat(cacheManager.getCache(UserExtRepository.USERS_EXT_BY_LOGIN_CACHE).get(TEST_USER_LOGIN)).isNotNull();
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getNonExistingUserExt() throws Exception {
        restUserExtMockMvc.perform(get("/api/users/unknown"))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void updateUserExt() throws Exception {
        // Initialize the database
        userExtRepository.saveAndFlush(userExt);
        int databaseSizeBeforeUpdate = userExtRepository.findAll().size();

        Subdepartment updatedSubdepartment = SubdepartmentResourceIntTest.createEntity(em);
        updatedSubdepartment.name("UPDATED SUBDEPARTMENT")
            .shortName("UPD_NAME");
        updatedSubdepartment.setDepartment(userExt.getSubdepartment().getDepartment());
        em.persist(updatedSubdepartment);
        em.flush();

        UserGroup updatedUserGroup = UserGroupResourceIntTest.createEntity(em);
        updatedUserGroup.name("UPDATED GROUP");
        em.persist(updatedUserGroup);
        em.flush();

        // Update the user
        UserExt updatedUser = userExtRepository.findOne(userExt.getId());
        UserExtDTO userExtDTO = userMapper.toDto(updatedUser);
        userExtDTO.setFirstName(UPDATED_FIRSTNAME);
        userExtDTO.setLastName(UPDATED_LASTNAME);
        userExtDTO.setEmail(UPDATED_EMAIL);
        userExtDTO.setLangKey(UPDATED_LANGKEY);
        userExtDTO.setSubdepartmentId(updatedSubdepartment.getId());
        userExtDTO.setUserGroupId(updatedUserGroup.getId());

        restUserExtMockMvc.perform(put("/api/users")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userExtDTO)))
            .andExpect(status().isOk());

        // Validate the User in the database
        List<UserExt> userList = userExtRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeUpdate);
        UserExt testUser = userList.get(userList.size() - 1);
        assertThat(testUser.getUser().getFirstName()).isEqualTo(UPDATED_FIRSTNAME);
        assertThat(testUser.getUser().getLastName()).isEqualTo(UPDATED_LASTNAME);
        assertThat(testUser.getUser().getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testUser.getUser().getLangKey()).isEqualTo(UPDATED_LANGKEY);
        assertThat(testUser.getSubdepartment()).isEqualTo(updatedSubdepartment);
        assertThat(testUser.getUserGroup()).isEqualTo(updatedUserGroup);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void updateUserLogin() throws Exception {
        // Initialize the database
        userExtRepository.saveAndFlush(userExt);
        int databaseSizeBeforeUpdate = userExtRepository.findAll().size();

        // Update the user
        UserExt updatedUser = userExtRepository.findOne(userExt.getId());

        UserExtDTO userDTO = new UserExtDTO();
        userDTO.setId(updatedUser.getId());
        userDTO.setLogin(UPDATED_LOGIN);
        userDTO.setFirstName(UPDATED_FIRSTNAME);
        userDTO.setLastName(UPDATED_LASTNAME);
        userDTO.setEmail(UPDATED_EMAIL);
        userDTO.setActivated(updatedUser.getUser().getActivated());
        userDTO.setLangKey(UPDATED_LANGKEY);
        userDTO.setCreatedBy(updatedUser.getUser().getCreatedBy());
        userDTO.setCreatedDate(updatedUser.getUser().getCreatedDate());
        userDTO.setLastModifiedBy(updatedUser.getUser().getLastModifiedBy());
        userDTO.setLastModifiedDate(updatedUser.getUser().getLastModifiedDate());
        userDTO.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));
        userDTO.setSubdepartmentId(userExt.getSubdepartment().getId());
        userDTO.setUserGroupId(userExt.getUserGroup().getId());

        restUserExtMockMvc.perform(put("/api/users")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userDTO)))
            .andExpect(status().isOk());

        // Validate the User in the database
        List<UserExt> userList = userExtRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeUpdate);
        UserExt testUser = userList.get(userList.size() - 1);
        assertThat(testUser.getUser().getLogin()).isEqualTo(UPDATED_LOGIN);
        assertThat(testUser.getUser().getFirstName()).isEqualTo(UPDATED_FIRSTNAME);
        assertThat(testUser.getUser().getLastName()).isEqualTo(UPDATED_LASTNAME);
        assertThat(testUser.getUser().getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testUser.getUser().getLangKey()).isEqualTo(UPDATED_LANGKEY);
        assertThat(testUser.getSubdepartment()).isEqualTo(userExt.getSubdepartment());
        assertThat(testUser.getUserGroup()).isEqualTo(userExt.getUserGroup());
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void updateUserExistingEmail() throws Exception {
        // Initialize the database with 2 users
        userExtRepository.saveAndFlush(userExt);

        User anotherUser = new User();
        anotherUser.setLogin("jhipster");
        anotherUser.setPassword(RandomStringUtils.random(60));
        anotherUser.setActivated(true);
        anotherUser.setEmail("jhipster@localhost");
        anotherUser.setFirstName("java");
        anotherUser.setLastName("hipster");
        anotherUser.setLangKey("en");

        em.persist(anotherUser);
        em.flush();

        UserExt anotherUserExt = new UserExt();
        anotherUserExt.setId(anotherUser.getId());
        anotherUserExt.setUser(anotherUser);
        anotherUserExt.setSubdepartment(userExt.getSubdepartment());
        anotherUserExt.setUserGroup(userExt.getUserGroup());

        userExtRepository.saveAndFlush(anotherUserExt);

        // Update the user
        UserExt updatedUser = userExtRepository.findOne(userExt.getId());

        UserExtDTO userDTO = new UserExtDTO();
        userDTO.setId(updatedUser.getId());
        userDTO.setLogin(updatedUser.getUser().getLogin());
        userDTO.setFirstName(updatedUser.getUser().getFirstName());
        userDTO.setLastName(updatedUser.getUser().getLastName());
        userDTO.setEmail("jhipster@localhost");// this email should already be used by anotherUser
        userDTO.setActivated(updatedUser.getUser().getActivated());
        userDTO.setLangKey(updatedUser.getUser().getLangKey());
        userDTO.setCreatedBy(updatedUser.getUser().getCreatedBy());
        userDTO.setCreatedDate(updatedUser.getUser().getCreatedDate());
        userDTO.setLastModifiedBy(updatedUser.getUser().getLastModifiedBy());
        userDTO.setLastModifiedDate(updatedUser.getUser().getLastModifiedDate());
        userDTO.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));
        userDTO.setSubdepartmentId(userExt.getSubdepartment().getId());
        userDTO.setUserGroupId(userExt.getUserGroup().getId());

        restUserExtMockMvc.perform(put("/api/users")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userDTO)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void updateUserExistingLogin() throws Exception {
        // Initialize the database
        userExtRepository.saveAndFlush(userExt);

        User anotherUser = new User();
        anotherUser.setLogin("jhipster");
        anotherUser.setPassword(RandomStringUtils.random(60));
        anotherUser.setActivated(true);
        anotherUser.setEmail("jhipster@localhost");
        anotherUser.setFirstName("java");
        anotherUser.setLastName("hipster");
        anotherUser.setLangKey("en");

        em.persist(anotherUser);
        em.flush();

        UserExt anotherUserExt = new UserExt();
        anotherUserExt.setId(anotherUser.getId());
        anotherUserExt.setUser(anotherUser);
        anotherUserExt.setSubdepartment(userExt.getSubdepartment());
        anotherUserExt.setUserGroup(userExt.getUserGroup());

        userExtRepository.saveAndFlush(anotherUserExt);

        // Update the user
        UserExt updatedUser = userExtRepository.findOne(userExt.getId());

        UserExtDTO userDTO = new UserExtDTO();
        userDTO.setId(updatedUser.getId());
        userDTO.setLogin("jhipster");// this login should already be used by anotherUser
        userDTO.setFirstName(updatedUser.getUser().getFirstName());
        userDTO.setLastName(updatedUser.getUser().getLastName());
        userDTO.setEmail(updatedUser.getUser().getEmail());
        userDTO.setActivated(updatedUser.getUser().getActivated());
        userDTO.setLangKey(updatedUser.getUser().getLangKey());
        userDTO.setCreatedBy(updatedUser.getUser().getCreatedBy());
        userDTO.setCreatedDate(updatedUser.getUser().getCreatedDate());
        userDTO.setLastModifiedBy(updatedUser.getUser().getLastModifiedBy());
        userDTO.setLastModifiedDate(updatedUser.getUser().getLastModifiedDate());
        userDTO.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));
        userDTO.setSubdepartmentId(userExt.getSubdepartment().getId());
        userDTO.setUserGroupId(userExt.getUserGroup().getId());

        restUserExtMockMvc.perform(put("/api/users")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userDTO)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void deleteUser() throws Exception {
        // Initialize the database
        userExtRepository.saveAndFlush(userExt);
        int databaseSizeBeforeDelete = userExtRepository.findAll().size();

        // Delete the user
        restUserExtMockMvc.perform(delete("/api/users/{login}", userExt.getUser().getLogin())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        assertThat(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).get(userExt.getUser().getLogin())).isNull();

        // Validate the database is empty
        List<UserExt> userList = userExtRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllAuthorities() throws Exception {
        restUserExtMockMvc.perform(get("/api/users/authorities")
            .accept(TestUtil.APPLICATION_JSON_UTF8)
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").value(containsInAnyOrder(AuthoritiesConstants.USER,
                AuthoritiesConstants.MANAGER, AuthoritiesConstants.PLANNER)));
    }

    @Test
    @Transactional
    public void testUserEquals() throws Exception {
        TestUtil.equalsVerifier(UserExt.class);
        UserExt user1 = new UserExt();
        user1.setId(1L);
        UserExt user2 = new UserExt();
        user2.setId(user1.getId());
        assertThat(user1).isEqualTo(user2);
        user2.setId(2L);
        assertThat(user1).isNotEqualTo(user2);
        user1.setId(null);
        assertThat(user1).isNotEqualTo(user2);
    }

    @Test
    @Transactional
    public void testUserExtFromId() {
        assertThat(userMapper.fromId(DEFAULT_ID).getId()).isEqualTo(DEFAULT_ID);
        assertThat(userMapper.fromId(null)).isNull();
    }

    @Test
    @Transactional
    public void testUserExtDTOtoUserExt() {
        UserExtDTO userExtDTO = new UserExtDTO();
        userExtDTO.setId(DEFAULT_ID);
        userExtDTO.setLogin(DEFAULT_LOGIN);
        userExtDTO.setFirstName(DEFAULT_FIRSTNAME);
        userExtDTO.setLastName(DEFAULT_LASTNAME);
        userExtDTO.setEmail(DEFAULT_EMAIL);
        userExtDTO.setActivated(true);
        userExtDTO.setLangKey(DEFAULT_LANGKEY);
        userExtDTO.setCreatedBy(DEFAULT_LOGIN);
        userExtDTO.setLastModifiedBy(DEFAULT_LOGIN);
        userExtDTO.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));
        userExtDTO.setSubdepartmentId(DEFAULT_SUBDEPARTMENT_ID);
        userExtDTO.setSubdepartmentShortName(DEFAULT_SUBDEPARTMENT_SHORT_NAME);
        userExtDTO.setUserGroupId(userExt.getUserGroup().getId());

        UserExt newUserExt = userMapper.toEntity(userExtDTO);
        assertThat(newUserExt.getId()).isEqualTo(DEFAULT_ID);
        assertThat(newUserExt.getSubdepartment().getId()).isEqualTo(DEFAULT_SUBDEPARTMENT_ID);
        assertThat(newUserExt.getUserGroup().getId()).isEqualTo(userExt.getUserGroup().getId());

        User user = newUserExt.getUser();
        assertThat(user.getLogin()).isEqualTo(DEFAULT_LOGIN);
        assertThat(user.getFirstName()).isEqualTo(DEFAULT_FIRSTNAME);
        assertThat(user.getLastName()).isEqualTo(DEFAULT_LASTNAME);
        assertThat(user.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(user.getActivated()).isEqualTo(true);
        assertThat(user.getLangKey()).isEqualTo(DEFAULT_LANGKEY);
        assertThat(user.getCreatedBy()).isNull();
        assertThat(user.getCreatedDate()).isNotNull();
        assertThat(user.getLastModifiedBy()).isNull();
        assertThat(user.getLastModifiedDate()).isNotNull();
        assertThat(user.getAuthorities()).extracting("name").containsExactly(AuthoritiesConstants.USER);
    }

    @Test
    @Transactional
    public void testUserExtToUserExtDTO() {
        userExt.setId(DEFAULT_ID);
        userExt.getUser().setId(DEFAULT_ID);
        userExt.getUser().setCreatedBy(DEFAULT_LOGIN);
        userExt.getUser().setCreatedDate(Instant.now());
        userExt.getUser().setLastModifiedBy(DEFAULT_LOGIN);
        userExt.getUser().setLastModifiedDate(Instant.now());
        Set<Authority> authorities = new HashSet<>();
        Authority authority = new Authority();
        authority.setName(AuthoritiesConstants.USER);
        authorities.add(authority);
        userExt.getUser().setAuthorities(authorities);
        userExt.setSubdepartment(DEFAULT_SUBDEPARTMENT_ENTITY);
        userExt.setUserGroup(UserGroupResourceIntTest.createEntity(em));

        UserExtDTO userExtDTO = userMapper.toDto(userExt);

        assertThat(userExtDTO.getId()).isEqualTo(DEFAULT_ID);
        assertThat(userExtDTO.getLogin()).isEqualTo(DEFAULT_LOGIN);
        assertThat(userExtDTO.getFirstName()).isEqualTo(DEFAULT_FIRSTNAME);
        assertThat(userExtDTO.getLastName()).isEqualTo(DEFAULT_LASTNAME);
        assertThat(userExtDTO.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(userExtDTO.isActivated()).isEqualTo(true);
        assertThat(userExtDTO.getLangKey()).isEqualTo(DEFAULT_LANGKEY);
        assertThat(userExtDTO.getCreatedBy()).isEqualTo(DEFAULT_LOGIN);
        assertThat(userExtDTO.getCreatedDate()).isEqualTo(userExt.getUser().getCreatedDate());
        assertThat(userExtDTO.getLastModifiedBy()).isEqualTo(DEFAULT_LOGIN);
        assertThat(userExtDTO.getLastModifiedDate()).isEqualTo(userExt.getUser().getLastModifiedDate());
        assertThat(userExtDTO.getAuthorities()).containsExactly(AuthoritiesConstants.USER);
        assertThat(userExtDTO.getSubdepartmentId()).isEqualTo(DEFAULT_SUBDEPARTMENT_ENTITY.getId());
        assertThat(userExtDTO.getSubdepartmentShortName()).isEqualTo(DEFAULT_SUBDEPARTMENT_SHORT_NAME);
        assertThat(userExtDTO.getUserGroupId()).isEqualTo(userExt.getUserGroup().getId());
        assertThat(userExtDTO.getUserGroupName()).isEqualTo(userExt.getUserGroup().getName());
        assertThat(userExtDTO.toString()).isNotNull();
    }

    @Test
    @Transactional
    public void testAuthorityEquals() {
        Authority authorityA = new Authority();
        assertThat(authorityA).isEqualTo(authorityA);
        assertThat(authorityA).isNotEqualTo(null);
        assertThat(authorityA).isNotEqualTo(new Object());
        assertThat(authorityA.toString()).isNotNull();

        Authority authorityB = new Authority();
        assertThat(authorityA).isEqualTo(authorityB);

        authorityB.setName(AuthoritiesConstants.ADMIN);
        assertThat(authorityA).isNotEqualTo(authorityB);

        authorityB.setName(AuthoritiesConstants.MANAGER);
        assertThat(authorityA).isNotEqualTo(authorityB);

        authorityA.setName(AuthoritiesConstants.USER);
        assertThat(authorityA).isNotEqualTo(authorityB);

        authorityB.setName(AuthoritiesConstants.USER);
        assertThat(authorityA).isEqualTo(authorityB);
        assertThat(authorityA.hashCode()).isEqualTo(authorityB.hashCode());
    }
}
