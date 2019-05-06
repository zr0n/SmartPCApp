package com.souluizfernando.smartpc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener, SeekBar.OnSeekBarChangeListener {

    int currentX = 0;
    int currentY = 0;

    int initialX = 0;
    int initialY = 0;

    Bitmap bitMap;
    Canvas canvas;
    Paint paint;
    WSListener socket;

    //Views
    Button leftButton;
    Button rightButton;
    TextView tv;
    ImageView iv;
    SeekBar volumeControl;

    boolean bListenToClick = false;
    boolean bListenToTouch = !bListenToClick;

    @Override
    public void onClick(View v) {
        if(!bListenToClick)
            return;
        boolean sendMouseClickMsg = false;
        String mouseClickCommand = "";
        if(v == leftButton){
            mouseClickCommand = "left_click";
            sendMouseClickMsg = true;
            tv.setText("Left Click Triggered");
        }
        else if(v == rightButton){
            mouseClickCommand = "right_click";
            sendMouseClickMsg = true;
            tv.setText("Right Click Triggered");
        }

        if(sendMouseClickMsg){
            try {
                socket.sendMessage(new MouseCommand(mouseClickCommand, currentX, currentY).toJson());
            } catch (WSListener.WebSocketNotConnectedException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {

        currentX = (int) e.getX();
        currentY = (int) e.getY();
        if(v == leftButton || v == rightButton) {
            if(!bListenToTouch)
                return false;

            switch(e.getAction()){
                case MotionEvent.ACTION_DOWN:
                    try {
                        socket.sendMessage(new MouseCommand(
                                v == leftButton ? "left_down" : "right_down", currentX, currentY).toJson()
                        );
                    } catch (WSListener.WebSocketNotConnectedException e1) {
                        e1.printStackTrace();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    try {
                        socket.sendMessage(new MouseCommand(
                                v == leftButton ? "left_up" : "right_up", currentX, currentY).toJson()
                        );
                    } catch (WSListener.WebSocketNotConnectedException e1) {
                        e1.printStackTrace();
                    }
                    break;
            }
        }
        else if(v == iv){

            switch(e.getAction()){
                case MotionEvent.ACTION_MOVE:
                    HandleMovement();
                    return true;
                case MotionEvent.ACTION_DOWN:
                    initialX = currentX;
                    initialY = currentY;
                    try {
                        socket.sendMessage(
                                new MouseCommand("touchpad_start", initialX, initialY).toJson()
                        );
                    } catch (WSListener.WebSocketNotConnectedException e1) {
                        e1.printStackTrace();
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    initialX = -1;
                    initialY = -1;
                    canvas.drawLine(0,0,0,0,paint);
                    return true;

            }
        }

        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        try {
            socket.sendMessage(
                    new MouseCommand("set_volume", progress, 666 /* Random x) */).toJson()
            );
        } catch (WSListener.WebSocketNotConnectedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    public static class MouseCommand{

        public String command;
        public int x;
        public int y;
        public float extra;

        public MouseCommand(String command, int x, int y){
            this.x = x;
            this.y = y;
            this.command = command;
        }


        public String toJson(){
            Gson gson = new Gson();
            return gson.toJson(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        saveViewsRefs();
        bindViewListeners();
        setupSocket();
        setupCanvas();
    }
    void saveViewsRefs(){
        //Get Views
        tv = findViewById(R.id.textView);
        leftButton = findViewById(R.id.leftClickBtn);
        rightButton = findViewById(R.id.rightClickBtn);
        iv = findViewById(R.id.touchableArea);
        volumeControl = findViewById(R.id.volumeControl);
    }
    void bindViewListeners(){
        //Bind Views Listeners
        leftButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);
        iv.setOnTouchListener(this);
        rightButton.setOnTouchListener(this);
        leftButton.setOnTouchListener(this);
        volumeControl.setOnSeekBarChangeListener(this);

    }
    void setupSocket(){
        socket = new WSListener(this);
        socket.connect(new OkHttpClient());
    }
    void setupCanvas(){
        bitMap = Bitmap.createBitmap(
                (int) getWindowManager().getDefaultDisplay().getWidth(),
                (int) getWindowManager().getDefaultDisplay().getHeight(),
                Bitmap.Config.ARGB_8888
        );

        canvas = new Canvas(bitMap);
        iv.setImageBitmap(bitMap);

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(10);
    }


    private void HandleMovement(){


        //tv.setText("iX: " + initialX + " iY: " + initialY + " cX: " + currentX + " cY: " + currentY);
        //tv.setText(new MouseCommand("Move", 0, 0).toJson());
        tv.setText(socket.bConnected ? "Socket Connected" : "Socket Not Connected");
        if(socket.bConnected)
            try {
                socket.sendMessage(
                        new MouseCommand("touchpad_move", currentX, currentY).toJson()
                );
            } catch (WSListener.WebSocketNotConnectedException e) {
                e.printStackTrace();
            }

        DrawLine();
    }

    private void DrawLine(){

        canvas.drawColor(getResources().getColor(R.color.Meteorite));
        canvas.drawLine(initialX, initialY, currentX, currentY, paint);
    }
}
