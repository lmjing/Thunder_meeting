package com.sopt.android.thunder.v3.Comment.Image;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by lmjin_000 on 2016-02-09.
 */
public class MemoryImageCache implements ImageCache {
    private LruCache<String, Bitmap> lruCache;

    public MemoryImageCache() {
        final int maxMemory = (int)(Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        lruCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf( String key, Bitmap bitmap){
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    @Override
    public void addBitmap(String key, Bitmap bitmap) {
        if (bitmap == null)
            return;
        lruCache.put(key, bitmap);
    }

    @Override
    public Bitmap getBitmap(String key) {
        return lruCache.get(key);
    }

    @Override
        public void clear() {
        lruCache.evictAll();
    }
}