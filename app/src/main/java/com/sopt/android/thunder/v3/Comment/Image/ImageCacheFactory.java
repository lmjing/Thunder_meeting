package com.sopt.android.thunder.v3.Comment.Image;

import android.util.Log;

import java.util.HashMap;

/**
 * Created by lmjin_000 on 2016-02-09.
 */
public class ImageCacheFactory {

    private static ImageCacheFactory instance = new ImageCacheFactory();
    public static ImageCacheFactory getInstance() {
        return instance;
    }

    private HashMap<String, ImageCache> cacheMap = new HashMap<String, ImageCache>();
    private ImageCacheFactory() {
    }

    public ImageCache createMemoryCache(String cacheName) {
        synchronized (cacheMap) {
            checkAleadyExists(cacheName);
            ImageCache cache = new MemoryImageCache();
            cacheMap.put(cacheName, cache);
            return cache;
        }
    }

    private void checkAleadyExists(String cacheName) {
        ImageCache cache = cacheMap.get(cacheName);
        if (cache != null) {
            Log.i("Image","이미 캐쉬가 있습니다");
        }
    }

    public ImageCache getCache(String cacheName) {
        ImageCache cache = cacheMap.get(cacheName);
        if (cache == null) {
            Log.i("Image","캐쉬를 찾을 수 없습니다");
        }
        return cache;
    }
}