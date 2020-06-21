package pl.edu.agh.geotime.service.util;

import com.google.common.collect.Lists;
import pl.edu.agh.geotime.domain.BookingUnit;
import pl.edu.agh.geotime.domain.enumeration.AcademicUnitGroup;
import pl.edu.agh.geotime.service.dto.AcademicUnitGroupDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BookingUnitQueryUtil {

    private BookingUnitQueryUtil() {}

    public static Map<Long, List<String>> createAcademicUnitQueryMap(Set<AcademicUnitGroupDTO> academicUnitGroupsDTO) {
        Map<Long, List<String>> academicUnitQueryMap = new HashMap<>();

        for(AcademicUnitGroupDTO academicUnitGroupDTO : academicUnitGroupsDTO) {
            Long academicUnitId = academicUnitGroupDTO.getAcademicUnitId();

            String academicUnitGroupName = academicUnitGroupDTO.getName();
            if(academicUnitGroupName == null || academicUnitGroupName.isEmpty()) {
                academicUnitGroupName = AcademicUnitGroup.ALL_GROUPS;
            }

            if(academicUnitQueryMap.containsKey(academicUnitId)) {
                academicUnitQueryMap.get(academicUnitId).add(academicUnitGroupName);
            } else {
                academicUnitQueryMap.put(academicUnitId, Lists.newArrayList(academicUnitGroupName));
            }
        }

        return academicUnitQueryMap;
    }

    public static boolean filterResultsByQueryMap(BookingUnit bookingUnit, Map<Long, List<String>> academicUnitQueryMap) {
        Long academicUnitId = bookingUnit.getClassUnit().getAcademicUnit().getId();

        List<String> academicUnitQueryGroups = academicUnitQueryMap.get(academicUnitId);
        if(academicUnitQueryGroups == null) {
            return false;
        }

        AcademicUnitGroup academicUnitGroup = bookingUnit.getClassUnit().getAcademicUnitGroup();
        return academicUnitGroup == null || academicUnitQueryGroups.contains(AcademicUnitGroup.ALL_GROUPS)
            || academicUnitQueryGroups.contains(academicUnitGroup.name());
    }
}
