package pl.edu.agh.geotime.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.geotime.domain.Location;
import pl.edu.agh.geotime.service.LocationService;
import pl.edu.agh.geotime.web.rest.dto.LocationDTO;
import pl.edu.agh.geotime.web.rest.mapper.LocationMapper;
import pl.edu.agh.geotime.web.rest.errors.BadRequestAlertException;
import pl.edu.agh.geotime.web.rest.util.HeaderUtil;
import pl.edu.agh.geotime.web.rest.util.PaginationUtil;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/locations")
public class LocationResource {

    private static final String ENTITY_NAME = "location";

    private final Logger log = LoggerFactory.getLogger(LocationResource.class);
    private final LocationService locationService;
    private final LocationMapper locationMapper;

    LocationResource(LocationService locationService, LocationMapper locationMapper) {
        this.locationService = locationService;
        this.locationMapper = locationMapper;
    }

    @PostMapping
    @Timed
    public ResponseEntity<LocationDTO> createLocation(@Valid @RequestBody LocationDTO locationDTO) throws URISyntaxException {
        log.debug("REST request to save Location: {}", locationDTO);
        if (locationDTO.getId() != null) {
            throw new BadRequestAlertException("A new location cannot already have an ID", ENTITY_NAME, "idexists");
        }

        Location result = locationService.save(locationMapper.toEntity(locationDTO));
        return ResponseEntity.created(new URI("/api/locations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(locationMapper.toDto(result));
    }

    @PutMapping
    @Timed
    public ResponseEntity<LocationDTO> updateLocation(@Valid @RequestBody LocationDTO locationDTO) throws URISyntaxException {
        log.debug("REST request to update Location: {}", locationDTO);
        if (locationDTO.getId() == null) {
            return createLocation(locationDTO);
        }

        Location result = locationService.save(locationMapper.toEntity(locationDTO));
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, result.getId().toString()))
            .body(locationMapper.toDto(result));
    }

    @GetMapping
    @Timed
    public ResponseEntity<List<LocationDTO>> getAllLocations(Pageable pageable) {
        log.debug("REST request to get a page of Locations");
        Page<LocationDTO> page = locationService.findAll(pageable)
            .map(locationMapper::toDto);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/locations");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Timed
    public ResponseEntity<LocationDTO> getLocation(@PathVariable Long id) {
        log.debug("REST request to get Location : {}", id);
        Optional<LocationDTO> location = locationService.findById(id)
            .map(locationMapper::toDto);
        return ResponseUtil.wrapOrNotFound(location);
    }

    @DeleteMapping("/{id}")
    @Timed
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        log.debug("REST request to delete Location : {}", id);
        locationService.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
