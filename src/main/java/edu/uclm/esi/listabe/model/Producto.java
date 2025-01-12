package edu.uclm.esi.listabe.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;



@Entity
public class Producto {
	@Id @Column (length = 36)
	private String id;
	@Column ( length = 80, nullable = false)
	private String nombre;
	
	private float unidadesPedidas;
	private float unidadesCompradas;
	@ManyToOne
	private Lista lista;
	
	public Producto() {
		this.id = UUID.randomUUID().toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public float getUnidadesPedidas() {
		return unidadesPedidas;
	}

	public void setUnidadesPedidas(float unidadesPedidas) {
		this.unidadesPedidas = unidadesPedidas;
	}

	public float getUnidadesCompradas() {
		return unidadesCompradas;
	}

	public void setUnidadesCompradas(float unidadesCompradas) {
		this.unidadesCompradas = unidadesCompradas;
	}

	
	@JsonIgnore
	public Lista getLista() {
		return lista;
	}

	public void setLista(Lista lista) {
		this.lista = lista;
	}
	
	
	
	

}
