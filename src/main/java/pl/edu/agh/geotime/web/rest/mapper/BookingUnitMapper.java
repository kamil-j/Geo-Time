package pl.edu.agh.geotime.web.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.edu.agh.geotime.domain.BookingUnit;
import pl.edu.agh.geotime.service.util.DateUtil;
import pl.edu.agh.geotime.web.rest.dto.BookingUnitDTO;

import java.time.LocalTime;
import java.time.ZonedDateTime;

@Mapper(componentModel = "spring", uses = {ClassUnitMapper.class, RoomMapper.class})
public interface BookingUnitMapper extends EntityMapper<BookingUnitDTO, BookingUnit> {

    @Mapping(source = "classUnit.id", target = "classUnitId")
    @Mapping(source = "classUnit.title", target = "classUnitTitle")
    @Mapping(source = "room.id", target = "roomId")
    @Mapping(source = "room.name", target = "roomName")
    BookingUnitDTO toDto(BookingUnit bookingUnit);

    @Mapping(source = "classUnitId", target = "classUnit")
    @Mapping(source = "roomId", target = "room")
    BookingUnit toEntity(BookingUnitDTO bookingUnitDTO);

    default LocalTime map(ZonedDateTime zonedDateTime) {
        return zonedDateTime != null ? zonedDateTime.toLocalTime() : null;
    }

    default ZonedDateTime map(LocalTime localTime) {
        return DateUtil.convertToZonedDateTime(localTime);
    }

    default BookingUnit fromId(Long id) {
        if (id == null) {
            return null;
        }
        BookingUnit bookingUnit = new BookingUnit();
        bookingUnit.setId(id);
        return bookingUnit;
    }
}
