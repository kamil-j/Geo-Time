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
import pl.edu.agh.geotime.domain.enumeration.ClassFrequency;
import pl.edu.agh.geotime.domain.enumeration.DayOfWeek;
import pl.edu.agh.geotime.domain.enumeration.SemesterHalf;
import pl.edu.agh.geotime.domain.enumeration.WeekType;
import pl.edu.agh.geotime.repository.BookingUnitRepository;
import pl.edu.agh.geotime.repository.ClassUnitRepository;
import pl.edu.agh.geotime.repository.DepartmentRepository;
import pl.edu.agh.geotime.repository.UserExtRepository;
import pl.edu.agh.geotime.service.SemesterService;
import pl.edu.agh.geotime.service.helper.UserHelper;
import pl.edu.agh.geotime.web.rest.errors.ExceptionTranslator;
import pl.edu.agh.geotime.web.rest.helper.BookingsHelper;
import pl.edu.agh.geotime.web.rest.util.TimetableBookingsUtil;
import pl.edu.agh.geotime.web.rest.vm.CreateBookingVM;
import pl.edu.agh.geotime.web.rest.vm.LockBookingVM;
import pl.edu.agh.geotime.web.rest.vm.UpdateBookingVM;

import javax.persistence.EntityManager;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.edu.agh.geotime.web.rest.TestUtil.createFormattingConversionService;
import static pl.edu.agh.geotime.web.rest.TestUtil.sameInstant;

