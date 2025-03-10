package com.doctorpc.apirest.apirest.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.doctorpc.apirest.apirest.Entities.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByNombre(String nombre);

}
