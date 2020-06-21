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
import pl.edu.agh.geotime.domain.AcademicUnit;
import pl.edu.agh.geotime.domain.Department;
import pl.edu.agh.geotime.domain.StudyField;
import pl.edu.agh.geotime.domain.enumeration.AcademicUnitDegree;
import pl.edu.agh.geotime.domain.enumeration.AcademicUnitYear;
import pl.edu.agh.geotime.repository.AcademicUnitRepository;
import pl.edu.agh.geotime.repository.DepartmentRepository;
import pl.edu.agh.geotime.service.AcademicUnitService;
import pl.edu.agh.geotime.web.rest.dto.AcademicUnitDTO;
import pl.edu.agh.geotime.web.rest.mapper.AcademicUnitMapper;
import pl.edu.agh.geotime.web.rest.errors.ExceptionTranslator;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.edu.agh.geotime.web.rest.TestUtil.createFormattingConversionService;

/**
 * Test class for the AcademicUnitResource REST controller.
 *
 * @see AcademicUnitResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GeoTimeApp.class)
public class AcademicUnitResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final AcademicUnitYear DEFAULT_YEAR = AcademicUnitYear.YEAR1;
    private static final AcademicUnitYear UPDATED_YEAR = AcademicUnitYear.YEAR2;

    private static final AcademicUnitDegree DEFAULT_DEGREE = AcademicUnitDegree.DEGREE1;
    private static final AcademicUnitDegree UPDATED_DEGREE = AcademicUnitDegree.DEGREE2;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String TEST_USER_LOGIN = "system";

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private AcademicUnitRepository academicUnitRepository;

    @Autowired
    private AcademicUnitMapper academicUnitMapper;

    @Autowired
    private AcademicUnitService academicUnitService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restAcademicUnitMockMvc;

    private AcademicUnit academicUnit;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final AcademicUnitResource academicUnitResource = new AcademicUnitResource(academicUnitService, academicUnitMapper);
        this.restAcademicUnitMockMvc = MockMvcBuilders.standaloneSetup(academicUnitResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    public static AcademicUnit createEntity(EntityManager em) {
        return new AcademicUnit()
            .name(DEFAULT_NAME)
            .year(DEFAULT_YEAR)
            .degree(DEFAULT_DEGREE)
            .description(DEFAULT_DESCRIPTION);
    }

    @Before
    public void initTest() {
        academicUnit = createEntity(em);
        initOtherEntityFields();
    }

    private void initOtherEntityFields(){
        Department department = departmentRepository.findOne(1L);

        StudyField studyField = StudyFieldResourceIntTest.createEntity(em);
        studyField.setDepartment(department);
        em.persist(studyField);
        em.flush();
        academicUnit.setStudyField(studyField);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void createAcademicUnit() throws Exception {
        int databaseSizeBeforeCreate = academicUnitRepository.findAll().size();
        AcademicUnitDTO academicUnitDTO = academicUnitMapper.toDto(academicUnit);

        // Create the AcademicUnit
        restAcademicUnitMockMvc.perform(post("/api/academic-units")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(academicUnitDTO)))
            .andExpect(status().isCreated());

        // Validate the AcademicUnit in the database
        String academicUnitName = academicUnit.getStudyField().getShortName() + "-"
            + academicUnit.getDegree().getShortName() + "-" + academicUnit.getYear().getShortName();

        List<AcademicUnit> academicUnitList = academicUnitRepository.findAll();
        assertThat(academicUnitList).hasSize(databaseSizeBeforeCreate + 1);
        AcademicUnit testAcademicUnit = academicUnitList.get(academicUnitList.size() - 1);
        assertThat(testAcademicUnit.getName()).isEqualTo(academicUnitName);
        assertThat(testAcademicUnit.getYear()).isEqualTo(DEFAULT_YEAR);
        assertThat(testAcademicUnit.getDegree()).isEqualTo(DEFAULT_DEGREE);
        assertThat(testAcademicUnit.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void createAcademicUnitWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = academicUnitRepository.findAll().size();

        // Create the AcademicUnit with an existing ID
        academicUnit.setId(1L);

        AcademicUnitDTO academicUnitDTO = academicUnitMapper.toDto(academicUnit);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAcademicUnitMockMvc.perform(post("/api/academic-units")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(academicUnitDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AcademicUnit in the database
        List<AcademicUnit> academicUnitList = academicUnitRepository.findAll();
        assertThat(academicUnitList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void checkYearIsRequired() throws Exception {
        int databaseSizeBeforeTest = academicUnitRepository.findAll().size();
        // set the field null
        academicUnit.setYear(null);

        AcademicUnitDTO academicUnitDTO = academicUnitMapper.toDto(academicUnit);

        // Create the AcademicUnit, which fails.
        restAcademicUnitMockMvc.perform(post("/api/academic-units")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(academicUnitDTO)))
            .andExpect(status().isBadRequest());

        List<AcademicUnit> academicUnitList = academicUnitRepository.findAll();
        assertThat(academicUnitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void checkDegreeIsRequired() throws Exception {
        int databaseSizeBeforeTest = academicUnitRepository.findAll().size();
        // set the field null
        academicUnit.setDegree(null);

        AcademicUnitDTO academicUnitDTO = academicUnitMapper.toDto(academicUnit);

        // Create the AcademicUnit, which fails.
        restAcademicUnitMockMvc.perform(post("/api/academic-units")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(academicUnitDTO)))
            .andExpect(status().isBadRequest());

        List<AcademicUnit> academicUnitList = academicUnitRepository.findAll();
        assertThat(academicUnitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllAcademicUnits() throws Exception {
        // Initialize the database
        academicUnitRepository.saveAndFlush(academicUnit);

        // Get all the academicUnitList
        restAcademicUnitMockMvc.perform(get("/api/academic-units?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(academicUnit.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].year").value(hasItem(DEFAULT_YEAR.toString())))
            .andExpect(jsonPath("$.[*].degree").value(hasItem(DEFAULT_DEGREE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAcademicUnit() throws Exception {
        // Initialize the database
        academicUnitRepository.saveAndFlush(academicUnit);

        // Get the academicUnit
        restAcademicUnitMockMvc.perform(get("/api/academic-units/{id}", academicUnit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(academicUnit.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.year").value(DEFAULT_YEAR.toString()))
            .andExpect(jsonPath("$.degree").value(DEFAULT_DEGREE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getNonExistingAcademicUnit() throws Exception {
        // Get the academicUnit
        restAcademicUnitMockMvc.perform(get("/api/academic-units/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void updateAcademicUnit() throws Exception {
        // Initialize the database
        academicUnitRepository.saveAndFlush(academicUnit);
        int databaseSizeBeforeUpdate = academicUnitRepository.findAll().size();

        // Update the academicUnit
        AcademicUnit updatedAcademicUnit = academicUnitRepository.findOne(academicUnit.getId());
        // Disconnect from session so that the updates on updatedAcademicUnit are not directly saved in db
        em.detach(updatedAcademicUnit);
        updatedAcademicUnit
            .name(UPDATED_NAME)
            .year(UPDATED_YEAR)
            .degree(UPDATED_DEGREE)
            .description(UPDATED_DESCRIPTION);

        AcademicUnitDTO academicUnitDTO = academicUnitMapper.toDto(updatedAcademicUnit);

        restAcademicUnitMockMvc.perform(put("/api/academic-units")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(academicUnitDTO)))
            .andExpect(status().isOk());

        // Validate the AcademicUnit in the database
        String academicUnitName = updatedAcademicUnit.getStudyField().getShortName() + "-"
            + updatedAcademicUnit.getDegree().getShortName() + "-" + updatedAcademicUnit.getYear().getShortName();

        List<AcademicUnit> academicUnitList = academicUnitRepository.findAll();
        assertThat(academicUnitList).hasSize(databaseSizeBeforeUpdate);
        AcademicUnit testAcademicUnit = academicUnitList.get(academicUnitList.size() - 1);
        assertThat(testAcademicUnit.getName()).isEqualTo(academicUnitName);
        assertThat(testAcademicUnit.getYear()).isEqualTo(UPDATED_YEAR);
        assertThat(testAcademicUnit.getDegree()).isEqualTo(UPDATED_DEGREE);
        assertThat(testAcademicUnit.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void updateNonExistingAcademicUnit() throws Exception {
        int databaseSizeBeforeUpdate = academicUnitRepository.findAll().size();

        AcademicUnitDTO academicUnitDTO = academicUnitMapper.toDto(academicUnit);

        // Create the AcademicUnit

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restAcademicUnitMockMvc.perform(put("/api/academic-units")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(academicUnitDTO)))
            .andExpect(status().isCreated());

        // Validate the AcademicUnit in the database
        List<AcademicUnit> academicUnitList = academicUnitRepository.findAll();
        assertThat(academicUnitList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void deleteAcademicUnit() throws Exception {
        // Initialize the database
        academicUnitRepository.saveAndFlush(academicUnit);
        int databaseSizeBeforeDelete = academicUnitRepository.findAll().size();

        // Get the academicUnit
        restAcademicUnitMockMvc.perform(delete("/api/academic-units/{id}", academicUnit.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<AcademicUnit> academicUnitList = academicUnitRepository.findAll();
        assertThat(academicUnitList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AcademicUnit.class);
        AcademicUnit academicUnit1 = new AcademicUnit();
        academicUnit1.setId(1L);
        AcademicUnit academicUnit2 = new AcademicUnit();
        academicUnit2.setId(academicUnit1.getId());
        assertThat(academicUnit1).isEqualTo(academicUnit2);
        academicUnit2.setId(2L);
        assertThat(academicUnit1).isNotEqualTo(academicUnit2);
        academicUnit1.setId(null);
        assertThat(academicUnit1).isNotEqualTo(academicUnit2);
    }
}
