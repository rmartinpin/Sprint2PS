package prueba.app.raulmartin.com.sprint2;

import android.graphics.RectF;

import java.util.Random;

public class Bat implements Cloneable{

    private RectF rect;
    private float xVelocity;
    private float yVelocity;
    private float batWidth = 250;
    private float batHeight = 30;

    Bat(){

        xVelocity = 600;
        yVelocity = -1000;

        rect = new RectF();
    }

    RectF getRect(){
        return rect;
    }

    void update(long fps){
        rect.left = rect.left + (xVelocity / fps);
        rect.top = rect.top + (yVelocity / fps);
        rect.right = rect.left + batWidth;
        rect.bottom = rect.top - batHeight;
    }

    void reverseYVelocity(){
        yVelocity = -yVelocity;
    }

    void reverseXVelocity(){
        xVelocity = - xVelocity;
    }

    void setRandomXVelocity(){
        Random generator = new Random();
        int answer = generator.nextInt(2);

        if(answer == 0){
            reverseXVelocity();
        }
    }


    void reset(int x, int y){
        rect.left = x / 2;
        rect.top = y - 20;
        rect.right = x / 2 + batWidth;
        rect.bottom = y - 20 - batHeight;
    }


}

