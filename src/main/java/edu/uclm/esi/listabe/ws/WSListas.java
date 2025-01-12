package edu.uclm.esi.listabe.ws;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import edu.uclm.esi.listabe.dao.ListaDao;
import edu.uclm.esi.listabe.model.Lista;
import edu.uclm.esi.listabe.model.Producto;

@Component
public class WSListas extends TextWebSocketHandler {
	
	@Autowired
	private static ListaDao listaDao;
	
	
	private Map<String, List <WebSocketSession>> sessionsByIdLista = new ConcurrentHashMap<>();
	
	@Autowired
	public void setListaDao(ListaDao listaDao) { //este método sirve para que listaDao no sea null y no falle
		WSListas.listaDao=listaDao;
	}
	
	
	
	//estos metodos son del Websocket del servidor
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception { //se ejecuta cuando el navegador ejecuta el websocket
		String email = this.getParameter(session, "email");
		
		List<String> listas = this.listaDao.getListasDe(email);
		for (String idLista : listas) {
			List<WebSocketSession> auxi = this.sessionsByIdLista.get(idLista);
			if(auxi==null) {
				auxi = new ArrayList<>();
				auxi.add(session);
			} else {
				auxi.add(session);
			}
			this.sessionsByIdLista.put(idLista, auxi);
		}
		
	}

	public void notificar(String idLista, Producto producto) {
		JSONObject jso = new JSONObject();
		jso.put("tipo", "actualizacionDeLista");
		jso.put("idLista", idLista);
		jso.put("unidadesCompradas", producto.getUnidadesCompradas());
		jso.put("unidadesPedidas", producto.getUnidadesPedidas());
		jso.put("nombre", producto.getNombre());
		
		TextMessage message = new TextMessage(jso.toString());
		
		List<WebSocketSession> interesados = this.sessionsByIdLista.get(idLista);
		for (WebSocketSession target : interesados) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						target.sendMessage(message);
					} catch (IOException e) {
						WSListas.this.sessionsByIdLista.remove(target.getId());
					} 
				}
			}).start();
			
		}
	}
	
	private String getParameter(WebSocketSession session, String parName) {
		URI uri = session.getUri();
		String query = uri.getQuery();
		for (String param : query.split("&")) {
			String[] pair = param.split("=");
			if(pair.length > 1 && parName.equals(pair[0])) {
				return pair[1];
			}
		}
		return null;
	}
	
	
	
	/*private void difundir(JSONObject jso) throws IOException {
		TextMessage message = new TextMessage(jso.toString());
		for (WebSocketSession target : this.sessions.values()) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						target.sendMessage(message);
					} catch (IOException e) {
						WSListas.this.sessions.remove(target.getId());
					} 
				}
			}).start();
			
		}
	}*/
	
	
	
	/*@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status)  { //throws 
		this.sessions.remove(session.getId());
	}*/

	
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception { // se ejecuta cuando hay algun error en la comunicación del websocket
	}

	
}
