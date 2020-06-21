package pl.edu.agh.geotime.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.GeoTimeApp;
import pl.edu.agh.geotime.domain.ClassUnitGroup;
import pl.edu.agh.geotime.domain.Department;
import pl.edu.agh.geotime.repository.ClassUnitGroupRepository;
import pl.edu.agh.geotime.repository.DepartmentRepository;
import pl.edu.agh.geotime.service.ClassUnitGroupService;
import pl.edu.agh.geotime.web.rest.mapper.ClassUnitGroupMapper;
import pl.edu.agh.geotime.web.rest.errors.ExceptionTranslator;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.edu.agh.geotime.web.rest.TestUtil.createFormattingConversionService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GeoTimeApp.class)
public class ClassUnitGroupResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String TEST_USER_LOGIN = "system";

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private ClassUnitGroupService classUnitGroupService;

    @Autowired
    private ClassUnitGroupMapper classUnitGroupMapper;

    @Autowired
    private ClassUnitGroupRepository classUnitGroupRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restClassUnitGroupMockMvc;

    private ClassUnitGroup classUnitGroup;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ClassUnitGroupResource classUnitGroupResource = new ClassUnitGroupResource(classUnitGroupService, classUnitGroupMapper);
        this.restClassUnitGroupMockMvc = MockMvcBuilders.standaloneSetup(classUnitGroupResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    public static ClassUnitGroup createEntity(EntityManager em) {
        return new ClassUnitGroup()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION);
    }

    @Before
    public void initTest() {
        classUnitGroup = createEntity(em);
        initOtherEntityFields();
    }

    private void initOtherEntityFields() {
        Department department = departmentRepository.findOne(1L);
        classUnitGroup.setDepartment(department);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void createClassUnitGroup() throws Exception {
        int databaseSizeBeforeCreate = classUnitGroupRepository.findAll().size();

        // Create the ClassUnitGroup
        restClassUnitGroupMockMvc.perform(post("/api/class-unit-groups")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(classUnitGroupMapper.toDto(classUnitGroup))))
            .andExpect(status().isCreated());

        // Validate the ClassUnitGroup in the database
        List<ClassUnitGroup> classUnitGroupList = classUnitGroupRepository.findAll();
        assertThat(classUnitGroupList).hasSize(databaseSizeBeforeCreate + 1);
        ClassUnitGroup testClassUnitGroup = classUnitGroupList.get(classUnitGroupList.size() - 1);
        assertThat(testClassUnitGroup.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testClassUnitGroup.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void createClassUnitGroupWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = classUnitGroupRepository.findAll().size();

        // Create the ClassUnitGroup with an existing ID
        classUnitGroup.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restClassUnitGroupMockMvc.perform(post("/api/class-unit-groups")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(classUnitGroupMapper.toDto(classUnitGroup))))
            .andExpect(status().isBadRequest());

        // Validate the ClassUnitGroup in the database
        List<ClassUnitGroup> classUnitGroupList = classUnitGroupRepository.findAll();
        assertThat(classUnitGroupList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = classUnitGroupRepository.findAll().size();
        // set the field null
        classUnitGroup.setName(null);

        // Create the ClassUnitGroup, which fails.

        restClassUnitGroupMockMvc.perform(post("/api/class-unit-groups")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(classUnitGroupMapper.toDto(classUnitGroup))))
            .andExpect(status().isBadRequest());

        List<ClassUnitGroup> classUnitGroupList = classUnitGroupRepository.findAll();
        assertThat(classUnitGroupList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllClassUnitGroups() throws Exception {
        // Initialize the database
        classUnitGroupRepository.saveAndFlush(classUnitGroup);

        // Get all the classUnitGroupList
        restClassUnitGroupMockMvc.perform(get("/api/class-unit-groups?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(classUnitGroup.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getClassUnitGroup() throws Exception {
        // Initialize the database
        classUnitGroupRepository.saveAndFlush(classUnitGroup);

        // Get the classUnitGroup
        restClassUnitGroupMockMvc.perform(get("/api/class-unit-groups/{id}", classUnitGroup.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(classUnitGroup.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getNonExistingClassUnitGroup() throws Exception {
        // Get the classUnitGroup
        restClassUnitGroupMockMvc.perform(get("/api/class-unit-groups/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void updateClassUnitGroup() throws Exception {
        // Initialize the database
        classUnitGroupRepository.saveAndFlush(classUnitGroup);
        int databaseSizeBeforeUpdate = classUnitGroupRepository.findAll().size();

        // Update the classUnitGroup
        ClassUnitGroup updatedClassUnitGroup = classUnitGroupRepository.findOne(classUnitGroup.getId());
        // Disconnect from session so that the updates on updatedClassUnitGroup are not directly saved in db
        em.detach(updatedClassUnitGroup);
        updatedClassUnitGroup
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION);

        restClassUnitGroupMockMvc.perform(put("/api/class-unit-groups")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(classUnitGroupMapper.toDto(updatedClassUnitGroup))))
            .andExpect(status().isOk());

        // Validate the ClassUnitGroup in the database
        List<ClassUnitGroup> classUnitGroupList = classUnitGroupRepository.findAll();
        assertThat(classUnitGroupList).hasSize(databaseSizeBeforeUpdate);
        ClassUnitGroup testClassUnitGroup = classUnitGroupList.get(classUnitGroupList.size() - 1);
        assertThat(testClassUnitGroup.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testClassUnitGroup.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void updateNonExistingClassUnitGroup() throws Exception {
        int databaseSizeBeforeUpdate = classUnitGroupRepository.findAll().size();

        // Create the ClassUnitGroup

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restClassUnitGroupMockMvc.perform(put("/api/class-unit-groups")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(classUnitGroupMapper.toDto(classUnitGroup))))
            .andExpect(status().isCreated());

        // Validate the ClassUnitGroup in the database
        List<ClassUnitGroup> classUnitGroupList = classUnitGroupRepository.findAll();
        assertThat(classUnitGroupList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void deleteClassUnitGroup() throws Exception {
        // Initialize the database
        classUnitGroupRepository.saveAndFlush(classUnitGroup);
        int databaseSizeBeforeDelete = classUnitGroupRepository.findAll().size();

        // Get the classUnitGroup
        restClassUnitGroupMockMvc.perform(delete("/api/class-unit-groups/{id}", classUnitGroup.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<ClassUnitGroup> classUnitGroupList = classUnitGroupRepository.findAll();
        assertThat(classUnitGroupList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClassUnitGroup.class);
        ClassUnitGroup classUnitGroup1 = new ClassUnitGroup();
        classUnitGroup1.setId(1L);
        ClassUnitGroup classUnitGroup2 = new ClassUnitGroup();
        classUnitGroup2.setId(classUnitGroup1.getId());
        assertThat(classUnitGroup1).isEqualTo(classUnitGroup2);
        classUnitGroup2.setId(2L);
        assertThat(classUnitGroup1).isNotEqualTo(classUnitGroup2);
        classUnitGroup1.setId(null);
        assertThat(classUnitGroup1).isNotEqualTo(classUnitGroup2);
    }
}
