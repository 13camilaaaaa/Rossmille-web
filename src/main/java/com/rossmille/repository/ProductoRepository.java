package com.rossmille.repository;

import com.rossmille.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findByStockLessThanEqual(int umbral);

    @Query("SELECT p FROM Producto p WHERE " +
            "LOWER(p.nombre) LIKE :q OR " +
            "LOWER(p.descripcion) LIKE :q OR " +
            "LOWER(p.genero) LIKE :q OR " +
            "LOWER(p.categoria) LIKE :q OR " +
            "LOWER(p.color) LIKE :q")
    List<Producto> buscar(@Param("q") String q);
}
