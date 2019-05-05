package com.souluizfernando.smartpc;

import android.content.Context;
import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WSListener extends WebSocketListener {
    public static final String SOCKET_URL = "ws://192.168.11.66:5000/ws";
    public static final String LOG_TAG = "WS Listener";

    public boolean bConnected = false;
    private Context ctx;

    private WebSocket webSocket;

    public WSListener(Context inCtx){
        ctx = inCtx;
    }
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

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        output("Error : " + t.getMessage());
    }


    private void output(String message){
        Log.e(LOG_TAG, message);
        return;
    }


}
