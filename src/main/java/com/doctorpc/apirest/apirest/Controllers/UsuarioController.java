package com.doctorpc.apirest.apirest.Controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.doctorpc.apirest.apirest.Entities.Usuario;
import com.doctorpc.apirest.apirest.Repository.UsuarioRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/usuario")
@CrossOrigin(origins = "*")

public class UsuarioController {

    @Autowired
    UsuarioRepository usuarioRepository;

    @GetMapping("/get")
    public List<Usuario> allUsuarios() {
        return usuarioRepository.findAll();
    }

    @PostMapping("/post")
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario) {

        if (usuario.getNombre() == null || usuario.getNombre().length() <= 8) {
            return ResponseEntity.ok("El usuario debe tener más de 8 caracteres");
        }
        if (usuario.getPassword() == null || usuario.getPassword().length() <= 8
                || !Pattern.compile(".*\\d.*").matcher(usuario.getPassword()).matches()) {
            return ResponseEntity.badRequest()
                    .body("La contraseña debe tener más de 8 caracteres y al menos un número.");
        }

        usuario.setEstado(true);

        Usuario nuevoUsuario = usuarioRepository.save(usuario);
        return ResponseEntity.ok(nuevoUsuario);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@RequestBody Usuario usuario) {
        Optional<Usuario> usuarioExiste = usuarioRepository.findByNombre(usuario.getNombre());
        if (usuarioExiste.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("message", "0")); // Usuario incorrecto
        }
        Usuario usuarioEncontrado = usuarioExiste.get();

        if (!usuarioEncontrado.isEstado()) {
            return ResponseEntity.status(403).body("Este usuario esta desactivado " + usuarioEncontrado.getNombre()); // USUARIO                                                                                                                   // MAL
        }

        if (!usuarioEncontrado.getPassword().equals(usuario.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("message", "1")); // Contraseña incorrecta
        }

        return ResponseEntity.status(200).body(Map.of("message", "2")); // Login exitoso
    }

    @PutMapping("/put/{idUsuario}")
    public ResponseEntity<?> putMethodName(@PathVariable Long idUsuario, @RequestBody Usuario usuario) {
        Usuario usuarioExiste = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getNombre() == null || usuario.getNombre().length() <= 8) {
            return ResponseEntity.status(401).body("El usuario debe tener más de 8 caracteres.");
        }
        if (usuario.getPassword() == null || usuario.getPassword().length() <= 8
                || !Pattern.compile(".*\\d.*").matcher(usuario.getPassword()).matches()) {
            return ResponseEntity.badRequest()
                    .body("La contraseña debe tener más de 8 caracteres y al menos un número.");
        }

        usuarioExiste.setNombre(usuario.getNombre());
        usuarioExiste.setPassword(usuario.getPassword());
        usuarioExiste.setEstado(usuario.isEstado());

        Usuario usuarioActualziado = usuarioRepository.save(usuarioExiste);

        return ResponseEntity.status(200).body(usuarioActualziado);
    }

}
