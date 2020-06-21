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
import pl.edu.agh.geotime.domain.*;
import pl.edu.agh.geotime.repository.DepartmentRepository;
import pl.edu.agh.geotime.repository.ScheduleUnitRepository;
import pl.edu.agh.geotime.service.ScheduleUnitService;
import pl.edu.agh.geotime.web.rest.dto.ScheduleUnitDTO;
import pl.edu.agh.geotime.web.rest.mapper.ScheduleUnitMapper;
import pl.edu.agh.geotime.service.query.ScheduleUnitQueryService;
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

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GeoTimeApp.class)
public class ScheduleUnitResourceIntTest {

    private static final ZonedDateTime DEFAULT_START_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_START_DATE = ZonedDateTime.now(ZoneId.systemDefault()).plusYears(99999).withNano(0);

    private static final ZonedDateTime DEFAULT_END_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_END_DATE = ZonedDateTime.now(ZoneId.systemDefault()).plusYears(99999).withNano(0);

    private static final String TEST_USER_LOGIN = "system";

    @Autowired
    private ScheduleUnitRepository scheduleUnitRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private ScheduleUnitMapper scheduleUnitMapper;

    @Autowired
    private ScheduleUnitService scheduleUnitService;

    @Autowired
    private ScheduleUnitQueryService scheduleUnitQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restScheduleUnitMockMvc;

    private ScheduleUnit scheduleUnit;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ScheduleUnitResource scheduleUnitResource = new ScheduleUnitResource(scheduleUnitService, scheduleUnitMapper,
            scheduleUnitQueryService);
        this.restScheduleUnitMockMvc = MockMvcBuilders.standaloneSetup(scheduleUnitResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ScheduleUnit createEntity(EntityManager em) {
        return new ScheduleUnit()
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE);
    }

    @Before
    public void initTest() {
        scheduleUnit = createEntity(em);

        Location location = LocationResourceIntTest.createEntity(em);
        Department department = departmentRepository.findOne(1L);
        location.setDepartment(department);
        em.persist(location);
        em.flush();

        Room room = RoomResourceIntTest.createEntity(em);
        room.setLocation(location);
        em.persist(room);
        em.flush();
        scheduleUnit.setRoom(room);

        StudyField studyField = StudyFieldResourceIntTest.createEntity(em);
        studyField.setDepartment(department);
        em.persist(studyField);
        em.flush();

        AcademicUnit academicUnit = AcademicUnitResourceIntTest.createEntity(em);
        academicUnit.setStudyField(studyField);
        em.persist(academicUnit);
        em.flush();

        Subdepartment subdepartment = SubdepartmentResourceIntTest.createEntity(em);
        subdepartment.setDepartment(department);
        em.persist(subdepartment);
        em.flush();

        ClassUnit classUnit = ClassUnitResourceIntTest.createEntity(em);
        classUnit.setAcademicUnit(academicUnit);
        classUnit.setSubdepartment(subdepartment);
        em.persist(classUnit);
        em.flush();
        scheduleUnit.setClassUnit(classUnit);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void createScheduleUnit() throws Exception {
        int databaseSizeBeforeCreate = scheduleUnitRepository.findAll().size();

        // Create the ScheduleUnit
        restScheduleUnitMockMvc.perform(post("/api/schedule-units")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scheduleUnitMapper.toDto(scheduleUnit))))
            .andExpect(status().isCreated());

        // Validate the ScheduleUnit in the database
        List<ScheduleUnit> scheduleUnitList = scheduleUnitRepository.findAll();
        assertThat(scheduleUnitList).hasSize(databaseSizeBeforeCreate + 1);
        ScheduleUnit testScheduleUnit = scheduleUnitList.get(scheduleUnitList.size() - 1);
        assertThat(testScheduleUnit.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testScheduleUnit.getEndDate()).isEqualTo(DEFAULT_END_DATE);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void createScheduleUnitWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = scheduleUnitRepository.findAll().size();

