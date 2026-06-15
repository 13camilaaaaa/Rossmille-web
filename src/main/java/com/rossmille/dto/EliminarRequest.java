package com.rossmille.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EliminarRequest {

    @NotBlank(message = "La contrasena es obligatoria")
    private String contrasena;
}
