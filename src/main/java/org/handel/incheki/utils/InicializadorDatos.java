package org.handel.incheki.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.handel.incheki.enums.*;
import org.handel.incheki.model.*;
import org.handel.incheki.repository.*;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InicializadorDatos implements CommandLineRunner {

    private final AdministradorRepository administradorRepository;
    private final CajeroRepository cajeroRepository;
    private final ClienteRepository clienteRepository;
    private final ProveedorRepository proveedorRepository;

    @Override
    @Transactional
    public void run(String @NonNull ... args) {
        inicializarAdministradores();
        inicializarCajeros();
        inicializarClientes();
        inicializarProveedores();
        log.info("Inicialización de datos de Incheky finalizada.");
    }

    private void inicializarAdministradores() {
        if (administradorRepository.count() > 0) return;

        administradorRepository.saveAll(List.of(
                Administrador.builder()
                        .nombre("Zadkiel")
                        .apellido("Barrón Domínguez")
                        .email("zadkiel@incheky.com")
                        .contrasena("ZadkielIncheky#123")
                        .rol(Rol.ADMINISTRADOR)
                        .numeroAdministrador(1L)
                        .build(),
                Administrador.builder()
                        .nombre("Leonardo")
                        .apellido("Gonzalez Rendón")
                        .email("leonardo@incheky.com")
                        .contrasena("LeonardoIncheky#123")
                        .rol(Rol.ADMINISTRADOR)
                        .numeroAdministrador(2L)
                        .build(),
                Administrador.builder()
                        .nombre("Yuliana")
                        .apellido("Jiménez Linares")
                        .email("yuliana@incheky.com")
                        .contrasena("YulianaIncheky#123")
                        .rol(Rol.ADMINISTRADOR)
                        .numeroAdministrador(3L)
                        .build(),
                Administrador.builder()
                        .nombre("Joshua")
                        .apellido("Jiménez Campuzano")
                        .email("joshua@incheky.com")
                        .contrasena("JoshuaIncheky#123")
                        .rol(Rol.ADMINISTRADOR)
                        .numeroAdministrador(4L)
                        .build()
        ));

        log.info("Se crearon los administradores iniciales.");
    }

    private void inicializarCajeros() {
        if (cajeroRepository.count() > 0) return;

        cajeroRepository.saveAll(List.of(
                Cajero.builder()
                        .nombre("Mariana").apellido("Lopez Garcia")
                        .email("mariana.cajera@incheky.com")
                        .contrasena("CajeroIncheky#123")
                        .rol(Rol.CAJERO)
                        .numeroEmpleado(1L)
                        .turno(Turno.MATUTINO)
                        .cajaAsignada(1L)
                        .build(),
                Cajero.builder()
                        .nombre("Carlos").apellido("Hernandez Ruiz")
                        .email("carlos.cajero@incheky.com")
                        .contrasena("CajeroIncheky#123")
                        .rol(Rol.CAJERO)
                        .numeroEmpleado(2L)
                        .turno(Turno.VESPERTINO)
                        .cajaAsignada(2L)
                        .build(),
                Cajero.builder()
                        .nombre("Fernanda").apellido("Martinez Soto")
                        .email("fernanda.cajera@incheky.com")
                        .contrasena("CajeroIncheky#123")
                        .rol(Rol.CAJERO)
                        .numeroEmpleado(3L)
                        .turno(Turno.NOCTURNO)
                        .cajaAsignada(3L)
                        .build()
        ));

        log.info("Se crearon los cajeros iniciales.");
    }

    private void inicializarClientes() {
        if (clienteRepository.count() > 0) return;

        clienteRepository.saveAll(List.of(
                crearCliente("Ana", "Martinez Perez", "ana.martinez@example.com", 120, NivelLealtad.PLATA),
                crearCliente("Luis", "Ramirez Torres", "luis.ramirez@example.com", 35, NivelLealtad.BRONCE),
                crearCliente("Sofia", "Hernandez Cruz", "sofia.hernandez@example.com", 580, NivelLealtad.ORO),
                crearCliente("Diego", "Flores Morales", "diego.flores@example.com", 0, NivelLealtad.BRONCE),
                crearCliente("Valeria", "Castillo Reyes", "valeria.castillo@example.com", 260, NivelLealtad.PLATA)
        ));

        log.info("Se crearon los clientes iniciales.");
    }

    private Cliente crearCliente(String nombre, String apellido, String email,
                                 int puntos, NivelLealtad nivel) {
        return Cliente.builder()
                .nombre(nombre)
                .apellido(apellido)
                .email(email)
                .contrasena("ClienteIncheky#123")
                .rol(Rol.CLIENTE)
                .puntos(puntos)
                .nivelLealtad(nivel)
                .build();
    }

    private void inicializarProveedores() {
        if (proveedorRepository.count() > 0) return;

        proveedorRepository.saveAll(List.of(
                Proveedor.builder()
                        .nombre("Distribuidora del Centro")
                        .telefono("5551234567")
                        .email("ventas@distribuidoracentro.example")
                        .direccion("Av. Central 120, Ciudad de México")
                        .activo(true)
                        .build(),
                Proveedor.builder()
                        .nombre("Abarrotes y Bebidas del Valle")
                        .telefono("5552345678")
                        .email("pedidos@abarrotesvalle.example")
                        .direccion("Calle del Comercio 45, Ciudad de México")
                        .activo(true)
                        .build(),
                Proveedor.builder()
                        .nombre("Limpieza Integral")
                        .telefono("5553456789")
                        .email("contacto@limpiezaintegral.example")
                        .direccion("Calzada Industrial 88, Estado de México")
                        .activo(true)
                        .build()
        ));

        log.info("Se crearon los proveedores iniciales.");
    }

}
