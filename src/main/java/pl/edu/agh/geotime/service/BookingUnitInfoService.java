package pl.edu.agh.geotime.service;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.domain.BookingUnit;
import pl.edu.agh.geotime.domain.Semester;
import pl.edu.agh.geotime.domain.UserExt;
import pl.edu.agh.geotime.domain.enumeration.SemesterHalf;
import pl.edu.agh.geotime.domain.enumeration.WeekType;
import pl.edu.agh.geotime.service.criteria.BookingUnitCriteria;
import pl.edu.agh.geotime.service.dto.AcademicUnitGroupDTO;
import pl.edu.agh.geotime.service.query.BookingUnitQueryService;
import pl.edu.agh.geotime.service.util.BookingUnitQueryUtil;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingUnitInfoService {

    private static final Logger log = LoggerFactory.getLogger(BookingUnitInfoService.class);

    private final BookingUnitQueryService bookingUnitQueryService;
    private final SemesterService semesterService;

    public BookingUnitInfoService(BookingUnitQueryService bookingUnitQueryService, SemesterService semesterService) {
        this.bookingUnitQueryService = bookingUnitQueryService;
        this.semesterService = semesterService;
    }

    public List<BookingUnit> getRoomsBookings(Set<Long> roomIds) {
        log.debug("Request to get current semester rooms bookings - roomIds: {}", roomIds);

        BookingUnitCriteria criteria = createSearchCommonCriteria();
        if(criteria == null) {
            return Collections.emptyList();
        }

        criteria.setRoomIdIn(Lists.newArrayList(roomIds));

        return bookingUnitQueryService.findByCriteria(criteria);
    }

    public List<BookingUnit> getUserBookings(UserExt user, SemesterHalf semesterHalf, WeekType weekType) {
        log.debug("Request to get all current semester bookings by user: {}, semesterHalf: {} and weekType: {}",
            user, semesterHalf, weekType);

        BookingUnitCriteria criteria = createSearchCommonCriteria();
        if(criteria == null) {
            return Collections.emptyList();
        }
        criteria.setUserIdEquals(user.getId());
        criteria.setSemesterHalfEquals(semesterHalf);
        criteria.setWeekTypeEquals(weekType);

        return bookingUnitQueryService.findByCriteria(criteria);
    }

    public List<BookingUnit> getAcademicUnitBookings(Set<AcademicUnitGroupDTO> academicUnitGroups) {
        log.debug("Request to get all current semester academic unit bookings  - academicUnitGroups: {}", academicUnitGroups);

        BookingUnitCriteria criteria = createSearchCommonCriteria();
        if(criteria == null) {
            return Collections.emptyList();
        }

        Map<Long, List<String>> academicUnitQueryMap = BookingUnitQueryUtil.createAcademicUnitQueryMap(academicUnitGroups);

        return bookingUnitQueryService.findByCriteriaAndAcademicUnitIdIn(criteria, academicUnitQueryMap.keySet())
            .stream()
            .filter(bookingUnit -> BookingUnitQueryUtil.filterResultsByQueryMap(bookingUnit, academicUnitQueryMap))
            .collect(Collectors.toList());
    }

    public List<BookingUnit> getUsersBookings(Set<Long> userIds) {
        log.debug("Request to get all current semester users bookings  - userIds: {}", userIds);

        BookingUnitCriteria criteria = createSearchCommonCriteria();
        if(criteria == null) {
            return Collections.emptyList();
        }

        return bookingUnitQueryService.findByCriteriaAndUserIdIn(criteria, userIds);
    }

    private BookingUnitCriteria createSearchCommonCriteria() {
        Optional<Semester> currentSemester = semesterService.getCurrentSemester();
        if (!currentSemester.isPresent()) {
            return null;
        }

        BookingUnitCriteria criteria = new BookingUnitCriteria();
        criteria.setSemesterIdEquals(currentSemester.get().getId());

        return criteria;
    }
}
