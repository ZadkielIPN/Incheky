package org.handel.incheki.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginNfcRequest {

    @NotBlank(message = "El UID NFC no puede estar vacío")
    private String uidNfc;
}
