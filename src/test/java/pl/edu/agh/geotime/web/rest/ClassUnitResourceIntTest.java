package pl.edu.agh.geotime.web.rest;

import com.google.common.collect.Sets;
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
import pl.edu.agh.geotime.domain.enumeration.AcademicUnitGroup;
import pl.edu.agh.geotime.domain.enumeration.ClassFrequency;
import pl.edu.agh.geotime.repository.ClassUnitGroupRepository;
import pl.edu.agh.geotime.repository.ClassUnitRepository;
import pl.edu.agh.geotime.repository.DepartmentRepository;
import pl.edu.agh.geotime.service.ClassUnitService;
import pl.edu.agh.geotime.service.query.ClassUnitQueryService;
import pl.edu.agh.geotime.web.rest.dto.ClassUnitDTO;
import pl.edu.agh.geotime.web.rest.errors.ExceptionTranslator;
import pl.edu.agh.geotime.web.rest.helper.ClassUnitResourceHelper;
import pl.edu.agh.geotime.web.rest.helper.ClassUnitUploadHelper;
import pl.edu.agh.geotime.web.rest.mapper.ClassUnitMapper;
import pl.edu.agh.geotime.web.rest.vm.ClassUnitCreateVM;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.edu.agh.geotime.web.rest.TestUtil.createFormattingConversionService;

