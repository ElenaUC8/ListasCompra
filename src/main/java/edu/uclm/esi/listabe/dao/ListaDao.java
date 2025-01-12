package edu.uclm.esi.listabe.dao;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import edu.uclm.esi.listabe.model.Lista;


public interface ListaDao extends CrudRepository<Lista, String> {

	
	@Query(value = "select lista_id fromlista_emails_usuarios where emails_usuarios=:email", nativeQuery = true)
	List<String> getListasDe(@Param("email")String email); //lo de param es para indicar que el elemento email es parte de la consulta
	
	
	@Modifying @Transactional
	@Query(value = "update lista_emails_usuarios set confirmado=true " + 
			"where emails_usuarios=:email and lista_id=:id", 
			nativeQuery = true)
	public void confirmar(@Param("id") String id, @Param("email") String email);
	

}
