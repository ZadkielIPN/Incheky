package org.handel.incheki.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReporteContableResponse {
    private int anio;
    private int mes;
    private BigDecimal subtotalVentas;
    private BigDecimal descuentosVentas;
    private BigDecimal ingresosVentas;
    private BigDecimal costoMercanciaVendida;
    private BigDecimal utilidadBruta;
    private BigDecimal gastosOperativos;
    private BigDecimal utilidadNeta;
    private BigDecimal comprasInventario;
    private List<ReporteVentaResponse> ventas;
    private List<ReporteAbastecimientoResponse> abastecimientos;
    private List<GastoResponse> gastos;
}
