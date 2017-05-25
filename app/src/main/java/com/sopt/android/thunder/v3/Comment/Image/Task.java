package com.sopt.android.thunder.v3.Comment.Image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lmjin_000 on 2016-02-09.
 */
public class Task extends AsyncTask<String, Void, Bitmap> {

    private Bitmap bmImg;
    private final WeakReference<ImageView> imageViewReference;
    //캐쉬
    private ImageCacheFactory mMemoryCache;

    public Task( ImageView imageView){
        // WeakReference 를 사용하는 이유는 image 처럼 메모리를 많이 차지하는 객체에 대한 가비지컬렉터를 보장하기 위해서입니다.
        imageViewReference = new WeakReference<ImageView>(imageView);
        this.mMemoryCache = mMemoryCache;
    }

    @Override
    protected void onPreExecute(){
        //캐쉬 초기화
    }

    @Override
    protected Bitmap doInBackground(String... id) {
        try {
            ImageCache imageCache = ImageCacheFactory.getInstance().getCache("여여붙");
            Log.i("Image","Task id받음 id : "+id[0]);
            Bitmap bitmap = imageCache.getBitmap(id[0]);

            if(bitmap == null) {//캐쉬에 이미지가 없다면
                URL url = new URL("https://graph.facebook.com/" + id[0] + "/picture?type=normal");
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setDoInput(true);
                conn.connect();

                InputStream is = conn.getInputStream();

                bmImg = BitmapFactory.decodeStream(is);

                imageCache.addBitmap(id[0], bmImg);
                Log.i("Image", "캐쉬에 이미지 없음");
                Log.i("Image","이미지 저장완료");
                is.close();
            }else { // 캐쉬에 이미지가 있다면
                bmImg = imageCache.getBitmap(id[0]);
                Log.i("Image","캐쉬에 이미지 있음");
                Log.i("Image","이미지 꺼내옴");
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return bmImg;
    }

    @Override
    protected void onPostExecute( Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final CircularImageView imageView = (CircularImageView)imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}