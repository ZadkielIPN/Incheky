package org.handel.incheki.mappers;

import org.handel.incheki.entidades.Cajero;
import org.handel.incheki.entidades.Cliente;
import org.handel.incheki.entidades.Producto;
import org.handel.incheki.entidades.Promocion;
import org.handel.incheki.entidades.Proveedor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class EntityReferenceMapper {

    @Named("toProveedor")
    public Proveedor toProveedor(Long id) {
        if (id == null) {
            return null;
        }
        Proveedor proveedor = new Proveedor();
        proveedor.setId(id);
        return proveedor;
    }

    @Named("toProveedorId")
    public Long toProveedorId(Proveedor proveedor) {
        return proveedor == null ? null : proveedor.getId();
    }

    @Named("toProducto")
    public Producto toProducto(Long id) {
        if (id == null) {
            return null;
        }
        Producto producto = new Producto();
        producto.setId(id);
        return producto;
    }

    @Named("toProductoId")
    public Long toProductoId(Producto producto) {
        return producto == null ? null : producto.getId();
    }

    @Named("toPromocion")
    public Promocion toPromocion(Long id) {
        if (id == null) {
            return null;
        }
        Promocion promocion = new Promocion();
        promocion.setId(id);
        return promocion;
    }

    @Named("toPromocionId")
    public Long toPromocionId(Promocion promocion) {
        return promocion == null ? null : promocion.getId();
    }

    @Named("toPromociones")
    public Set<Promocion> toPromociones(Set<Long> ids) {
        if (ids == null) {
            return null;
        }
        Set<Promocion> promociones = new LinkedHashSet<>();
        ids.forEach(id -> promociones.add(toPromocion(id)));
        return promociones;
    }

    @Named("toPromocionIds")
    public Set<Long> toPromocionIds(Set<Promocion> promociones) {
        if (promociones == null) {
            return null;
        }
        Set<Long> ids = new LinkedHashSet<>();
        promociones.forEach(promocion -> ids.add(toPromocionId(promocion)));
        return ids;
    }

    @Named("toProductos")
    public Set<Producto> toProductos(Set<Long> ids) {
        if (ids == null) {
            return null;
        }
        Set<Producto> productos = new LinkedHashSet<>();
        ids.forEach(id -> productos.add(toProducto(id)));
        return productos;
    }

    @Named("toProductoIds")
    public Set<Long> toProductoIds(Set<Producto> productos) {
        if (productos == null) {
            return null;
        }
        Set<Long> ids = new LinkedHashSet<>();
        productos.forEach(producto -> ids.add(toProductoId(producto)));
        return ids;
    }

    @Named("toCajero")
    public Cajero toCajero(Long id) {
        if (id == null) {
            return null;
        }
        Cajero cajero = new Cajero();
        cajero.setId(id);
        return cajero;
    }

    @Named("toCajeroId")
    public Long toCajeroId(Cajero cajero) {
        return cajero == null ? null : cajero.getId();
    }

    @Named("toCliente")
    public Cliente toCliente(Long id) {
        if (id == null) {
            return null;
        }
        Cliente cliente = new Cliente();
        cliente.setId(id);
        return cliente;
    }

    @Named("toClienteId")
    public Long toClienteId(Cliente cliente) {
        return cliente == null ? null : cliente.getId();
    }

}
