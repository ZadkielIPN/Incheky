package org.handel.incheki.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.handel.incheki.enums.*;
import org.handel.incheki.model.*;
import org.handel.incheki.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Carga datos iniciales para desarrollo y demostración.
 *
 * La carga es idempotente por tabla: únicamente inserta datos cuando
 * la tabla correspondiente está vacía. No elimina ni modifica registros existentes.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InicializadorDatos implements CommandLineRunner {

    private final AdministradorRepository administradorRepository;
    private final CajeroRepository cajeroRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final PromocionRepository promocionRepository;
    private final ProveedorRepository proveedorRepository;
    private final AbastecimientoRepository abastecimientoRepository;

    @Override
    @Transactional
    public void run(String... args) {
        inicializarAdministradores();
        inicializarCajeros();
        inicializarClientes();
        inicializarProveedores();
        inicializarProductos();
        inicializarPromociones();
        relacionarProductosConPromociones();
        inicializarAbastecimientos();

        log.info("Inicialización de datos de Incheky finalizada.");
    }

    private void inicializarAdministradores() {
        if (administradorRepository.count() > 0) return;

        administradorRepository.saveAll(List.of(
                Administrador.builder()
                        .nombre("Zadkiel")
                        .apellido("Barron Dominguez")
                        .email("admin@incheky.com")
                        .contrasena("AdminIncheky#123")
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

    private void inicializarProductos() {
        if (productoRepository.count() > 0) return;

        productoRepository.saveAll(List.of(
                producto("Agua natural 1 L", "Agua purificada sin gas, botella de 1 litro.", "7501000000001", "18.00", "1.00", "4.8", UnidadMedida.PIEZA, "agua-1l.jpg"),
                producto("Leche entera 1 L", "Leche entera ultrapasteurizada, envase de 1 litro.", "7501000000002", "28.50", "1.00", "4.7", UnidadMedida.PIEZA, "leche-1l.jpg"),
                producto("Arroz 1 kg", "Arroz blanco de grano largo, bolsa de 1 kilogramo.", "7501000000003", "32.00", "1.00", "4.6", UnidadMedida.PIEZA, "arroz-1kg.jpg"),
                producto("Frijol negro 1 kg", "Frijol negro, bolsa de 1 kilogramo.", "7501000000004", "39.90", "1.00", "4.7", UnidadMedida.PIEZA, "frijol-1kg.jpg"),
                producto("Aceite vegetal 850 ml", "Aceite vegetal comestible, botella de 850 mililitros.", "7501000000005", "42.00", "0.85", "4.5", UnidadMedida.PIEZA, "aceite-850ml.jpg"),
                producto("Pasta corta 200 g", "Pasta de trigo tipo codito, paquete de 200 gramos.", "7501000000006", "12.50", "0.20", "4.4", UnidadMedida.PIEZA, "pasta-200g.jpg"),
                producto("Atún en agua 140 g", "Atún en agua, lata de 140 gramos.", "7501000000007", "19.90", "0.14", "4.6", UnidadMedida.PIEZA, "atun-140g.jpg"),
                producto("Cereal de maíz 300 g", "Cereal de maíz, caja de 300 gramos.", "7501000000008", "48.00", "0.30", "4.3", UnidadMedida.PIEZA, "cereal-300g.jpg"),
                producto("Jabón de tocador 150 g", "Jabón de tocador en barra de 150 gramos.", "7501000000009", "16.00", "0.15", "4.5", UnidadMedida.PIEZA, "jabon-150g.jpg"),
                producto("Detergente en polvo 1 kg", "Detergente para ropa, presentación de 1 kilogramo.", "7501000000010", "54.90", "1.00", "4.6", UnidadMedida.PIEZA, "detergente-1kg.jpg"),
                producto("Galletas de vainilla 150 g", "Galletas dulces sabor vainilla, paquete de 150 gramos.", "7501000000011", "21.50", "0.15", "4.4", UnidadMedida.PIEZA, "galletas-150g.jpg"),
                producto("Jugo de naranja 1 L", "Bebida de naranja, envase de 1 litro.", "7501000000012", "29.00", "1.00", "4.2", UnidadMedida.PIEZA, "jugo-1l.jpg")
        ));

        log.info("Se crearon los productos iniciales.");
    }

    private Producto producto(String nombre, String descripcion, String codigoBarras,
                              String precio, String peso, String valoracion,
                              UnidadMedida unidad, String imagen) {
        return Producto.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .codigoBarras(codigoBarras)
                .precio(new BigDecimal(precio))
                .peso(new BigDecimal(peso))
                .valoracion(new BigDecimal(valoracion))
                .stock(0)
                .activo(true)
                .unidadMedida(unidad)
                .imagen(imagen)
                .build();
    }

    private void inicializarPromociones() {
        if (promocionRepository.count() > 0) return;

        LocalDate hoy = LocalDate.now();

        promocionRepository.saveAll(List.of(
                Promocion.builder()
                        .nombre("Ahorro en bebidas")
                        .porcentajeDescuento(new BigDecimal("10.00"))
                        .fechaInicio(hoy.minusDays(7))
                        .fechaFin(hoy.plusDays(30))
                        .activa(true)
                        .productos(new HashSet<>())
                        .build(),
                Promocion.builder()
                        .nombre("Canasta básica")
                        .porcentajeDescuento(new BigDecimal("5.00"))
                        .fechaInicio(hoy.minusDays(3))
                        .fechaFin(hoy.plusDays(45))
                        .activa(true)
                        .productos(new HashSet<>())
                        .build(),
                Promocion.builder()
                        .nombre("Limpieza en oferta")
                        .porcentajeDescuento(new BigDecimal("15.00"))
                        .fechaInicio(hoy.minusDays(1))
                        .fechaFin(hoy.plusDays(14))
                        .activa(true)
                        .productos(new HashSet<>())
                        .build()
        ));

        log.info("Se crearon las promociones iniciales.");
    }

    private void relacionarProductosConPromociones() {
        List<Producto> productos = productoRepository.findAll();
        List<Promocion> promociones = promocionRepository.findAll();

        if (productos.isEmpty() || promociones.isEmpty()) return;

        Promocion bebidas = promociones.stream()
                .filter(p -> p.getNombre().equals("Ahorro en bebidas"))
                .findFirst().orElse(null);
        Promocion canasta = promociones.stream()
                .filter(p -> p.getNombre().equals("Canasta básica"))
                .findFirst().orElse(null);
        Promocion limpieza = promociones.stream()
                .filter(p -> p.getNombre().equals("Limpieza en oferta"))
                .findFirst().orElse(null);

        for (Producto producto : productos) {
            Set<Promocion> asignadas = producto.getPromociones() == null
                    ? new HashSet<>()
                    : new HashSet<>(producto.getPromociones());

            String codigo = producto.getCodigoBarras();
            if (bebidas != null && (codigo.endsWith("0001") || codigo.endsWith("0012"))) {
                asignadas.add(bebidas);
            }
            if (canasta != null && (codigo.endsWith("0003") || codigo.endsWith("0004")
                    || codigo.endsWith("0005") || codigo.endsWith("0006"))) {
                asignadas.add(canasta);
            }
            if (limpieza != null && (codigo.endsWith("0009") || codigo.endsWith("0010"))) {
                asignadas.add(limpieza);
            }

            producto.setPromociones(asignadas);
        }

        productoRepository.saveAll(productos);
    }

    private void inicializarAbastecimientos() {
        if (abastecimientoRepository.count() > 0 || productoRepository.count() == 0
                || proveedorRepository.count() == 0) return;

        List<Producto> productos = productoRepository.findAll();
        List<Proveedor> proveedores = proveedorRepository.findAll();
        if (productos.isEmpty() || proveedores.isEmpty()) return;

        Proveedor proveedorGeneral = proveedores.get(0);
        Proveedor proveedorBebidas = proveedores.size() > 1 ? proveedores.get(1) : proveedorGeneral;
        Proveedor proveedorLimpieza = proveedores.size() > 2 ? proveedores.get(2) : proveedorGeneral;

        crearAbastecimiento(proveedorBebidas, productos, List.of(
                new Entrada("7501000000001", 40, "10.00"),
                new Entrada("7501000000002", 30, "19.00"),
                new Entrada("7501000000012", 24, "18.00")
        ));

        crearAbastecimiento(proveedorGeneral, productos, List.of(
                new Entrada("7501000000003", 25, "23.00"),
                new Entrada("7501000000004", 25, "29.00"),
                new Entrada("7501000000005", 20, "31.00"),
                new Entrada("7501000000006", 50, "8.00"),
                new Entrada("7501000000007", 36, "14.00"),
                new Entrada("7501000000008", 18, "35.00"),
                new Entrada("7501000000011", 30, "15.00")
        ));

        crearAbastecimiento(proveedorLimpieza, productos, List.of(
                new Entrada("7501000000009", 30, "10.00"),
                new Entrada("7501000000010", 20, "41.00")
        ));

        log.info("Se crearon los abastecimientos iniciales y se actualizó el inventario.");
    }

    private void crearAbastecimiento(Proveedor proveedor, List<Producto> productos,
                                    List<Entrada> entradas) {
        Abastecimiento abastecimiento = Abastecimiento.builder()
                .fecha(LocalDateTime.now().minusDays(2))
                .proveedor(proveedor)
                .total(BigDecimal.ZERO)
                .detalles(new ArrayList<>())
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (Entrada entrada : entradas) {
            Producto producto = productos.stream()
                    .filter(p -> p.getCodigoBarras().equals(entrada.codigoBarras()))
                    .findFirst()
                    .orElse(null);

            if (producto == null) continue;

            BigDecimal costo = new BigDecimal(entrada.costoUnitario());
            BigDecimal subtotal = costo.multiply(BigDecimal.valueOf(entrada.cantidad()))
                    .setScale(2, java.math.RoundingMode.HALF_UP);

            producto.setStock(producto.getStock() + entrada.cantidad());

            DetalleAbastecimiento detalle = DetalleAbastecimiento.builder()
                    .abastecimiento(abastecimiento)
                    .producto(producto)
                    .cantidad(entrada.cantidad())
                    .costoUnitario(costo)
                    .subtotal(subtotal)
                    .build();

            abastecimiento.getDetalles().add(detalle);
            total = total.add(subtotal);
        }

        abastecimiento.setTotal(total.setScale(2, java.math.RoundingMode.HALF_UP));
        abastecimientoRepository.save(abastecimiento);
    }

    private record Entrada(String codigoBarras, int cantidad, String costoUnitario) {}
}
