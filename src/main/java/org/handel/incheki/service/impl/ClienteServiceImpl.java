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
import org.handel.incheki.repository.UsuarioRepository;
import org.handel.incheki.exceptions.EmailDuplicadoException;
import org.handel.incheki.service.ClienteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.handel.incheki.enums.NivelLealtad.BRONCE;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {
    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public ClienteResponse create(ClienteCreateRequest request) {
        String emailNormalizado = normalizarEmail(request.getEmail());
        validarEmailDisponible(emailNormalizado, null);
        Cliente cliente = clienteMapper.toEntity(request);
        cliente.setEmail(emailNormalizado);

        request.setPuntos(0);
        request.setNivelLealtad(BRONCE);
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
    public Page<ClienteResponse> findAll(String query, Pageable pageable) {
        Page<Cliente> clientes = query == null || query.isBlank()
                ? clienteRepository.findAll(pageable)
                : clienteRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        query, query, query, pageable);
        return clientes
                .map(clienteMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse findByUidNfc(String uidNfc) {
        if (uidNfc == null || uidNfc.trim().isEmpty()) {
            throw new org.handel.incheki.exceptions.UidNfcInvalidoException(
                    "El UID NFC no puede estar vacío");
        }

        String uidNormalizado = uidNfc.trim();
        if (uidNormalizado.length() > 255) {
            throw new org.handel.incheki.exceptions.UidNfcInvalidoException(
                    "El UID NFC no puede superar 255 caracteres");
        }

        Cliente cliente = clienteRepository.findByUidNfc(uidNormalizado)
                .orElseThrow(() -> new ResourcesNotFoundException("No se encontró un cliente con esa tarjeta NFC"));
        return clienteMapper.toResponse(cliente);
    }

    @Override
    @Transactional
    public ClienteResponse update(Long id, ClienteUpdateRequest request) {
        Cliente cliente = findClienteById(id);
        String emailNormalizado = normalizarEmail(request.getEmail());
        validarEmailDisponible(emailNormalizado, id);

        clienteMapper.update(request, cliente);
        cliente.setEmail(emailNormalizado);
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

    private String normalizarEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    private void validarEmailDisponible(String email, Long idActual) {
        usuarioRepository.findByEmailIgnoreCase(email)
                .filter(existing -> idActual == null || !existing.getId().equals(idActual))
                .ifPresent(existing -> {
                    throw new EmailDuplicadoException("El email " + email + " ya está registrado");
                });
    }
}
