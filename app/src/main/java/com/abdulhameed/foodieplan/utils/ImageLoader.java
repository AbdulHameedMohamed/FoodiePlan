package com.abdulhameed.foodieplan.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.BitmapShader;

import java.io.IOException;
import java.net.URL;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ImageLoader {

    private static final String TAG = "ImageLoader";

    public static Observable<Bitmap> loadCircularBitmap(String profileUrl) {
        return Observable.create((ObservableOnSubscribe<Bitmap>) emitter -> {
            try {
                Bitmap originalBitmap = BitmapFactory.decodeStream(new URL(profileUrl).openStream());
                Bitmap circularBitmap = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(circularBitmap);
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setShader(new BitmapShader(originalBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
                float radius = Math.min(originalBitmap.getWidth(), originalBitmap.getHeight()) / 2f;
                canvas.drawCircle(originalBitmap.getWidth() / 2f, originalBitmap.getHeight() / 2f, radius, paint);

                int desiredSize = 100;
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(circularBitmap, desiredSize, desiredSize, true);

                emitter.onNext(scaledBitmap);
                emitter.onComplete();
            } catch (IOException e) {
                emitter.onError(e);
            }
        }).subscribeOn(Schedulers.io());
    }
}