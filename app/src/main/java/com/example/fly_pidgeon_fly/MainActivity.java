package com.example.fly_pidgeon_fly;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private Boolean isMute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        findViewById(R.id.play_txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, gameActivity.class));
            }
        });

        TextView highScoreTxt = findViewById(R.id.highscore_tv);
        SharedPreferences sharedPreferences = getSharedPreferences("game", MODE_PRIVATE);
        highScoreTxt.setText("HIGHSCORE: "+sharedPreferences.getInt("highscore", 0));

        isMute = sharedPreferences.getBoolean("isMute", false);
        ImageView volumeCtr = findViewById(R.id.vol_btn);

        if (isMute){
            volumeCtr.setImageResource(R.drawable.ic_baseline_volume_off_24);
        }else{
            volumeCtr.setImageResource(R.drawable.ic_baseline_volume_up_24);
        }

        volumeCtr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isMute = !isMute;
                if (isMute){
                    volumeCtr.setImageResource(R.drawable.ic_baseline_volume_off_24);
                }else{
                    volumeCtr.setImageResource(R.drawable.ic_baseline_volume_up_24);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isMute", isMute);
                    editor.apply();

                }
            }
        });
    }
}