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
import pl.edu.agh.geotime.domain.enumeration.DayOfWeek;
import pl.edu.agh.geotime.domain.enumeration.SemesterHalf;
import pl.edu.agh.geotime.domain.enumeration.WeekType;
import pl.edu.agh.geotime.repository.BookingUnitRepository;
import pl.edu.agh.geotime.repository.DepartmentRepository;
import pl.edu.agh.geotime.service.BookingUnitService;
import pl.edu.agh.geotime.web.rest.dto.BookingUnitDTO;
import pl.edu.agh.geotime.web.rest.mapper.BookingUnitMapper;
import pl.edu.agh.geotime.service.query.BookingUnitQueryService;
import pl.edu.agh.geotime.service.util.DateUtil;
import pl.edu.agh.geotime.web.rest.errors.ExceptionTranslator;

import javax.persistence.EntityManager;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.edu.agh.geotime.web.rest.TestUtil.createFormattingConversionService;
import static pl.edu.agh.geotime.web.rest.TestUtil.sameInstant;
/**
 * Test class for the BookingUnitResource REST controller.
 *
 * @see BookingUnitResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GeoTimeApp.class)
public class BookingUnitResourceIntTest {

    private static final LocalTime DEFAULT_START_TIME = LocalTime.of(0, 1);
    private static final LocalTime UPDATED_START_TIME = LocalTime.of(23, 2);

    private static final LocalTime DEFAULT_END_TIME = LocalTime.of(1, 20);
    private static final LocalTime UPDATED_END_TIME = LocalTime.of(23, 33);

    private static final DayOfWeek DEFAULT_DAY = DayOfWeek.MON;
    private static final DayOfWeek UPDATED_DAY = DayOfWeek.TUE;

    private static final WeekType DEFAULT_WEEK = WeekType.A;
    private static final WeekType UPDATED_WEEK = WeekType.B;

    private static final SemesterHalf DEFAULT_SEMESTER_HALF = SemesterHalf.HALF1;
    private static final SemesterHalf UPDATED_SEMESTER_HALF = SemesterHalf.HALF2;

    private static final Boolean DEFAULT_LOCKED = false;
    private static final Boolean UPDATED_LOCKED = true;

    private static final String TEST_USER_LOGIN = "system";

    @Autowired
    private BookingUnitRepository bookingUnitRepository;

    @Autowired
    private BookingUnitMapper bookingUnitMapper;

    @Autowired
    private BookingUnitService bookingUnitService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private BookingUnitQueryService bookingUnitQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restBookingUnitMockMvc;

    private BookingUnit bookingUnit;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final BookingUnitResource bookingUnitResource = new BookingUnitResource(bookingUnitService, bookingUnitMapper,
            bookingUnitQueryService);
        this.restBookingUnitMockMvc = MockMvcBuilders.standaloneSetup(bookingUnitResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    public static BookingUnit createEntity(EntityManager em) {
        return new BookingUnit()
            .startTime(DateUtil.convertToZonedDateTime(DEFAULT_START_TIME))
            .endTime(DateUtil.convertToZonedDateTime(DEFAULT_END_TIME))
            .day(DEFAULT_DAY)
            .week(DEFAULT_WEEK)
            .semesterHalf(DEFAULT_SEMESTER_HALF)
            .locked(DEFAULT_LOCKED);
    }

    @Before
    public void initTest() {
        bookingUnit = createEntity(em);
        initOtherEntityFields();
    }

    private void initOtherEntityFields() {
        Department department = departmentRepository.findOne(1L);

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
        bookingUnit.setClassUnit(classUnit);

        Location location = LocationResourceIntTest.createEntity(em);
        location.setDepartment(department);
        em.persist(location);
        em.flush();

        Room room = RoomResourceIntTest.createEntity(em);
        room.setLocation(location);
        em.persist(room);
        em.flush();
        bookingUnit.setRoom(room);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void createBookingUnit() throws Exception {
        int databaseSizeBeforeCreate = bookingUnitRepository.findAll().size();

        // Create the BookingUnit
        BookingUnitDTO bookingUnitDTO = bookingUnitMapper.toDto(bookingUnit);
        restBookingUnitMockMvc.perform(post("/api/booking-units")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bookingUnitDTO)))
            .andExpect(status().isCreated());

        ZonedDateTime expectedStartDate = DateUtil.convertToZonedDateTime(DEFAULT_START_TIME);
        ZonedDateTime expectedEndDate = DateUtil.convertToZonedDateTime(DEFAULT_END_TIME);

        // Validate the BookingUnit in the database
        List<BookingUnit> bookingUnitList = bookingUnitRepository.findAll();
        assertThat(bookingUnitList).hasSize(databaseSizeBeforeCreate + 1);
        BookingUnit testBookingUnit = bookingUnitList.get(bookingUnitList.size() - 1);
        assertThat(testBookingUnit.getStartTime()).isEqualTo(expectedStartDate);
        assertThat(testBookingUnit.getEndTime()).isEqualTo(expectedEndDate);
        assertThat(testBookingUnit.getDay()).isEqualTo(DEFAULT_DAY);
        assertThat(testBookingUnit.getWeek()).isEqualTo(DEFAULT_WEEK);
        assertThat(testBookingUnit.getSemesterHalf()).isEqualTo(DEFAULT_SEMESTER_HALF);
        assertThat(testBookingUnit.isLocked()).isEqualTo(DEFAULT_LOCKED);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void createBookingUnitWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = bookingUnitRepository.findAll().size();

        // Create the BookingUnit with an existing ID
        bookingUnit.setId(1L);
        BookingUnitDTO bookingUnitDTO = bookingUnitMapper.toDto(bookingUnit);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBookingUnitMockMvc.perform(post("/api/booking-units")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bookingUnitDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BookingUnit in the database
        List<BookingUnit> bookingUnitList = bookingUnitRepository.findAll();
        assertThat(bookingUnitList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void checkStartTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = bookingUnitRepository.findAll().size();
        // set the field null
        bookingUnit.setStartTime(null);

        // Create the BookingUnit, which fails.
        BookingUnitDTO bookingUnitDTO = bookingUnitMapper.toDto(bookingUnit);

        restBookingUnitMockMvc.perform(post("/api/booking-units")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bookingUnitDTO)))
            .andExpect(status().isBadRequest());

        List<BookingUnit> bookingUnitList = bookingUnitRepository.findAll();
        assertThat(bookingUnitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void checkEndTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = bookingUnitRepository.findAll().size();
        // set the field null
        bookingUnit.setEndTime(null);

        // Create the BookingUnit, which fails.
        BookingUnitDTO bookingUnitDTO = bookingUnitMapper.toDto(bookingUnit);

        restBookingUnitMockMvc.perform(post("/api/booking-units")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bookingUnitDTO)))
            .andExpect(status().isBadRequest());

        List<BookingUnit> bookingUnitList = bookingUnitRepository.findAll();
        assertThat(bookingUnitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void checkDayIsRequired() throws Exception {
        int databaseSizeBeforeTest = bookingUnitRepository.findAll().size();
        // set the field null
        bookingUnit.setDay(null);

        // Create the BookingUnit, which fails.
        BookingUnitDTO bookingUnitDTO = bookingUnitMapper.toDto(bookingUnit);

        restBookingUnitMockMvc.perform(post("/api/booking-units")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bookingUnitDTO)))
            .andExpect(status().isBadRequest());

        List<BookingUnit> bookingUnitList = bookingUnitRepository.findAll();
        assertThat(bookingUnitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void checkWeekIsRequired() throws Exception {
        int databaseSizeBeforeTest = bookingUnitRepository.findAll().size();
        // set the field null
        bookingUnit.setWeek(null);

        // Create the BookingUnit, which fails.
        BookingUnitDTO bookingUnitDTO = bookingUnitMapper.toDto(bookingUnit);

        restBookingUnitMockMvc.perform(post("/api/booking-units")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bookingUnitDTO)))
            .andExpect(status().isBadRequest());

        List<BookingUnit> bookingUnitList = bookingUnitRepository.findAll();
        assertThat(bookingUnitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllBookingUnits() throws Exception {
        // Initialize the database
        bookingUnitRepository.saveAndFlush(bookingUnit);

        // Get all the bookingUnitList
        restBookingUnitMockMvc.perform(get("/api/booking-units?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookingUnit.getId().intValue())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(sameInstant(DEFAULT_START_TIME))))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(sameInstant(DEFAULT_END_TIME))))
            .andExpect(jsonPath("$.[*].day").value(hasItem(DEFAULT_DAY.toString())))
            .andExpect(jsonPath("$.[*].week").value(hasItem(DEFAULT_WEEK.toString())))
            .andExpect(jsonPath("$.[*].semesterHalf").value(hasItem(DEFAULT_SEMESTER_HALF.toString())))
            .andExpect(jsonPath("$.[*].locked").value(hasItem(DEFAULT_LOCKED)));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getBookingUnit() throws Exception {
        // Initialize the database
        bookingUnitRepository.saveAndFlush(bookingUnit);

        // Get the bookingUnit
        restBookingUnitMockMvc.perform(get("/api/booking-units/{id}", bookingUnit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(bookingUnit.getId().intValue()))
            .andExpect(jsonPath("$.startTime").value(sameInstant(DEFAULT_START_TIME)))
            .andExpect(jsonPath("$.endTime").value(sameInstant(DEFAULT_END_TIME)))
            .andExpect(jsonPath("$.day").value(DEFAULT_DAY.toString()))
            .andExpect(jsonPath("$.week").value(DEFAULT_WEEK.toString()))
            .andExpect(jsonPath("$.semesterHalf").value(DEFAULT_SEMESTER_HALF.toString()))
            .andExpect(jsonPath("$.locked").value(DEFAULT_LOCKED));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllBookingUnitsByStartTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        bookingUnitRepository.saveAndFlush(bookingUnit);

        // Get all the bookingUnitList where startTime equals to DEFAULT_START_TIME
        defaultBookingUnitShouldBeFound("startTime.equals=" + DEFAULT_START_TIME);

        // Get all the bookingUnitList where startTime equals to UPDATED_START_TIME
        defaultBookingUnitShouldNotBeFound("startTime.equals=" + UPDATED_START_TIME);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllBookingUnitsByStartTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookingUnitRepository.saveAndFlush(bookingUnit);

        // Get all the bookingUnitList where startTime is not null
        defaultBookingUnitShouldBeFound("startTime.specified=true");

        // Get all the bookingUnitList where startTime is null
        defaultBookingUnitShouldNotBeFound("startTime.specified=false");
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllBookingUnitsByStartTimeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bookingUnitRepository.saveAndFlush(bookingUnit);

        // Get all the bookingUnitList where startTime greater than or equals to DEFAULT_START_TIME
        defaultBookingUnitShouldBeFound("startTime.greaterOrEqualThan=" + DEFAULT_START_TIME);

        // Get all the bookingUnitList where startTime greater than or equals to UPDATED_START_TIME
        defaultBookingUnitShouldNotBeFound("startTime.greaterOrEqualThan=" + UPDATED_START_TIME);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllBookingUnitsByStartTimeIsLessThanSomething() throws Exception {
        // Initialize the database
        bookingUnitRepository.saveAndFlush(bookingUnit);

        // Get all the bookingUnitList where startTime less than or equals to DEFAULT_START_TIME
        defaultBookingUnitShouldNotBeFound("startTime.lessThan=" + DEFAULT_START_TIME);

        // Get all the bookingUnitList where startTime less than or equals to UPDATED_START_TIME
        defaultBookingUnitShouldBeFound("startTime.lessThan=" + UPDATED_START_TIME);
    }


    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllBookingUnitsByEndTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        bookingUnitRepository.saveAndFlush(bookingUnit);

        // Get all the bookingUnitList where endTime equals to DEFAULT_END_TIME
        defaultBookingUnitShouldBeFound("endTime.equals=" + DEFAULT_END_TIME);

        // Get all the bookingUnitList where endTime equals to UPDATED_END_TIME
        defaultBookingUnitShouldNotBeFound("endTime.equals=" + UPDATED_END_TIME);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllBookingUnitsByEndTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookingUnitRepository.saveAndFlush(bookingUnit);

        // Get all the bookingUnitList where endTime is not null
        defaultBookingUnitShouldBeFound("endTime.specified=true");

        // Get all the bookingUnitList where endTime is null
        defaultBookingUnitShouldNotBeFound("endTime.specified=false");
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllBookingUnitsByEndTimeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bookingUnitRepository.saveAndFlush(bookingUnit);

        // Get all the bookingUnitList where endTime greater than or equals to DEFAULT_END_TIME
        defaultBookingUnitShouldBeFound("endTime.greaterOrEqualThan=" + DEFAULT_END_TIME);

        // Get all the bookingUnitList where endTime greater than or equals to UPDATED_END_TIME
        defaultBookingUnitShouldNotBeFound("endTime.greaterOrEqualThan=" + UPDATED_END_TIME);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllBookingUnitsByEndTimeIsLessThanSomething() throws Exception {
        // Initialize the database
        bookingUnitRepository.saveAndFlush(bookingUnit);

        // Get all the bookingUnitList where endTime less than or equals to DEFAULT_END_TIME
        defaultBookingUnitShouldNotBeFound("endTime.lessThan=" + DEFAULT_END_TIME);

        // Get all the bookingUnitList where endTime less than or equals to UPDATED_END_TIME
        defaultBookingUnitShouldBeFound("endTime.lessThan=" + UPDATED_END_TIME);
    }


    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllBookingUnitsByDayIsEqualToSomething() throws Exception {
        // Initialize the database
        bookingUnitRepository.saveAndFlush(bookingUnit);

        // Get all the bookingUnitList where day equals to DEFAULT_DAY
        defaultBookingUnitShouldBeFound("day.equals=" + DEFAULT_DAY);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllBookingUnitsByDayIsInShouldWork() throws Exception {
        // Initialize the database
        bookingUnitRepository.saveAndFlush(bookingUnit);

        // Get all the bookingUnitList where day in DEFAULT_DAY or UPDATED_DAY
        defaultBookingUnitShouldBeFound("day.in=" + DEFAULT_DAY + "," + UPDATED_DAY);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllBookingUnitsByDayIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookingUnitRepository.saveAndFlush(bookingUnit);

        // Get all the bookingUnitList where day is not null
        defaultBookingUnitShouldBeFound("day.specified=true");

        // Get all the bookingUnitList where day is null
        defaultBookingUnitShouldNotBeFound("day.specified=false");
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllBookingUnitsByWeekIsEqualToSomething() throws Exception {
        // Initialize the database
        bookingUnitRepository.saveAndFlush(bookingUnit);

        // Get all the bookingUnitList where week equals to DEFAULT_WEEK
        defaultBookingUnitShouldBeFound("week.equals=" + DEFAULT_WEEK);

        // Get all the bookingUnitList where week equals to UPDATED_WEEK
        defaultBookingUnitShouldNotBeFound("week.equals=" + UPDATED_WEEK);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllBookingUnitsByWeekIsInShouldWork() throws Exception {
        // Initialize the database
        bookingUnitRepository.saveAndFlush(bookingUnit);

        // Get all the bookingUnitList where week in DEFAULT_WEEK or UPDATED_WEEK
        defaultBookingUnitShouldBeFound("week.in=" + DEFAULT_WEEK + "," + UPDATED_WEEK);

        // Get all the bookingUnitList where week equals to UPDATED_WEEK
        defaultBookingUnitShouldNotBeFound("week.in=" + UPDATED_WEEK);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllBookingUnitsByWeekIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookingUnitRepository.saveAndFlush(bookingUnit);

        // Get all the bookingUnitList where week is not null
        defaultBookingUnitShouldBeFound("week.specified=true");

        // Get all the bookingUnitList where week is null
        defaultBookingUnitShouldNotBeFound("week.specified=false");
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllBookingUnitsBySemesterHalfIsEqualToSomething() throws Exception {
        // Initialize the database
        bookingUnitRepository.saveAndFlush(bookingUnit);

        // Get all the bookingUnitList where semesterHalf equals to DEFAULT_SEMESTER_HALF
        defaultBookingUnitShouldBeFound("semesterHalf.equals=" + DEFAULT_SEMESTER_HALF);

        // Get all the bookingUnitList where semesterHalf equals to UPDATED_SEMESTER_HALF
        defaultBookingUnitShouldNotBeFound("semesterHalf.equals=" + UPDATED_SEMESTER_HALF);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllBookingUnitsBySemesterHalfIsInShouldWork() throws Exception {
        // Initialize the database
        bookingUnitRepository.saveAndFlush(bookingUnit);

        // Get all the bookingUnitList where semesterHalf in DEFAULT_SEMESTER_HALF or UPDATED_SEMESTER_HALF
        defaultBookingUnitShouldBeFound("semesterHalf.in=" + DEFAULT_SEMESTER_HALF + "," + UPDATED_SEMESTER_HALF);

        // Get all the bookingUnitList where semesterHalf equals to UPDATED_SEMESTER_HALF
        defaultBookingUnitShouldNotBeFound("semesterHalf.in=" + UPDATED_SEMESTER_HALF);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllBookingUnitsBySemesterHalfIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookingUnitRepository.saveAndFlush(bookingUnit);

        // Get all the bookingUnitList where semesterHalf is not null
        defaultBookingUnitShouldBeFound("semesterHalf.specified=true");

        // Get all the bookingUnitList where semesterHalf is null
        defaultBookingUnitShouldNotBeFound("semesterHalf.specified=false");
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllBookingUnitsByLockedIsEqualToSomething() throws Exception {
        // Initialize the database
        bookingUnitRepository.saveAndFlush(bookingUnit);

        // Get all the bookingUnitList where locked equals to DEFAULT_LOCKED
        defaultBookingUnitShouldBeFound("locked.equals=" + DEFAULT_LOCKED);

        // Get all the bookingUnitList where locked equals to UPDATED_LOCKED
        defaultBookingUnitShouldNotBeFound("locked.equals=" + UPDATED_LOCKED);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllBookingUnitsByLockedIsInShouldWork() throws Exception {
        // Initialize the database
        bookingUnitRepository.saveAndFlush(bookingUnit);

        // Get all the bookingUnitList where locked in DEFAULT_LOCKED or UPDATED_LOCKED
        defaultBookingUnitShouldBeFound("locked.in=" + DEFAULT_LOCKED + "," + UPDATED_LOCKED);

        // Get all the bookingUnitList where locked equals to UPDATED_LOCKED
        defaultBookingUnitShouldNotBeFound("locked.in=" + UPDATED_LOCKED);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllBookingUnitsByLockedIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookingUnitRepository.saveAndFlush(bookingUnit);

        // Get all the bookingUnitList where locked is not null
        defaultBookingUnitShouldBeFound("locked.specified=true");

        // Get all the bookingUnitList where locked is null
        defaultBookingUnitShouldNotBeFound("locked.specified=false");
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllBookingUnitsByClassUnitIsEqualToSomething() throws Exception {
        bookingUnitRepository.saveAndFlush(bookingUnit);
        Long classUnitId = bookingUnit.getClassUnit().getId();

        // Get all the bookingUnitList where classUnit equals to classUnitId
        defaultBookingUnitShouldBeFound("classUnitId.equals=" + classUnitId);

        // Get all the bookingUnitList where classUnit equals to classUnitId + 1
        defaultBookingUnitShouldNotBeFound("classUnitId.equals=" + (classUnitId + 9999999));
    }


    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllBookingUnitsByRoomIsEqualToSomething() throws Exception {
        // Initialize the database
        bookingUnitRepository.saveAndFlush(bookingUnit);
        Long roomId = bookingUnit.getRoom().getId();

        // Get all the bookingUnitList where room equals to roomId
        defaultBookingUnitShouldBeFound("roomId.equals=" + roomId);

        // Get all the bookingUnitList where room equals to roomId + 1
        defaultBookingUnitShouldNotBeFound("roomId.equals=" + (roomId + 9999999));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultBookingUnitShouldBeFound(String filter) throws Exception {
        restBookingUnitMockMvc.perform(get("/api/booking-units?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookingUnit.getId().intValue())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(sameInstant(DEFAULT_START_TIME))))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(sameInstant(DEFAULT_END_TIME))))
            .andExpect(jsonPath("$.[*].day").value(hasItem(DEFAULT_DAY.toString())))
            .andExpect(jsonPath("$.[*].week").value(hasItem(DEFAULT_WEEK.toString())))
            .andExpect(jsonPath("$.[*].semesterHalf").value(hasItem(DEFAULT_SEMESTER_HALF.toString())))
            .andExpect(jsonPath("$.[*].locked").value(hasItem(DEFAULT_LOCKED)));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultBookingUnitShouldNotBeFound(String filter) throws Exception {
        restBookingUnitMockMvc.perform(get("/api/booking-units?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getNonExistingBookingUnit() throws Exception {
        // Get the bookingUnit
        restBookingUnitMockMvc.perform(get("/api/booking-units/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void updateBookingUnit() throws Exception {
        // Initialize the database
        bookingUnitRepository.saveAndFlush(bookingUnit);
        int databaseSizeBeforeUpdate = bookingUnitRepository.findAll().size();

        // Update the bookingUnit
        BookingUnit updatedBookingUnit = bookingUnitRepository.findOne(bookingUnit.getId());
        // Disconnect from session so that the updates on updatedBookingUnit are not directly saved in db
        em.detach(updatedBookingUnit);

        ZonedDateTime updatedStartDate = DateUtil.convertToZonedDateTime(UPDATED_START_TIME);
        ZonedDateTime updatedEndDate = DateUtil.convertToZonedDateTime(UPDATED_START_TIME);

        updatedBookingUnit
            .startTime(updatedStartDate)
            .endTime(updatedEndDate)
            .day(UPDATED_DAY)
            .week(UPDATED_WEEK)
            .semesterHalf(UPDATED_SEMESTER_HALF)
            .locked(UPDATED_LOCKED);
        BookingUnitDTO bookingUnitDTO = bookingUnitMapper.toDto(updatedBookingUnit);

        restBookingUnitMockMvc.perform(put("/api/booking-units")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bookingUnitDTO)))
            .andExpect(status().isOk());

        // Validate the BookingUnit in the database
        List<BookingUnit> bookingUnitList = bookingUnitRepository.findAll();
        assertThat(bookingUnitList).hasSize(databaseSizeBeforeUpdate);
        BookingUnit testBookingUnit = bookingUnitList.get(bookingUnitList.size() - 1);
        assertThat(testBookingUnit.getStartTime()).isEqualTo(updatedStartDate);
        assertThat(testBookingUnit.getEndTime()).isEqualTo(updatedEndDate);
        assertThat(testBookingUnit.getDay()).isEqualTo(UPDATED_DAY);
        assertThat(testBookingUnit.getWeek()).isEqualTo(UPDATED_WEEK);
        assertThat(testBookingUnit.getSemesterHalf()).isEqualTo(UPDATED_SEMESTER_HALF);
        assertThat(testBookingUnit.isLocked()).isEqualTo(UPDATED_LOCKED);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void updateNonExistingBookingUnit() throws Exception {
        int databaseSizeBeforeUpdate = bookingUnitRepository.findAll().size();

        // Create the BookingUnit
        BookingUnitDTO bookingUnitDTO = bookingUnitMapper.toDto(bookingUnit);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restBookingUnitMockMvc.perform(put("/api/booking-units")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bookingUnitDTO)))
            .andExpect(status().isCreated());

        // Validate the BookingUnit in the database
        List<BookingUnit> bookingUnitList = bookingUnitRepository.findAll();
        assertThat(bookingUnitList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void deleteBookingUnit() throws Exception {
        // Initialize the database
        bookingUnitRepository.saveAndFlush(bookingUnit);
        int databaseSizeBeforeDelete = bookingUnitRepository.findAll().size();

        // Get the bookingUnit
        restBookingUnitMockMvc.perform(delete("/api/booking-units/{id}", bookingUnit.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<BookingUnit> bookingUnitList = bookingUnitRepository.findAll();
        assertThat(bookingUnitList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BookingUnit.class);
        BookingUnit bookingUnit1 = new BookingUnit();
        bookingUnit1.setId(1L);
        BookingUnit bookingUnit2 = new BookingUnit();
        bookingUnit2.setId(bookingUnit1.getId());
        assertThat(bookingUnit1).isEqualTo(bookingUnit2);
        bookingUnit2.setId(2L);
        assertThat(bookingUnit1).isNotEqualTo(bookingUnit2);
        bookingUnit1.setId(null);
        assertThat(bookingUnit1).isNotEqualTo(bookingUnit2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BookingUnitDTO.class);
        BookingUnitDTO bookingUnitDTO1 = new BookingUnitDTO();
        bookingUnitDTO1.setId(1L);
        BookingUnitDTO bookingUnitDTO2 = new BookingUnitDTO();
        assertThat(bookingUnitDTO1).isNotEqualTo(bookingUnitDTO2);
        bookingUnitDTO2.setId(bookingUnitDTO1.getId());
        assertThat(bookingUnitDTO1).isEqualTo(bookingUnitDTO2);
        bookingUnitDTO2.setId(2L);
        assertThat(bookingUnitDTO1).isNotEqualTo(bookingUnitDTO2);
        bookingUnitDTO1.setId(null);
        assertThat(bookingUnitDTO1).isNotEqualTo(bookingUnitDTO2);
    }
}
