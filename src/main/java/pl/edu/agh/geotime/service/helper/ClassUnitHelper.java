package pl.edu.agh.geotime.service.helper;

import org.springframework.stereotype.Component;
import pl.edu.agh.geotime.domain.ClassUnit;
import pl.edu.agh.geotime.domain.enumeration.ClassFrequency;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@Component
public class ClassUnitHelper {

    public void fillOnlyHalfSemesterInd(Set<ClassUnit> classUnits) {
        classUnits.forEach(classUnit -> classUnit.setOnlySemesterHalf(isOnlySemesterHalf(classUnit)));
    }

    public void fillOnlyHalfSemesterInd(ClassUnit classUnit) {
        if(classUnit.getFrequency().equals(ClassFrequency.SINGLE)) {
            classUnit.setOnlySemesterHalf(true);
            return;
        }
        classUnit.setOnlySemesterHalf(isOnlySemesterHalf(classUnit));
    }

    private boolean isOnlySemesterHalf(ClassUnit classUnit) {
        ZonedDateTime semesterStartDate = classUnit.getSemester().getStartDate();
        ZonedDateTime semesterEndDate = classUnit.getSemester().getEndDate();
        long semesterWeeksCount = ChronoUnit.WEEKS.between(semesterStartDate, semesterEndDate);

        ClassFrequency frequency = classUnit.getFrequency();
        Integer hoursQuantity = classUnit.getHoursQuantity();
        Integer duration = classUnit.getDuration();
        long classesCount = (long)Math.ceil((hoursQuantity * 60) / (double)duration);

        long weeksNeeded = semesterWeeksCount;
        if(frequency.equals(ClassFrequency.EVERY_WEEK)) {
            weeksNeeded = classesCount;
        } else if(frequency.equals(ClassFrequency.EVERY_TWO_WEEKS)) {
            weeksNeeded = classesCount * 2;
        } else if(frequency.equals(ClassFrequency.EVERY_MONTH)) {
            weeksNeeded = classesCount * 4;
        } else if(frequency.equals(ClassFrequency.EVERY_DAY)) {
            weeksNeeded = (int)Math.ceil((double)classesCount / 7);
        }

        return weeksNeeded <= (semesterWeeksCount/2);
    }
}
