package com.alura.ForoHub.domain.topico;

import java.time.LocalDateTime;

public record DatosListarTopicos(
        String titulo,
        String mensaje,
        LocalDateTime fechaDeCreacion,
        Boolean status,
        String autorUsername,
        String curso
) {
    public DatosListarTopicos(Topico topico) {
        this(
                topico.getTitulo(),
                topico.getMensaje(),
                topico.getFechaDeCreacion(),
                topico.getStatus(),
                topico.getAutor().getUsername(),
                topico.getCurso()
        );
    }
}
