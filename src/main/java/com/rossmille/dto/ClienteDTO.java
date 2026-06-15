package com.rossmille.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteDTO {

    private String id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String correo;
    private String telefono;
    private String direccion;
}
