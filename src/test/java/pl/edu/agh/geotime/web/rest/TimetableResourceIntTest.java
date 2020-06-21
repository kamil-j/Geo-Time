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
import pl.edu.agh.geotime.repository.ClassUnitRepository;
import pl.edu.agh.geotime.repository.DepartmentRepository;
import pl.edu.agh.geotime.repository.UserExtRepository;
import pl.edu.agh.geotime.service.ClassUnitService;
import pl.edu.agh.geotime.service.SemesterService;
import pl.edu.agh.geotime.service.helper.UserHelper;
import pl.edu.agh.geotime.web.rest.errors.ExceptionTranslator;
import pl.edu.agh.geotime.web.rest.mapper.ClassUnitExtMapper;

import javax.persistence.EntityManager;
import java.util.Collections;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.edu.agh.geotime.web.rest.TestUtil.createFormattingConversionService;

/**
 * Test class for the Timetable REST controller.
 *
 * @see TimetableResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GeoTimeApp.class)
public class TimetableResourceIntTest {

    private static final String TEST_USER_LOGIN = "system";

    @Autowired
    private UserHelper userHelper;

    @Autowired
    private UserExtRepository userExtRepository;

    @Autowired
    private ClassUnitRepository classUnitRepository;

    @Autowired
    private ClassUnitService classUnitService;

    @Autowired
    private ClassUnitExtMapper classUnitExtMapper;

    @Autowired
    private DepartmentRepository departmentRepository;

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

    private MockMvc restTimetableMockMvc;

    private ClassUnit classUnit;

    private ScheduleUnit scheduleUnit;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TimetableResource timetableResource = new TimetableResource(classUnitService, classUnitExtMapper,
            semesterService, userHelper);
        this.restTimetableMockMvc = MockMvcBuilders.standaloneSetup(timetableResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        classUnit = ClassUnitResourceIntTest.createEntity(em);
        scheduleUnit = ScheduleUnitResourceIntTest.createEntity(em);
        initOtherEntityFields();
    }

    private void initOtherEntityFields(){
        Department department = departmentRepository.findOne(1L);

        StudyField studyField = StudyFieldResourceIntTest.createEntity(em);
        studyField.setDepartment(department);
        em.persist(studyField);
        em.flush();

        UserExt user = userExtRepository.findOne(1L);

        Location location = LocationResourceIntTest.createEntity(em);
        location.setDepartment(department);
        em.persist(location);
        em.flush();

        Room room = RoomResourceIntTest.createEntity(em);
        room.setLocation(location);
        em.persist(room);
        em.flush();

        AcademicUnit academicUnit = AcademicUnitResourceIntTest.createEntity(em);
        academicUnit.setStudyField(studyField);
        em.persist(academicUnit);
        em.flush();

        classUnit.setSemester(semesterService.getCurrentSemester().orElse(SemesterResourceIntTest.createEntity(em)));
        classUnit.setUserExt(user);
        classUnit.setRooms(Collections.singleton(room));
        classUnit.setAcademicUnit(academicUnit);

        scheduleUnit.setClassUnit(classUnit);
        scheduleUnit.setRoom(room);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllNotScheduledCurrentUserClasses() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);

        // Get all the classesList
        restTimetableMockMvc.perform(get("/api/timetable/classes?sort=id,desc")
                .param("userId", classUnit.getUserExt().getId().toString())
            ).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(classUnit.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(classUnit.getTitle())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(classUnit.getDescription())));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllNotScheduledOtherUserClasses() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);

        Long userId = classUnit.getUserExt().getId();

        // Get all the classesList
        restTimetableMockMvc.perform(get("/api/timetable/classes?userId=" + userId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(classUnit.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(classUnit.getTitle())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(classUnit.getDescription())));
    }
}
