package pl.edu.agh.geotime.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.domain.Location;
import pl.edu.agh.geotime.repository.LocationRepository;
import pl.edu.agh.geotime.service.helper.UserHelper;

import java.util.Optional;

@Service
public class LocationService {

    private final Logger log = LoggerFactory.getLogger(LocationService.class);
    private final LocationRepository locationRepository;
    private final UserHelper userHelper;

    public LocationService(LocationRepository locationRepository, UserHelper userHelper) {
        this.locationRepository = locationRepository;
        this.userHelper = userHelper;
    }

    public Location save(Location location) {
        log.debug("Request to save Location: {}", location);
        location.setDepartment(userHelper.getCurrentUserDepartment());
        location = locationRepository.save(location);
        return location;
    }

    @Transactional(readOnly = true)
    public Page<Location> findAll(Pageable pageable) {
        log.debug("Request to get all locations");
        Long departmentId = userHelper.getCurrentUserDepartmentId();
        return locationRepository.findAllByDepartmentId(pageable, departmentId);
    }

    @Transactional(readOnly = true)
    public Optional<Location> findById(Long id) {
        log.debug("Request to get location by id: {}", id);
        Long departmentId = userHelper.getCurrentUserDepartmentId();
        return locationRepository.findByIdAndDepartmentId(id, departmentId);
    }

    public void deleteById(Long id) {
        log.debug("Request to delete location by id: {}", id);
        Long departmentId = userHelper.getCurrentUserDepartmentId();
        locationRepository.deleteByIdAndDepartmentId(id, departmentId);
    }
}
