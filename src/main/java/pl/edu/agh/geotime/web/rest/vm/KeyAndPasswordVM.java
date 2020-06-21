package pl.edu.agh.geotime.web.rest.vm;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class KeyAndPasswordVM implements Serializable {

    private String key;
    private String newPassword;
}
