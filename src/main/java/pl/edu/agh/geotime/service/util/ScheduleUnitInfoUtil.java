package pl.edu.agh.geotime.service.util;

import pl.edu.agh.geotime.domain.ClassUnitGroup;
import pl.edu.agh.geotime.domain.ScheduleUnit;
import pl.edu.agh.geotime.service.dto.ScheduleUnitInfoDTO;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ScheduleUnitInfoUtil {

    private ScheduleUnitInfoUtil() {
    }

    public static List<ScheduleUnitInfoDTO> combineScheduleUnitsWithSameClassUnitGroup(List<ScheduleUnit> scheduleUnits) {
        Map<Boolean, List<ScheduleUnit>> byClassUnitGroupExist = splitByClassUnitGroupExist(scheduleUnits);

        List<ScheduleUnitInfoDTO> result = byClassUnitGroupExist.get(false)
            .stream()
            .map(ScheduleUnitInfoDTO::new)
            .collect(Collectors.toList());

        if(byClassUnitGroupExist.get(true).isEmpty()) {
            return result;
        }

        splitByClassUnitGroup(byClassUnitGroupExist.get(true))
            .forEach((classUnitGroup, scheduleUnitsByClassUnitGroup) ->
                splitByStartTime(scheduleUnitsByClassUnitGroup)
                    .forEach((dateTime, scheduleUnitsByStartTime) ->
                        result.add(getCombinedScheduleUnitInfo(scheduleUnitsByStartTime)))
            );

        return result;
    }

    private static Map<Boolean, List<ScheduleUnit>> splitByClassUnitGroupExist(List<ScheduleUnit> scheduleUnits) {
        return scheduleUnits.stream()
            .collect(Collectors.partitioningBy(
                scheduleUnit -> scheduleUnit.getClassUnit().getClassUnitGroup() != null
            ));
    }

    private static Map<ClassUnitGroup, List<ScheduleUnit>> splitByClassUnitGroup(List<ScheduleUnit> scheduleUnits) {
        return scheduleUnits.stream()
            .collect(Collectors.groupingBy(
                scheduleUnit -> scheduleUnit.getClassUnit().getClassUnitGroup()
            ));
    }

    private static Map<ZonedDateTime, List<ScheduleUnit>> splitByStartTime(List<ScheduleUnit> scheduleUnits) {
        return scheduleUnits.stream()
            .collect(Collectors.groupingBy(
                ScheduleUnit::getStartDate
            ));
    }

    private static ScheduleUnitInfoDTO getCombinedScheduleUnitInfo(List<ScheduleUnit> scheduleUnits) {
        ScheduleUnitInfoDTO result = new ScheduleUnitInfoDTO(scheduleUnits.get(0));

        for(int i = 1; i < scheduleUnits.size(); i++) {
            ScheduleUnitInfoDTO scheduleUnitInfo = new ScheduleUnitInfoDTO(scheduleUnits.get(i));
            result.setLecturerName(result.getLecturerName() + ", " + scheduleUnitInfo.getLecturerName());
        }

        return result;
    }
}
