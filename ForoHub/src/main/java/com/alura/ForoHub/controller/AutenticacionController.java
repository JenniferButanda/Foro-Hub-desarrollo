package com.alura.ForoHub.controller;

import com.alura.ForoHub.domain.usuario.DatosAutenticacion;
import com.alura.ForoHub.domain.usuario.Usuario;
import com.alura.ForoHub.infra.DatosTokenJWT;
import com.alura.ForoHub.infra.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AutenticacionController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager manager;

    @PostMapping
    public ResponseEntity<DatosTokenJWT> iniciarSesion(@RequestBody @Valid DatosAutenticacion datos) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(datos.username(), datos.contrasena());
        var autenticacion = manager.authenticate(authenticationToken);

        //var usuario = (Usuario) manager.authenticate(...);
        var usuario = (Usuario) autenticacion.getPrincipal();
        var tokenJWT = tokenService.generarToken(usuario);

        return ResponseEntity.ok(new DatosTokenJWT(
                tokenJWT,
                usuario.getId(),
                usuario.getUsername()
        ));
    }
}
