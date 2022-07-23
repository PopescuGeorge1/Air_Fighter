package com.example.fly_pidgeon_fly;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class gameView extends SurfaceView implements Runnable{

    private Thread thread;
    private boolean isPlaying, isGameOver = false;
    private background background1, background2;
    private int screenX, screenY, score=0;
    public static float screenRatioX, screenRatioY;
    private flight f;
    private Paint paint;
    private bird []birds;
    private Random random;
    private List<bullet> bullets;
    private gameActivity activity;
    private SoundPool soundPool;
    private int sound;
    private SharedPreferences sharedPreferences;

    public gameView(gameActivity activity, int screenX, int screenY) {
        super(activity);

        this.activity = activity;
        sharedPreferences = activity.getSharedPreferences("game", Context.MODE_PRIVATE);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            AudioAttributes audioAttributes= new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .build();

        }else{
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }
        sound = soundPool.load(activity, R.raw.shoot, 1);

        this.screenX = screenX;
        this.screenY = screenY;
        screenRatioX = 1920f/screenX;
        screenRatioY = 1920f/screenY;
        background1 = new background(screenX, screenY, getResources());
        background2 = new background(screenX, screenY, getResources());

        f = new flight (this, screenY, getResources());

        bullets = new ArrayList<>();

        background2.x = screenX;

        paint = new Paint();
        paint.setTextSize(128);
        paint.setColor(Color.WHITE);



        birds = new bird[4];
        for (int i=0;i<4;i++){
            bird b = new bird(getResources());
            birds[i] = b;
        }

        random = new Random();
    }

    @Override
    public void run() {
        while (isPlaying){
            update ();
            draw ();
            sleep ();

        }
    }

    private void update(){
        background1.x -=10*screenRatioX;
        background2.x -=10*screenRatioX;
        //background will move 10pixels to the left
        if (background1.x+background1.background.getWidth()<0){
            //out of the screen
            background1.x = screenX;
        }
        if (background2.x+background2.background.getWidth()<0){
            //out of the screen
            background2.x = screenX;
        }

        if (f.isGoingUp){
            f.y -=30 * screenRatioY;//step length of the flight object; how much up on a tick
        }else{
            f.y += 30* screenRatioY;
        }

        if (f.y<0){
            f.y=0;
        }

        if (f.y > screenY - f.height)
            f.y = screenY - f.height;

        List <bullet> trash = new ArrayList<>();

        for (bullet b : bullets){
            if (b.x > screenX)
                //bullet off the screen
                trash.add(b);
                b.x+=50 *screenRatioX;

                for (bird bb : birds){
                    if (Rect.intersects(bb.getCollisionShape(), b.getCollisionShape())){//bullet intersects with the bird

                        score++;
                        bb.x =-500;
                        b.x = screenX + 500;
                        bb.wasShot = true;
                    }
                }

        }
        for (bullet b : trash){
            bullets.remove(b);
        }

        for (bird b:birds){
            b.x -=b.speed;
            if (b.x +b.width<0){

                if (!b.wasShot){
                    isGameOver=true;
                    return;
                }

                int bound = (int)(30*screenRatioX);
                b.speed = random.nextInt(bound);
                if (b.speed< 10*screenRatioX)
                    b.speed = (int)(10*screenRatioX);

                b.x = screenX;
                b.y = random.nextInt(screenY-b.height);

                b.wasShot=false;

            }
            if (Rect.intersects(b.getCollisionShape(), f.getCollisionShape())){
                isGameOver = true;
                return;
            }
        }

    }
    private void draw(){
        if (getHolder().getSurface().isValid()){
            Canvas canvas = getHolder().lockCanvas();
            //on the canvas will the background be drew
            canvas.drawBitmap(background1.background, background1.x, background1.y, paint);
            canvas.drawBitmap(background2.background, background2.x, background2.y, paint);
            canvas.drawText(score+"", screenX/2f, 164, paint);
            saveIfHighScore();

            for (bird bb : birds){
                canvas.drawBitmap(bb.getBird(), bb.x, bb.y, paint);
            }

            if (isGameOver){
                isPlaying=false;
                canvas.drawBitmap(f.getDead(), f.x, f.y, paint);
                getHolder().unlockCanvasAndPost(canvas);
                saveIfHighScore();
                waitBeforeExiting();

                return;
            }



            //draw the flight
            canvas.drawBitmap(f.getFlight(), f.x, f.y, paint);

            for (bullet b :  bullets){
                canvas.drawBitmap(b.bullet, b.x, b.y, paint);
            }

            //show canvas on the screen
            getHolder().unlockCanvasAndPost(canvas);


        }
    }

    private void waitBeforeExiting() {
        try{
            Thread.sleep(3000);
            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saveIfHighScore() {
        if (sharedPreferences.getInt("highscore", 0)<score){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("highscore", score);
            editor.apply();
        }
    }

    private  void sleep(){
        try{
            thread.sleep(17);
        }catch (Exception e){

        }
    }

    public void resume(){
        isPlaying=true;
        thread = new Thread (this);
        thread.start();
    }

    public void pause(){
        try{
            isPlaying=false;
            thread.join();
        }catch(Exception e){

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (event.getX()< screenX/2);
                    f.isGoingUp = true;
                break;
            case MotionEvent.ACTION_UP:
                f.isGoingUp = false;
                if (event.getX()>screenX/2)
                    f.toShoot++;
                break;
        }
        return true;
    }

    public void newBullet() {

        if (!sharedPreferences.getBoolean("isMute", false))
            soundPool.play(sound, 1, 1, 0, 0, 1);

        bullet b = new bullet(getResources());
        b.x = f.x + f.width;
        b.y = f.y + (f.height/2);
        bullets.add(b);

    }

    //THE PLANE GOES UP IF YOU TOUCH THE LEFT SIDE OF THE SCREEN AND DOWN WHEN YOU DON'T TOUCH THE SCREEN
}
