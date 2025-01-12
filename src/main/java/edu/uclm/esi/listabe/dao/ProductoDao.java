package edu.uclm.esi.listabe.dao;

import org.springframework.data.repository.CrudRepository;
import edu.uclm.esi.listabe.model.Producto;

public interface ProductoDao extends CrudRepository<Producto, String> {

}
