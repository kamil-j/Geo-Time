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
import pl.edu.agh.geotime.domain.SchedulingTimeFrame;
import pl.edu.agh.geotime.domain.Semester;
import pl.edu.agh.geotime.domain.Subdepartment;
import pl.edu.agh.geotime.domain.UserGroup;
import pl.edu.agh.geotime.repository.SchedulingTimeFrameRepository;
import pl.edu.agh.geotime.repository.SubdepartmentRepository;
import pl.edu.agh.geotime.repository.UserGroupRepository;
import pl.edu.agh.geotime.service.SchedulingTimeFrameInfoService;
import pl.edu.agh.geotime.service.SchedulingTimeFrameService;
import pl.edu.agh.geotime.service.SemesterService;
import pl.edu.agh.geotime.web.rest.mapper.SchedulingTimeFrameMapper;
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
 * Test class for the SchedulingTimeFrameResource REST controller.
 *
 * @see SchedulingTimeFrameResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GeoTimeApp.class)
public class SchedulingTimeFrameResourceIntTest {

    private static final ZonedDateTime DEFAULT_START_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_START_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_END_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_END_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String TEST_USER_LOGIN = "system";

    @Autowired
    private SubdepartmentRepository subdepartmentRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private SchedulingTimeFrameService schedulingTimeFrameService;

    @Autowired
    private SchedulingTimeFrameMapper schedulingTimeFrameMapper;

    @Autowired
    private SchedulingTimeFrameRepository schedulingTimeFrameRepository;

    @Autowired
    private SchedulingTimeFrameInfoService schedulingTimeFrameInfoService;

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

    private MockMvc restSchedulingTimeFrameMockMvc;

