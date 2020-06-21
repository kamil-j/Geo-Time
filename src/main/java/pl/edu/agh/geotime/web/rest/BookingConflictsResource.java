package pl.edu.agh.geotime.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.geotime.domain.UserExt;
import pl.edu.agh.geotime.domain.enumeration.ClassFrequency;
import pl.edu.agh.geotime.domain.enumeration.SemesterHalf;
import pl.edu.agh.geotime.domain.enumeration.WeekType;
import pl.edu.agh.geotime.service.helper.UserHelper;
import pl.edu.agh.geotime.web.rest.helper.BookingConflictsHelper;
import pl.edu.agh.geotime.web.rest.vm.AcademicUnitBookingConflictVM;
import pl.edu.agh.geotime.web.rest.vm.RoomBookingConflictVM;
import pl.edu.agh.geotime.web.rest.vm.UserBookingConflictVM;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/booking-conflicts/")
public class BookingConflictsResource {

    private static final Logger log = LoggerFactory.getLogger(BookingConflictsResource.class);

    private final BookingConflictsHelper bookingConflictsHelper;
    private final UserHelper userHelper;

    @Autowired
    public BookingConflictsResource(BookingConflictsHelper bookingConflictsHelper, UserHelper userHelper) {
        this.bookingConflictsHelper = bookingConflictsHelper;
        this.userHelper = userHelper;

    }

    @GetMapping("/room")
    @Timed
    public ResponseEntity<List<RoomBookingConflictVM>> getRoomBookingConflicts(@RequestParam SemesterHalf semesterHalf,
                                                                               @RequestParam WeekType weekType,
                                                                               @RequestParam ClassFrequency frequency,
                                                                               @RequestParam(required = false) Long roomId,
                                                                               @RequestParam Long userId) {
        log.debug("REST request to get info about room booking conflicts - roomId: {}, userId: {}", roomId, userId);
        UserExt user = userHelper.getActionUser(userId);

        List<RoomBookingConflictVM> roomBookingConflicts = bookingConflictsHelper.getRoomBookingConflicts(roomId, user,
            semesterHalf, weekType, frequency);

        return new ResponseEntity<>(roomBookingConflicts, HttpStatus.OK);
    }

    @GetMapping("/academicUnit")
    @Timed
    public ResponseEntity<List<AcademicUnitBookingConflictVM>> getAcademicUnitBookingConflicts(@RequestParam SemesterHalf semesterHalf,
                                                                                               @RequestParam WeekType weekType,
                                                                                               @RequestParam ClassFrequency frequency,
                                                                                               @RequestParam(required = false) Long classUnitId,
                                                                                               @RequestParam Long userId) {
        log.debug("REST request to get info about academic unit booking conflicts - classUnitId: {}, userId: {}",
            classUnitId, userId);
        UserExt user = userHelper.getActionUser(userId);

        List<AcademicUnitBookingConflictVM> academicUnitBookingConflicts = bookingConflictsHelper.getAcademicUnitBookingConflicts(
            classUnitId, user, semesterHalf, weekType, frequency);

        return new ResponseEntity<>(academicUnitBookingConflicts, HttpStatus.OK);
    }

    @GetMapping("/user")
    @Timed
    public ResponseEntity<List<UserBookingConflictVM>> getUserBookingConflicts(@RequestParam SemesterHalf semesterHalf,
                                                                               @RequestParam WeekType weekType,
                                                                               @RequestParam ClassFrequency frequency,
                                                                               @RequestParam(required = false) Set<Long> userIds,
                                                                               @RequestParam Long userId) {
        log.debug("REST request to get info about user booking conflicts - userIds: {}, userId: {}", userIds, userId);
        UserExt user = userHelper.getActionUser(userId);

        List<UserBookingConflictVM> userBookingConflicts = bookingConflictsHelper.getUserBookingConflicts(userIds, user,
            semesterHalf, weekType, frequency);

        return new ResponseEntity<>(userBookingConflicts, HttpStatus.OK);
    }
}
