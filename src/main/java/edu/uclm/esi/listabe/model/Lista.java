package edu.uclm.esi.listabe.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



@Entity
public class Lista {
	
	@Id @Column(length = 36)
	private String id;
	
	@Column (length = 80)
	private String nombre;
	
	@OneToMany(mappedBy = "lista")
	private List<Producto> productos;
	
	@ElementCollection
	private List<String> emailsUsuarios;
	
	public Lista(){
		this.id = UUID.randomUUID().toString();
		this.productos = new ArrayList<>();
		this.emailsUsuarios = new ArrayList<>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public List<String> getEmailsUsuarios(){
		return emailsUsuarios;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void add(Producto producto) {
		this.productos.add(producto);
		
	}
	
	public void addEmailUsuario(String email) {
		this.emailsUsuarios.add(email);
		
	}

	public List<Producto> getProductos() {
		return productos;
	}

	public void setProductos(List<Producto> productos) {
		this.productos = productos;
	}
	
	public void setEmailsUsuarios(List<String> emailsUsuarios) {
		this.emailsUsuarios = emailsUsuarios;
	}
	
	
	
	
	
	
	
}
