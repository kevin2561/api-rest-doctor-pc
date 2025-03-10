package com.doctorpc.apirest.apirest.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doctorpc.apirest.apirest.Entities.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Validar si ya existe un producto con el mismo nombre, modelo y marca (para
    // CREAR)

    boolean existsByNombreAndModeloAndMarca(String nombre, String modelo, String marca);

    // Validar si ya existe otro producto con el mismo nombre, modelo y marca (para
    // ACTUALIZAR)
    boolean existsByNombreAndModeloAndMarcaAndIdProductoNot(String nombre, String modelo, String marca, Long idProducto);
}