package com.rossmille.repository;

import com.rossmille.entity.Producto;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Producto p WHERE p.id = :id")
    Optional<Producto> findByIdForUpdate(@Param("id") Integer id);
}
