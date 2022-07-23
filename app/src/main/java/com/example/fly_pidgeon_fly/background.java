package com.example.fly_pidgeon_fly;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class background {
    int x,y;
    Bitmap background;

    background (int screenX, int screenY, Resources res){
        background = BitmapFactory.decodeResource(res, R.drawable.background);
        background = Bitmap.createScaledBitmap(background, screenX, screenY, false);
    }
}
