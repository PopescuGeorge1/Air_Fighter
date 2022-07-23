package com.example.fly_pidgeon_fly;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class gameActivity extends AppCompatActivity {

    private gameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //set to fullscreen
getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);

        gameView = new gameView(this, point.x, point.y);

        setContentView(gameView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        gameView.resume();
    }
}