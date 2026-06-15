package com.rossmille.service;

import com.rossmille.dto.ClienteDTO;
import com.rossmille.dto.HistorialComprasDTO;
import com.rossmille.dto.ItemCompraDTO;
import com.rossmille.entity.Cliente;
import com.rossmille.entity.Usuario;
import com.rossmille.repository.ClienteRepository;
import com.rossmille.repository.UsuarioRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClienteService {

    private static final DateTimeFormatter FECHA_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    public ClienteService(ClienteRepository clienteRepository,
                          UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          JdbcTemplate jdbcTemplate) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ClienteDTO> listar() {
        return clienteRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public List<ClienteDTO> buscar(String q) {
        String pattern = "%" + q.toLowerCase() + "%";
        return clienteRepository.buscar(pattern).stream()
                .map(this::toDto)
                .toList();
    }

    public ClienteDTO obtener(String id) {
        return toDto(findOrThrow(id));
    }

    @Transactional
    public ClienteDTO crear(ClienteDTO dto) {
        if (dto.getId() == null || dto.getId().isBlank()) {
            throw new IllegalArgumentException("El ID del cliente es obligatorio");
        }
        if (dto.getId().length() > 10) {
            throw new IllegalArgumentException("El ID no puede tener mas de 10 caracteres");
        }
        if (clienteRepository.existsById(dto.getId())) {
            throw new IllegalArgumentException("Ya existe un cliente con ese ID");
        }
        Cliente c = new Cliente();
        c.setIdClientes(dto.getId().trim());
        aplicarCambios(c, dto);
        return toDto(clienteRepository.save(c));
    }

    @Transactional
    public ClienteDTO actualizar(String id, ClienteDTO dto) {
        Cliente c = findOrThrow(id);
        aplicarCambios(c, dto);
        return toDto(clienteRepository.save(c));
    }

    @Transactional
    public void eliminar(String id, String contrasena) {
        String idUsuario = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        Usuario usuario = usuarioRepository.findByIdUsuario(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (!passwordEncoder.matches(contrasena, usuario.getContrasena())) {
            throw new IllegalArgumentException("Contrasena incorrecta");
        }

        findOrThrow(id);
        clienteRepository.deleteById(id);
    }

    public List<HistorialComprasDTO> historialCompras(String id) {
        findOrThrow(id);

        String sql = "SELECT v.id AS venta_id, v.fecha, v.total, v.metodo_pago," +
                " p.nombre AS producto_nombre, dv.cantidad, dv.precio_unitario" +
                " FROM ventas v" +
                " JOIN detalle_venta dv ON dv.venta_id = v.id" +
                " JOIN productos p ON p.id = dv.producto_id" +
                " WHERE v.id_cliente = ?" +
                " ORDER BY v.fecha DESC, v.id DESC, dv.id ASC";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, id);

        Map<Integer, HistorialComprasDTO> ventasMap = new LinkedHashMap<>();

        for (Map<String, Object> row : rows) {
            Integer ventaId = ((Number) row.get("venta_id")).intValue();

            if (!ventasMap.containsKey(ventaId)) {
                HistorialComprasDTO dto = new HistorialComprasDTO();
                dto.setVentaId(ventaId);
                dto.setTotal((BigDecimal) row.get("total"));
                dto.setMetodoPago((String) row.get("metodo_pago"));
                dto.setItems(new ArrayList<>());

                Timestamp ts = (Timestamp) row.get("fecha");
                if (ts != null) {
                    dto.setFecha(ts.toLocalDateTime().format(FECHA_FMT));
                }
                ventasMap.put(ventaId, dto);
            }

            BigDecimal precio = (BigDecimal) row.get("precio_unitario");
            int cantidad = ((Number) row.get("cantidad")).intValue();

            ItemCompraDTO item = new ItemCompraDTO();
            item.setNombreProducto((String) row.get("producto_nombre"));
            item.setCantidad(cantidad);
            item.setPrecioUnitario(precio);
            item.setSubtotal(precio.multiply(BigDecimal.valueOf(cantidad)));
            ventasMap.get(ventaId).getItems().add(item);
        }

        return new ArrayList<>(ventasMap.values());
    }

    private Cliente findOrThrow(String id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
    }

    private void aplicarCambios(Cliente c, ClienteDTO dto) {
        c.setNombre(dto.getNombre());
        c.setCorreo(dto.getCorreo() != null ? dto.getCorreo().trim() : null);
        c.setTelefono(dto.getTelefono() != null ? dto.getTelefono().trim() : null);
        c.setDireccion(dto.getDireccion() != null ? dto.getDireccion().trim() : null);
    }

    private ClienteDTO toDto(Cliente c) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(c.getIdClientes());
        dto.setNombre(c.getNombre());
        dto.setCorreo(c.getCorreo());
        dto.setTelefono(c.getTelefono());
        dto.setDireccion(c.getDireccion());
        return dto;
    }
}
