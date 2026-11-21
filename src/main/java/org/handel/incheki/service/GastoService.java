package org.handel.incheki.service;

import org.handel.incheki.dtos.request.GastoRequest;
import org.handel.incheki.dtos.response.GastoResponse;

public interface GastoService {
    GastoResponse create(GastoRequest request);
}
