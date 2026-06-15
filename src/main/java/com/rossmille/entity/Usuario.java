package com.rossmille.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    @Id
    @Column(name = "id_usuario", length = 10)
    private String idUsuario;

    @Column(name = "nombre_usuario", nullable = false, length = 100)
    private String nombreUsuario;

    @Column(name = "rol_usuarios", nullable = false, length = 20)
    private String rolUsuarios;

    @Column(name = "correo_usuario", length = 100)
    private String correoUsuario;

    @Column(name = "telefono_usuario", length = 15)
    private String telefonoUsuario;

    @Column(name = "contrasena", nullable = false, length = 255)
    private String contrasena;
}
