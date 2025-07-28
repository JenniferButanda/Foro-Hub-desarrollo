package com.alura.ForoHub.domain.topico;

import com.alura.ForoHub.domain.usuario.Usuario;

import java.time.LocalDateTime;

public record DatosDetalleTopico(
        Long id,
        String titulo,
        String mensaje,
        LocalDateTime fechaDeCreacion,
        Boolean status,
        String autorUsername,
        String curso
) {
    public DatosDetalleTopico(Topico topico, Usuario autor) {
        this(
                topico.getId(),
                topico.getTitulo(),
                topico.getMensaje(),
                topico.getFechaDeCreacion(),
                topico.getStatus(),
                autor.getUsername(),
                topico.getCurso()
        );
    }
}
