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
import pl.edu.agh.geotime.domain.enumeration.*;
import pl.edu.agh.geotime.repository.BookingUnitRepository;
import pl.edu.agh.geotime.repository.ClassUnitRepository;
import pl.edu.agh.geotime.repository.DepartmentRepository;
import pl.edu.agh.geotime.repository.UserExtRepository;
import pl.edu.agh.geotime.service.SemesterService;
import pl.edu.agh.geotime.service.UserService;
import pl.edu.agh.geotime.service.helper.UserHelper;
import pl.edu.agh.geotime.web.rest.errors.ExceptionTranslator;
import pl.edu.agh.geotime.web.rest.helper.BookingConflictsHelper;
import pl.edu.agh.geotime.web.rest.util.TimetableBookingsUtil;

import javax.persistence.EntityManager;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashSet;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.edu.agh.geotime.web.rest.TestUtil.createFormattingConversionService;
import static pl.edu.agh.geotime.web.rest.TestUtil.sameInstant;

/**
 * Test class for the BookingConflicts REST controller.
 *
 * @see BookingConflictsResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GeoTimeApp.class)
public class BookingConflictsResourceIntTest {

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
    private BookingConflictsHelper bookingConflictsHelper;

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

    private MockMvc restBookingConflictsMockMvc;

    private ClassUnit classUnit;

    private BookingUnit bookingUnit;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final BookingConflictsResource bookingConflictsResource = new BookingConflictsResource(bookingConflictsHelper, userHelper);
        this.restBookingConflictsMockMvc = MockMvcBuilders.standaloneSetup(bookingConflictsResource)
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
        classUnit.setRooms(Collections.singleton(room));
        classUnit.setAcademicUnit(academicUnit);
        classUnit.setSubdepartment(user.getSubdepartment());

        bookingUnit.setClassUnit(classUnit);
        bookingUnit.setRoom(room);
    }


    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllCurrentUserRoomsBookingsConflicts() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);
        bookingUnitRepository.saveAndFlush(bookingUnit);

        ClassUnit classUnitForTest = getDifferentClassUnitForTest();
        classUnitRepository.saveAndFlush(classUnitForTest);

        BookingUnit bookingUnitForTest = getDifferentBookingUnitForTest(classUnitForTest);
        bookingUnitRepository.saveAndFlush(bookingUnitForTest);

        DayOfWeek day = bookingUnitForTest.getDay();
        ZonedDateTime startDateTime = TimetableBookingsUtil.convertToDateTime(
            bookingUnitForTest.getStartTime().toLocalTime(), day
        );
        ZonedDateTime endDateTime = TimetableBookingsUtil.convertToDateTime(
            bookingUnitForTest.getEndTime().toLocalTime(), day
        );

        // Get all the rooms booking conflicts
        restBookingConflictsMockMvc.perform(get("/api/booking-conflicts/room")
                .param("semesterHalf", SemesterHalf.HALF1.toString())
                .param("weekType", WeekType.A.toString())
                .param("frequency", ClassFrequency.EVERY_WEEK.toString())
                .param("userId", classUnit.getUserExt().getId().toString())
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].start").value(hasItem(sameInstant(startDateTime))))
            .andExpect(jsonPath("$.[*].end").value(hasItem(sameInstant(endDateTime))))
            .andExpect(jsonPath("$.[*].frequency").value(hasItem(classUnitForTest.getFrequency().toString())))
            .andExpect(jsonPath("$.[*].weekType").value(hasItem(bookingUnitForTest.getWeek().toString())))
            .andExpect(jsonPath("$.[*].roomId").value(hasItem(bookingUnitForTest.getRoom().getId().intValue())));
    }

    private ClassUnit getDifferentClassUnitForTest() {
        UserExt testUser = UserResourceIntTest.createEntity(em);
        em.persist(testUser);
        em.flush();

        ClassUnit classUnitForTest = new ClassUnit()
            .title("Title")
            .description("Description")
            .duration(90)
            .hoursQuantity(50)
            .frequency(ClassFrequency.EVERY_WEEK)
            .academicUnitGroup(AcademicUnitGroup.GROUP1)
            .onlySemesterHalf(false);

        classUnitForTest.setClassType(classUnit.getClassType());
        classUnitForTest.setSemester(classUnit.getSemester());
        classUnitForTest.setUserExt(testUser);
        classUnitForTest.setSubdepartment(testUser.getSubdepartment());
        classUnitForTest.setRooms(new HashSet<>(classUnit.getRooms()));
        classUnitForTest.setAcademicUnit(classUnit.getAcademicUnit());

        return classUnitForTest;
    }

    private BookingUnit getDifferentBookingUnitForTest(ClassUnit classUnitForTest) {
        BookingUnit bookingUnitForTest = BookingUnitResourceIntTest.createEntity(em);
        bookingUnitForTest.setClassUnit(classUnitForTest);
        bookingUnitForTest.setRoom(classUnitForTest.getRooms().iterator().next());
        return bookingUnitForTest;
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getCurrentUserRoomBookingsConflicts() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);
        bookingUnitRepository.saveAndFlush(bookingUnit);

        ClassUnit classUnitForTest = getDifferentClassUnitForTest();
        classUnitRepository.saveAndFlush(classUnitForTest);

        BookingUnit bookingUnitForTest = getDifferentBookingUnitForTest(classUnitForTest);
        bookingUnitRepository.saveAndFlush(bookingUnitForTest);

        Long roomId = bookingUnitForTest.getRoom().getId();
        DayOfWeek day = bookingUnitForTest.getDay();
        ZonedDateTime startDateTime = TimetableBookingsUtil.convertToDateTime(
            bookingUnitForTest.getStartTime().toLocalTime(), day
        );
        ZonedDateTime endDateTime = TimetableBookingsUtil.convertToDateTime(
            bookingUnitForTest.getEndTime().toLocalTime(), day
        );

        // Get all the classesList
        restBookingConflictsMockMvc.perform(get("/api/booking-conflicts/room")
                .param("roomId", roomId.toString())
                .param("semesterHalf", SemesterHalf.HALF1.toString())
                .param("weekType", WeekType.A.toString())
                .param("frequency", ClassFrequency.EVERY_WEEK.toString())
                .param("userId", classUnit.getUserExt().getId().toString())
            ).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].start").value(hasItem(sameInstant(startDateTime))))
            .andExpect(jsonPath("$.[*].end").value(hasItem(sameInstant(endDateTime))))
            .andExpect(jsonPath("$.[*].frequency").value(hasItem(classUnitForTest.getFrequency().toString())))
            .andExpect(jsonPath("$.[*].weekType").value(hasItem(bookingUnitForTest.getWeek().toString())))
            .andExpect(jsonPath("$.[*].roomId").value(hasItem(roomId.intValue())));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllOtherUserRoomsBookingsConflicts() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);
        bookingUnitRepository.saveAndFlush(bookingUnit);

        ClassUnit classUnitForTest = getDifferentClassUnitForTest();
        classUnitRepository.saveAndFlush(classUnitForTest);

        BookingUnit bookingUnitForTest = getDifferentBookingUnitForTest(classUnitForTest);
        bookingUnitRepository.saveAndFlush(bookingUnitForTest);

        DayOfWeek day = bookingUnitForTest.getDay();
        ZonedDateTime startDateTime = TimetableBookingsUtil.convertToDateTime(
            bookingUnitForTest.getStartTime().toLocalTime(), day
        );
        ZonedDateTime endDateTime = TimetableBookingsUtil.convertToDateTime(
            bookingUnitForTest.getEndTime().toLocalTime(), day
        );

        // Get all the rooms booking conflicts
        restBookingConflictsMockMvc.perform(get("/api/booking-conflicts/room")
                .param("userId", "1")
                .param("semesterHalf", SemesterHalf.HALF1.toString())
                .param("weekType", WeekType.A.toString())
                .param("frequency", ClassFrequency.EVERY_WEEK.toString())
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].start").value(hasItem(sameInstant(startDateTime))))
            .andExpect(jsonPath("$.[*].end").value(hasItem(sameInstant(endDateTime))))
            .andExpect(jsonPath("$.[*].frequency").value(hasItem(classUnitForTest.getFrequency().toString())))
            .andExpect(jsonPath("$.[*].weekType").value(hasItem(bookingUnitForTest.getWeek().toString())))
            .andExpect(jsonPath("$.[*].roomId").value(hasItem(bookingUnitForTest.getRoom().getId().intValue())));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getOtherUserRoomBookingsConflicts() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);
        bookingUnitRepository.saveAndFlush(bookingUnit);

        ClassUnit classUnitForTest = getDifferentClassUnitForTest();
        classUnitRepository.saveAndFlush(classUnitForTest);

        BookingUnit bookingUnitForTest = getDifferentBookingUnitForTest(classUnitForTest);
        bookingUnitRepository.saveAndFlush(bookingUnitForTest);

        Long roomId = bookingUnitForTest.getRoom().getId();
        DayOfWeek day = bookingUnitForTest.getDay();
        ZonedDateTime startDateTime = TimetableBookingsUtil.convertToDateTime(
            bookingUnitForTest.getStartTime().toLocalTime(), day
        );
        ZonedDateTime endDateTime = TimetableBookingsUtil.convertToDateTime(
            bookingUnitForTest.getEndTime().toLocalTime(), day
        );

        // Get all the classesList
        restBookingConflictsMockMvc.perform(get("/api/booking-conflicts/room")
            .param("roomId", roomId.toString())
            .param("userId", "1")
            .param("semesterHalf", SemesterHalf.HALF1.toString())
            .param("weekType", WeekType.A.toString())
            .param("frequency", ClassFrequency.EVERY_WEEK.toString())
        ).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].start").value(hasItem(sameInstant(startDateTime))))
            .andExpect(jsonPath("$.[*].end").value(hasItem(sameInstant(endDateTime))))
            .andExpect(jsonPath("$.[*].frequency").value(hasItem(classUnitForTest.getFrequency().toString())))
            .andExpect(jsonPath("$.[*].weekType").value(hasItem(bookingUnitForTest.getWeek().toString())))
            .andExpect(jsonPath("$.[*].roomId").value(hasItem(roomId.intValue())));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllCurrentUserAcademicUnitBookingsConflicts() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);
        bookingUnitRepository.saveAndFlush(bookingUnit);

        ClassUnit classUnitForTest = getDifferentClassUnitForTest();
        classUnitRepository.saveAndFlush(classUnitForTest);

        BookingUnit bookingUnitForTest = getDifferentBookingUnitForTest(classUnitForTest);
        bookingUnitRepository.saveAndFlush(bookingUnitForTest);

        DayOfWeek day = bookingUnitForTest.getDay();
        ZonedDateTime startDateTime = TimetableBookingsUtil.convertToDateTime(
            bookingUnitForTest.getStartTime().toLocalTime(), day
        );
        ZonedDateTime endDateTime = TimetableBookingsUtil.convertToDateTime(
            bookingUnitForTest.getEndTime().toLocalTime(), day
        );

        // Get all the classesList
        restBookingConflictsMockMvc.perform(get("/api/booking-conflicts/academicUnit")
                .param("semesterHalf", SemesterHalf.HALF1.toString())
                .param("weekType", WeekType.A.toString())
                .param("frequency", ClassFrequency.EVERY_WEEK.toString())
                .param("userId", classUnit.getUserExt().getId().toString())
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].start").value(hasItem(sameInstant(startDateTime))))
            .andExpect(jsonPath("$.[*].end").value(hasItem(sameInstant(endDateTime))))
            .andExpect(jsonPath("$.[*].frequency").value(hasItem(classUnitForTest.getFrequency().toString())))
            .andExpect(jsonPath("$.[*].weekType").value(hasItem(bookingUnitForTest.getWeek().toString())))
            .andExpect(jsonPath("$.[*].academicUnitId").value(hasItem(bookingUnitForTest.getClassUnit().getAcademicUnit().getId().intValue())));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getCurrentUserAcademicUnitBookingsConflictsByClassUnitId() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);

        ClassUnit classUnitForTest = getDifferentClassUnitForTest();
        classUnitRepository.saveAndFlush(classUnitForTest);

        BookingUnit bookingUnitForTest = getDifferentBookingUnitForTest(classUnitForTest);
        bookingUnitRepository.saveAndFlush(bookingUnitForTest);

        Long academicUnitId = bookingUnitForTest.getClassUnit().getAcademicUnit().getId();
        DayOfWeek day = bookingUnitForTest.getDay();
        ZonedDateTime startDateTime = TimetableBookingsUtil.convertToDateTime(
            bookingUnitForTest.getStartTime().toLocalTime(), day
        );
        ZonedDateTime endDateTime = TimetableBookingsUtil.convertToDateTime(
            bookingUnitForTest.getEndTime().toLocalTime(), day
        );

        // Get all the classesList
        restBookingConflictsMockMvc.perform(get("/api/booking-conflicts/academicUnit")
            .param("classUnitId", classUnit.getId().toString())
            .param("semesterHalf", SemesterHalf.HALF1.toString())
            .param("weekType", WeekType.A.toString())
            .param("frequency", ClassFrequency.EVERY_WEEK.toString())
            .param("userId", classUnit.getUserExt().getId().toString())
        ).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].start").value(hasItem(sameInstant(startDateTime))))
            .andExpect(jsonPath("$.[*].end").value(hasItem(sameInstant(endDateTime))))
            .andExpect(jsonPath("$.[*].frequency").value(hasItem(classUnitForTest.getFrequency().toString())))
            .andExpect(jsonPath("$.[*].weekType").value(hasItem(bookingUnitForTest.getWeek().toString())))
            .andExpect(jsonPath("$.[*].academicUnitId").value(hasItem(academicUnitId.intValue())));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllOtherUserAcademicUnitBookingsConflicts() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);
        bookingUnitRepository.saveAndFlush(bookingUnit);

        ClassUnit classUnitForTest = getDifferentClassUnitForTest();
        classUnitRepository.saveAndFlush(classUnitForTest);

        BookingUnit bookingUnitForTest = getDifferentBookingUnitForTest(classUnitForTest);
        bookingUnitRepository.saveAndFlush(bookingUnitForTest);

        DayOfWeek day = bookingUnitForTest.getDay();
        ZonedDateTime startDateTime = TimetableBookingsUtil.convertToDateTime(
            bookingUnitForTest.getStartTime().toLocalTime(), day
        );
        ZonedDateTime endDateTime = TimetableBookingsUtil.convertToDateTime(
            bookingUnitForTest.getEndTime().toLocalTime(), day
        );

        // Get all the classesList
        restBookingConflictsMockMvc.perform(get("/api/booking-conflicts/academicUnit", 1)
                .param("userId", "1")
                .param("semesterHalf", SemesterHalf.HALF1.toString())
                .param("weekType", WeekType.A.toString())
                .param("frequency", ClassFrequency.EVERY_WEEK.toString())
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].start").value(hasItem(sameInstant(startDateTime))))
            .andExpect(jsonPath("$.[*].end").value(hasItem(sameInstant(endDateTime))))
            .andExpect(jsonPath("$.[*].frequency").value(hasItem(classUnitForTest.getFrequency().toString())))
            .andExpect(jsonPath("$.[*].weekType").value(hasItem(bookingUnitForTest.getWeek().toString())))
            .andExpect(jsonPath("$.[*].academicUnitId").value(hasItem(bookingUnitForTest.getClassUnit().getAcademicUnit().getId().intValue())));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getOtherUserAcademicUnitReservedHoursByClassUnitId() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);

        ClassUnit classUnitForTest = getDifferentClassUnitForTest();
        classUnitRepository.saveAndFlush(classUnitForTest);

        BookingUnit bookingUnitForTest = getDifferentBookingUnitForTest(classUnitForTest);
        bookingUnitRepository.saveAndFlush(bookingUnitForTest);

        Long academicUnitId = bookingUnitForTest.getClassUnit().getAcademicUnit().getId();
        DayOfWeek day = bookingUnitForTest.getDay();
        ZonedDateTime startDateTime = TimetableBookingsUtil.convertToDateTime(
            bookingUnitForTest.getStartTime().toLocalTime(), day
        );
        ZonedDateTime endDateTime = TimetableBookingsUtil.convertToDateTime(
            bookingUnitForTest.getEndTime().toLocalTime(), day
        );

        // Get all the booking conflicts
        restBookingConflictsMockMvc.perform(get("/api/booking-conflicts/academicUnit")
            .param("classUnitId", classUnit.getId().toString())
            .param("userId", "1")
            .param("semesterHalf", SemesterHalf.HALF1.toString())
            .param("weekType", WeekType.A.toString())
            .param("frequency", ClassFrequency.EVERY_WEEK.toString())
        ).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].start").value(hasItem(sameInstant(startDateTime))))
            .andExpect(jsonPath("$.[*].end").value(hasItem(sameInstant(endDateTime))))
            .andExpect(jsonPath("$.[*].frequency").value(hasItem(classUnitForTest.getFrequency().toString())))
            .andExpect(jsonPath("$.[*].weekType").value(hasItem(bookingUnitForTest.getWeek().toString())))
            .andExpect(jsonPath("$.[*].academicUnitId").value(hasItem(academicUnitId.intValue())));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllCurrentUserUsersBookings() throws Exception {
        // Initialize the database
        ClassUnitGroup classUnitGroup = ClassUnitGroupResourceIntTest.createEntity(em);
        classUnitGroup.setDepartment(classUnit.getAcademicUnit().getStudyField().getDepartment());
        em.persist(classUnitGroup);
        em.flush();

        classUnit.setClassUnitGroup(classUnitGroup);
        classUnitRepository.saveAndFlush(classUnit);
        bookingUnitRepository.saveAndFlush(bookingUnit);

        ClassUnit classUnitForTest = getDifferentClassUnitForTest();
        classUnitForTest.setClassUnitGroup(classUnitGroup);
        classUnitRepository.saveAndFlush(classUnitForTest);

        BookingUnit bookingUnitForTest = getDifferentBookingUnitForTest(classUnitForTest);
        bookingUnitRepository.saveAndFlush(bookingUnitForTest);

        DayOfWeek day = bookingUnitForTest.getDay();
        ZonedDateTime startDateTime = TimetableBookingsUtil.convertToDateTime(
            bookingUnitForTest.getStartTime().toLocalTime(), day
        );
        ZonedDateTime endDateTime = TimetableBookingsUtil.convertToDateTime(
            bookingUnitForTest.getEndTime().toLocalTime(), day
        );

        // Get all the booking conflicts
        restBookingConflictsMockMvc.perform(get("/api/booking-conflicts/user")
                .param("semesterHalf", SemesterHalf.HALF1.toString())
                .param("weekType", WeekType.A.toString())
                .param("frequency", ClassFrequency.EVERY_WEEK.toString())
                .param("userId", classUnit.getUserExt().getId().toString())
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].start").value(hasItem(sameInstant(startDateTime))))
            .andExpect(jsonPath("$.[*].end").value(hasItem(sameInstant(endDateTime))))
            .andExpect(jsonPath("$.[*].frequency").value(hasItem(classUnitForTest.getFrequency().toString())))
            .andExpect(jsonPath("$.[*].weekType").value(hasItem(bookingUnitForTest.getWeek().toString())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(bookingUnitForTest.getClassUnit().getUserExt().getId().intValue())));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getCurrentUserUsersBookingsByUserId() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);
        bookingUnitRepository.saveAndFlush(bookingUnit);

        ClassUnit classUnitForTest = getDifferentClassUnitForTest();
        classUnitRepository.saveAndFlush(classUnitForTest);

        BookingUnit bookingUnitForTest = getDifferentBookingUnitForTest(classUnitForTest);
        bookingUnitRepository.saveAndFlush(bookingUnitForTest);

        Long userId = bookingUnitForTest.getClassUnit().getUserExt().getId();
        DayOfWeek day = bookingUnitForTest.getDay();
        ZonedDateTime startDateTime = TimetableBookingsUtil.convertToDateTime(
            bookingUnitForTest.getStartTime().toLocalTime(), day
        );
        ZonedDateTime endDateTime = TimetableBookingsUtil.convertToDateTime(
            bookingUnitForTest.getEndTime().toLocalTime(), day
        );

        // Get all the booking conflicts
        restBookingConflictsMockMvc.perform(get("/api/booking-conflicts/user")
            .param("userIds", userId.toString())
            .param("semesterHalf", SemesterHalf.HALF1.toString())
            .param("weekType", WeekType.A.toString())
            .param("frequency", ClassFrequency.EVERY_WEEK.toString())
            .param("userId", classUnit.getUserExt().getId().toString())
        ).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].start").value(hasItem(sameInstant(startDateTime))))
            .andExpect(jsonPath("$.[*].end").value(hasItem(sameInstant(endDateTime))))
            .andExpect(jsonPath("$.[*].frequency").value(hasItem(classUnitForTest.getFrequency().toString())))
            .andExpect(jsonPath("$.[*].weekType").value(hasItem(bookingUnitForTest.getWeek().toString())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(userId.intValue())));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllOtherUserUsersBookings() throws Exception {
        // Initialize the database
        ClassUnitGroup classUnitGroup = ClassUnitGroupResourceIntTest.createEntity(em);
        classUnitGroup.setDepartment(classUnit.getAcademicUnit().getStudyField().getDepartment());
        em.persist(classUnitGroup);
        em.flush();

        classUnit.setClassUnitGroup(classUnitGroup);
        classUnitRepository.saveAndFlush(classUnit);
        bookingUnitRepository.saveAndFlush(bookingUnit);

        ClassUnit classUnitForTest = getDifferentClassUnitForTest();
        classUnitForTest.setClassUnitGroup(classUnitGroup);
        classUnitRepository.saveAndFlush(classUnitForTest);

        BookingUnit bookingUnitForTest = getDifferentBookingUnitForTest(classUnitForTest);
        bookingUnitRepository.saveAndFlush(bookingUnitForTest);

        DayOfWeek day = bookingUnitForTest.getDay();
        ZonedDateTime startDateTime = TimetableBookingsUtil.convertToDateTime(
            bookingUnitForTest.getStartTime().toLocalTime(), day
        );
        ZonedDateTime endDateTime = TimetableBookingsUtil.convertToDateTime(
            bookingUnitForTest.getEndTime().toLocalTime(), day
        );

        // Get all the booking conflicts
        restBookingConflictsMockMvc.perform(get("/api/booking-conflicts/user")
                .param("userId", "1")
                .param("semesterHalf", SemesterHalf.HALF1.toString())
                .param("weekType", WeekType.A.toString())
                .param("frequency", ClassFrequency.EVERY_WEEK.toString())
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].start").value(hasItem(sameInstant(startDateTime))))
            .andExpect(jsonPath("$.[*].end").value(hasItem(sameInstant(endDateTime))))
            .andExpect(jsonPath("$.[*].frequency").value(hasItem(classUnitForTest.getFrequency().toString())))
            .andExpect(jsonPath("$.[*].weekType").value(hasItem(bookingUnitForTest.getWeek().toString())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(bookingUnitForTest.getClassUnit().getUserExt().getId().intValue())));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getUsersReservedHoursByUserId() throws Exception {
        // Initialize the database
        classUnitRepository.saveAndFlush(classUnit);
        bookingUnitRepository.saveAndFlush(bookingUnit);

        ClassUnit classUnitForTest = getDifferentClassUnitForTest();
        classUnitRepository.saveAndFlush(classUnitForTest);

        BookingUnit bookingUnitForTest = getDifferentBookingUnitForTest(classUnitForTest);
        bookingUnitRepository.saveAndFlush(bookingUnitForTest);

        Long userId = bookingUnitForTest.getClassUnit().getUserExt().getId();
        DayOfWeek day = bookingUnitForTest.getDay();
        ZonedDateTime startDateTime = TimetableBookingsUtil.convertToDateTime(
            bookingUnitForTest.getStartTime().toLocalTime(), day
        );
        ZonedDateTime endDateTime = TimetableBookingsUtil.convertToDateTime(
            bookingUnitForTest.getEndTime().toLocalTime(), day
        );

        // Get all the booking conflicts
        restBookingConflictsMockMvc.perform(get("/api/booking-conflicts/user", 1)
            .param("userIds", userId.toString())
            .param("userId", "1")
            .param("semesterHalf", SemesterHalf.HALF1.toString())
            .param("weekType", WeekType.A.toString())
            .param("frequency", ClassFrequency.EVERY_WEEK.toString())
        ).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].start").value(hasItem(sameInstant(startDateTime))))
            .andExpect(jsonPath("$.[*].end").value(hasItem(sameInstant(endDateTime))))
            .andExpect(jsonPath("$.[*].frequency").value(hasItem(classUnitForTest.getFrequency().toString())))
            .andExpect(jsonPath("$.[*].weekType").value(hasItem(bookingUnitForTest.getWeek().toString())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(userId.intValue())));
    }
}