    private SchedulingTimeFrame schedulingTimeFrame;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SchedulingTimeFrameResource schedulingTimeFrameResource = new SchedulingTimeFrameResource(schedulingTimeFrameService,
            schedulingTimeFrameMapper, schedulingTimeFrameInfoService);
        this.restSchedulingTimeFrameMockMvc = MockMvcBuilders.standaloneSetup(schedulingTimeFrameResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    public static SchedulingTimeFrame createEntity(EntityManager em) {
        SchedulingTimeFrame schedulingTimeFrame = new SchedulingTimeFrame()
            .startTime(DEFAULT_START_TIME)
            .endTime(DEFAULT_END_TIME);
        // Add required entity
        UserGroup userGroup = UserGroupResourceIntTest.createEntity(em);
        em.persist(userGroup);
        em.flush();
        schedulingTimeFrame.setUserGroup(userGroup);
        // Add required entity
        Semester semester = SemesterResourceIntTest.createEntity(em);
        em.persist(semester);
        em.flush();
        schedulingTimeFrame.setSemester(semester);
        return schedulingTimeFrame;
    }

    @Before
    public void initTest() {
        schedulingTimeFrame = createEntity(em);
        initOtherEntityFields();
    }

    private void initOtherEntityFields() {
        Subdepartment subdepartment = subdepartmentRepository.findOne(1L);

        schedulingTimeFrame.setSubdepartment(subdepartment);
        schedulingTimeFrame.setSemester(semesterService.getCurrentSemester().orElse(null));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void createSchedulingTimeFrame() throws Exception {
        int databaseSizeBeforeCreate = schedulingTimeFrameRepository.findAll().size();

        // Create the SchedulingTimeFrame
        restSchedulingTimeFrameMockMvc.perform(post("/api/scheduling-time-frames")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(schedulingTimeFrameMapper.toDto(schedulingTimeFrame))))
            .andExpect(status().isCreated());

        // Validate the SchedulingTimeFrame in the database
        List<SchedulingTimeFrame> schedulingTimeFrameList = schedulingTimeFrameRepository.findAll();
        assertThat(schedulingTimeFrameList).hasSize(databaseSizeBeforeCreate + 1);
        SchedulingTimeFrame testSchedulingTimeFrame = schedulingTimeFrameList.get(schedulingTimeFrameList.size() - 1);
        assertThat(testSchedulingTimeFrame.getStartTime()).isEqualTo(DEFAULT_START_TIME);
        assertThat(testSchedulingTimeFrame.getEndTime()).isEqualTo(DEFAULT_END_TIME);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void createSchedulingTimeFrameWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = schedulingTimeFrameRepository.findAll().size();

        // Create the SchedulingTimeFrame with an existing ID
        schedulingTimeFrame.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSchedulingTimeFrameMockMvc.perform(post("/api/scheduling-time-frames")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(schedulingTimeFrameMapper.toDto(schedulingTimeFrame))))
            .andExpect(status().isBadRequest());

        // Validate the SchedulingTimeFrame in the database
        List<SchedulingTimeFrame> schedulingTimeFrameList = schedulingTimeFrameRepository.findAll();
        assertThat(schedulingTimeFrameList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void checkStartTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = schedulingTimeFrameRepository.findAll().size();
        // set the field null
        schedulingTimeFrame.setStartTime(null);

        // Create the SchedulingTimeFrame, which fails.

        restSchedulingTimeFrameMockMvc.perform(post("/api/scheduling-time-frames")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(schedulingTimeFrameMapper.toDto(schedulingTimeFrame))))
            .andExpect(status().isBadRequest());

        List<SchedulingTimeFrame> schedulingTimeFrameList = schedulingTimeFrameRepository.findAll();
        assertThat(schedulingTimeFrameList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void checkEndTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = schedulingTimeFrameRepository.findAll().size();
        // set the field null
        schedulingTimeFrame.setEndTime(null);

        // Create the SchedulingTimeFrame, which fails.

        restSchedulingTimeFrameMockMvc.perform(post("/api/scheduling-time-frames")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(schedulingTimeFrameMapper.toDto(schedulingTimeFrame))))
            .andExpect(status().isBadRequest());

        List<SchedulingTimeFrame> schedulingTimeFrameList = schedulingTimeFrameRepository.findAll();
        assertThat(schedulingTimeFrameList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllSchedulingTimeFrames() throws Exception {
        // Initialize the database
        schedulingTimeFrameRepository.saveAndFlush(schedulingTimeFrame);

        // Get all the schedulingTimeFrameList
        restSchedulingTimeFrameMockMvc.perform(get("/api/scheduling-time-frames?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(schedulingTimeFrame.getId().intValue())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(sameInstant(DEFAULT_START_TIME))))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(sameInstant(DEFAULT_END_TIME))));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getSchedulingTimeFrame() throws Exception {
        // Initialize the database
        schedulingTimeFrameRepository.saveAndFlush(schedulingTimeFrame);

        // Get the schedulingTimeFrame
        restSchedulingTimeFrameMockMvc.perform(get("/api/scheduling-time-frames/{id}", schedulingTimeFrame.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(schedulingTimeFrame.getId().intValue()))
            .andExpect(jsonPath("$.startTime").value(sameInstant(DEFAULT_START_TIME)))
            .andExpect(jsonPath("$.endTime").value(sameInstant(DEFAULT_END_TIME)));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getCurrentUserSchedulingTimeFrame() throws Exception {
        // Initialize the database
        UserGroup userGroup = userGroupRepository.findOne(1L);
        schedulingTimeFrame.setUserGroup(userGroup);
        schedulingTimeFrameRepository.saveAndFlush(schedulingTimeFrame);

        // Get the schedulingTimeFrame
        restSchedulingTimeFrameMockMvc.perform(get("/api/scheduling-time-frames/user/{userId}", 1))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(schedulingTimeFrame.getId().intValue()))
            .andExpect(jsonPath("$.startTime").value(sameInstant(DEFAULT_START_TIME)))
            .andExpect(jsonPath("$.endTime").value(sameInstant(DEFAULT_END_TIME)));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getNonExistingSchedulingTimeFrame() throws Exception {
        // Get the schedulingTimeFrame
        restSchedulingTimeFrameMockMvc.perform(get("/api/scheduling-time-frames/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void updateSchedulingTimeFrame() throws Exception {
        // Initialize the database
        schedulingTimeFrameRepository.saveAndFlush(schedulingTimeFrame);
        int databaseSizeBeforeUpdate = schedulingTimeFrameRepository.findAll().size();

        // Update the schedulingTimeFrame
        SchedulingTimeFrame updatedSchedulingTimeFrame = schedulingTimeFrameRepository.findOne(schedulingTimeFrame.getId());
        // Disconnect from session so that the updates on updatedSchedulingTimeFrame are not directly saved in db
        em.detach(updatedSchedulingTimeFrame);
        updatedSchedulingTimeFrame
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME);

        restSchedulingTimeFrameMockMvc.perform(put("/api/scheduling-time-frames")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(schedulingTimeFrameMapper.toDto(updatedSchedulingTimeFrame))))
            .andExpect(status().isOk());

        // Validate the SchedulingTimeFrame in the database
        List<SchedulingTimeFrame> schedulingTimeFrameList = schedulingTimeFrameRepository.findAll();
        assertThat(schedulingTimeFrameList).hasSize(databaseSizeBeforeUpdate);
        SchedulingTimeFrame testSchedulingTimeFrame = schedulingTimeFrameList.get(schedulingTimeFrameList.size() - 1);
        assertThat(testSchedulingTimeFrame.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testSchedulingTimeFrame.getEndTime()).isEqualTo(UPDATED_END_TIME);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void updateNonExistingSchedulingTimeFrame() throws Exception {
        int databaseSizeBeforeUpdate = schedulingTimeFrameRepository.findAll().size();

        // Create the SchedulingTimeFrame

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restSchedulingTimeFrameMockMvc.perform(put("/api/scheduling-time-frames")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(schedulingTimeFrameMapper.toDto(schedulingTimeFrame))))
            .andExpect(status().isCreated());

        // Validate the SchedulingTimeFrame in the database
        List<SchedulingTimeFrame> schedulingTimeFrameList = schedulingTimeFrameRepository.findAll();
        assertThat(schedulingTimeFrameList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void deleteSchedulingTimeFrame() throws Exception {
        // Initialize the database
        schedulingTimeFrameRepository.saveAndFlush(schedulingTimeFrame);
        int databaseSizeBeforeDelete = schedulingTimeFrameRepository.findAll().size();

        // Get the schedulingTimeFrame
        restSchedulingTimeFrameMockMvc.perform(delete("/api/scheduling-time-frames/{id}", schedulingTimeFrame.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<SchedulingTimeFrame> schedulingTimeFrameList = schedulingTimeFrameRepository.findAll();
        assertThat(schedulingTimeFrameList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SchedulingTimeFrame.class);
        SchedulingTimeFrame schedulingTimeFrame1 = new SchedulingTimeFrame();
        schedulingTimeFrame1.setId(1L);
        SchedulingTimeFrame schedulingTimeFrame2 = new SchedulingTimeFrame();
        schedulingTimeFrame2.setId(schedulingTimeFrame1.getId());
        assertThat(schedulingTimeFrame1).isEqualTo(schedulingTimeFrame2);
        schedulingTimeFrame2.setId(2L);
        assertThat(schedulingTimeFrame1).isNotEqualTo(schedulingTimeFrame2);
        schedulingTimeFrame1.setId(null);
        assertThat(schedulingTimeFrame1).isNotEqualTo(schedulingTimeFrame2);
    }
}
