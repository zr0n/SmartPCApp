package com.souluizfernando.smartpc;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    int currentX = 0;
    int currentY = 0;

    int initialX = 0;
    int initialY = 0;

    Bitmap bitMap;
    Canvas canvas;
    Paint paint;

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
        tv.setText("iX: " + initialX + " iY: " + initialY + " cX: " + currentX + " cY: " + currentY);
        DrawLine();
    }

    private void DrawLine(){

        canvas.drawColor(getResources().getColor(R.color.Meteorite));
        canvas.drawLine(initialX, initialY, currentX, currentY, paint);
    }
}
