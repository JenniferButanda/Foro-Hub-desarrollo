package com.alura.ForoHub.domain.topico;

import com.alura.ForoHub.domain.usuario.Usuario;
import com.alura.ForoHub.domain.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TopicoService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TopicoRepository topicoRepository;

    public Page<TopicoResponse> obtenerTopicos(Pageable paginacion) {
        return topicoRepository.findAll(paginacion).map(this::toResponse);
    }

    public TopicoResponse crearTopico(TopicoRequest request, Usuario usuario) {
        Usuario autor = usuarioRepository.findById(request.autorId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Topico topico = new Topico(
                null,
                request.titulo(),
                request.mensaje(),
                LocalDateTime.now(),
                StatusTopico.ABIERTO,
                autor,
                request.curso()
        );

        Topico topicoGuardado = topicoRepository.save(topico);
        return toResponse(topicoGuardado);
    }

    public TopicoResponse toResponse(Topico topico) {
        return new TopicoResponse(
                topico.getId(),
                topico.getTitulo(),
                topico.getMensaje(),
                topico.getFechaDeCreacion(),
                topico.getStatus(),
                topico.getAutor().getId(),
                topico.getAutor().getUsername(),
                topico.getCurso()
        );
    }

    public List<TopicoResponse> toResponseList(List<Topico> topicos) {
        return topicos.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
