package com.rossmille.repository;

import com.rossmille.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, String> {

    @Query("SELECT c FROM Cliente c WHERE " +
            "LOWER(c.idClientes) LIKE :q OR " +
            "LOWER(c.nombre) LIKE :q OR " +
            "LOWER(c.correo) LIKE :q OR " +
            "LOWER(c.telefono) LIKE :q")
    List<Cliente> buscar(@Param("q") String q);
}
