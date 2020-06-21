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
import pl.edu.agh.geotime.domain.enumeration.AcademicUnitDegree;
import pl.edu.agh.geotime.domain.enumeration.AcademicUnitYear;
import pl.edu.agh.geotime.repository.AcademicUnitRepository;
import pl.edu.agh.geotime.repository.DepartmentRepository;
import pl.edu.agh.geotime.service.AcademicUnitService;
import pl.edu.agh.geotime.service.query.AcademicUnitQueryService;
import pl.edu.agh.geotime.web.rest.errors.ExceptionTranslator;

import javax.persistence.EntityManager;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.edu.agh.geotime.web.rest.TestUtil.createFormattingConversionService;

/**
 * Test class for the AcademicUnitInfo REST controller.
 *
 * @see AcademicUnitInfoResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GeoTimeApp.class)
public class AcademicUnitInfoResourceIntTest {

    @Autowired
    private AcademicUnitRepository academicUnitRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private AcademicUnitService academicUnitService;

    @Autowired
    private AcademicUnitQueryService academicUnitQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restAcademicUnitInfoMockMvc;

    private AcademicUnit academicUnit;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final AcademicUnitInfoResource academicUnitInfoResource = new AcademicUnitInfoResource(academicUnitService, academicUnitQueryService);
        this.restAcademicUnitInfoMockMvc = MockMvcBuilders.standaloneSetup(academicUnitInfoResource)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        academicUnit = AcademicUnitResourceIntTest.createEntity(em);
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
    @Transactional
    public void getAllAcademicUnitsInfo() throws Exception {
        // Initialize the database
        academicUnitRepository.saveAndFlush(academicUnit);

        // Get all the departmentsInfoList
        StudyField studyField = academicUnit.getStudyField();
        Department department = studyField.getDepartment();

        restAcademicUnitInfoMockMvc.perform(get("/api/academic-units-info"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(academicUnit.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(academicUnit.getName())))
            .andExpect(jsonPath("$.[*].studyFieldId").value(hasItem(studyField.getId().intValue())))
            .andExpect(jsonPath("$.[*].studyFieldName").value(hasItem(studyField.getName())))
            .andExpect(jsonPath("$.[*].studyFieldShortName").value(hasItem(studyField.getShortName())))
            .andExpect(jsonPath("$.[*].departmentId").value(hasItem(department.getId().intValue())))
            .andExpect(jsonPath("$.[*].departmentName").value(hasItem(department.getName())))
            .andExpect(jsonPath("$.[*].departmentShortName").value(hasItem(department.getShortName())));
    }

    @Test
    @Transactional
    public void getAllAcademicUnitsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        academicUnitRepository.saveAndFlush(academicUnit);

        // Get all the academicUnitInfoList where name equals to academicUnit name
        defaultAcademicUnitShouldBeFound("name.equals=" + academicUnit.getName());

        // Get all the academicUnitInfoList where name equals to NOT_EXISTING
        defaultAcademicUnitShouldNotBeFound("name.equals=NOT_EXISTING");
    }

    @Test
    @Transactional
    public void getAllAcademicUnitsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        academicUnitRepository.saveAndFlush(academicUnit);

        // Get all the academicUnitInfoList where name in academicUnit name or NOT_EXISTING
        defaultAcademicUnitShouldBeFound("name.in=" + academicUnit.getName() + ",NOT_EXISTING");

        // Get all the academicUnitInfoList where name equals to NOT_EXISTING
        defaultAcademicUnitShouldNotBeFound("name.in=NOT_EXISTING");
    }

    @Test
    @Transactional
    public void getAllAcademicUnitsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        academicUnitRepository.saveAndFlush(academicUnit);

        // Get all the academicUnitInfoList where name is not null
        defaultAcademicUnitShouldBeFound("name.specified=true");

        // Get all the academicUnitInfoList where name is null
        defaultAcademicUnitShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllAcademicUnitsByYearIsEqualToSomething() throws Exception {
        // Initialize the database
        academicUnitRepository.saveAndFlush(academicUnit);

        // Get all the academicUnitInfoList where year equals to academicUnit year
        defaultAcademicUnitShouldBeFound("year.equals=" + academicUnit.getYear().toString());
    }

    @Test
    @Transactional
    public void getAllAcademicUnitsByYearIsInShouldWork() throws Exception {
        // Initialize the database
        academicUnitRepository.saveAndFlush(academicUnit);

        // Get all the academicUnitInfoList where year in academicUnit year or YEAR4
        defaultAcademicUnitShouldBeFound("year.in=" + academicUnit.getYear().toString() + "," + AcademicUnitYear.YEAR4);
    }

    @Test
    @Transactional
    public void getAllAcademicUnitsByYearIsNullOrNotNull() throws Exception {
        // Initialize the database
        academicUnitRepository.saveAndFlush(academicUnit);

        // Get all the academicUnitInfoList where year is not null
        defaultAcademicUnitShouldBeFound("year.specified=true");

        // Get all the academicUnitInfoList where year is null
        defaultAcademicUnitShouldNotBeFound("year.specified=false");
    }

    @Test
    @Transactional
    public void getAllAcademicUnitsByDegreeIsEqualToSomething() throws Exception {
        // Initialize the database
        academicUnitRepository.saveAndFlush(academicUnit);

        // Get all the academicUnitInfoList where degree equals to academicUnit degree
        defaultAcademicUnitShouldBeFound("degree.equals=" + academicUnit.getDegree());
    }

    @Test
    @Transactional
    public void getAllAcademicUnitsByDegreeIsInShouldWork() throws Exception {
        // Initialize the database
        academicUnitRepository.saveAndFlush(academicUnit);

        // Get all the academicUnitInfoList where degree in academicUnit degree or DEGREE2
        defaultAcademicUnitShouldBeFound("degree.in=" + academicUnit.getDegree() + "," + AcademicUnitDegree.DEGREE2);
    }

    @Test
    @Transactional
    public void getAllAcademicUnitsByDegreeIsNullOrNotNull() throws Exception {
        // Initialize the database
        academicUnitRepository.saveAndFlush(academicUnit);

        // Get all the academicUnitInfoList where degree is not null
        defaultAcademicUnitShouldBeFound("degree.specified=true");

        // Get all the academicUnitInfoList where degree is null
        defaultAcademicUnitShouldNotBeFound("degree.specified=false");
    }

    @Test
    @Transactional
    public void getAllAcademicUnitsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        academicUnitRepository.saveAndFlush(academicUnit);

        // Get all the academicUnitInfoList where description equals to academicUnit description
        defaultAcademicUnitShouldBeFound("description.equals=" + academicUnit.getDescription());

        // Get all the academicUnitInfoList where description equals to NOT_EXISTING
        defaultAcademicUnitShouldNotBeFound("description.equals=NOT_EXISTING");
    }

    @Test
    @Transactional
    public void getAllAcademicUnitsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        academicUnitRepository.saveAndFlush(academicUnit);

        // Get all the academicUnitInfoList where description in academicUnit description or NOT_EXISTING
        defaultAcademicUnitShouldBeFound("description.in=" + academicUnit.getDescription() + ",NOT_EXISTING");

        // Get all the academicUnitInfoList where description equals to NOT_EXISTING
        defaultAcademicUnitShouldNotBeFound("description.in=NOT_EXISTING");
    }

    @Test
    @Transactional
    public void getAllAcademicUnitsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        academicUnitRepository.saveAndFlush(academicUnit);

        // Get all the academicUnitInfoList where description is not null
        defaultAcademicUnitShouldBeFound("description.specified=true");
    }

    @Test
    @Transactional
    public void getAllAcademicUnitsByStudyFieldIsEqualToSomething() throws Exception {
        // Initialize the database
        academicUnitRepository.saveAndFlush(academicUnit);
        Long studyFieldId = academicUnit.getStudyField().getId();

        // Get all the academicUnitInfoList where studyField equals to studyFieldId
        defaultAcademicUnitShouldBeFound("studyFieldId.equals=" + studyFieldId);

        // Get all the academicUnitInfoList where studyField equals to studyFieldId + 99999999
        defaultAcademicUnitShouldNotBeFound("studyFieldId.equals=" + (studyFieldId + 99999999));
    }


    @Test
    @Transactional
    public void getAllAcademicUnitsByClassUnitIsEqualToSomething() throws Exception {
        // Initialize the database
        academicUnitRepository.saveAndFlush(academicUnit);
        ClassUnit classUnit = ClassUnitResourceIntTest.createEntity(em);
        classUnit.setAcademicUnit(academicUnit);
        em.persist(classUnit);
        em.flush();

        Long classUnitId = classUnit.getId();

        // Get all the academicUnitInfoList where classUnit equals to classUnitId
        defaultAcademicUnitShouldBeFound("classUnitId.equals=" + classUnitId);

        // Get all the academicUnitInfoList where classUnit equals to classUnitId + 99999999
        defaultAcademicUnitShouldNotBeFound("classUnitId.equals=" + (classUnitId + 99999999));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultAcademicUnitShouldBeFound(String filter) throws Exception {
        StudyField studyField = academicUnit.getStudyField();
        Department department = studyField.getDepartment();

        restAcademicUnitInfoMockMvc.perform(get("/api/academic-units-info?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(academicUnit.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(academicUnit.getName())))
            .andExpect(jsonPath("$.[*].studyFieldId").value(hasItem(studyField.getId().intValue())))
            .andExpect(jsonPath("$.[*].studyFieldName").value(hasItem(studyField.getName())))
            .andExpect(jsonPath("$.[*].studyFieldShortName").value(hasItem(studyField.getShortName())))
            .andExpect(jsonPath("$.[*].departmentId").value(hasItem(department.getId().intValue())))
            .andExpect(jsonPath("$.[*].departmentName").value(hasItem(department.getName())))
            .andExpect(jsonPath("$.[*].departmentShortName").value(hasItem(department.getShortName())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultAcademicUnitShouldNotBeFound(String filter) throws Exception {
        restAcademicUnitInfoMockMvc.perform(get("/api/academic-units-info?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @Transactional
    public void getAcademicUnitInfo() throws Exception {
        // Initialize the database
        academicUnitRepository.saveAndFlush(academicUnit);

        StudyField studyField = academicUnit.getStudyField();
        Department department = studyField.getDepartment();

        restAcademicUnitInfoMockMvc.perform(get("/api/academic-units-info/{id}", academicUnit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(academicUnit.getId().intValue()))
            .andExpect(jsonPath("$.name").value(academicUnit.getName()))
            .andExpect(jsonPath("$.studyFieldId").value(studyField.getId().intValue()))
            .andExpect(jsonPath("$.studyFieldName").value(studyField.getName()))
            .andExpect(jsonPath("$.studyFieldShortName").value(studyField.getShortName()))
            .andExpect(jsonPath("$.departmentId").value(department.getId().intValue()))
            .andExpect(jsonPath("$.departmentName").value(department.getName()))
            .andExpect(jsonPath("$.departmentShortName").value(department.getShortName()));
    }

    @Test
    @Transactional
    public void getAcademicUnitGroupsInfo() throws Exception {
        // Initialize the database
        academicUnitRepository.saveAndFlush(academicUnit);
        ClassUnit classUnit = ClassUnitResourceIntTest.createEntity(em);
        classUnit.setAcademicUnit(academicUnit);
        em.persist(classUnit);
        em.flush();
        em.refresh(academicUnit);

        Location location = LocationResourceIntTest.createEntity(em);
        Department department = departmentRepository.findOne(1L);
        location.setDepartment(department);
        em.persist(location);
        em.flush();

        Room room = RoomResourceIntTest.createEntity(em);
        room.setLocation(location);
        em.persist(room);
        em.flush();

        ScheduleUnit scheduleUnit = ScheduleUnitResourceIntTest.createEntity(em);
        scheduleUnit.setRoom(room);
        scheduleUnit.setClassUnit(classUnit);
        em.persist(scheduleUnit);
        em.flush();
        em.refresh(classUnit);

        restAcademicUnitInfoMockMvc.perform(get("/api/academic-units-info/{id}/groups", academicUnit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.academicUnitId").value(academicUnit.getId().intValue()))
            .andExpect(jsonPath("$.academicUnitGroups").isArray())
            .andExpect(jsonPath("$.academicUnitGroups").value(containsInAnyOrder(classUnit.getAcademicUnitGroup().toString())));
    }
}
