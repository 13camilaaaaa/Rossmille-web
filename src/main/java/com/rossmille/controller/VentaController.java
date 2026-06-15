package com.rossmille.controller;

import com.rossmille.dto.ApiResponse;
import com.rossmille.dto.VentaRequest;
import com.rossmille.dto.VentaResponse;
import com.rossmille.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VentaResponse>> registrar(
            @Valid @RequestBody VentaRequest request) {
        VentaResponse response = ventaService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Venta registrada correctamente", response));
    }
}
