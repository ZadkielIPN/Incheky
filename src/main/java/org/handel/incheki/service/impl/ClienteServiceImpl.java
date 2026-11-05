package org.handel.incheki.service.impl;

import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.ClienteCreateRequest;
import org.handel.incheki.dtos.request.ClienteUpdateRequest;
import org.handel.incheki.dtos.response.ClienteResponse;
import org.handel.incheki.enums.Rol;
import org.handel.incheki.exceptions.ResourcesNotFoundException;
import org.handel.incheki.mappers.ClienteMapper;
import org.handel.incheki.model.Cliente;
import org.handel.incheki.repository.ClienteRepository;
import org.handel.incheki.service.ClienteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {
    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Override
    @Transactional
    public ClienteResponse create(ClienteCreateRequest request) {
        Cliente cliente = clienteMapper.toEntity(request);

        cliente.setRol(Rol.CLIENTE);

        clienteRepository.save(cliente);
        return clienteMapper.toResponse(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse findById(Long id) {
        Cliente cliente = findClienteById(id);

        return clienteMapper.toResponse(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteResponse> findAll(Pageable pageable) {
        return clienteRepository.findAll(pageable)
                .map(clienteMapper::toResponse);
    }

    @Override
    @Transactional
    public ClienteResponse update(Long id, ClienteUpdateRequest request) {
        Cliente cliente = findClienteById(id);

        clienteMapper.update(request, cliente);
        if (request.getContrasena() != null && !request.getContrasena().isBlank()) {
            cliente.setContrasena(request.getContrasena());
        }

        clienteRepository.save(cliente);
        return clienteMapper.toResponse(cliente);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Cliente cliente = findClienteById(id);

        clienteRepository.delete(cliente);
    }

    private Cliente findClienteById(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException("Cliente con id " + id + " no encontrado"));
    }
}
