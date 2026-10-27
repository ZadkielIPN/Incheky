package org.handel.incheki.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.handel.incheki.enums.Rol;
import org.handel.incheki.model.Administrador;
import org.handel.incheki.repository.AdministradorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InicializadorDatos implements CommandLineRunner {

    // Administrador
    private final AdministradorRepository administradorRepository;

    @Override
    public void run(String... args) throws Exception {
        inicializarAdministradores();
    }

    public void inicializarAdministradores() {
        if (administradorRepository.count() == 0) {
            List<Administrador> administradoresIniciales = List.of(
                    Administrador.builder()
                            .nombre("Zadkiel").apellido("Barron Dominguez")
                            .email("ZadkielBD@outlook.com")
                            .contrasena("ContraseñaSegura123")
                            .rol(Rol.ADMINISTRADOR)
                            .numeroAdministrador(1L)
                            .build(),
                    Administrador.builder()
                            .nombre("Leonardo")
                            .apellido("Gonzalez Rendón")
                            .email("LeonardoGR@handel.com")
                            .contrasena("HandelAppointlyLGR")
                            .rol(Rol.ADMINISTRADOR)
                            .numeroAdministrador(2L)
                            .build()
            );

            administradorRepository.saveAll(administradoresIniciales);
            log.info("Base de Datos Inicializada: Administradores iniciadas");
        }
    }
}
