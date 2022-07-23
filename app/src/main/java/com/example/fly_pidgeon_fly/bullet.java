package com.example.fly_pidgeon_fly;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import static com.example.fly_pidgeon_fly.gameView.screenRatioX;
import static com.example.fly_pidgeon_fly.gameView.screenRatioY;

public class bullet {
    int x,y, width, height;
    Bitmap bullet;

    bullet (Resources res){
        bullet = BitmapFactory.decodeResource(res, R.drawable.bullet);
        width = bullet.getWidth();
        height = bullet.getHeight();

        width/=4;
        height/=4;

        width *= (int) (width*screenRatioX);
        height *= (int) (height*screenRatioY);

        bullet = Bitmap.createScaledBitmap(bullet, width, height, false);

    }

    Rect getCollisionShape () {
        return new Rect (x,y, x+width, y+height);
    }
}
