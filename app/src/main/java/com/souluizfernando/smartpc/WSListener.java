package com.souluizfernando.smartpc;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WSListener extends WebSocketListener {
    public static final String SOCKET_URL = "ws://192.168.11.66:5000";
    public boolean bConnected = false;


    private WebSocket webSocket;
    @Override
    public void onOpen(WebSocket ws, Response response){
        bConnected = true;
        webSocket = ws;
    }

    public class WebSocketNotConnectedException extends Exception{

    }
    public void sendMessage(String message) throws WebSocketNotConnectedException{
        if(!bConnected){
            throw new WebSocketNotConnectedException();
        }

        webSocket.send(message);
    }

    public void connect(OkHttpClient client){
        Request request = new Request.Builder().url(SOCKET_URL).build();
        client.newWebSocket(request, this);
    }
}
