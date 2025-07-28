package com.alura.ForoHub.domain.usuario;

import jakarta.validation.constraints.NotBlank;

public record DatosAutenticacion(
        String username,
        String contrasena
) {
}
