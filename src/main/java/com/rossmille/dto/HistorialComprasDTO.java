package com.rossmille.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class HistorialComprasDTO {

    private Integer ventaId;
    private String fecha;
    private BigDecimal total;
    private String metodoPago;
    private List<ItemCompraDTO> items;
}
