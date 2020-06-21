package pl.edu.agh.geotime.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.domain.BookingUnit;
import pl.edu.agh.geotime.domain.ScheduleUnit;
import pl.edu.agh.geotime.domain.Semester;
import pl.edu.agh.geotime.domain.enumeration.ClassFrequency;
import pl.edu.agh.geotime.domain.enumeration.DayOfWeek;
import pl.edu.agh.geotime.domain.enumeration.SemesterHalf;
import pl.edu.agh.geotime.domain.enumeration.WeekType;
import pl.edu.agh.geotime.service.util.DateUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ScheduleService {

    private final Logger log = LoggerFactory.getLogger(ScheduleService.class);

    private final ScheduleUnitService scheduleUnitService;

    @Autowired
    public ScheduleService(ScheduleUnitService scheduleUnitService) {
        this.scheduleUnitService = scheduleUnitService;
    }

    @Async("taskExecutorWithContext")
    public void adjustSchedule(BookingUnit bookingUnit) {
        log.debug("Request to adjust schedule for booking: {}", bookingUnit);
        List<ScheduleUnit> newSchedule = generateNewScheduleFromBooking(bookingUnit);

        Long classUnitId = bookingUnit.getClassUnit().getId();
        Semester semester = bookingUnit.getClassUnit().getSemester();

        if(ZonedDateTime.now().isAfter(semester.getStartDate())) {
            ZonedDateTime nextMonday = ZonedDateTime.now()
                .with(TemporalAdjusters.next(DayOfWeek.MON.toDayOfWeek()))
                .withHour(0)
                .withMinute(0);

            scheduleUnitService.deleteByClassUnitIdAndStartDateAfter(classUnitId, nextMonday);

            newSchedule = newSchedule.stream()
                .filter(scheduleUnit -> scheduleUnit.getStartDate().isAfter(nextMonday))
                .collect(Collectors.toList());
        } else {
            scheduleUnitService.deleteByClassUnitId(classUnitId);
        }

        scheduleUnitService.save(newSchedule);
    }

    @Async("taskExecutorWithContext")
    public void removeClassUnitFromSchedule(Long classUnitId) {
        scheduleUnitService.deleteByClassUnitId(classUnitId);
    }

    private List<ScheduleUnit> generateNewScheduleFromBooking(BookingUnit bookingUnit) {
        List<ScheduleUnit> scheduleUnits = new ArrayList<>();
        Semester semester = bookingUnit.getClassUnit().getSemester();

        LocalTime startTime = bookingUnit.getStartTime().toLocalTime();
        LocalTime endTime = bookingUnit.getEndTime().toLocalTime();
        DayOfWeek day = bookingUnit.getDay();
        WeekType weekType = bookingUnit.getWeek();
        SemesterHalf semesterHalf = bookingUnit.getSemesterHalf();
        ClassFrequency frequency = bookingUnit.getClassUnit().getFrequency();
        Integer minutesToSchedule = bookingUnit.getClassUnit().getHoursQuantity() * 60;
        Integer duration = bookingUnit.getClassUnit().getDuration();
        Integer alreadyScheduled = 0;

        LocalDate localDate = getFirstSpecificDateForSemester(semester, day, weekType, semesterHalf);
        ZonedDateTime startDate = DateUtil.convertToZonedDateTime(localDate, startTime);
        ZonedDateTime endDate = DateUtil.convertToZonedDateTime(localDate, endTime);

        do {
            ScheduleUnit scheduleUnit = new ScheduleUnit();

            scheduleUnit.setClassUnit(bookingUnit.getClassUnit());
            scheduleUnit.setRoom(bookingUnit.getRoom());
            scheduleUnit.setStartDate(startDate);
            scheduleUnit.setEndDate(endDate);

            scheduleUnits.add(scheduleUnit);

            alreadyScheduled += duration;
            if(alreadyScheduled >= minutesToSchedule) {
                break;
            }

            Optional<ZonedDateTime> nextStartDate = frequency.getNextDate(startDate);
            Optional<ZonedDateTime> nextEndDate = frequency.getNextDate(endDate);
            if(!nextStartDate.isPresent() || !nextEndDate.isPresent()) {
                break;
            }

            startDate = nextStartDate.get();
            endDate = nextEndDate.get();

        } while(isBeforeSemesterEnd(startDate, endDate, semester.getEndDate()));

        return scheduleUnits;
    }

    private LocalDate getFirstSpecificDateForSemester(Semester semester, DayOfWeek day, WeekType weekType,
                                                      SemesterHalf semesterHalf) {
        ZonedDateTime startDate = semester.getStartDate();
        ZonedDateTime endDate = semester.getEndDate();

        LocalDate localDate = startDate.with(TemporalAdjusters.nextOrSame(day.toDayOfWeek())).toLocalDate();
        if(weekType.equals(WeekType.B)) {
            localDate = localDate.plusWeeks(1);
        }
        if(semesterHalf.equals(SemesterHalf.HALF2)) {
            long semesterWeeksCount = ChronoUnit.WEEKS.between(startDate, endDate);
            localDate = localDate.plusWeeks((long)Math.ceil((double)semesterWeeksCount / 2));
        }
        return localDate;
    }

    private Boolean isBeforeSemesterEnd(ZonedDateTime startDate, ZonedDateTime endDate, ZonedDateTime semesterEndDate) {
        return startDate.isBefore(semesterEndDate) && endDate.isBefore(semesterEndDate);
    }
}
