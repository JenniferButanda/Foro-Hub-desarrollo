package com.alura.ForoHub.infra;

import com.alura.ForoHub.domain.usuario.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.List;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        var tokenJWT = recuperarToken(request);
//
//        if(tokenJWT != null) {
//            var subject = tokenService.getSubject(tokenJWT);
//            var usuario = usuarioRepository.findByUsername(subject);
//
//            var authorities = usuario.getAuthorities() == null ?
//                    List.of(new SimpleGrantedAuthority("ROLE_USER")) :
//                    usuario.getAuthorities();
//
//            var authentication = new UsernamePasswordAuthenticationToken(
//                    usuario,
//                    null,
//                    authorities);
//
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
//
//        filterChain.doFilter(request, response);
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("=== INICIO FILTRO ===");
        System.out.println("URL: " + request.getRequestURL());
        System.out.println("Método: " + request.getMethod());

        var tokenJWT = recuperarToken(request);
        System.out.println("Token obtenido: " + (tokenJWT != null ? "*****" : "NULL"));

        if (tokenJWT != null) {
            try {
                System.out.println("Validando token...");
                var subject = tokenService.getSubject(tokenJWT);
                System.out.println("Subject del token: " + subject);

                var usuario = usuarioRepository.findByUsername(subject);
                System.out.println("Usuario encontrado: " + (usuario != null ? usuario.getUsername() : "NULL"));

                SecurityContextHolder.clearContext();

                var authorities = usuario.getAuthorities() != null ?
                        usuario.getAuthorities() :
                        List.of(new SimpleGrantedAuthority("ROLE_USER"));

                System.out.println("Authorities: " + authorities);

                var authentication = new UsernamePasswordAuthenticationToken(
                        usuario,
                        null,
                        authorities
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("Autenticación establecida: " + SecurityContextHolder.getContext().getAuthentication());

            } catch (Exception e) {
                System.err.println("ERROR en filtro: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: " + e.getMessage());
                return;
            }
        }

        filterChain.doFilter(request, response);
        System.out.println("=== FIN FILTRO ===");
    }

    private String recuperarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeader != null) {
            System.out.println("Header Authorization: " + authorizationHeader);
            return authorizationHeader.replace("Bearer ", "").trim();
        }
        return null;
    }
}