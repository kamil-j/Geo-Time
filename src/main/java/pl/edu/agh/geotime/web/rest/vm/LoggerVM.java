package pl.edu.agh.geotime.web.rest.vm;

import ch.qos.logback.classic.Logger;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class LoggerVM implements Serializable {

    private String name;
    private String level;

    public LoggerVM(Logger logger) {
        this.name = logger.getName();
        this.level = logger.getEffectiveLevel().toString();
    }
}
