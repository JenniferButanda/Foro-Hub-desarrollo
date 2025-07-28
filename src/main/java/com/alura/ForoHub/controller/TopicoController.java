package com.alura.ForoHub.controller;

import com.alura.ForoHub.domain.topico.*;
import com.alura.ForoHub.domain.usuario.Usuario;
import com.alura.ForoHub.domain.usuario.UsuarioRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/topicos")
@SecurityRequirement(name = "bearer-key")
public class TopicoController {
    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    HttpServletRequest request;

    @PostMapping
    @Transactional
    public ResponseEntity registrar(
            @RequestBody @Valid DatosRegistroTopico datos,
            @AuthenticationPrincipal Usuario usuarioAutenticado,
            UriComponentsBuilder uriComponentsBuilder) {

        // Validar que el usuario esté autenticado
        if (usuarioAutenticado == null) {
            throw new AccessDeniedException("Usuario no autenticado");
        }

        // Validar duplicados (Regla de negocio)
        if (topicoRepository.existsByTituloAndMensaje(datos.titulo(), datos.mensaje())) {
            throw new DuplicateTopicException("Ya existe un tópico con este título y mensaje");
        }

        // Crear el nuevo tópico
        var topico = new Topico(datos, usuarioAutenticado);
        topicoRepository.save(topico);

        // Obtener el usuario COMPLETO para incluir su username
        Usuario autor = usuarioRepository.findById(usuarioAutenticado.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Construir la URI de respuesta
        var uri = uriComponentsBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();

        // Retornar respuesta con los datos detallados
        return ResponseEntity.created(uri).body(new DatosDetalleTopico(topico, autor));
    }

    @GetMapping
    public ResponseEntity<Page<DatosListarTopicos>> listar(
            @PageableDefault(page = 0, size = 10, sort = "fechaDeCreacion", direction = Sort.Direction.ASC )
            Pageable paginacion) {
        var page = topicoRepository.findAllByStatusTrueOrderByFechaDeCreacionAsc(paginacion).map(DatosListarTopicos::new);

        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DatosDetalleTopico> detallar(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario) {

        Topico topico = topicoRepository.findByIdAndStatusTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tópico no encontrado"));

        return ResponseEntity.ok(new DatosDetalleTopico(topico, usuario));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<DatosDetalleTopico> actualizar(
            @PathVariable Long id,
            @RequestBody @Valid DatosActualizacionTopico datos,
            @AuthenticationPrincipal Usuario usuario) {

        Optional<Topico> optionalTopico = topicoRepository.findById(id);

        if (!optionalTopico.isPresent()) {
            throw new ResourceNotFoundException("Tópico con ID " + id + " no encontrado");
        }

        Topico topico = optionalTopico.get();

        if (!topico.getStatus()) {
            throw new ResourceNotFoundException("No se puede actualizar un tópico inactivo");
        }

        if (!topico.getAutor().getId().equals(usuario.getId())) {
            throw new AccessDeniedException("No tienes permisos para editar este tópico");
        }

        topico.actualizarTopico(datos);

        Usuario autor = usuarioRepository.findById(topico.getAutor().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Autor no encontrado"));

        return ResponseEntity.ok(new DatosDetalleTopico(topico, autor));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity eliminar(@PathVariable Long id) {
        var topico = topicoRepository.getReferenceById(id);
        topico.eliminar();
        return ResponseEntity.noContent().build();
    }

}
