package com.rossmille.controller;

import com.rossmille.dto.ApiResponse;
import com.rossmille.dto.EliminarRequest;
import com.rossmille.dto.ProductoDTO;
import com.rossmille.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductoDTO>>> listar(
            @RequestParam(required = false) String q) {
        List<ProductoDTO> result = (q != null && !q.isBlank())
                ? productoService.buscar(q)
                : productoService.listar();
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/stock-bajo")
    public ResponseEntity<ApiResponse<List<ProductoDTO>>> stockBajo() {
        return ResponseEntity.ok(ApiResponse.success(productoService.stockBajo()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoDTO>> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(productoService.obtener(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductoDTO>> crear(
            @Valid @RequestBody ProductoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Producto creado", productoService.crear(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoDTO>> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody ProductoDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Producto actualizado",
                productoService.actualizar(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(
            @PathVariable Integer id,
            @Valid @RequestBody EliminarRequest request) {
        productoService.eliminar(id, request.getContrasena());
        return ResponseEntity.ok(ApiResponse.success("Producto eliminado", null));
    }
}
