package prueba.app.raulmartin.com.sprint2;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

class PongMotor extends SurfaceView implements Runnable{

    // This is our thread
    private Thread gameThread = null;

    // This is new. We need a SurfaceHolder
    // When we use Paint and Canvas in a thread
    // We will see it in action in the draw method soon.
    private SurfaceHolder ourHolder;

    // A boolean which we will set and unset
    // when the game is running- or not.
    private volatile boolean playing;

    // Game is paused at the start
    private boolean paused = true;

    // A Canvas and a Paint object
    private Canvas canvas;
    private Paint paint;

    // How wide and high is the screen?
    private int screenX;
    private int screenY;

    // This variable tracks the game frame rate
    private long fps;

    // This is used to help calculate the fps
    private long timeThisFrame;

    // The player's bat
    Bat bat;

    // A ball
    Ball ball;





    // The constructor is called when the object is first created
    public PongMotor(Context context, int x, int y) {
        // This calls the default constructor to setup the rest of the object
        super(context);

        // Initialize ourHolder and paint objects
        ourHolder = getHolder();
        paint = new Paint();

        // Initialize screenX and screenY because x and y are local
        screenX = x;
        screenY = y;

        // Initialize the player's bat
        bat = new Bat();

        // Create a ball
        ball = new Ball(screenX/2, screenY/2);


        restart();
    }

    // Runs when the OS calls onPause on BreakoutActivity method
    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }
    }

    // Runs when the OS calls onResume on BreakoutActivity method
    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        while (playing) {

            // Capture the current time in milliseconds in startFrameTime
            long startFrameTime = System.currentTimeMillis();

            // Update the frame
            // Update the frame
            if(!paused){
                update();
            }

            // Draw the frame
            draw();

            // Calculate the fps this frame
            // We can then use the result to
            // time animations and more.
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 4500 / timeThisFrame;
            }


        }
    }

    private void update(){
        // Move the bat if required
        bat.update(fps);

        // Update the ball
        ball.update(fps);


        // Check for ball colliding with bat
        if(RectF.intersects(ball.getRect(),bat.getRect())) {
            bat.setRandomXVelocity();
            bat.reverseYVelocity();
        }

        // Bounce the ball back when it hits the bottom of screen
        // And deduct a life
        if(bat.getRect().bottom > screenY - 50){
            bat.reverseYVelocity();
        }

        // Bounce the ball back when it hits the top of screen
        if(bat.getRect().top < 30) {
            bat.reverseYVelocity();
        }

        // If the bat hits left wall bounce
        if(bat.getRect().left < 10){
            bat.reverseXVelocity();
        }

        // If the ball hits right wall bounce
        if(bat.getRect().right > screenX - 10){
            bat.reverseXVelocity();
        }

        //If de ball hits left wall
        if(ball.getRect().left < 2){
            ball.setMovementState(ball.STOPPED);
        }

        // If the ball hits right wall bounce
        if(ball.getRect().right > screenX - 2){
            ball.setMovementState(ball.STOPPED);
        }




    }

    void restart(){
        // Put the ball back to the start
        bat.reset(screenX/2, screenY/2);


    }

    private void draw(){
        // Make sure our drawing surface is valid or game will crash
        if (ourHolder.getSurface().isValid()) {
            // Lock the canvas ready to draw
            canvas = ourHolder.lockCanvas();

            // Draw the background color
            canvas.drawColor(Color.argb(255,  26, 128, 182));

            // Draw everything to the screen

            // Choose the brush color for drawing
            paint.setColor(Color.argb(255,  255, 255, 255));

            // Draw the bat
            canvas.drawRect(bat.getRect(), paint);

            // Draw the ball
            canvas.drawRect(ball.getRect(), paint);

            // Change the brush color for drawing
            paint.setColor(Color.argb(255,  249, 129, 0));


            // Show everything we have drawn
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    // The SurfaceView class implements onTouchListener
    // So we can override this method and detect screen touches.
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        // Our code here
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

            // Player has touched the screen
            case MotionEvent.ACTION_DOWN:

                paused = false;

                if(motionEvent.getX() > screenX / 2){
                    ball.setMovementState(ball.RIGHT);
                }
                else{
                    ball.setMovementState(ball.LEFT);
                }

                break;

            // Player has removed finger from screen
            case MotionEvent.ACTION_UP:
                ball.setMovementState(ball.STOPPED);
                break;
        }

        return true;
    }
}
