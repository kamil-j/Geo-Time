package pl.edu.agh.geotime.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.GeoTimeApp;
import pl.edu.agh.geotime.domain.*;
import pl.edu.agh.geotime.repository.AcademicUnitRepository;
import pl.edu.agh.geotime.repository.DepartmentRepository;
import pl.edu.agh.geotime.repository.ScheduleUnitRepository;
import pl.edu.agh.geotime.service.ScheduleUnitInfoService;
import pl.edu.agh.geotime.service.SemesterService;
import pl.edu.agh.geotime.web.rest.errors.ExceptionTranslator;

import javax.persistence.EntityManager;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.edu.agh.geotime.web.rest.TestUtil.createFormattingConversionService;
import static pl.edu.agh.geotime.web.rest.TestUtil.sameInstant;

/**
 * Test class for the ScheduleInfo REST controller.
 *
 * @see ScheduleInfoResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GeoTimeApp.class)
public class ScheduleInfoResourceIntTest {

    @Autowired
    private ScheduleUnitRepository scheduleUnitRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private AcademicUnitRepository academicUnitRepository;

    @Autowired
    private SemesterService semesterService;

    @Autowired
    private ScheduleUnitInfoService scheduleUnitInfoService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restScheduleInfoMockMvc;

    private ScheduleUnit scheduleUnit;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ScheduleInfoResource scheduleInfoResource = new ScheduleInfoResource(scheduleUnitInfoService, academicUnitRepository);
        this.restScheduleInfoMockMvc = MockMvcBuilders.standaloneSetup(scheduleInfoResource)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        this.scheduleUnit = ScheduleUnitResourceIntTest.createEntity(em);

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

        ClassUnit classUnit = ClassUnitResourceIntTest.createEntity(em);
        classUnit.setAcademicUnit(academicUnit);
        classUnit.setSemester(semesterService.getCurrentSemester().orElse(SemesterResourceIntTest.createEntity(em)));
        em.persist(classUnit);
        em.flush();
        scheduleUnit.setClassUnit(classUnit);
    }

    @Test
    @Transactional
    public void getScheduleInfo() throws Exception {
        // Initialize the database
        scheduleUnitRepository.saveAndFlush(scheduleUnit);

        User user = scheduleUnit.getClassUnit().getUserExt().getUser();
        String lecturerName = user.getFirstName() + " " + user.getLastName();

        Long requestAcademicUnitId = scheduleUnit.getClassUnit().getAcademicUnit().getId();

        // Get all the scheduleInfoList
        restScheduleInfoMockMvc.perform(get("/api/schedule-info/" + requestAcademicUnitId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scheduleUnit.getClassUnit().getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(scheduleUnit.getClassUnit().getTitle())))
            .andExpect(jsonPath("$.[*].start").value(hasItem(sameInstant(scheduleUnit.getStartDate()))))
            .andExpect(jsonPath("$.[*].end").value(hasItem(sameInstant(scheduleUnit.getEndDate()))))
            .andExpect(jsonPath("$.[*].type").value(hasItem(scheduleUnit.getClassUnit().getClassType().getName())))
            .andExpect(jsonPath("$.[*].roomName").value(hasItem(scheduleUnit.getRoom().getName())))
            .andExpect(jsonPath("$.[*].lecturerName").value(hasItem(lecturerName)));
    }
}
