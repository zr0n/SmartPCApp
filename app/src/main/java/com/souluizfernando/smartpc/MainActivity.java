package com.souluizfernando.smartpc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    int currentX = 0;
    int currentY = 0;

    int initialX = 0;
    int initialY = 0;

    Bitmap bitMap;
    Canvas canvas;
    Paint paint;
    WSListener socket;



    public static class MouseCommand{

        public String command;
        public int x;
        public int y;

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

        ImageView iv = (ImageView) findViewById(R.id.touchableArea);
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

        socket = new WSListener(this);
        socket.connect(new OkHttpClient());




    }
    public void output(String message){
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.show();
    }
    @Override
    public boolean onTouchEvent(MotionEvent e){
        currentX = (int) e.getX();
        currentY = (int) e.getY();

        switch(e.getAction()){
            case MotionEvent.ACTION_MOVE:
                HandleMovement();
                return true;
            case MotionEvent.ACTION_DOWN:
                initialX = currentX;
                initialY = currentY;
                return true;
            case MotionEvent.ACTION_UP:
                initialX = -1;
                initialY = -1;
                canvas.drawLine(0,0,0,0,paint);
                return true;

        }

        return true;
    }

    private void HandleMovement(){

        TextView tv = (TextView) findViewById(R.id.textView);
        //tv.setText("iX: " + initialX + " iY: " + initialY + " cX: " + currentX + " cY: " + currentY);
        //tv.setText(new MouseCommand("Move", 0, 0).toJson());
        tv.setText(socket.bConnected ? "Socket Connected" : "Socket Not Connected");
        if(socket.bConnected)
            try {
                socket.sendMessage(new MouseCommand("move", 1, 1).toJson());
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
