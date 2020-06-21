package pl.edu.agh.geotime.web.rest.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.geotime.domain.BookingUnit;
import pl.edu.agh.geotime.domain.ClassUnit;
import pl.edu.agh.geotime.domain.Semester;
import pl.edu.agh.geotime.domain.UserExt;
import pl.edu.agh.geotime.domain.enumeration.ClassFrequency;
import pl.edu.agh.geotime.domain.enumeration.SemesterHalf;
import pl.edu.agh.geotime.domain.enumeration.WeekType;
import pl.edu.agh.geotime.service.SchedulingTimeFrameInfoService;
import pl.edu.agh.geotime.web.rest.errors.BadRequestAlertException;
import pl.edu.agh.geotime.web.rest.vm.CreateBookingVM;
import pl.edu.agh.geotime.web.rest.vm.UpdateBookingVM;

import java.time.LocalTime;

@Service
public class TimetableValidator {

    private static final String ENTITY_NAME = "bookingUnit";
    private static final String NOT_ALLOWED = "notallowed";
    private final SchedulingTimeFrameInfoService schedulingTimeFrameInfoService;

    @Autowired
    public TimetableValidator(SchedulingTimeFrameInfoService schedulingTimeFrameInfoService) {
        this.schedulingTimeFrameInfoService = schedulingTimeFrameInfoService;
    }

    public void validateCreateBookingRequest(CreateBookingVM createBookingVM, ClassUnit classUnit, UserExt user, boolean hasFullAccess) {
        validateSemester(classUnit.getSemester());
        validateSemesterHalf(createBookingVM.getSemesterHalf(), classUnit.isOnlySemesterHalf());
        validateWeekType(createBookingVM.getWeek(), classUnit.getFrequency());

        if(!hasFullAccess) {
            validateSchedulingTimeFrame(user);
        }

        LocalTime startTime = createBookingVM.getStartTime();
        LocalTime endTime = createBookingVM.getStartTime();
        if(startTime != null && endTime != null) {
            validateBookingTime(startTime, endTime);
        }
    }

    public void validateUpdateBookingRequest(UpdateBookingVM updateBookingVM, BookingUnit bookingUnit, UserExt user, boolean hasFullAccess) {
        ClassUnit classUnit = bookingUnit.getClassUnit();
        validateSemester(classUnit.getSemester());
        validateSemesterHalf(updateBookingVM.getSemesterHalf(), classUnit.isOnlySemesterHalf());
        validateWeekType(updateBookingVM.getWeek(), classUnit.getFrequency());

        if(!hasFullAccess) {
            validateSchedulingTimeFrame(user);
            validateLockValue(bookingUnit.isLocked());
        }

        LocalTime startDate = updateBookingVM.getStartTime();
        LocalTime endDate = updateBookingVM.getEndTime();

        if(startDate != null && endDate != null) {
            validateBookingTime(startDate, endDate);
        }
    }

    public void validateDeleteBookingRequest(BookingUnit bookingUnit, UserExt user, boolean hasFullAccess) {
        ClassUnit classUnit = bookingUnit.getClassUnit();
        validateSemester(classUnit.getSemester());

        if(!hasFullAccess) {
            validateSchedulingTimeFrame(user);
            validateLockValue(bookingUnit.isLocked());
        }
    }

    private void validateSemesterHalf(SemesterHalf semesterHalf, boolean onlySemesterHalfInd) {
        if(semesterHalf.equals(SemesterHalf.HALF2) && !onlySemesterHalfInd){
            throw new BadRequestAlertException("Entity BookingUnit cannot be scheduled for only half of semester!",
                ENTITY_NAME, NOT_ALLOWED);
        }
    }

    private void validateWeekType(WeekType weekType, ClassFrequency classFrequency) {
        if(weekType.equals(WeekType.B) && !classFrequency.equals(ClassFrequency.EVERY_TWO_WEEKS)){
            throw new BadRequestAlertException("Entity BookingUnit cannot be scheduled for provided week type!",
                ENTITY_NAME, NOT_ALLOWED);
        }
    }

    private void validateLockValue(Boolean isLocked) {
        if(isLocked != null && isLocked){
            throw new BadRequestAlertException("Entity BookingUnit is locked! Cannot be modified by this user.",
                ENTITY_NAME, NOT_ALLOWED);
        }
    }

    private void validateSemester(Semester semester) {
        if(!semester.isActive()){
            throw new BadRequestAlertException("Cannot modify booking for non-active semester!",
                ENTITY_NAME, NOT_ALLOWED);
        }
    }

    private void validateSchedulingTimeFrame(UserExt user) {
        if(!schedulingTimeFrameInfoService.canUserModifySchedule(user)) {
            throw new BadRequestAlertException("User cannot modify bookings at this moment because of scheduling time frame",
                ENTITY_NAME, "timeFrameError");
        }
    }

    private void validateBookingTime(LocalTime startTime, LocalTime endTime) {
        if(endTime.isBefore(startTime)) {
            throw new BadRequestAlertException("Cannot add booking unit with start date after end date",
                ENTITY_NAME, "semesterEnded");
        }
    }
}
