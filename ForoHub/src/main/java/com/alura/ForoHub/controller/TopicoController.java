package com.alura.ForoHub.controller;

import com.alura.ForoHub.domain.topico.*;
import com.alura.ForoHub.domain.usuario.Usuario;
import com.alura.ForoHub.infra.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


@RestController
@RequestMapping("/topicos")
public class TopicoController {
    @Autowired
    private TopicoRepository repository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TopicoService topicoService;

    @GetMapping
    public ResponseEntity<Page<TopicoResponse>> listarTopicos(
            @PageableDefault(size = 10, sort = {"fechaDeCreacion"}) Pageable paginacion
    ) {
        return ResponseEntity.ok(topicoService.obtenerTopicos(paginacion));
    }

    @PostMapping
    public ResponseEntity<TopicoResponse> crear(
            @RequestBody @Valid TopicoRequest request,
            @AuthenticationPrincipal Usuario usuario,
            UriComponentsBuilder uriBuilder
    ) {
        TopicoResponse response = topicoService.crearTopico(request, usuario);

        var uri = uriBuilder.path("/topicos/{id}").buildAndExpand(response.id()).toUri();

        return ResponseEntity.created(uri).body(response);
    }
}
