package com.doctorpc.apirest.apirest.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.doctorpc.apirest.apirest.Entities.Ventas;

@Repository
public interface VentasRepository extends JpaRepository<Ventas, Long> {
    @Query("SELECT v FROM Ventas v WHERE MONTH(v.fecha) = :mes AND YEAR(v.fecha) = :anio")
    List<Ventas> findByMesYear(@Param("mes") int mes, @Param("anio") int anio);
}
