package com.Publish;

import com.Utility.Util;
import com.ge.predix.solsvc.restclient.impl.RestClientImpl;
import com.ge.predix.solsvc.websocket.client.WebSocketClient;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;

import java.io.IOException;

public class Publisher {
	private static WebSocketClient webSocketclient = null;

	public static void main(String[] args) {
		RestClientImpl restClient = Util.createRestClient();
		webSocketclient.init(restClient, restClient.getSecureTokenForClientId(), new WebSocketAdapter());
		postBinaryData();
		postStringData();
	}

	public static void postBinaryData() {
		byte[] inputBody = Util.createJsonMessage();
		try {
			webSocketclient.postBinaryWSData(inputBody);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WebSocketException e) {
			e.printStackTrace(); }
	}

	public static void postStringData() {

		String data = Util.createJsonMessage().toString();
		try {
			webSocketclient.postTextWSData(data);
		} catch (IOException e) {
			e.printStackTrace(); }
		  catch (WebSocketException e) {
			e.printStackTrace(); }
	}
}
