package org.handel.incheki.service;

import org.handel.incheki.dtos.response.ReporteContableResponse;

public interface ReporteContableService {
    ReporteContableResponse consultar(int anio, int mes);
}