        // Create the ScheduleUnit with an existing ID
        scheduleUnit.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restScheduleUnitMockMvc.perform(post("/api/schedule-units")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scheduleUnitMapper.toDto(scheduleUnit))))
            .andExpect(status().isBadRequest());

        // Validate the ScheduleUnit in the database
        List<ScheduleUnit> scheduleUnitList = scheduleUnitRepository.findAll();
        assertThat(scheduleUnitList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void checkStartDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = scheduleUnitRepository.findAll().size();
        // set the field null
        scheduleUnit.setStartDate(null);

        // Create the ScheduleUnit, which fails.

        restScheduleUnitMockMvc.perform(post("/api/schedule-units")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scheduleUnitMapper.toDto(scheduleUnit))))
            .andExpect(status().isBadRequest());

        List<ScheduleUnit> scheduleUnitList = scheduleUnitRepository.findAll();
        assertThat(scheduleUnitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void checkEndDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = scheduleUnitRepository.findAll().size();
        // set the field null
        scheduleUnit.setEndDate(null);

        // Create the ScheduleUnit, which fails.

        restScheduleUnitMockMvc.perform(post("/api/schedule-units")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scheduleUnitMapper.toDto(scheduleUnit))))
            .andExpect(status().isBadRequest());

        List<ScheduleUnit> scheduleUnitList = scheduleUnitRepository.findAll();
        assertThat(scheduleUnitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllScheduleUnits() throws Exception {
        // Initialize the database
        scheduleUnitRepository.saveAndFlush(scheduleUnit);

        // Get all the scheduleUnitList
        restScheduleUnitMockMvc.perform(get("/api/schedule-units?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scheduleUnit.getId().intValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(sameInstant(DEFAULT_START_DATE))))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(sameInstant(DEFAULT_END_DATE))));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getScheduleUnit() throws Exception {
        // Initialize the database
        scheduleUnitRepository.saveAndFlush(scheduleUnit);

        // Get the scheduleUnit
        restScheduleUnitMockMvc.perform(get("/api/schedule-units/{id}", scheduleUnit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(scheduleUnit.getId().intValue()))
            .andExpect(jsonPath("$.startDate").value(sameInstant(DEFAULT_START_DATE)))
            .andExpect(jsonPath("$.endDate").value(sameInstant(DEFAULT_END_DATE)));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllScheduleUnitsByStartDateIsEqualToSomething() throws Exception {
        // Initialize the database
        scheduleUnitRepository.saveAndFlush(scheduleUnit);

        // Get all the scheduleUnitList where startDate equals to DEFAULT_START_DATE
        defaultScheduleUnitShouldBeFound("startDate.equals=" + DEFAULT_START_DATE);

        // Get all the scheduleUnitList where startDate equals to UPDATED_START_DATE
        defaultScheduleUnitShouldNotBeFound("startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllScheduleUnitsByStartDateIsInShouldWork() throws Exception {
        // Initialize the database
        scheduleUnitRepository.saveAndFlush(scheduleUnit);

        // Get all the scheduleUnitList where startDate in DEFAULT_START_DATE or UPDATED_START_DATE
        defaultScheduleUnitShouldBeFound("startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE);

        // Get all the scheduleUnitList where startDate equals to UPDATED_START_DATE
        defaultScheduleUnitShouldNotBeFound("startDate.in=" + UPDATED_START_DATE);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllScheduleUnitsByStartDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        scheduleUnitRepository.saveAndFlush(scheduleUnit);

        // Get all the scheduleUnitList where startDate is not null
        defaultScheduleUnitShouldBeFound("startDate.specified=true");

        // Get all the scheduleUnitList where startDate is null
        defaultScheduleUnitShouldNotBeFound("startDate.specified=false");
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllScheduleUnitsByStartDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        scheduleUnitRepository.saveAndFlush(scheduleUnit);

        // Get all the scheduleUnitList where startDate greater than or equals to DEFAULT_START_DATE
        defaultScheduleUnitShouldBeFound("startDate.greaterOrEqualThan=" + DEFAULT_START_DATE);

        // Get all the scheduleUnitList where startDate greater than or equals to UPDATED_START_DATE
        defaultScheduleUnitShouldNotBeFound("startDate.greaterOrEqualThan=" + UPDATED_START_DATE);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllScheduleUnitsByStartDateIsLessThanSomething() throws Exception {
        // Initialize the database
        scheduleUnitRepository.saveAndFlush(scheduleUnit);

        // Get all the scheduleUnitList where startDate less than or equals to DEFAULT_START_DATE
        defaultScheduleUnitShouldNotBeFound("startDate.lessThan=" + DEFAULT_START_DATE);

        // Get all the scheduleUnitList where startDate less than or equals to UPDATED_START_DATE
        defaultScheduleUnitShouldBeFound("startDate.lessThan=" + UPDATED_START_DATE);
    }


    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllScheduleUnitsByEndDateIsEqualToSomething() throws Exception {
        // Initialize the database
        scheduleUnitRepository.saveAndFlush(scheduleUnit);

        // Get all the scheduleUnitList where endDate equals to DEFAULT_END_DATE
        defaultScheduleUnitShouldBeFound("endDate.equals=" + DEFAULT_END_DATE);

        // Get all the scheduleUnitList where endDate equals to UPDATED_END_DATE
        defaultScheduleUnitShouldNotBeFound("endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllScheduleUnitsByEndDateIsInShouldWork() throws Exception {
        // Initialize the database
        scheduleUnitRepository.saveAndFlush(scheduleUnit);

        // Get all the scheduleUnitList where endDate in DEFAULT_END_DATE or UPDATED_END_DATE
        defaultScheduleUnitShouldBeFound("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE);

        // Get all the scheduleUnitList where endDate equals to UPDATED_END_DATE
        defaultScheduleUnitShouldNotBeFound("endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllScheduleUnitsByEndDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        scheduleUnitRepository.saveAndFlush(scheduleUnit);

        // Get all the scheduleUnitList where endDate is not null
        defaultScheduleUnitShouldBeFound("endDate.specified=true");

        // Get all the scheduleUnitList where endDate is null
        defaultScheduleUnitShouldNotBeFound("endDate.specified=false");
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllScheduleUnitsByEndDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        scheduleUnitRepository.saveAndFlush(scheduleUnit);

        // Get all the scheduleUnitList where endDate greater than or equals to DEFAULT_END_DATE
        defaultScheduleUnitShouldBeFound("endDate.greaterOrEqualThan=" + DEFAULT_END_DATE);

        // Get all the scheduleUnitList where endDate greater than or equals to UPDATED_END_DATE
        defaultScheduleUnitShouldNotBeFound("endDate.greaterOrEqualThan=" + UPDATED_END_DATE);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllScheduleUnitsByEndDateIsLessThanSomething() throws Exception {
        // Initialize the database
        scheduleUnitRepository.saveAndFlush(scheduleUnit);

        // Get all the scheduleUnitList where endDate less than or equals to DEFAULT_END_DATE
        defaultScheduleUnitShouldNotBeFound("endDate.lessThan=" + DEFAULT_END_DATE);

        // Get all the scheduleUnitList where endDate less than or equals to UPDATED_END_DATE
        defaultScheduleUnitShouldBeFound("endDate.lessThan=" + UPDATED_END_DATE);
    }


    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllScheduleUnitsByClassUnitIsEqualToSomething() throws Exception {
        // Initialize the database
        scheduleUnitRepository.saveAndFlush(scheduleUnit);
        Long classUnitId = scheduleUnit.getClassUnit().getId();

        // Get all the scheduleUnitList where classUnit equals to classUnitId
        defaultScheduleUnitShouldBeFound("classUnitId.equals=" + classUnitId);

        // Get all the scheduleUnitList where classUnit equals to classUnitId + 1
        defaultScheduleUnitShouldNotBeFound("classUnitId.equals=" + (classUnitId + 1));
    }


    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllScheduleUnitsByRoomIsEqualToSomething() throws Exception {
        // Initialize the database
        scheduleUnitRepository.saveAndFlush(scheduleUnit);
        Long roomId = scheduleUnit.getRoom().getId();

        // Get all the scheduleUnitList where room equals to roomId
        defaultScheduleUnitShouldBeFound("roomId.equals=" + roomId);

        // Get all the scheduleUnitList where room equals to roomId + 1
        defaultScheduleUnitShouldNotBeFound("roomId.equals=" + (roomId + 99999));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultScheduleUnitShouldBeFound(String filter) throws Exception {
        restScheduleUnitMockMvc.perform(get("/api/schedule-units?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scheduleUnit.getId().intValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(sameInstant(DEFAULT_START_DATE))))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(sameInstant(DEFAULT_END_DATE))));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultScheduleUnitShouldNotBeFound(String filter) throws Exception {
        restScheduleUnitMockMvc.perform(get("/api/schedule-units?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getNonExistingScheduleUnit() throws Exception {
        // Get the scheduleUnit
        restScheduleUnitMockMvc.perform(get("/api/schedule-units/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void updateScheduleUnit() throws Exception {
        // Initialize the database
        scheduleUnitRepository.saveAndFlush(scheduleUnit);

        int databaseSizeBeforeUpdate = scheduleUnitRepository.findAll().size();

        // Update the scheduleUnit
        ScheduleUnit updatedScheduleUnit = scheduleUnitRepository.findOne(scheduleUnit.getId());
        // Disconnect from session so that the updates on updatedScheduleUnit are not directly saved in db
        em.detach(updatedScheduleUnit);
        updatedScheduleUnit
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);

        restScheduleUnitMockMvc.perform(put("/api/schedule-units")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scheduleUnitMapper.toDto(updatedScheduleUnit))))
            .andExpect(status().isOk());

        // Validate the ScheduleUnit in the database
        List<ScheduleUnit> scheduleUnitList = scheduleUnitRepository.findAll();
        assertThat(scheduleUnitList).hasSize(databaseSizeBeforeUpdate);
        ScheduleUnit testScheduleUnit = scheduleUnitList.get(scheduleUnitList.size() - 1);
        assertThat(testScheduleUnit.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testScheduleUnit.getEndDate()).isEqualTo(UPDATED_END_DATE);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void updateNonExistingScheduleUnit() throws Exception {
        int databaseSizeBeforeUpdate = scheduleUnitRepository.findAll().size();

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restScheduleUnitMockMvc.perform(put("/api/schedule-units")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scheduleUnitMapper.toDto(scheduleUnit))))
            .andExpect(status().isCreated());

        // Validate the ScheduleUnit in the database
        List<ScheduleUnit> scheduleUnitList = scheduleUnitRepository.findAll();
        assertThat(scheduleUnitList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void deleteScheduleUnit() throws Exception {
        // Initialize the database
        scheduleUnitRepository.saveAndFlush(scheduleUnit);

        int databaseSizeBeforeDelete = scheduleUnitRepository.findAll().size();

        // Get the scheduleUnit
        restScheduleUnitMockMvc.perform(delete("/api/schedule-units/{id}", scheduleUnit.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<ScheduleUnit> scheduleUnitList = scheduleUnitRepository.findAll();
        assertThat(scheduleUnitList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ScheduleUnit.class);
        ScheduleUnit scheduleUnit1 = new ScheduleUnit();
        scheduleUnit1.setId(1L);
        ScheduleUnit scheduleUnit2 = new ScheduleUnit();
        scheduleUnit2.setId(scheduleUnit1.getId());
        assertThat(scheduleUnit1).isEqualTo(scheduleUnit2);
        scheduleUnit2.setId(2L);
        assertThat(scheduleUnit1).isNotEqualTo(scheduleUnit2);
        scheduleUnit1.setId(null);
        assertThat(scheduleUnit1).isNotEqualTo(scheduleUnit2);
    }
}
