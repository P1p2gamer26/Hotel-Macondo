package com.hotel.macondo.controller.rest;

import com.hotel.macondo.dto.ComentarioClienteResponse;
import com.hotel.macondo.dto.ComentarioEdicion;
import com.hotel.macondo.dto.ComentarioResponse;
import com.hotel.macondo.dto.ComentarioSolicitud;
import com.hotel.macondo.service.ComentarioService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comentario")
@CrossOrigin(origins = "http://localhost:4200")
public class ComentarioRestController {

    @Autowired
    private ComentarioService comentarioService;

    @PostMapping
    public ResponseEntity<ComentarioResponse> crear(@RequestBody ComentarioSolicitud solicitud) {
        return ResponseEntity.status(HttpStatus.CREATED).body(comentarioService.crear(solicitud));
    }

    @GetMapping("/publicados")
    public List<ComentarioResponse> publicados() {
        return comentarioService.publicados();
    }

    @PutMapping("/{id}/publicar")
    public ComentarioResponse publicar(@PathVariable Long id) {
        return comentarioService.publicar(id);
    }

    @GetMapping("/cliente/{clienteId}")
    public List<ComentarioClienteResponse> porCliente(@PathVariable Long clienteId) {
        return comentarioService.porCliente(clienteId);
    }

    @PutMapping("/{id}")
    public ComentarioClienteResponse editar(@PathVariable Long id, @RequestParam Long clienteId,
            @RequestBody ComentarioEdicion edicion) {
        return comentarioService.editar(id, clienteId, edicion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, @RequestParam Long clienteId) {
        comentarioService.eliminar(id, clienteId);
        return ResponseEntity.noContent().build();
    }
}
