package edu.uclm.esi.listabe.services;

import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.protocol.BasicHttpContext;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.springframework.stereotype.Service;

@Service
public class ProxyBEU {
	
	//hay que cambiar este metodo para que devuelva un email
	
	public String validar(String token) { 
		String url = "http://localhost:8000/tokens/validar";
		
		try (CloseableHttpClient httpClient = HttpClients.createDefault()){
			HttpPut httpPut = new HttpPut(url);
			httpPut.setEntity(new StringEntity(token));
			httpPut.setHeader("Content-Type", "text/plain");
			
			HttpContext context = new BasicHttpContext();
			
			try(CloseableHttpResponse response = httpClient.execute(httpPut, context)){
				System.out.println("Response Status: " + response.getCode());
				return EntityUtils.toString(response.getEntity());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
		
		
	}

}
