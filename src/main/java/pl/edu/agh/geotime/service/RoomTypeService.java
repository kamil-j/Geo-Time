package pl.edu.agh.geotime.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.domain.RoomType;
import pl.edu.agh.geotime.repository.RoomTypeRepository;

import java.util.Optional;

@Service
@Transactional
public class RoomTypeService {

    private final Logger log = LoggerFactory.getLogger(RoomTypeService.class);
    private final RoomTypeRepository roomTypeRepository;

    public RoomTypeService(RoomTypeRepository roomTypeRepository) {
        this.roomTypeRepository = roomTypeRepository;
    }

    public RoomType save(RoomType roomType) {
        log.debug("Request to save RoomType: {}", roomType);
        return roomTypeRepository.save(roomType);
    }

    @Transactional(readOnly = true)
    public Page<RoomType> findAll(Pageable pageable) {
        log.debug("Request to get all RoomTypes");
        return roomTypeRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<RoomType> findById(Long id) {
        log.debug("Request to get RoomType by id: {}", id);
        return roomTypeRepository.findById(id);
    }

    public void deleteById(Long id) {
        log.debug("Request to delete RoomType by id: {}", id);
        roomTypeRepository.delete(id);
    }
}
