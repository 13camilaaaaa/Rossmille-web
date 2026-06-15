package com.rossmille.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank
    private String id;

    @NotBlank
    private String cargo;

    @NotBlank
    private String contrasena;
}
