package pl.edu.agh.geotime.web.rest;

import com.codahale.metrics.annotation.Timed;
import pl.edu.agh.geotime.domain.BookingUnit;
import pl.edu.agh.geotime.service.BookingUnitService;
import pl.edu.agh.geotime.web.rest.mapper.BookingUnitMapper;
import pl.edu.agh.geotime.web.rest.errors.BadRequestAlertException;
import pl.edu.agh.geotime.web.rest.util.HeaderUtil;
import pl.edu.agh.geotime.web.rest.util.PaginationUtil;
import pl.edu.agh.geotime.web.rest.dto.BookingUnitDTO;
import pl.edu.agh.geotime.service.criteria.BookingUnitCriteria;
import pl.edu.agh.geotime.service.query.BookingUnitQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/booking-units")
public class BookingUnitResource {

    private static final Logger log = LoggerFactory.getLogger(BookingUnitResource.class);
    private static final String ENTITY_NAME = "bookingUnit";

    private final BookingUnitService bookingUnitService;
    private final BookingUnitMapper bookingUnitMapper;
    private final BookingUnitQueryService bookingUnitQueryService;

    BookingUnitResource(BookingUnitService bookingUnitService, BookingUnitMapper bookingUnitMapper,
                        BookingUnitQueryService bookingUnitQueryService) {
        this.bookingUnitService = bookingUnitService;
        this.bookingUnitMapper = bookingUnitMapper;
        this.bookingUnitQueryService = bookingUnitQueryService;
    }

    @PostMapping
    @Timed
    public ResponseEntity<BookingUnitDTO> createBookingUnit(@Valid @RequestBody BookingUnitDTO bookingUnitDTO) throws URISyntaxException {
        log.debug("REST request to save BookingUnit : {}", bookingUnitDTO);
        if (bookingUnitDTO.getId() != null) {
            throw new BadRequestAlertException("A new bookingUnit cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BookingUnit result = bookingUnitService.save(bookingUnitMapper.toEntity(bookingUnitDTO));
        return ResponseEntity.created(new URI("/api/booking-units/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(bookingUnitMapper.toDto(result));
    }

    @PutMapping
    @Timed
    public ResponseEntity<BookingUnitDTO> updateBookingUnit(@Valid @RequestBody BookingUnitDTO bookingUnitDTO) throws URISyntaxException {
        log.debug("REST request to update BookingUnit : {}", bookingUnitDTO);
        if (bookingUnitDTO.getId() == null) {
            return createBookingUnit(bookingUnitDTO);
        }
        BookingUnit result = bookingUnitService.save(bookingUnitMapper.toEntity(bookingUnitDTO));
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, bookingUnitDTO.getId().toString()))
            .body(bookingUnitMapper.toDto(result));
    }

    @GetMapping
    @Timed
    public ResponseEntity<List<BookingUnitDTO>> getAllBookingUnits(BookingUnitCriteria criteria, Pageable pageable) {
        log.debug("REST request to get BookingUnits by criteria: {}", criteria);
        Page<BookingUnitDTO> page = bookingUnitQueryService.findByCriteria(criteria, pageable)
            .map(bookingUnitMapper::toDto);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/booking-units");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Timed
    public ResponseEntity<BookingUnitDTO> getBookingUnit(@PathVariable Long id) {
        log.debug("REST request to get BookingUnit : {}", id);
        Optional<BookingUnitDTO> bookingUnitDTO = bookingUnitService.findById(id)
            .map(bookingUnitMapper::toDto);
        return ResponseUtil.wrapOrNotFound(bookingUnitDTO);
    }

    @DeleteMapping("/{id}")
    @Timed
    public ResponseEntity<Void> deleteBookingUnit(@PathVariable Long id) {
        log.debug("REST request to delete BookingUnit : {}", id);
        bookingUnitService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
