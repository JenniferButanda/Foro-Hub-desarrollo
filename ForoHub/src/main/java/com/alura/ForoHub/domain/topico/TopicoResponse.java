package com.alura.ForoHub.domain.topico;

import java.time.LocalDateTime;

public record TopicoResponse(
        Long id,
        String titulo,
        String mensaje,
        LocalDateTime fechaDeCreacion,
        StatusTopico status,
        Long autorId,
        String autorUsername,
        String curso
) {}
