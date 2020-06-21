package pl.edu.agh.geotime.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.geotime.domain.SchedulingTimeFrame;
import pl.edu.agh.geotime.domain.Semester;
import pl.edu.agh.geotime.domain.UserExt;
import pl.edu.agh.geotime.repository.SchedulingTimeFrameRepository;
import pl.edu.agh.geotime.service.helper.UserHelper;

import java.time.ZonedDateTime;
import java.util.Optional;

@Service
public class SchedulingTimeFrameInfoService {

    private final SchedulingTimeFrameRepository schedulingTimeFrameRepository;
    private final SemesterService semesterService;
    private final UserHelper userHelper;

    @Autowired
    public SchedulingTimeFrameInfoService(SchedulingTimeFrameRepository schedulingTimeFrameRepository,
                                          SemesterService semesterService, UserHelper userHelper) {
        this.schedulingTimeFrameRepository = schedulingTimeFrameRepository;
        this.semesterService = semesterService;
        this.userHelper = userHelper;
    }

    public Optional<SchedulingTimeFrame> getUserSchedulingTimeFrames(Long userId) {
        UserExt user = userHelper.getActionUser(userId);
        return getUserSchedulingTimeFrames(user);
    }

    private Optional<SchedulingTimeFrame> getUserSchedulingTimeFrames(UserExt user) {
        Optional<Semester> currentSemester = semesterService.getCurrentSemester();
        if(!currentSemester.isPresent()) {
            return Optional.empty();
        }

        return schedulingTimeFrameRepository.findByUserGroupAndSubdepartmentAndSemester(
            user.getUserGroup(), user.getSubdepartment(), currentSemester.get());
    }

    public boolean canUserModifySchedule(UserExt user) {
        Optional<Semester> currentSemester = semesterService.getCurrentSemester();
        if(!currentSemester.isPresent()) {
            return false;
        }

        Optional<SchedulingTimeFrame> schedulingTimeFrameOptional = schedulingTimeFrameRepository
            .findByUserGroupAndSubdepartmentAndSemester(user.getUserGroup(), user.getSubdepartment(), currentSemester.get());
        if(!schedulingTimeFrameOptional.isPresent()){
            return true;
        }

        SchedulingTimeFrame schedulingTimeFrame = schedulingTimeFrameOptional.get();
        ZonedDateTime currentDateTime = ZonedDateTime.now();

        return !currentDateTime.isBefore(schedulingTimeFrame.getStartTime())
            && !currentDateTime.isAfter(schedulingTimeFrame.getEndTime());
    }
}
