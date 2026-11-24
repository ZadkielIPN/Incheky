package org.handel.incheki.dtos.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TarjetaNfcRequest {

    @Size(max = 255, message = "El UID NFC no puede superar 255 caracteres")
    private String uidNfc;
}
