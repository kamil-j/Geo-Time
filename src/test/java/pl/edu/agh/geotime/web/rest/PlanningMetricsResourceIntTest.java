package pl.edu.agh.geotime.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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
import pl.edu.agh.geotime.domain.enumeration.AcademicUnitGroup;
import pl.edu.agh.geotime.domain.enumeration.ClassFrequency;
import pl.edu.agh.geotime.service.PlanningMetricsService;
import pl.edu.agh.geotime.service.SemesterService;
import pl.edu.agh.geotime.service.SubdepartmentService;
import pl.edu.agh.geotime.service.UserService;
import pl.edu.agh.geotime.web.rest.errors.ExceptionTranslator;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.edu.agh.geotime.web.rest.TestUtil.createFormattingConversionService;

/**
 * Test class for the PlanningMetrics REST controller.
 *
 * @see PlanningMetricsResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GeoTimeApp.class)
public class PlanningMetricsResourceIntTest {

    @Mock
    private UserService userService;

    @Autowired
    private SemesterService semesterService;

    @Autowired
    private SubdepartmentService subdepartmentService;

    @Autowired
    private PlanningMetricsService planningMetricsService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restPlanningMetricsMockMvc;

    private UserExt testUser;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PlanningMetricsResource planningMetricsResource = new PlanningMetricsResource(planningMetricsService,
            subdepartmentService, userService);
        this.restPlanningMetricsMockMvc = MockMvcBuilders.standaloneSetup(planningMetricsResource)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }


    @Before
    public void initTest() {
        testUser = UserResourceIntTest.createEntity(em);
        em.persist(testUser);
        em.flush();
    }

    @Test
    @Transactional
    public void getDepartmentPlanningMetrics() throws Exception {
        //Initialize Database
        List<ClassUnit> classUnitsForTest = createClassUnitsForTest();

        when(userService.getUserWithAuthorities()).thenReturn(Optional.of(testUser));

        String departmentShortName = classUnitsForTest.get(0).getAcademicUnit().getStudyField().getDepartment().getShortName();
        String subdepartmentShortName = classUnitsForTest.get(0).getUserExt().getSubdepartment().getShortName();

        int classQuantity = classUnitsForTest.size();
        int scheduledClassQuantity = (int) classUnitsForTest.stream()
            .filter(classUnit1 -> !classUnit1.getScheduleUnits().isEmpty())
            .count();
        
        // Get all the departmentsInfoList
        restPlanningMetricsMockMvc.perform(get("/api/planning-metrics/department"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.departmentShortName").value(departmentShortName))
            .andExpect(jsonPath("$.classQuantity").value(classQuantity))
            .andExpect(jsonPath("$.bookedClassQuantity").value(scheduledClassQuantity))
            .andExpect(jsonPath("$.subdepartmentsPlanningMetrics.[*].subdepartmentShortName").value(hasItem(subdepartmentShortName)))
            .andExpect(jsonPath("$.subdepartmentsPlanningMetrics.[*].classQuantity").value(hasItem(classQuantity)))
            .andExpect(jsonPath("$.subdepartmentsPlanningMetrics.[*].bookedClassQuantity").value(hasItem(scheduledClassQuantity)));
    }

    @Test
    @Transactional
    public void getSubdepartmentPlanningMetrics() throws Exception {
        //Initialize Database
        List<ClassUnit> classUnitsForTest = createClassUnitsForTest();

        when(userService.getUserWithAuthorities()).thenReturn(Optional.of(testUser));

        String subdepartmentShortName = classUnitsForTest.get(0).getUserExt().getSubdepartment().getShortName();

        int classQuantity = classUnitsForTest.size();
        int scheduledClassQuantity = (int) classUnitsForTest.stream()
            .filter(classUnit1 -> !classUnit1.getScheduleUnits().isEmpty())
            .count();
        int userClassQuantity = (int) classUnitsForTest.stream()
            .filter(classUnit1 -> classUnit1.getUserExt().equals(testUser))
            .count();
        int userScheduledClassQuantity = (int) classUnitsForTest.stream()
            .filter(classUnit1 -> classUnit1.getUserExt().equals(testUser))
            .filter(classUnit1 -> !classUnit1.getScheduleUnits().isEmpty())
            .count();

        // Get all the departmentsInfoList
        restPlanningMetricsMockMvc.perform(get("/api/planning-metrics/subdepartment"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.subdepartmentShortName").value(subdepartmentShortName))
            .andExpect(jsonPath("$.classQuantity").value(classQuantity))
            .andExpect(jsonPath("$.bookedClassQuantity").value(scheduledClassQuantity))
            .andExpect(jsonPath("$.usersPlanningMetrics.[*].userName").value(hasItem(testUser.getUser().getLogin())))
            .andExpect(jsonPath("$.usersPlanningMetrics.[*].classQuantity").value(hasItem(userClassQuantity)))
            .andExpect(jsonPath("$.usersPlanningMetrics.[*].bookedClassQuantity").value(hasItem(userScheduledClassQuantity)));
    }

    private List<ClassUnit> createClassUnitsForTest() {
        Semester semester = getTestSemester();
        ClassType classType = getTestClassType();
        AcademicUnit academicUnit = getTestAcademicUnit();

        List<ClassUnit> results = new ArrayList<>();
        for(int i = 0; i < 10; i++) {

            ClassUnit classUnit = new ClassUnit()
                .title("Title123")
                .description("Description123")
                .duration(120)
                .hoursQuantity(70)
                .frequency(ClassFrequency.EVERY_TWO_WEEKS)
                .academicUnitGroup(AcademicUnitGroup.GROUP2)
                .onlySemesterHalf(false);

            classUnit.setAcademicUnit(academicUnit);
            classUnit.setClassType(classType);
            classUnit.setSemester(semester);
            classUnit.setUserExt(testUser);
            classUnit.setSubdepartment(testUser.getSubdepartment());

            em.persist(classUnit);
            em.flush();

            results.add(classUnit);
        }

        for(int i = 0; i < 4; i++) {
            ScheduleUnit scheduleUnit = ScheduleUnitResourceIntTest.createEntity(em);
            initOtherScheduleUnitFields(scheduleUnit);
            scheduleUnit.setClassUnit(results.get(i));

            em.persist(scheduleUnit);
            em.flush();
        }

        return results;
    }

    private Semester getTestSemester() {
        return semesterService.getCurrentSemester().orElseGet(() -> {
            Semester semesterEntity = SemesterResourceIntTest.createEntity(em);
            em.persist(semesterEntity);
            em.flush();
            return semesterEntity;
        });
    }

    private ClassType getTestClassType() {
        ClassType classType = ClassTypeResourceIntTest.createEntity(em);
        em.persist(classType);
        em.flush();
        return classType;
    }

    private AcademicUnit getTestAcademicUnit(){
        StudyField studyField = StudyFieldResourceIntTest.createEntity(em);
        studyField.setDepartment(testUser.getSubdepartment().getDepartment());
        em.persist(studyField);
        em.flush();

        AcademicUnit academicUnit = AcademicUnitResourceIntTest.createEntity(em);
        academicUnit.setStudyField(studyField);
        em.persist(academicUnit);
        em.flush();

        return academicUnit;
    }

    private void initOtherScheduleUnitFields(ScheduleUnit scheduleUnit){
        Location location = LocationResourceIntTest.createEntity(em);
        location.setDepartment(testUser.getSubdepartment().getDepartment());
        em.persist(location);
        em.flush();

        Room room = RoomResourceIntTest.createEntity(em);
        room.setLocation(location);
        em.persist(room);
        em.flush();
        scheduleUnit.setRoom(room);
    }
}
