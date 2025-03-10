package com.doctorpc.apirest.apirest.Controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.doctorpc.apirest.apirest.Entities.Categoria;
import com.doctorpc.apirest.apirest.Entities.Producto;
import com.doctorpc.apirest.apirest.Repository.CategoriaRepository;
import com.doctorpc.apirest.apirest.Repository.ProductoRepository;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/producto")
@CrossOrigin(origins = "*")

public class ProductoController {

    @Autowired
    ProductoRepository productoRepository;
    @Autowired
    CategoriaRepository categoriaRepository;

    @Autowired
    private Cloudinary cloudinary;

    // mostrar
    @GetMapping("/get")
    public List<Producto> allProductos() {
        return productoRepository.findAll();
    }

    // crear
    @PostMapping("/post")
    public ResponseEntity<?> crearProducto(
            @RequestParam("nombre") String nombre,
            @RequestParam("precio") Double precio,
            @RequestParam("stock") int stock,
            @RequestParam("estado") boolean estado,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen,
            @RequestParam("marca") String marca,
            @RequestParam("modelo") String modelo,
            @RequestParam("idCategoria") Long idCategoria

    ) throws IOException {

        if (productoRepository.existsByNombreAndModeloAndMarca(nombre, modelo, marca)) {
            return ResponseEntity.status(206).body(0);
        }
        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new RuntimeException("Id de la categoria no encontrado"));

        Producto nuevoProducto = new Producto();
        nuevoProducto.setNombre(nombre);
        nuevoProducto.setPrecio(precio);
        nuevoProducto.setStock(stock);
        nuevoProducto.setEstado(estado);
        nuevoProducto.setMarca(marca);
        nuevoProducto.setModelo(modelo);
        nuevoProducto.setCategoria(categoria);

        if (imagen != null && !imagen.isEmpty()) {

            Map<String, Object> opciones = new HashMap<>();

            opciones.put("folder", "DOCTOR-PC"); // 📂 Guarda las imágenes en la carpeta "java" String imageUrl =
                                                 // (String)
            opciones.put("resource_type", "image");
            opciones.put("type", "upload");

            Map uploadResult = cloudinary.uploader().upload(imagen.getBytes(), opciones);
            String imageUrl = (String) uploadResult.get("url");
            nuevoProducto.setImagen(imageUrl);
        } else {
            nuevoProducto.setImagen(null);

        }

        Producto productoGuardado = productoRepository.save(nuevoProducto);
        return ResponseEntity.status(201).body(productoGuardado);
    }

    // actualizar
    @PutMapping("put/{idProducto}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Long idProducto,
            @RequestParam("nombre") String nombre,
            @RequestParam("precio") Double precio,
            @RequestParam("stock") int stock,
            @RequestParam("estado") boolean estado,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen,
            @RequestParam("marca") String marca,
            @RequestParam("modelo") String modelo,
            @RequestParam("idCategoria") Long idCategoria) throws IOException {

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Id del producto no encontrado"));

        if (productoRepository.existsByNombreAndModeloAndMarcaAndIdProductoNot(nombre, modelo, marca, idProducto)) {
            return ResponseEntity.status(206)
                    .body(0); // ya exite el producto
        }

        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new RuntimeException("Id de la categoria no encontrado"));

        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setEstado(estado);

        if (imagen != null && !imagen.isEmpty()) {
            Map uploadResult = cloudinary.uploader().upload(imagen.getBytes(), ObjectUtils.asMap(
                    "folder", "DOCTOR-PC"

            ));
            String imageURL = (String) uploadResult.get("url");
            producto.setImagen(imageURL);
        }
        producto.setMarca(marca);
        producto.setModelo(modelo);
        producto.setCategoria(categoria);
        Producto productoActualizado = productoRepository.save(producto);
        return ResponseEntity.status(200).body(productoActualizado);
    }

    // desactivar
    @PatchMapping("/desactivar/{idProducto}")
    public ResponseEntity<?> desactivarProducto(@PathVariable Long idProducto) {

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Id no encontrado" + idProducto));

        if (!producto.getEstado()) {

            return ResponseEntity.status(409).body("El Producto " + producto.getNombre() + " ya esta desactivado");
        }

        producto.setEstado(false);
        Producto productoUpd = productoRepository.save(producto);

        return ResponseEntity.status(200).body(productoUpd);
    }

    // activar
    @PatchMapping("/activar/{idProducto}")
    public ResponseEntity<?> activarProducto(@PathVariable Long idProducto) {

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Id no encontrado" + idProducto));

        if (producto.getEstado()) {

            return ResponseEntity.status(409).body("El Producto " + producto.getNombre() + " ya esta activado");
        }

        producto.setEstado(true);
        Producto productoUpd = productoRepository.save(producto);

        return ResponseEntity.status(200).body(productoUpd);
    }

    @PatchMapping("/stock/{idProducto}")
    public ResponseEntity<?> stockCero(@PathVariable Long idProducto) {

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Id no encontrrado"));

        if (producto.getStock() == 0) {
            return ResponseEntity.status(200).body(0); // YA ESTABA EN 0

        }
        producto.setStock(0);
        productoRepository.save(producto);

        return ResponseEntity.status(200).body(1); // OK
    }

    @DeleteMapping("/delete/{idProducto}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long idProducto) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("ID del producto no encontrado"));

        // Obtener la URL de la imagen del producto
        String imageUrl = producto.getImagen();

        // Si el producto tiene una imagen, eliminarla de Cloudinary
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                // Extraer el public_id de la imagen en Cloudinary
                String publicId = imageUrl.substring(imageUrl.lastIndexOf("/") + 1, imageUrl.lastIndexOf("."));

                // Eliminar la imagen de Cloudinary
                Map result = cloudinary.uploader().destroy("DOCTOR-PC/" + publicId,
                        ObjectUtils.asMap("resource_type", "image"));
                System.out.println("Imagen eliminada: " + result);

            } catch (Exception e) {
                System.err.println("Error eliminando la imagen de Cloudinary: " + e.getMessage());
            }
        }

        // Eliminar el producto de la base de datos
        productoRepository.delete(producto);
        return ResponseEntity.status(200).body("Producto eliminado correctamente.");
    }

}
