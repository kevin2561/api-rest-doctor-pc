package com.doctorpc.apirest.apirest.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.doctorpc.apirest.apirest.Entities.Categoria;
import com.doctorpc.apirest.apirest.Repository.CategoriaRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/categoria")
@CrossOrigin(origins = "*")

public class CategoriaController {
    @Autowired
    CategoriaRepository categoriaRepository;

    @GetMapping("/get")
    public List<Categoria> allCategoria() {
        return categoriaRepository.findAll();
    }

    @PostMapping("/post")
    public ResponseEntity<?> crearCategoria(@RequestBody Categoria categoria) {
        // TODO: process POST request
        if (categoriaRepository.existsByNombre(categoria.getNombre())) {
            return ResponseEntity.status(206).body("La categoria " + categoria.getNombre() + " ya exite");

        }
        Categoria categoriaGuardada = categoriaRepository.save(categoria);
        return ResponseEntity.status(201).body(categoriaGuardada);
    }

    @PutMapping("/put/{idCategoria}")
    public ResponseEntity<?> actualiarCategoria(@PathVariable Long idCategoria, @RequestBody Categoria categoria) {
        Categoria categoriaExiste = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new RuntimeException("Id de la categoria no encotrado"));

        categoriaExiste.setNombre(categoria.getNombre());
        categoriaExiste.setEstado(categoria.isEstado());
        Categoria categoriaActualziada = categoriaRepository.save(categoriaExiste);

        return ResponseEntity.status(200).body(categoriaActualziada);

    }

    @PatchMapping("/desactivar/{idCategoria}")
    public ResponseEntity<?> desactivarCategoria(@PathVariable Long idCategoria) {
        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new RuntimeException("ID de la categoria no encontrado"));

        if (!categoria.isEstado()) {
            return ResponseEntity.status(409).body("La categoria " + categoria.getNombre() + " ya esta desactivada");

        }
        categoria.setEstado(false);
        categoriaRepository.save(categoria);
        Categoria categoriaDesactivada = categoriaRepository.save(categoria);

        return ResponseEntity.status(200).body(categoriaDesactivada);
    }

    @PatchMapping("/activar/{idCategoria}")
    public ResponseEntity<?> activarCategoria(@PathVariable Long idCategoria) {
        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new RuntimeException("ID de la categoria no encontrado"));

        if (categoria.isEstado()) {
            return ResponseEntity.status(409).body("La categoria " + categoria.getNombre() + " ya esta activada");

        }
        categoria.setEstado(true);
        Categoria categoriaActivada = categoriaRepository.save(categoria);

        return ResponseEntity.status(200).body(categoriaActivada);
    }

    @DeleteMapping("/delete/{idCategoria}")
    public ResponseEntity<?> eliminarCategoria(@PathVariable Long idCategoria){
        Categoria categoria = categoriaRepository.findById(idCategoria)
        .orElseThrow(() -> new RuntimeException("ID de la categoria no encontrado"));

        categoriaRepository.delete(categoria);
        
            return ResponseEntity.status(200).body("Eliminado " + categoria.getIdCategoria());

    }

}
