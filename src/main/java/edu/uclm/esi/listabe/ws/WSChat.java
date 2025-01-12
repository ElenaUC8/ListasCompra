package edu.uclm.esi.listabe.ws;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
@Component
public class WSChat extends TextWebSocketHandler {
	
	private Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
	private Map<String, WebSocketSession> sessionsByNombre = new ConcurrentHashMap<>();
	
	
	
	//estos metodos son del Websocket del servidor
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception { //se ejecuta cuando el navegador ejecuta el websocket
		System.out.println(session.getId());
		String nombreUsuario = this.getNombreParameter(session);
		this.sessions.put(session.getId(), session);
		this.sessionsByNombre.put(nombreUsuario, session);
		
		
		JSONObject jso = new JSONObject();
		jso.put("tipo", "llegadaDeUsuario");
		jso.put("contenido", nombreUsuario);
		this.difundir(jso);
	}

	private String getNombreParameter(WebSocketSession session) {
		URI uri = session.getUri();
		String query = uri.getQuery();
		for (String param : query.split("&")) {
			String[] pair = param.split("=");
			if(pair.length > 1 && "nombre".equals(pair[0])) {
				return pair[1];
			}
		}
		return null;
	}
	
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception { // se ejecuta cuando un cliente manda un mensaje por websocket al servidor, (primer parametro el socket que manda el mensaje, segundo es el mensaje)
		JSONObject jso = new JSONObject(message.getPayload());
		if(jso.getString("tipo").equalsIgnoreCase("difusion")) {
			jso.put("tipo", "mensajeDeTexto");
			jso.put("contenido", jso.getString("contenido"));
			this.difundir(jso);
		}else if (jso.getString("tipo").equalsIgnoreCase("mensajeParticular")) {
			String destinatario = jso.getString("destinatario");
			WebSocketSession wsDestinatario = this.sessionsByNombre.get(destinatario);
			if(wsDestinatario!=null) {
				wsDestinatario.sendMessage(message);
			}		
		}
	}
	
	private void difundir(JSONObject jso) throws IOException {
		TextMessage message = new TextMessage(jso.toString());
		for (WebSocketSession target : this.sessions.values()) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						target.sendMessage(message);
					} catch (IOException e) {
						WSChat.this.sessions.remove(target.getId());
					} 
				}
			}).start();
			
		}
	}
	
	
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status)  { //throws 
		this.sessions.remove(session.getId());
	}

	@Override
	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) { //binarymessage es un archivo
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception { // se ejecuta cuando hay algun error en la comunicación del websocket
	}
}
