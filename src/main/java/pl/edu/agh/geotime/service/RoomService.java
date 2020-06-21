package pl.edu.agh.geotime.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.domain.Room;
import pl.edu.agh.geotime.repository.RoomRepository;
import pl.edu.agh.geotime.service.helper.UserHelper;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class RoomService {

    private final Logger log = LoggerFactory.getLogger(RoomService.class);
    private final DepartmentAccessService departmentAccessService;
    private final RoomRepository roomRepository;
    private final UserHelper userHelper;

    public RoomService(DepartmentAccessService departmentAccessService, RoomRepository roomRepository,
                       UserHelper userHelper) {
        this.departmentAccessService = departmentAccessService;
        this.roomRepository = roomRepository;
        this.userHelper = userHelper;
    }

    public Room save(Room room) {
        log.debug("Request to save Room: {}", room);
        departmentAccessService.checkAccess(room);
        return roomRepository.save(room);
    }

    @Transactional(readOnly = true)
    public Page<Room> findAll(Pageable pageable) {
        log.debug("Request to get all rooms");
        Long departmentId = userHelper.getCurrentUserDepartmentId();
        return roomRepository.findAllByLocation_Department_Id(pageable, departmentId);
    }

    @Transactional(readOnly = true)
    public Optional<Room> findById(Long id) {
        log.debug("Request to get room by id: {}", id);
        Long departmentId = userHelper.getCurrentUserDepartmentId();
        return roomRepository.findByIdAndLocation_Department_Id(id, departmentId);
    }

    @Transactional(readOnly = true)
    public List<Room> findByIdIn(Set<Long> ids) {
        log.debug("Request to get room by id in: {}", ids);
        Long departmentId = userHelper.getCurrentUserDepartmentId();
        return roomRepository.findByIdInAndLocation_Department_Id(ids, departmentId);
    }

    @Transactional(readOnly = true)
    public Optional<Room> findByName(String name) {
        log.debug("Request to get room by name: {}", name);
        Long departmentId = userHelper.getCurrentUserDepartmentId();
        return roomRepository.findByNameAndLocation_Department_Id(name, departmentId);
    }

    public void deleteById(Long id) {
        log.debug("Request to delete Room by id: {}", id);
        Long departmentId = userHelper.getCurrentUserDepartmentId();
        roomRepository.deleteByIdAndLocation_Department_Id(id, departmentId);
    }
}
