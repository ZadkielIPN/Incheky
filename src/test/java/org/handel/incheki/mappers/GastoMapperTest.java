package org.handel.incheki.mappers;

import org.handel.incheki.dtos.request.GastoRequest;
import org.handel.incheki.dtos.response.GastoResponse;
import org.handel.incheki.model.Gasto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GastoMapperTest {
    private final GastoMapper mapper = Mappers.getMapper(GastoMapper.class);

    @Test
    void mapsRequestAndTrimsTextFields() {
        GastoRequest request = new GastoRequest(LocalDateTime.of(2026, 11, 20, 10, 0),
                " Servicios ", " Internet ", new BigDecimal("25.50"));

        Gasto gasto = mapper.toEntity(request);

        assertEquals("Servicios", gasto.getCategoria());
        assertEquals("Internet", gasto.getDescripcion());
        assertEquals(request.getFecha(), gasto.getFecha());
        assertEquals(request.getMonto(), gasto.getMonto());
    }

    @Test
    void mapsEntityToResponse() {
        Gasto gasto = Gasto.builder()
                .id(1L)
                .fecha(LocalDateTime.of(2026, 11, 20, 10, 0))
                .categoria("Servicios")
                .descripcion("Internet")
                .monto(new BigDecimal("25.50"))
                .build();

        GastoResponse response = mapper.toResponse(gasto);

        assertEquals(gasto.getId(), response.getId());
        assertEquals(gasto.getFecha(), response.getFecha());
        assertEquals(gasto.getCategoria(), response.getCategoria());
        assertEquals(gasto.getDescripcion(), response.getDescripcion());
        assertEquals(gasto.getMonto(), response.getMonto());
    }

    @Test
    void updateTrimsProvidedFieldsAndLeavesNullFieldsUnchanged() {
        Gasto gasto = Gasto.builder()
                .id(1L)
                .fecha(LocalDateTime.of(2026, 11, 20, 10, 0))
                .categoria("Servicios")
                .descripcion("Internet")
                .monto(new BigDecimal("25.50"))
                .build();
        GastoRequest request = new GastoRequest(null, " Transporte ", null, null);

        mapper.update(request, gasto);

        assertEquals("Transporte", gasto.getCategoria());
        assertEquals("Internet", gasto.getDescripcion());
        assertEquals(new BigDecimal("25.50"), gasto.getMonto());
    }
}
