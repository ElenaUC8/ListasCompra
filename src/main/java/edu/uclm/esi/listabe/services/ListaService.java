package edu.uclm.esi.listabe.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.listabe.dao.ListaDao;
import edu.uclm.esi.listabe.dao.ProductoDao;
import edu.uclm.esi.listabe.model.Lista;
import edu.uclm.esi.listabe.model.Producto;
import edu.uclm.esi.listabe.ws.WSListas;



@Service
public class ListaService {
	@Autowired	
	private ListaDao listaDao;
	
	@Autowired
	private ProxyBEU proxy;
	
	@Autowired
	private ProductoDao productoDao;
	
	@Autowired
	private WSListas wsListas;
	
	
	public List<Lista> getListas(String email) {
		List<Lista> result = new ArrayList<>();
		List<String >ids = this.listaDao.getListasDe(email);
		for (String id : ids) {
			result.add(this.listaDao.findById(id).get());
		}
		return result;
	}
	
	
	public Lista crearLista(String nombre, String token) {
		String email = this.proxy.validar(token);
		
		if(email==null)
			throw new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED);
		
		Lista lista = new Lista();
		lista.setNombre(nombre);
		lista.addEmailUsuario(email);
		this.listaDao.save(lista);
		this.listaDao.confirmar(lista.getId(), email);
		return lista;
	}
	
	public void eliminarLista(String idLista, String token) {
	  
	    String email = this.proxy.validar(token);
	    if (email == null) {
	        throw new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED);
	    }

	    // Buscar la lista por ID
	    Optional<Lista> optLista = this.listaDao.findById(idLista);
	    if (optLista.isEmpty()) {
	        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lista no encontrada");
	    }

	    Lista lista = optLista.get();

	    

	    // Eliminar todos los productos asociados a la lista
	    List<Producto> productos = lista.getProductos();
	    if (productos != null) {
	        for (Producto producto : productos) {
	            this.productoDao.delete(producto);
	        }
	    }

	    // Eliminar la lista
	    this.listaDao.delete(lista);
	}

	
	public Lista addProducto(String idLista, Producto producto) {
		Optional<Lista> optLista = this.listaDao.findById(idLista);
		if(optLista.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encuentra...");
		Lista lista = optLista.get();
		lista.add(producto);
		
		producto.setLista(lista);
		this.productoDao.save(producto);
		
		this.wsListas.notificar(idLista, producto);
		return lista;
	}
	
	public String addInvitado(String idLista, String email) {
		Optional<Lista> optLista = this.listaDao.findById(idLista);
		if(optLista.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encuentra...");
		Lista lista = optLista.get();
		lista.addEmailUsuario(email);
		this.listaDao.save(lista);
		
		String url ="https://localhost:80/listas/aceptarInvitacion?email=" + email + "&idLista=" + idLista;
		return url;
		
		/*añadir en la base de datos en la tabla lista_emails_usuarios una nueva columna "confirmado"
		(cuando este creada después de ejecutar esto)
		poner el certificado seguro en este backend HECHO
		yo tengo el puerto 80 pero los profesores lo tienen en el 8443 pero da igual cual poner*/
	}
	
	public Producto comprar(String idProducto, float unidadesCompradas) {
		return null;
		
	}
	
	public void aceptarInvitacion(String idLista, String email) {
		this.listaDao.confirmar(idLista, email);
	}

	
	

}

