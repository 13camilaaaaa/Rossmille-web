package com.rossmille.controller;

import com.rossmille.dto.ApiResponse;
import com.rossmille.dto.ClienteDTO;
import com.rossmille.dto.EliminarRequest;
import com.rossmille.dto.HistorialComprasDTO;
import com.rossmille.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClienteDTO>>> listar(
            @RequestParam(required = false) String q) {
        List<ClienteDTO> result = (q != null && !q.isBlank())
                ? clienteService.buscar(q)
                : clienteService.listar();
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteDTO>> obtener(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(clienteService.obtener(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ClienteDTO>> crear(
            @Valid @RequestBody ClienteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Cliente creado", clienteService.crear(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteDTO>> actualizar(
            @PathVariable String id,
            @Valid @RequestBody ClienteDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Cliente actualizado",
                clienteService.actualizar(id, dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> eliminar(
            @PathVariable String id,
            @Valid @RequestBody EliminarRequest request) {
        clienteService.eliminar(id, request.getContrasena());
        return ResponseEntity.ok(ApiResponse.success("Cliente eliminado", null));
    }

    @GetMapping("/{id}/compras")
    public ResponseEntity<ApiResponse<List<HistorialComprasDTO>>> historialCompras(
            @PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(clienteService.historialCompras(id)));
    }
}
