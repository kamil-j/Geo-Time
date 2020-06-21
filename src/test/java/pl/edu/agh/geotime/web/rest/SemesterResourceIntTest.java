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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.GeoTimeApp;
import pl.edu.agh.geotime.domain.Semester;
import pl.edu.agh.geotime.repository.SemesterRepository;
import pl.edu.agh.geotime.service.SemesterService;
import pl.edu.agh.geotime.web.rest.mapper.SemesterMapper;
import pl.edu.agh.geotime.web.rest.errors.ExceptionTranslator;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.edu.agh.geotime.web.rest.TestUtil.createFormattingConversionService;
import static pl.edu.agh.geotime.web.rest.TestUtil.sameInstant;

/**
 * Test class for the SemesterResource REST controller.
 *
 * @see SemesterResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GeoTimeApp.class)
public class SemesterResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_START_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_START_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_END_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_END_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    private SemesterMapper semesterMapper;

    @Autowired
    private SemesterService semesterService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restSemesterMockMvc;

    private Semester semester;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SemesterResource semesterResource = new SemesterResource(semesterService, semesterMapper);
        this.restSemesterMockMvc = MockMvcBuilders.standaloneSetup(semesterResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    public static Semester createEntity(EntityManager em) {
        return new Semester()
            .name(DEFAULT_NAME)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .active(DEFAULT_ACTIVE);
    }

    @Before
    public void initTest() {
        semester = createEntity(em);
    }

    @Test
    @Transactional
    public void createSemester() throws Exception {
        int databaseSizeBeforeCreate = semesterRepository.findAll().size();

        // Create the Semester
        restSemesterMockMvc.perform(post("/api/semesters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(semesterMapper.toDto(semester))))
            .andExpect(status().isCreated());

        // Validate the Semester in the database
        List<Semester> semesterList = semesterRepository.findAll();
        assertThat(semesterList).hasSize(databaseSizeBeforeCreate + 1);
        Semester testSemester = semesterList.get(semesterList.size() - 1);
        assertThat(testSemester.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSemester.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testSemester.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testSemester.isActive()).isEqualTo(DEFAULT_ACTIVE);
    }

    @Test
    @Transactional
    public void createSemesterWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = semesterRepository.findAll().size();

        // Create the Semester with an existing ID
        semester.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSemesterMockMvc.perform(post("/api/semesters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(semesterMapper.toDto(semester))))
            .andExpect(status().isBadRequest());

        // Validate the Semester in the database
        List<Semester> semesterList = semesterRepository.findAll();
        assertThat(semesterList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = semesterRepository.findAll().size();
        // set the field null
        semester.setName(null);

        // Create the Semester, which fails.

        restSemesterMockMvc.perform(post("/api/semesters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(semesterMapper.toDto(semester))))
            .andExpect(status().isBadRequest());

        List<Semester> semesterList = semesterRepository.findAll();
        assertThat(semesterList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStartDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = semesterRepository.findAll().size();
        // set the field null
        semester.setStartDate(null);

        // Create the Semester, which fails.

        restSemesterMockMvc.perform(post("/api/semesters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(semesterMapper.toDto(semester))))
            .andExpect(status().isBadRequest());

        List<Semester> semesterList = semesterRepository.findAll();
        assertThat(semesterList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEndDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = semesterRepository.findAll().size();
        // set the field null
        semester.setEndDate(null);

        // Create the Semester, which fails.

        restSemesterMockMvc.perform(post("/api/semesters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(semesterMapper.toDto(semester))))
            .andExpect(status().isBadRequest());

        List<Semester> semesterList = semesterRepository.findAll();
        assertThat(semesterList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSemesters() throws Exception {
        // Initialize the database
        semesterRepository.saveAndFlush(semester);

        // Get all the semesterList
        restSemesterMockMvc.perform(get("/api/semesters?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(semester.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(sameInstant(DEFAULT_START_DATE))))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(sameInstant(DEFAULT_END_DATE))))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    @Test
    @Transactional
    public void getSemester() throws Exception {
        // Initialize the database
        semesterRepository.saveAndFlush(semester);

        // Get the semester
        restSemesterMockMvc.perform(get("/api/semesters/{id}", semester.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(semester.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.startDate").value(sameInstant(DEFAULT_START_DATE)))
            .andExpect(jsonPath("$.endDate").value(sameInstant(DEFAULT_END_DATE)))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE));
    }

    @Test
    @Transactional
    public void getNonExistingSemester() throws Exception {
        // Get the semester
        restSemesterMockMvc.perform(get("/api/semesters/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSemester() throws Exception {
        // Initialize the database
        semesterRepository.saveAndFlush(semester);
        int databaseSizeBeforeUpdate = semesterRepository.findAll().size();

        // Update the semester
        Semester updatedSemester = semesterRepository.findOne(semester.getId());
        // Disconnect from session so that the updates on updatedSemester are not directly saved in db
        em.detach(updatedSemester);
        updatedSemester
            .name(UPDATED_NAME)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .active(UPDATED_ACTIVE);

        restSemesterMockMvc.perform(put("/api/semesters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(semesterMapper.toDto(updatedSemester))))
            .andExpect(status().isOk());

        // Validate the Semester in the database
        List<Semester> semesterList = semesterRepository.findAll();
        assertThat(semesterList).hasSize(databaseSizeBeforeUpdate);
        Semester testSemester = semesterList.get(semesterList.size() - 1);
        assertThat(testSemester.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSemester.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testSemester.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testSemester.isActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    public void updateNonExistingSemester() throws Exception {
        int databaseSizeBeforeUpdate = semesterRepository.findAll().size();

        // Create the Semester

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restSemesterMockMvc.perform(put("/api/semesters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(semesterMapper.toDto(semester))))
            .andExpect(status().isCreated());

        // Validate the Semester in the database
        List<Semester> semesterList = semesterRepository.findAll();
        assertThat(semesterList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteSemester() throws Exception {
        // Initialize the database
        semesterRepository.saveAndFlush(semester);
        int databaseSizeBeforeDelete = semesterRepository.findAll().size();

        // Get the semester
        restSemesterMockMvc.perform(delete("/api/semesters/{id}", semester.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Semester> semesterList = semesterRepository.findAll();
        assertThat(semesterList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Semester.class);
        Semester semester1 = new Semester();
        semester1.setId(1L);
        Semester semester2 = new Semester();
        semester2.setId(semester1.getId());
        assertThat(semester1).isEqualTo(semester2);
        semester2.setId(2L);
        assertThat(semester1).isNotEqualTo(semester2);
        semester1.setId(null);
        assertThat(semester1).isNotEqualTo(semester2);
    }
}
