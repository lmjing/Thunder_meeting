package com.sopt.android.thunder.v3.Comment.Image;

import android.graphics.Bitmap;

/**
 * Created by lmjin_000 on 2016-02-09.
 */
public interface ImageCache {
    public void addBitmap(String key, Bitmap bitmap);
    public Bitmap getBitmap(String key);
    public void clear();
}
