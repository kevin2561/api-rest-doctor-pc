package com.doctorpc.apirest.apirest.Controllers;

import org.springframework.web.bind.annotation.RestController;

import com.doctorpc.apirest.apirest.Entities.Ventas;
import com.doctorpc.apirest.apirest.Repository.VentasRepository;
import com.doctorpc.apirest.apirest.servicio.VentasService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/ventas")
@CrossOrigin(origins = "*")

public class VentasController {
    @Autowired
    VentasRepository ventasRepository;

    @Autowired
    VentasService ventasService;

    @GetMapping("/filtrar")
    public ResponseEntity<?> filtrarMesYear(@RequestParam int mes, @RequestParam int anio) {
        List<Ventas> ventas = ventasService.obtenerVentasPorMesYear(mes, anio);
        if (ventas.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());

        }
        return ResponseEntity.status(200).body(ventas);
    }

    @GetMapping("/get")
    public List<Ventas> allVentas() {
        return ventasRepository.findAll();
    }

    @PostMapping("/post")
    public ResponseEntity<?> crearVenta(@RequestBody Ventas ventas) {
        ventas.setIdVenta(null); // Asegurarse de que sea un nuevo registro
        Ventas nuevaventa = ventasRepository.save(ventas);
        return ResponseEntity.status(200).body(nuevaventa);
    }

    @PutMapping("/put/{idVenta}")
    public ResponseEntity<Ventas> actualizarVentas(@PathVariable Long idVenta, @RequestBody Ventas ventas) {
        Ventas ventasExiste = ventasRepository.findById(idVenta)
                .orElseThrow(() -> new RuntimeException("Id de la venta no encotrado"));

        ventasExiste.setNombre(ventas.getNombre());
        ventasExiste.setDocumento(ventas.getDocumento());
        ventasExiste.setDescripcion(ventas.getDescripcion());
        ventasExiste.setFecha(ventas.getFecha());
        ventasExiste.setMetodoPago(ventas.getMetodoPago());
        ventasExiste.setTotal(ventas.getTotal());

        Ventas ventasActualziada = ventasRepository.save(ventasExiste);

        return ResponseEntity.status(200).body(ventasActualziada);
    }

    @DeleteMapping("/delete/{idVenta}")
    public ResponseEntity<?> eliminarVenta(@PathVariable Long idVenta) {
        Ventas ventasexite = ventasRepository.findById(idVenta)
                .orElseThrow(() -> new RuntimeException("Id de la venta no encotrado"));
        ventasRepository.delete(ventasexite);
        return ResponseEntity.status(200).body("Venta Eliminada");
    }
}