/**
 * Test class for the Timetable REST controller.
 *
 * @see TimetableResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GeoTimeApp.class)
public class TimetableBookingsResourceIntTest {

    private static final String TEST_USER_LOGIN = "system";

    @Autowired
    private UserHelper userHelper;

    @Autowired
    private UserExtRepository userExtRepository;

    @Autowired
    private ClassUnitRepository classUnitRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private BookingsHelper bookingsHelper;

    @Autowired
    private SemesterService semesterService;

    @Autowired
    private BookingUnitRepository bookingUnitRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restTimetableBookingsMockMvc;

    private ClassUnit classUnit;

    private BookingUnit bookingUnit;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TimetableBookingsResource timetableBookingsResource = new TimetableBookingsResource(
            bookingsHelper, userHelper);
        this.restTimetableBookingsMockMvc = MockMvcBuilders.standaloneSetup(timetableBookingsResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        classUnit = ClassUnitResourceIntTest.createEntity(em);
        bookingUnit = BookingUnitResourceIntTest.createEntity(em);
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
        classUnit.setSubdepartment(user.getSubdepartment());
        classUnit.setRooms(Collections.singleton(room));
        classUnit.setAcademicUnit(academicUnit);

        bookingUnit.setClassUnit(classUnit);
        bookingUnit.setRoom(room);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getCurrentUserBookings() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);
        bookingUnitRepository.saveAndFlush(bookingUnit);

        User user = bookingUnit.getClassUnit().getUserExt().getUser();
        String lecturerName = user.getFirstName() + " " + user.getLastName();

        DayOfWeek day = bookingUnit.getDay();
        ZonedDateTime startDateTime = TimetableBookingsUtil.convertToDateTime(
            bookingUnit.getStartTime().toLocalTime(), day
        );
        ZonedDateTime endDateTime = TimetableBookingsUtil.convertToDateTime(
            bookingUnit.getEndTime().toLocalTime(), day
        );

        restTimetableBookingsMockMvc.perform(get("/api/timetable-bookings")
                .param("userId", classUnit.getUserExt().getId().toString())
            ).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookingUnit.getClassUnit().getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(bookingUnit.getClassUnit().getTitle())))
            .andExpect(jsonPath("$.[*].start").value(hasItem(sameInstant(startDateTime))))
            .andExpect(jsonPath("$.[*].end").value(hasItem(sameInstant(endDateTime))))
            .andExpect(jsonPath("$.[*].type").value(hasItem(bookingUnit.getClassUnit().getClassType().getName())))
            .andExpect(jsonPath("$.[*].roomId").value(hasItem(bookingUnit.getRoom().getId().intValue())))
            .andExpect(jsonPath("$.[*].roomName").value(hasItem(bookingUnit.getRoom().getName())))
            .andExpect(jsonPath("$.[*].lecturerName").value(hasItem(lecturerName)));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getOtherUserScheduleInfo() throws Exception {
        // Initialize the database
        UserExt userExt = userExtRepository.findOne(2L);
        classUnit.setUserExt(userExt);
        classUnitRepository.saveAndFlush(classUnit);

        bookingUnit.setClassUnit(classUnit);
        bookingUnitRepository.saveAndFlush(bookingUnit);

        User user = userExt.getUser();
        String lecturerName = user.getFirstName() + " " + user.getLastName();

        DayOfWeek day = bookingUnit.getDay();
        ZonedDateTime startDateTime = TimetableBookingsUtil.convertToDateTime(
            bookingUnit.getStartTime().toLocalTime(), day
        );
        ZonedDateTime endDateTime = TimetableBookingsUtil.convertToDateTime(
            bookingUnit.getEndTime().toLocalTime(), day
        );

        restTimetableBookingsMockMvc.perform(get("/api/timetable-bookings?userId=" + user.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookingUnit.getClassUnit().getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(bookingUnit.getClassUnit().getTitle())))
            .andExpect(jsonPath("$.[*].start").value(hasItem(sameInstant(startDateTime))))
            .andExpect(jsonPath("$.[*].end").value(hasItem(sameInstant(endDateTime))))
            .andExpect(jsonPath("$.[*].type").value(hasItem(bookingUnit.getClassUnit().getClassType().getName())))
            .andExpect(jsonPath("$.[*].roomId").value(hasItem(bookingUnit.getRoom().getId().intValue())))
            .andExpect(jsonPath("$.[*].roomName").value(hasItem(bookingUnit.getRoom().getName())))
            .andExpect(jsonPath("$.[*].lecturerName").value(hasItem(lecturerName)));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void createCurrentUserBooking() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);

        int databaseSizeBeforeCreate = bookingUnitRepository.findAll().size();

        // Create the ScheduleUnit
        CreateBookingVM createBookingVM = getCreateBookingVMForTest();
        restTimetableBookingsMockMvc.perform(post("/api/timetable-bookings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(createBookingVM)))
            .andExpect(status().isCreated());

        // Validate the ScheduleUnit in the database
        List<BookingUnit> bookingUnitList = bookingUnitRepository.findAll();
        assertThat(bookingUnitList).hasSize(databaseSizeBeforeCreate + 1);

        BookingUnit testBookingUnit = bookingUnitList.get(bookingUnitList.size() - 1);
        assertThat(testBookingUnit.getClassUnit().getId()).isEqualTo(createBookingVM.getClassUnitId());

        LocalTime startTime = createBookingVM.getStartTime();
        assertThat(testBookingUnit.getStartTime().toLocalTime()).isEqualTo(startTime);

        LocalTime endTime = createBookingVM.getEndTime();
        assertThat(testBookingUnit.getEndTime().toLocalTime()).isEqualTo(endTime);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void createOtherUserBooking() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);

        int databaseSizeBeforeCreate = bookingUnitRepository.findAll().size();

        // Create the ScheduleUnit
        CreateBookingVM createBookingVM = getCreateBookingVMForTest();
        createBookingVM.setUserId(classUnit.getUserExt().getId());

        restTimetableBookingsMockMvc.perform(post("/api/timetable-bookings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(createBookingVM)))
            .andExpect(status().isCreated());

        // Validate the ScheduleUnit in the database
        List<BookingUnit> bookingUnitList = bookingUnitRepository.findAll();
        assertThat(bookingUnitList).hasSize(databaseSizeBeforeCreate + 1);

        BookingUnit testBookingUnit = bookingUnitList.get(bookingUnitList.size() - 1);
        assertThat(testBookingUnit.getClassUnit().getId()).isEqualTo(createBookingVM.getClassUnitId());
        assertThat(testBookingUnit.getClassUnit().getUserExt().getId()).isEqualTo(classUnit.getUserExt().getId());

        LocalTime startTime = createBookingVM.getStartTime();
        assertThat(testBookingUnit.getStartTime().toLocalTime()).isEqualTo(startTime);

        LocalTime endTime = createBookingVM.getEndTime();
        assertThat(testBookingUnit.getEndTime().toLocalTime()).isEqualTo(endTime);
    }

    private CreateBookingVM getCreateBookingVMForTest() {
        CreateBookingVM createBookingVM = new CreateBookingVM();
        createBookingVM.setUserId(classUnit.getUserExt().getId());
        createBookingVM.setClassUnitId(classUnit.getId());
        createBookingVM.setStartTime(LocalTime.of(10,10));
        createBookingVM.setEndTime(LocalTime.of(11,11));
        createBookingVM.setDay(DayOfWeek.MON);
        createBookingVM.setWeek(WeekType.A);
        createBookingVM.setSemesterHalf(SemesterHalf.HALF1);
        createBookingVM.setRoomId(classUnit.getRooms().iterator().next().getId());

        return createBookingVM;
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void checkClassUnitIdIsRequiredToCreate() throws Exception {
        int databaseSizeBeforeTest = bookingUnitRepository.findAll().size();
        // set the field null
        CreateBookingVM createBookingVM = getCreateBookingVMForTest();
        createBookingVM.setClassUnitId(null);

        // Create the BookingUnit, which fails.

        restTimetableBookingsMockMvc.perform(post("/api/timetable-bookings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(createBookingVM)))
            .andExpect(status().isBadRequest());

        List<BookingUnit> bookingUnitList = bookingUnitRepository.findAll();
        assertThat(bookingUnitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void checkStartTimeIsRequiredToCreate() throws Exception {
        int databaseSizeBeforeTest = bookingUnitRepository.findAll().size();
        // set the field null
        CreateBookingVM createBookingVM = getCreateBookingVMForTest();
        createBookingVM.setStartTime(null);

        // Create the BookingUnit, which fails.

        restTimetableBookingsMockMvc.perform(post("/api/timetable-bookings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(createBookingVM)))
            .andExpect(status().isBadRequest());

        List<BookingUnit> bookingUnitList = bookingUnitRepository.findAll();
        assertThat(bookingUnitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void checkEndTimeIsRequiredToCreate() throws Exception {
        int databaseSizeBeforeTest = bookingUnitRepository.findAll().size();
        // set the field null
        CreateBookingVM createBookingVM = getCreateBookingVMForTest();
        createBookingVM.setEndTime(null);

        // Create the BookingUnit, which fails.

        restTimetableBookingsMockMvc.perform(post("/api/timetable-bookings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(createBookingVM)))
            .andExpect(status().isBadRequest());

        List<BookingUnit> bookingUnitList = bookingUnitRepository.findAll();
        assertThat(bookingUnitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void checkRoomIdIsRequiredToCreate() throws Exception {
        int databaseSizeBeforeTest = bookingUnitRepository.findAll().size();
        // set the field null
        CreateBookingVM createBookingVM = getCreateBookingVMForTest();
        createBookingVM.setRoomId(null);

        // Create the BookingUnit, which fails.

        restTimetableBookingsMockMvc.perform(post("/api/timetable-bookings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(createBookingVM)))
            .andExpect(status().isBadRequest());

        List<BookingUnit> bookingUnitList = bookingUnitRepository.findAll();
        assertThat(bookingUnitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void checkDayIsRequiredToCreate() throws Exception {
        int databaseSizeBeforeTest = bookingUnitRepository.findAll().size();
        // set the field null
        CreateBookingVM createBookingVM = getCreateBookingVMForTest();
        createBookingVM.setDay(null);

        // Create the BookingUnit, which fails.

        restTimetableBookingsMockMvc.perform(post("/api/timetable-bookings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(createBookingVM)))
            .andExpect(status().isBadRequest());

        List<BookingUnit> bookingUnitList = bookingUnitRepository.findAll();
        assertThat(bookingUnitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void checkWeekIsRequiredToCreate() throws Exception {
        int databaseSizeBeforeTest = bookingUnitRepository.findAll().size();
        // set the field null
        CreateBookingVM createBookingVM = getCreateBookingVMForTest();
        createBookingVM.setWeek(null);

        // Create the BookingUnit, which fails.

        restTimetableBookingsMockMvc.perform(post("/api/timetable-bookings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(createBookingVM)))
            .andExpect(status().isBadRequest());

        List<BookingUnit> bookingUnitList = bookingUnitRepository.findAll();
        assertThat(bookingUnitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void checkSemesterHalfIsRequiredToCreate() throws Exception {
        int databaseSizeBeforeTest = bookingUnitRepository.findAll().size();
        // set the field null
        CreateBookingVM createBookingVM = getCreateBookingVMForTest();
        createBookingVM.setSemesterHalf(null);

        // Create the BookingUnit, which fails.

        restTimetableBookingsMockMvc.perform(post("/api/timetable-bookings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(createBookingVM)))
            .andExpect(status().isBadRequest());

        List<BookingUnit> bookingUnitList = bookingUnitRepository.findAll();
        assertThat(bookingUnitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void updateCurrentUserBooking() throws Exception {
        // Initialize the database
        classUnit.setOnlySemesterHalf(true);
        classUnit.setFrequency(ClassFrequency.EVERY_TWO_WEEKS);
        classUnitRepository.saveAndFlush(classUnit);
        bookingUnitRepository.saveAndFlush(bookingUnit);
        int databaseSizeBeforeUpdate = bookingUnitRepository.findAll().size();

        UpdateBookingVM updateBookingVM = getUpdateBookingVMForTest();
        updateBookingVM.setUserId(classUnit.getUserExt().getId());

        restTimetableBookingsMockMvc.perform(put("/api/timetable-bookings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updateBookingVM)))
            .andExpect(status().isOk());

        List<BookingUnit> bookingUnitList = bookingUnitRepository.findAll();
        assertThat(bookingUnitList).hasSize(databaseSizeBeforeUpdate);

        BookingUnit testBookingUnit = bookingUnitList.get(bookingUnitList.size() - 1);

        assertThat(testBookingUnit.getStartTime().toLocalTime()).isEqualTo(updateBookingVM.getStartTime());
        assertThat(testBookingUnit.getEndTime().toLocalTime()).isEqualTo(updateBookingVM.getEndTime());
        assertThat(testBookingUnit.getDay()).isEqualTo(updateBookingVM.getDay());
        assertThat(testBookingUnit.getWeek()).isEqualTo(updateBookingVM.getWeek());
        assertThat(testBookingUnit.getSemesterHalf()).isEqualTo(updateBookingVM.getSemesterHalf());
    }

    private UpdateBookingVM getUpdateBookingVMForTest() {
        UpdateBookingVM updateBookingVM = new UpdateBookingVM();
        updateBookingVM.setUserId(classUnit.getUserExt().getId());
        updateBookingVM.setClassUnitId(classUnit.getId());
        updateBookingVM.setStartTime(LocalTime.of(12,12));
        updateBookingVM.setEndTime(LocalTime.of(14,14));
        updateBookingVM.setDay(DayOfWeek.TUE);
        updateBookingVM.setWeek(WeekType.B);
        updateBookingVM.setSemesterHalf(SemesterHalf.HALF2);

        return updateBookingVM;
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void updateOtherUserBooking() throws Exception {
        // Initialize the database
        classUnit.setOnlySemesterHalf(true);
        classUnit.setFrequency(ClassFrequency.EVERY_TWO_WEEKS);
        classUnitRepository.saveAndFlush(classUnit);
        bookingUnitRepository.saveAndFlush(bookingUnit);
        int databaseSizeBeforeUpdate = bookingUnitRepository.findAll().size();

        UpdateBookingVM updateBookingVM = getUpdateBookingVMForTest();

        restTimetableBookingsMockMvc.perform(put("/api/timetable-bookings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updateBookingVM)))
            .andExpect(status().isOk());

        List<BookingUnit> bookingUnitList = bookingUnitRepository.findAll();
        assertThat(bookingUnitList).hasSize(databaseSizeBeforeUpdate);

        BookingUnit testBookingUnit = bookingUnitList.get(bookingUnitList.size() - 1);

        assertThat(testBookingUnit.getStartTime().toLocalTime()).isEqualTo(updateBookingVM.getStartTime());
        assertThat(testBookingUnit.getEndTime().toLocalTime()).isEqualTo(updateBookingVM.getEndTime());
        assertThat(testBookingUnit.getDay()).isEqualTo(updateBookingVM.getDay());
        assertThat(testBookingUnit.getWeek()).isEqualTo(updateBookingVM.getWeek());
        assertThat(testBookingUnit.getSemesterHalf()).isEqualTo(updateBookingVM.getSemesterHalf());
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void checkClassUnitIdIsRequiredToUpdate() throws Exception {
        int databaseSizeBeforeTest = bookingUnitRepository.findAll().size();
        // set the field null
        UpdateBookingVM updateBookingVM = getUpdateBookingVMForTest();
        updateBookingVM.setClassUnitId(null);

        // Update the BookingUnit, which fails.

        restTimetableBookingsMockMvc.perform(put("/api/timetable-bookings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updateBookingVM)))
            .andExpect(status().isBadRequest());

        List<BookingUnit> bookingUnitList = bookingUnitRepository.findAll();
        assertThat(bookingUnitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void checkStartTimeIsRequiredToUpdate() throws Exception {
        int databaseSizeBeforeTest = bookingUnitRepository.findAll().size();
        // set the field null
        UpdateBookingVM updateBookingVM = getUpdateBookingVMForTest();
        updateBookingVM.setStartTime(null);

        // Update the BookingUnit, which fails.

        restTimetableBookingsMockMvc.perform(put("/api/timetable-bookings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updateBookingVM)))
            .andExpect(status().isBadRequest());

        List<BookingUnit> bookingUnitList = bookingUnitRepository.findAll();
        assertThat(bookingUnitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void checkEndTimeIsRequiredToUpdate() throws Exception {
        int databaseSizeBeforeTest = bookingUnitRepository.findAll().size();
        // set the field null
        UpdateBookingVM updateBookingVM = getUpdateBookingVMForTest();
        updateBookingVM.setEndTime(null);

        // Update the BookingUnit, which fails.

        restTimetableBookingsMockMvc.perform(put("/api/timetable-bookings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updateBookingVM)))
            .andExpect(status().isBadRequest());

        List<BookingUnit> bookingUnitList = bookingUnitRepository.findAll();
        assertThat(bookingUnitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void checkDayIsRequiredToUpdate() throws Exception {
        int databaseSizeBeforeTest = bookingUnitRepository.findAll().size();
        // set the field null
        UpdateBookingVM updateBookingVM = getUpdateBookingVMForTest();
        updateBookingVM.setDay(null);

        // Update the BookingUnit, which fails.

        restTimetableBookingsMockMvc.perform(put("/api/timetable-bookings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updateBookingVM)))
            .andExpect(status().isBadRequest());

        List<BookingUnit> bookingUnitList = bookingUnitRepository.findAll();
        assertThat(bookingUnitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void checkWeekIsRequiredToUpdate() throws Exception {
        int databaseSizeBeforeTest = bookingUnitRepository.findAll().size();
        // set the field null
        UpdateBookingVM updateBookingVM = getUpdateBookingVMForTest();
        updateBookingVM.setWeek(null);

        // Update the BookingUnit, which fails.

        restTimetableBookingsMockMvc.perform(put("/api/timetable-bookings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updateBookingVM)))
            .andExpect(status().isBadRequest());

        List<BookingUnit> bookingUnitList = bookingUnitRepository.findAll();
        assertThat(bookingUnitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void checkSemesterHalfIsRequiredToUpdate() throws Exception {
        int databaseSizeBeforeTest = bookingUnitRepository.findAll().size();
        // set the field null
        UpdateBookingVM updateBookingVM = getUpdateBookingVMForTest();
        updateBookingVM.setSemesterHalf(null);

        // Update the BookingUnit, which fails.

        restTimetableBookingsMockMvc.perform(put("/api/timetable-bookings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updateBookingVM)))
            .andExpect(status().isBadRequest());

        List<BookingUnit> bookingUnitList = bookingUnitRepository.findAll();
        assertThat(bookingUnitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void deleteCurrentUserBooking() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);
        BookingUnit savedBookingUnit = bookingUnitRepository.saveAndFlush(bookingUnit);

        Long classUnitId = savedBookingUnit.getClassUnit().getId();

        // Delete the BookingUnit
        restTimetableBookingsMockMvc.perform(delete("/api/timetable-bookings/{classUnitId}", classUnitId)
            .param("userId", classUnit.getUserExt().getId().toString())
            .accept(TestUtil.APPLICATION_JSON_UTF8)
        ).andExpect(status().isOk());

        // Validate the database not contain selected booking units
        List<BookingUnit> bookingUnitList = bookingUnitRepository.findAll().stream()
            .filter(bookingUnit -> bookingUnit.getClassUnit().getId().equals(classUnitId))
            .collect(Collectors.toList());

        assertThat(bookingUnitList).hasSize(0);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void deleteOtherUserBooking() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);
        BookingUnit savedBookingUnit = bookingUnitRepository.saveAndFlush(bookingUnit);

        Long classUnitId = savedBookingUnit.getClassUnit().getId();
        UserExt user = classUnit.getUserExt();

        // Delete the BookingUnit
        restTimetableBookingsMockMvc.perform(delete("/api/timetable-bookings/{classUnitId}?userId={userId}",
            classUnitId, user.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database not contain selected booking units
        List<BookingUnit> bookingUnitList = bookingUnitRepository.findAll().stream()
            .filter(bookingUnit -> bookingUnit.getClassUnit().getId().equals(classUnitId))
            .collect(Collectors.toList());

        assertThat(bookingUnitList).hasSize(0);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void lockScheduleUnits() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);
        bookingUnitRepository.saveAndFlush(bookingUnit);

        Long classUnitId = classUnit.getId();
        Long userId = classUnit.getUserExt().getId();

        LockBookingVM lockBookingVM = new LockBookingVM();
        lockBookingVM.setClassUnitId(classUnitId);
        lockBookingVM.setUserId(userId);

        // Lock the bookingUnit
        restTimetableBookingsMockMvc.perform(put("/api/timetable-bookings/lock")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(lockBookingVM)))
            .andExpect(status().isOk());

        // Validate the database not contain selected booking units
        List<BookingUnit> bookingUnitList = bookingUnitRepository.findAll().stream()
            .filter(bookingUnit -> bookingUnit.getClassUnit().getId().equals(classUnitId))
            .filter(bookingUnit -> !bookingUnit.isLocked())
            .collect(Collectors.toList());

        assertThat(bookingUnitList).hasSize(0);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void unlockScheduleUnits() throws Exception {
        // Initialize the database
        bookingUnit.setLocked(true);

        classUnitRepository.saveAndFlush(classUnit);
        bookingUnitRepository.saveAndFlush(bookingUnit);

        Long classUnitId = classUnit.getId();
        Long userId = classUnit.getUserExt().getId();

        LockBookingVM lockBookingVM = new LockBookingVM();
        lockBookingVM.setClassUnitId(classUnitId);
        lockBookingVM.setUserId(userId);
        lockBookingVM.setLock(false);

        // Get the bookingUnit
        restTimetableBookingsMockMvc.perform(put("/api/timetable-bookings/lock")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(lockBookingVM)))
            .andExpect(status().isOk());

        // Validate the database not contain selected booking units
        List<BookingUnit> bookingUnitList = bookingUnitRepository.findAll().stream()
            .filter(bookingUnit -> bookingUnit.getClassUnit().getId().equals(classUnitId))
            .filter(BookingUnit::isLocked)
            .collect(Collectors.toList());

        assertThat(bookingUnitList).hasSize(0);
    }
}
