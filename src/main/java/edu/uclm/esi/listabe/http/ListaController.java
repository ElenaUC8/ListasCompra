package edu.uclm.esi.listabe.http;

import java.io.IOException;

import java.util.List;
import java.util.Map;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.listabe.model.Lista;
import edu.uclm.esi.listabe.model.Producto;
import edu.uclm.esi.listabe.services.ListaService;
import edu.uclm.esi.listabe.ws.WSListas;


@RestController
@RequestMapping("listas")
@CrossOrigin("*")
public class ListaController {
	@Autowired
	private ListaService listaService;
	
	
	@GetMapping("/getListas")
	public List<Lista> getLista(@RequestParam String email){
		return this.listaService.getListas(email);
		
	}
	
	@PostMapping("/crearLista")
	public Lista crearLista(HttpServletRequest request, @RequestBody String nombre) {
		String token = request.getHeader("tokenListas");
		nombre = nombre.trim();
		if (nombre.isEmpty())
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre no puede estar vacío");
		
		if(nombre.length()>80)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de la lista está limitado a 80 caracteres");
		
		return this.listaService.crearLista(nombre, token);
	}
	
	@DeleteMapping("/eliminarLista")
	public void eliminarLista(HttpServletRequest request, @RequestParam String idLista) {
	    String token = request.getHeader("tokenListas");

	    if (idLista == null || idLista.trim().isEmpty()) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID de la lista no puede estar vacío");
	    }

	    
	    this.listaService.eliminarLista(idLista, token);
	   
	}
	
	
	
	@PostMapping("/addProducto")
	public Lista addProducto(HttpServletRequest request, @RequestBody Producto producto) {
		if (producto.getNombre().isEmpty())
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre no puede estar vacío");
		
		if(producto.getNombre().length()>80)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de la lista está limitado a 80 caracteres");
		
		String idLista = request.getHeader("idLista");
		return this.listaService.addProducto(idLista, producto);
	}
	
	@PostMapping("/addInvitado")
	public String addInvitado(HttpServletRequest request, @RequestBody String email) {
		String idLista = request.getHeader("idLista");
		return this.listaService.addInvitado(idLista, email);
	}
	
	@PutMapping("/comprar")
	public Producto comprar(@RequestBody Map<String, Object> compra) {
		String idProducto = compra.get("idProducto").toString();
;		float unidadesCompradas = (float) compra.get("unidadesCompradas");
		
		return this.listaService.comprar(idProducto, unidadesCompradas);
		
	}
	
	@GetMapping("/aceptarInvitacion")
	public void aceptarInvitacion(HttpServletResponse response, @RequestBody String idLista, @RequestBody String email) throws IOException {
		this.listaService.aceptarInvitacion(idLista, email);
		response.sendRedirect("https://localhost:4200");
	}
	
	
	
	
	
}
