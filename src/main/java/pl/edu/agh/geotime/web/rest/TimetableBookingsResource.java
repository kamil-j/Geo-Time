package pl.edu.agh.geotime.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.geotime.domain.UserExt;
import pl.edu.agh.geotime.domain.enumeration.SemesterHalf;
import pl.edu.agh.geotime.domain.enumeration.WeekType;
import pl.edu.agh.geotime.service.helper.UserHelper;
import pl.edu.agh.geotime.service.util.UserUtil;
import pl.edu.agh.geotime.web.rest.helper.BookingsHelper;
import pl.edu.agh.geotime.web.rest.util.HeaderUtil;
import pl.edu.agh.geotime.web.rest.vm.CreateBookingVM;
import pl.edu.agh.geotime.web.rest.vm.LockBookingVM;
import pl.edu.agh.geotime.web.rest.vm.TimetableBookingVM;
import pl.edu.agh.geotime.web.rest.vm.UpdateBookingVM;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/timetable-bookings")
public class TimetableBookingsResource {

    private static final Logger log = LoggerFactory.getLogger(TimetableBookingsResource.class);

    private final BookingsHelper bookingsHelper;
    private final UserHelper userHelper;

    @Autowired
    public TimetableBookingsResource(BookingsHelper bookingsHelper, UserHelper userHelper) {
        this.bookingsHelper = bookingsHelper;
        this.userHelper = userHelper;
    }

    @GetMapping
    @Timed
    public ResponseEntity<List<TimetableBookingVM>> getAllBookingUnits(@RequestParam Long userId,
                                                                       @RequestParam(required = false) SemesterHalf semesterHalf,
                                                                       @RequestParam(required = false) WeekType weekType) {
        log.debug("REST request to get all bookings - userId: {}", userId);
        UserExt user = userHelper.getActionUser(userId);

        List<TimetableBookingVM> bookings = bookingsHelper.getUserBookings(user, semesterHalf, weekType);

        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @PostMapping
    @Timed
    public ResponseEntity<TimetableBookingVM> createBooking(@Valid @RequestBody CreateBookingVM createBookingVM) throws URISyntaxException {
        log.debug("REST request to create booking for user - : {}", createBookingVM);
        UserExt user = userHelper.getCurrentUser();
        boolean hasFullAccess = UserUtil.isManager(user);
        if(!user.getId().equals(createBookingVM.getUserId())) {
            user = userHelper.getUserById(createBookingVM.getUserId());
        }

        TimetableBookingVM booking = bookingsHelper.createBooking(createBookingVM, user, hasFullAccess);

        return ResponseEntity.created(new URI("/api/booking-units/" + booking.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("bookingUnit", booking.getId().toString()))
            .body(booking);
    }

    @PutMapping
    @Timed
    public ResponseEntity<TimetableBookingVM> updateBooking(@Valid @RequestBody UpdateBookingVM updateBookingVM) {
        log.debug("REST request to update booking for user - : {}", updateBookingVM);
        UserExt user = userHelper.getCurrentUser();
        boolean hasFullAccess = UserUtil.isManager(user);
        if(!user.getId().equals(updateBookingVM.getUserId())) {
            user = userHelper.getUserById(updateBookingVM.getUserId());
        }

        TimetableBookingVM booking = bookingsHelper.updateBooking(updateBookingVM, user, hasFullAccess);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("bookingUnit", booking.getId().toString()))
            .body(booking);
    }

    @DeleteMapping("/{classUnitId}")
    @Timed
    public ResponseEntity<Void> deleteBooking(@PathVariable Long classUnitId, @RequestParam Long userId) {
        log.debug("REST request to delete booking by classUnitId: {}", classUnitId);
        UserExt user = userHelper.getCurrentUser();
        boolean hasFullAccess = UserUtil.isManager(user);
        if(!user.getId().equals(userId)) {
            user = userHelper.getUserById(userId);
        }

        bookingsHelper.deleteBooking(classUnitId, user, hasFullAccess);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityDeletionAlert("bookingUnit", classUnitId.toString())).build();
    }

    @PutMapping("/lock")
    @Timed
    public ResponseEntity<TimetableBookingVM> lockUserBooking(@Valid @RequestBody LockBookingVM lockBookingVM) {
        log.debug("REST request to lock user booking - lockBookingVM: {}", lockBookingVM);
        UserExt user = userHelper.getActionUser(lockBookingVM.getUserId());

        TimetableBookingVM booking = bookingsHelper.lockBooking(lockBookingVM, user);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("bookingUnit", lockBookingVM.getClassUnitId().toString()))
            .body(booking);
    }
}
