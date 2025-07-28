package com.alura.ForoHub.domain.usuario;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerandoContrasena {
    public static void main(String[] args) {
        String passwordEnTextoPlano = "123456"; // <-- aquí escribes tu nueva contraseña
        String passwordEncriptada = new BCryptPasswordEncoder().encode(passwordEnTextoPlano);

        System.out.println("Contraseña en texto plano: " + passwordEnTextoPlano);
        System.out.println("Contraseña encriptada: " + passwordEncriptada);
    }
}