/**
 * Test class for the ClassUnitResource REST controller.
 *
 * @see ClassUnitResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GeoTimeApp.class)
public class ClassUnitResourceIntTest {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_DURATION = 0;
    private static final Integer UPDATED_DURATION = 1;

    private static final Integer DEFAULT_HOURS_QUANTITY = 0;
    private static final Integer UPDATED_HOURS_QUANTITY = 1;

    private static final ClassFrequency DEFAULT_FREQUENCY = ClassFrequency.SINGLE;
    private static final ClassFrequency UPDATED_FREQUENCY = ClassFrequency.EVERY_DAY;
    private static final String TEST_USER_LOGIN = "system";

    private static final AcademicUnitGroup DEFAULT_ACADEMIC_UNIT_GROUP = AcademicUnitGroup.GROUP1;
    private static final AcademicUnitGroup UPDATED_ACADEMIC_UNIT_GROUP = AcademicUnitGroup.GROUP2;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private ClassUnitRepository classUnitRepository;

    @Autowired
    private ClassUnitGroupRepository classUnitGroupRepository;

    @Autowired
    private ClassUnitMapper classUnitMapper;

    @Autowired
    private ClassUnitService classUnitService;

    @Autowired
    private ClassUnitResourceHelper classUnitResourceHelper;

    @Autowired
    private ClassUnitUploadHelper classUnitUploadHelper;

    @Autowired
    private ClassUnitQueryService classUnitQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restClassUnitMockMvc;

    private ClassUnit classUnit;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ClassUnitResource classUnitResource = new ClassUnitResource(classUnitService, classUnitMapper, classUnitQueryService,
            classUnitResourceHelper, classUnitUploadHelper);
        this.restClassUnitMockMvc = MockMvcBuilders.standaloneSetup(classUnitResource)
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
    public static ClassUnit createEntity(EntityManager em) {
        ClassUnit classUnit = new ClassUnit()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .duration(DEFAULT_DURATION)
            .hoursQuantity(DEFAULT_HOURS_QUANTITY)
            .frequency(DEFAULT_FREQUENCY)
            .academicUnitGroup(DEFAULT_ACADEMIC_UNIT_GROUP)
            .onlySemesterHalf(false);
        // Add required entity
        ClassType classType = ClassTypeResourceIntTest.createEntity(em);
        em.persist(classType);
        em.flush();
        classUnit.setClassType(classType);
        // Add required entity
        UserExt userExt = UserResourceIntTest.createEntity(em);
        em.persist(userExt);
        em.flush();
        classUnit.setUserExt(userExt);
        classUnit.setSubdepartment(userExt.getSubdepartment());
        // Add required entity
        Semester semester = SemesterResourceIntTest.createEntity(em);
        em.persist(semester);
        em.flush();
        classUnit.setSemester(semester);
        return classUnit;
    }

    @Before
    public void initTest() {
        classUnit = createEntity(em);
        initOtherEntityFields();
    }

    private void initOtherEntityFields(){
        Department department = departmentRepository.findOne(1L);

        StudyField studyField = StudyFieldResourceIntTest.createEntity(em);
        studyField.setDepartment(department);
        em.persist(studyField);
        em.flush();

        AcademicUnit academicUnit = AcademicUnitResourceIntTest.createEntity(em);
        academicUnit.setStudyField(studyField);
        em.persist(academicUnit);
        em.flush();
        classUnit.setAcademicUnit(academicUnit);

        Subdepartment subdepartment = classUnit.getSubdepartment();
        subdepartment.setDepartment(department);
        em.persist(subdepartment);
        em.flush();

        Location location = LocationResourceIntTest.createEntity(em);
        location.setDepartment(department);
        em.persist(location);
        em.flush();

        Room room = RoomResourceIntTest.createEntity(em);
        room.setLocation(location);
        em.persist(room);
        em.flush();
        classUnit.setRooms(Sets.newHashSet(room));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void createClassUnit() throws Exception {
        int databaseSizeBeforeCreate = classUnitRepository.findAll().size();

        // Create the ClassUnit
        ClassUnitCreateVM classUnitCreateVM = mapToClassUnitVM(classUnit);
        restClassUnitMockMvc.perform(post("/api/class-units")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(classUnitCreateVM)))
            .andExpect(status().isCreated());

        // Validate the ClassUnit in the database
        List<ClassUnit> classUnitList = classUnitRepository.findAll();
        assertThat(classUnitList).hasSize(databaseSizeBeforeCreate + 1);
        ClassUnit testClassUnit = classUnitList.get(classUnitList.size() - 1);
        assertThat(testClassUnit.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testClassUnit.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testClassUnit.getDuration()).isEqualTo(DEFAULT_DURATION);
        assertThat(testClassUnit.getHoursQuantity()).isEqualTo(DEFAULT_HOURS_QUANTITY);
        assertThat(testClassUnit.getFrequency()).isEqualTo(DEFAULT_FREQUENCY);
        assertThat(testClassUnit.getAcademicUnitGroup()).isEqualTo(DEFAULT_ACADEMIC_UNIT_GROUP);
    }

    private ClassUnitCreateVM mapToClassUnitVM(ClassUnit classUnit) {
        ClassUnitCreateVM classUnitCreateVM = new ClassUnitCreateVM();
        classUnitCreateVM.setTitle(classUnit.getTitle());
        classUnitCreateVM.setDescription(classUnit.getDescription());
        classUnitCreateVM.setDuration(classUnit.getDuration());
        classUnitCreateVM.setHoursQuantity(classUnit.getHoursQuantity());
        classUnitCreateVM.setFrequency(classUnit.getFrequency());
        classUnitCreateVM.setAcademicUnitGroup(classUnit.getAcademicUnitGroup());
        classUnitCreateVM.setClassTypeId(classUnit.getClassType().getId());
        classUnitCreateVM.setUserId(classUnit.getUserExt().getId());
        classUnitCreateVM.setRoomIds(classUnit.getRooms().stream()
            .map(Room::getId)
            .collect(Collectors.toSet())
        );
        classUnitCreateVM.setAcademicUnitId(classUnit.getAcademicUnit().getId());
        classUnitCreateVM.setSemesterId(classUnit.getSemester().getId());
        classUnitCreateVM.setClassUnitGroup(classUnit.getClassUnitGroup());
        classUnitCreateVM.setSubdepartmentId(classUnit.getSubdepartment().getId());
        return classUnitCreateVM;
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void createClassUnitWithNewClassUnitGroup() throws Exception {
        int databaseSizeBeforeCreate = classUnitRepository.findAll().size();
        int classUnitGroupQuantityBefore = classUnitGroupRepository.findAll().size();

        // Create the ClassUnit
        ClassUnitCreateVM classUnitCreateVM = mapToClassUnitVM(classUnit);

        String newClassUnitGroupName = "New test name";
        ClassUnitGroup classUnitGroup = new ClassUnitGroup();
        classUnitGroup.setName(newClassUnitGroupName);
        classUnitCreateVM.setClassUnitGroup(classUnitGroup);

        restClassUnitMockMvc.perform(post("/api/class-units")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(classUnitCreateVM)))
            .andExpect(status().isCreated());

        // Validate the ClassUnit in the database
        List<ClassUnit> classUnitList = classUnitRepository.findAll();
        assertThat(classUnitList).hasSize(databaseSizeBeforeCreate + 1);
        ClassUnit testClassUnit = classUnitList.get(classUnitList.size() - 1);
        assertThat(testClassUnit.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testClassUnit.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testClassUnit.getDuration()).isEqualTo(DEFAULT_DURATION);
        assertThat(testClassUnit.getHoursQuantity()).isEqualTo(DEFAULT_HOURS_QUANTITY);
        assertThat(testClassUnit.getFrequency()).isEqualTo(DEFAULT_FREQUENCY);
        assertThat(testClassUnit.getAcademicUnitGroup()).isEqualTo(DEFAULT_ACADEMIC_UNIT_GROUP);

        // Validate the ClassUnitGroup in the database
        List<ClassUnitGroup> classUnitGroups = classUnitGroupRepository.findAll();
        assertThat(classUnitGroups).hasSize(classUnitGroupQuantityBefore + 1);
        ClassUnitGroup testClassUnitGroup = classUnitGroups.get(classUnitGroups.size() - 1);
        assertThat(testClassUnitGroup.getName()).isEqualTo(newClassUnitGroupName);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = classUnitRepository.findAll().size();
        // set the field null
        classUnit.setTitle(null);

        // Create the ClassUnit, which fails.
        ClassUnitDTO classUnitDTO = classUnitMapper.toDto(classUnit);

        restClassUnitMockMvc.perform(post("/api/class-units")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(classUnitDTO)))
            .andExpect(status().isBadRequest());

        List<ClassUnit> classUnitList = classUnitRepository.findAll();
        assertThat(classUnitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void checkDurationIsRequired() throws Exception {
        int databaseSizeBeforeTest = classUnitRepository.findAll().size();
        // set the field null
        classUnit.setDuration(null);

        // Create the ClassUnit, which fails.
        ClassUnitDTO classUnitDTO = classUnitMapper.toDto(classUnit);

        restClassUnitMockMvc.perform(post("/api/class-units")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(classUnitDTO)))
            .andExpect(status().isBadRequest());

        List<ClassUnit> classUnitList = classUnitRepository.findAll();
        assertThat(classUnitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void checkFrequencyIsRequired() throws Exception {
        int databaseSizeBeforeTest = classUnitRepository.findAll().size();
        // set the field null
        classUnit.setFrequency(null);

        // Create the ClassUnit, which fails.
        ClassUnitDTO classUnitDTO = classUnitMapper.toDto(classUnit);

        restClassUnitMockMvc.perform(post("/api/class-units")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(classUnitDTO)))
            .andExpect(status().isBadRequest());

        List<ClassUnit> classUnitList = classUnitRepository.findAll();
        assertThat(classUnitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllClassUnits() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);

        // Get all the classUnitList
        restClassUnitMockMvc.perform(get("/api/class-units?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(classUnit.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION)))
            .andExpect(jsonPath("$.[*].hoursQuantity").value(hasItem(DEFAULT_HOURS_QUANTITY)))
            .andExpect(jsonPath("$.[*].frequency").value(hasItem(DEFAULT_FREQUENCY.toString())))
            .andExpect(jsonPath("$.[*].academicUnitGroup").value(hasItem(DEFAULT_ACADEMIC_UNIT_GROUP.toString())));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getClassUnit() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);

        // Get the classUnit
        restClassUnitMockMvc.perform(get("/api/class-units/{id}", classUnit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(classUnit.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.duration").value(DEFAULT_DURATION))
            .andExpect(jsonPath("$.hoursQuantity").value(DEFAULT_HOURS_QUANTITY))
            .andExpect(jsonPath("$.frequency").value(DEFAULT_FREQUENCY.toString()))
            .andExpect(jsonPath("$.academicUnitGroup").value(DEFAULT_ACADEMIC_UNIT_GROUP.toString()));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllClassUnitsByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);

        // Get all the classUnitList where title equals to DEFAULT_TITLE
        defaultClassUnitShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the classUnitList where title equals to UPDATED_TITLE
        defaultClassUnitShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllClassUnitsByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);

        // Get all the classUnitList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultClassUnitShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the classUnitList where title equals to UPDATED_TITLE
        defaultClassUnitShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllClassUnitsByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);

        // Get all the classUnitList where title is not null
        defaultClassUnitShouldBeFound("title.specified=true");

        // Get all the classUnitList where title is null
        defaultClassUnitShouldNotBeFound("title.specified=false");
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllClassUnitsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);

        // Get all the classUnitList where description equals to DEFAULT_DESCRIPTION
        defaultClassUnitShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the classUnitList where description equals to UPDATED_DESCRIPTION
        defaultClassUnitShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllClassUnitsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);

        // Get all the classUnitList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultClassUnitShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the classUnitList where description equals to UPDATED_DESCRIPTION
        defaultClassUnitShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllClassUnitsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);

        // Get all the classUnitList where description is not null
        defaultClassUnitShouldBeFound("description.specified=true");
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllClassUnitsByDurationIsEqualToSomething() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);

        // Get all the classUnitList where duration equals to DEFAULT_DURATION
        defaultClassUnitShouldBeFound("duration.equals=" + DEFAULT_DURATION);

        // Get all the classUnitList where duration equals to UPDATED_DURATION
        defaultClassUnitShouldNotBeFound("duration.equals=" + UPDATED_DURATION);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllClassUnitsByDurationIsInShouldWork() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);

        // Get all the classUnitList where duration in DEFAULT_DURATION or UPDATED_DURATION
        defaultClassUnitShouldBeFound("duration.in=" + DEFAULT_DURATION + "," + UPDATED_DURATION);

        // Get all the classUnitList where duration equals to UPDATED_DURATION
        defaultClassUnitShouldNotBeFound("duration.in=" + UPDATED_DURATION);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllClassUnitsByDurationNotNull() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);

        // Get all the classUnitList where duration is not null
        defaultClassUnitShouldBeFound("duration.specified=true");
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllClassUnitsByDurationIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);

        // Get all the classUnitList where duration greater than or equals to DEFAULT_DURATION
        defaultClassUnitShouldBeFound("duration.greaterOrEqualThan=" + DEFAULT_DURATION);

        // Get all the classUnitList where duration greater than or equals to (DEFAULT_DURATION + 1)
        defaultClassUnitShouldNotBeFound("duration.greaterOrEqualThan=" + (DEFAULT_DURATION + 999999999));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllClassUnitsByDurationIsLessThanSomething() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);

        // Get all the classUnitList where duration less than or equals to DEFAULT_DURATION
        defaultClassUnitShouldNotBeFound("duration.lessThan=" + DEFAULT_DURATION);

        // Get all the classUnitList where duration less than or equals to (DEFAULT_DURATION + 1)
        defaultClassUnitShouldBeFound("duration.lessThan=" + (DEFAULT_DURATION + 1));
    }


    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllClassUnitsByHoursQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);

        // Get all the classUnitList where hoursQuantity equals to DEFAULT_HOURS_QUANTITY
        defaultClassUnitShouldBeFound("hoursQuantity.equals=" + DEFAULT_HOURS_QUANTITY);

        // Get all the classUnitList where hoursQuantity equals to UPDATED_HOURS_QUANTITY
        defaultClassUnitShouldNotBeFound("hoursQuantity.equals=" + UPDATED_HOURS_QUANTITY);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllClassUnitsByHoursQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);

        // Get all the classUnitList where hoursQuantity in DEFAULT_HOURS_QUANTITY or UPDATED_HOURS_QUANTITY
        defaultClassUnitShouldBeFound("hoursQuantity.in=" + DEFAULT_HOURS_QUANTITY + "," + UPDATED_HOURS_QUANTITY);

        // Get all the classUnitList where hoursQuantity equals to UPDATED_HOURS_QUANTITY
        defaultClassUnitShouldNotBeFound("hoursQuantity.in=" + UPDATED_HOURS_QUANTITY);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllClassUnitsByHoursQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);

        // Get all the classUnitList where hoursQuantity is not null
        defaultClassUnitShouldBeFound("hoursQuantity.specified=true");

        // Get all the classUnitList where hoursQuantity is null
        defaultClassUnitShouldNotBeFound("hoursQuantity.specified=false");
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllClassUnitsByHoursQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);

        // Get all the classUnitList where hoursQuantity greater than or equals to DEFAULT_HOURS_QUANTITY
        defaultClassUnitShouldBeFound("hoursQuantity.greaterOrEqualThan=" + DEFAULT_HOURS_QUANTITY);

        // Get all the classUnitList where hoursQuantity greater than or equals to (DEFAULT_HOURS_QUANTITY + 1)
        defaultClassUnitShouldNotBeFound("hoursQuantity.greaterOrEqualThan=" + (DEFAULT_HOURS_QUANTITY + 500));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllClassUnitsByHoursQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);

        // Get all the classUnitList where hoursQuantity less than or equals to DEFAULT_HOURS_QUANTITY
        defaultClassUnitShouldNotBeFound("hoursQuantity.lessThan=" + DEFAULT_HOURS_QUANTITY);

        // Get all the classUnitList where hoursQuantity less than or equals to (DEFAULT_HOURS_QUANTITY + 1)
        defaultClassUnitShouldBeFound("hoursQuantity.lessThan=" + (DEFAULT_HOURS_QUANTITY + 1));
    }


    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllClassUnitsByFrequencyIsEqualToSomething() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);

        // Get all the classUnitList where frequency equals to DEFAULT_FREQUENCY
        defaultClassUnitShouldBeFound("frequency.equals=" + DEFAULT_FREQUENCY);

        // Get all the classUnitList where frequency equals to UPDATED_FREQUENCY
        defaultClassUnitShouldNotBeFound("frequency.equals=" + UPDATED_FREQUENCY);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllClassUnitsByFrequencyIsInShouldWork() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);

        // Get all the classUnitList where frequency in DEFAULT_FREQUENCY or UPDATED_FREQUENCY
        defaultClassUnitShouldBeFound("frequency.in=" + DEFAULT_FREQUENCY + "," + UPDATED_FREQUENCY);

        // Get all the classUnitList where frequency equals to UPDATED_FREQUENCY
        defaultClassUnitShouldNotBeFound("frequency.in=" + UPDATED_FREQUENCY);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllClassUnitsByFrequencyIsNullOrNotNull() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);

        // Get all the classUnitList where frequency is not null
        defaultClassUnitShouldBeFound("frequency.specified=true");

        // Get all the classUnitList where frequency is null
        defaultClassUnitShouldNotBeFound("frequency.specified=false");
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllClassUnitsByAcademicUnitGroupIsEqualToSomething() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);

        // Get all the classUnitList where academicUnitGroup equals to DEFAULT_ACADEMIC_UNIT_GROUP
        defaultClassUnitShouldBeFound("academicUnitGroup.equals=" + DEFAULT_ACADEMIC_UNIT_GROUP);

        // Get all the classUnitList where academicUnitGroup equals to UPDATED_ACADEMIC_UNIT_GROUP
        defaultClassUnitShouldNotBeFound("academicUnitGroup.equals=" + UPDATED_ACADEMIC_UNIT_GROUP);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllClassUnitsByAcademicUnitGroupIsInShouldWork() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);

        // Get all the classUnitList where academicUnitGroup in DEFAULT_ACADEMIC_UNIT_GROUP or UPDATED_ACADEMIC_UNIT_GROUP
        defaultClassUnitShouldBeFound("academicUnitGroup.in=" + DEFAULT_ACADEMIC_UNIT_GROUP + "," + UPDATED_ACADEMIC_UNIT_GROUP);

        // Get all the classUnitList where academicUnitGroup equals to UPDATED_ACADEMIC_UNIT_GROUP
        defaultClassUnitShouldNotBeFound("academicUnitGroup.in=" + UPDATED_ACADEMIC_UNIT_GROUP);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllClassUnitsByAcademicUnitGroupIsNullOrNotNull() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);

        // Get all the classUnitList where academicUnitGroup is not null
        defaultClassUnitShouldBeFound("academicUnitGroup.specified=true");
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllClassUnitsByClassTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);
        Long classTypeId = classUnit.getClassType().getId();

        // Get all the classUnitList where classType equals to classTypeId
        defaultClassUnitShouldBeFound("classTypeId.equals=" + classTypeId);

        // Get all the classUnitList where classType equals to classTypeId + 1
        defaultClassUnitShouldNotBeFound("classTypeId.equals=" + (classTypeId + 1));
    }


    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllClassUnitsByUserExtIsEqualToSomething() throws Exception {
        // Initialize the database
        UserExt userExt = UserResourceIntTest.createEntity(em);
        em.persist(userExt);
        em.flush();
        classUnit.setUserExt(userExt);
        classUnitRepository.saveAndFlush(classUnit);
        Long userExtId = userExt.getId();

        // Get all the classUnitList where userExt equals to userExtId
        defaultClassUnitShouldBeFound("userExtId.equals=" + userExtId);

        // Get all the classUnitList where userExt equals to userExtId + 1
        defaultClassUnitShouldNotBeFound("userExtId.equals=" + (userExtId + 1));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllClassUnitsByRoomIsEqualToSomething() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);
        Long roomId = classUnit.getRooms().iterator().next().getId();

        // Get all the classUnitList where room equals to roomId
        defaultClassUnitShouldBeFound("roomId.equals=" + roomId);

        // Get all the classUnitList where room equals to roomId + 1
        defaultClassUnitShouldNotBeFound("roomId.equals=" + (roomId + 1));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllClassUnitsByBookingUnitIsEqualToSomething() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);

        BookingUnit bookingUnit = BookingUnitResourceIntTest.createEntity(em);
        bookingUnit.setClassUnit(classUnit);
        bookingUnit.setRoom(classUnit.getRooms().iterator().next());
        em.persist(bookingUnit);
        em.flush();

        classUnit.setBookingUnit(bookingUnit);
        classUnitRepository.saveAndFlush(classUnit);
        Long bookingUnitId = bookingUnit.getId();

        // Get all the classUnitList where bookingUnit equals to bookingUnitId
        defaultClassUnitShouldBeFound("bookingUnitId.equals=" + bookingUnitId);

        // Get all the classUnitList where bookingUnit equals to bookingUnitId + 1
        defaultClassUnitShouldNotBeFound("bookingUnitId.equals=" + (bookingUnitId + 999999999));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllClassUnitsByScheduleUnitIsEqualToSomething() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);

        ScheduleUnit scheduleUnit = ScheduleUnitResourceIntTest.createEntity(em);
        scheduleUnit.setClassUnit(classUnit);
        scheduleUnit.setRoom(classUnit.getRooms().iterator().next());
        em.persist(scheduleUnit);
        em.flush();

        classUnit.addScheduleUnit(scheduleUnit);
        classUnitRepository.saveAndFlush(classUnit);
        Long scheduleUnitId = scheduleUnit.getId();

        // Get all the classUnitList where scheduleUnit equals to scheduleUnitId
        defaultClassUnitShouldBeFound("scheduleUnitId.equals=" + scheduleUnitId);

        // Get all the classUnitList where scheduleUnit equals to scheduleUnitId + 1
        defaultClassUnitShouldNotBeFound("scheduleUnitId.equals=" + (scheduleUnitId + 1));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllClassUnitsBySemesterIsEqualToSomething() throws Exception {
        // Initialize the database
        Semester semester = SemesterResourceIntTest.createEntity(em);
        em.persist(semester);
        em.flush();
        classUnit.setSemester(semester);
        classUnitRepository.saveAndFlush(classUnit);
        Long semesterId = semester.getId();

        // Get all the classUnitList where semester equals to semesterId
        defaultClassUnitShouldBeFound("semesterId.equals=" + semesterId);

        // Get all the classUnitList where semester equals to semesterId + 1
        defaultClassUnitShouldNotBeFound("semesterId.equals=" + (semesterId + 1));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllClassUnitsByAcademicUnitIsEqualToSomething() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);
        Long academicUnitId = classUnit.getAcademicUnit().getId();

        // Get all the classUnitList where academicUnit equals to academicUnitId
        defaultClassUnitShouldBeFound("academicUnitId.equals=" + academicUnitId);

        // Get all the classUnitList where academicUnit equals to academicUnitId + 1
        defaultClassUnitShouldNotBeFound("academicUnitId.equals=" + (academicUnitId + 99999999));
    }


    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllClassUnitsByClassUnitGroupIsEqualToSomething() throws Exception {
        // Initialize the database
        ClassUnitGroup classUnitGroup = ClassUnitGroupResourceIntTest.createEntity(em);
        classUnitGroup.setDepartment(classUnit.getAcademicUnit().getStudyField().getDepartment());
        em.persist(classUnitGroup);
        em.flush();
        classUnit.setClassUnitGroup(classUnitGroup);
        classUnitRepository.saveAndFlush(classUnit);
        Long classUnitGroupId = classUnitGroup.getId();

        // Get all the classUnitList where classUnitGroup equals to classUnitGroupId
        defaultClassUnitShouldBeFound("classUnitGroupId.equals=" + classUnitGroupId);

        // Get all the classUnitList where classUnitGroup equals to classUnitGroupId + 1
        defaultClassUnitShouldNotBeFound("classUnitGroupId.equals=" + (classUnitGroupId + 999999999));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultClassUnitShouldBeFound(String filter) throws Exception {
        restClassUnitMockMvc.perform(get("/api/class-units?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(classUnit.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION)))
            .andExpect(jsonPath("$.[*].hoursQuantity").value(hasItem(DEFAULT_HOURS_QUANTITY)))
            .andExpect(jsonPath("$.[*].frequency").value(hasItem(DEFAULT_FREQUENCY.toString())))
            .andExpect(jsonPath("$.[*].academicUnitGroup").value(hasItem(DEFAULT_ACADEMIC_UNIT_GROUP.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultClassUnitShouldNotBeFound(String filter) throws Exception {
        restClassUnitMockMvc.perform(get("/api/class-units?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getNonExistingClassUnit() throws Exception {
        // Get the classUnit
        restClassUnitMockMvc.perform(get("/api/class-units/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void updateClassUnit() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);
        int databaseSizeBeforeUpdate = classUnitRepository.findAll().size();

        // Update the classUnit
        ClassUnit updatedClassUnit = classUnitRepository.findOne(classUnit.getId());
        // Disconnect from session so that the updates on updatedClassUnit are not directly saved in db
        em.detach(updatedClassUnit);
        updatedClassUnit
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .duration(UPDATED_DURATION)
            .hoursQuantity(UPDATED_HOURS_QUANTITY)
            .frequency(UPDATED_FREQUENCY)
            .academicUnitGroup(UPDATED_ACADEMIC_UNIT_GROUP);

        ClassUnitDTO updatedClassUnitDTO = classUnitMapper.toDto(updatedClassUnit);

        restClassUnitMockMvc.perform(put("/api/class-units")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedClassUnitDTO)))
            .andExpect(status().isOk());

        // Validate the ClassUnit in the database
        List<ClassUnit> classUnitList = classUnitRepository.findAll();
        assertThat(classUnitList).hasSize(databaseSizeBeforeUpdate);
        ClassUnit testClassUnit = classUnitList.get(classUnitList.size() - 1);
        assertThat(testClassUnit.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testClassUnit.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testClassUnit.getDuration()).isEqualTo(UPDATED_DURATION);
        assertThat(testClassUnit.getHoursQuantity()).isEqualTo(UPDATED_HOURS_QUANTITY);
        assertThat(testClassUnit.getFrequency()).isEqualTo(UPDATED_FREQUENCY);
        assertThat(testClassUnit.getAcademicUnitGroup()).isEqualTo(UPDATED_ACADEMIC_UNIT_GROUP);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void updateNonExistingClassUnit() throws Exception {
        int databaseSizeBeforeUpdate = classUnitRepository.findAll().size();

        // Create the ClassUnit
        ClassUnitDTO classUnitDTO = classUnitMapper.toDto(classUnit);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restClassUnitMockMvc.perform(put("/api/class-units")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(classUnitDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ClassUnit in the database
        List<ClassUnit> classUnitList = classUnitRepository.findAll();
        assertThat(classUnitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void assignClassUnit() throws Exception {
        // Initialize the database
        UserExt classUnitUser = classUnit.getUserExt();
        classUnit.setUserExt(null);
        ClassUnit savedClassUnit = classUnitRepository.saveAndFlush(this.classUnit);

        restClassUnitMockMvc.perform(put("/api/class-units/assign")
            .param("classUnitId", savedClassUnit.getId().toString())
            .param("userId", classUnitUser.getId().toString())
        ).andExpect(status().isOk());

        // Validate the ClassUnit in the database
        List<ClassUnit> classUnitList = classUnitRepository.findAll();
        ClassUnit testClassUnit = classUnitList.get(classUnitList.size() - 1);
        assertThat(testClassUnit.getTitle()).isEqualTo(classUnit.getTitle());
        assertThat(testClassUnit.getUserExt().getId()).isEqualTo(classUnitUser.getId());
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void deleteClassUnit() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);
        int databaseSizeBeforeDelete = classUnitRepository.findAll().size();

        // Get the classUnit
        restClassUnitMockMvc.perform(delete("/api/class-units/{id}", classUnit.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<ClassUnit> classUnitList = classUnitRepository.findAll();
        assertThat(classUnitList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClassUnit.class);
        ClassUnit classUnit1 = new ClassUnit();
        classUnit1.setId(1L);
        ClassUnit classUnit2 = new ClassUnit();
        classUnit2.setId(classUnit1.getId());
        assertThat(classUnit1).isEqualTo(classUnit2);
        classUnit2.setId(2L);
        assertThat(classUnit1).isNotEqualTo(classUnit2);
        classUnit1.setId(null);
        assertThat(classUnit1).isNotEqualTo(classUnit2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClassUnitDTO.class);
        ClassUnitDTO classUnitDTO1 = new ClassUnitDTO();
        classUnitDTO1.setId(1L);
        ClassUnitDTO classUnitDTO2 = new ClassUnitDTO();
        assertThat(classUnitDTO1).isNotEqualTo(classUnitDTO2);
        classUnitDTO2.setId(classUnitDTO1.getId());
        assertThat(classUnitDTO1).isEqualTo(classUnitDTO2);
        classUnitDTO2.setId(2L);
        assertThat(classUnitDTO1).isNotEqualTo(classUnitDTO2);
        classUnitDTO1.setId(null);
        assertThat(classUnitDTO1).isNotEqualTo(classUnitDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(classUnitMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(classUnitMapper.fromId(null)).isNull();
    }
}
