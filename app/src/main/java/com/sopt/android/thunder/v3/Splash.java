package com.sopt.android.thunder.v3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.sopt.android.thunder.MyGroupActivity;
import com.sopt.android.thunder.R;
import com.sopt.android.thunder.component.ApplicationController;
import com.sopt.android.thunder.detail.model.Login;
import com.sopt.android.thunder.detail.model.User;
import com.sopt.android.thunder.network.NetworkService;
import com.sopt.android.thunder.v3.Comment.Image.ImageCacheFactory;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by lmjin_000 on 2015-11-09.
 */
public class Splash extends AppCompatActivity {
    Handler handler = new Handler(); //Handler객체를 이용해 2~3초 뒤에 무언가 작동하는 기능을 수행할 수 있습니다.
    //Handler은 AutoImport가 되지 않는데 이건 Handler 클래스가 여러 종류가 있을때 발생합니다. Alt + Enter를 누르고 android.os.Handler 클래스를 선택해주세요.

    SharedPreferences setting;
    private NetworkService networkService;
    User user = new User();;

    //v2
    private ImageCacheFactory mMemoryCache;
    private String name="", id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        // ip, port 연결
        ApplicationController application = ApplicationController. getInstance();
        application.buildNetworkService("54.200.153.39", 8080);
        initNetworkService();

        //v2 처음 캐쉬생성
        mMemoryCache.getInstance().createMemoryCache("여여붙");
        Log.i("Image", "처음 로그인 캐쉬생성");

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getID();
            }
        }, 1000);

    }

    private void initNetworkService(){
        // TODO: 13. ApplicationConoller 객체를 이용하여 NetworkService 가져오기
        networkService = ApplicationController.getInstance().getNetworkService();
    }

    private void getID(){
        setting = getSharedPreferences("setting", 0);
        id = setting.getString("login_id","");

        Call<Login> userCall = networkService.idCheck(id);

        userCall.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Response<Login> response, Retrofit retrofit) {
                if (response.isSuccess()) { //로그인 성공
                    Log.i("Show", "메인으로 가자");
                    getuserinfo();

                } else {
                    int statusCode = response.code();
                    Login content_temp = response.body();
                    Log.i("Show", "로그인으로 가자");
                    Intent intent = new Intent(getApplicationContext(), FacebookLogin.class);
                    startActivity(intent);
                    finish();
                }
            }


            @Override
            public void onFailure(Throwable t) {
                Log.i("Show", "로그인 에러내용 : " + t.getMessage());
            }
        });
    }

    //user 정보 가져오기
    public void getuserinfo()
    {
        try{
        Log.i("User", "1");
        // TODO: 정보 가져오기
        Call<User> userCall = networkService.getUser(id);   //id,passwd가 저장된 user객체를 전달
        Log.i("User", "id : " + id);
        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                if (response.isSuccess()) { //로그인 성공
                    Log.i("User", "성공");
                    user = response.body();
                    Log.i("User", "아이디 : " + user.getId());
                    Log.i("User", "이름 : " + user.getName());
                    Log.i("User", "생일 : " + user.getBirth());
                    Log.i("User", "번호 : " + user.getNumber());

                    Intent intent = new Intent(getApplicationContext(), MyGroupActivity.class);
                    intent.putExtra("UserInfo",user); //user 정보가 들어있는 객체 전달
                    startActivity(intent);
                    finish();
                    //로그인 유지 위해 아이디 저장
                } else {
                    int statusCode = response.code();
                    Toast.makeText(getApplicationContext(), "아이디 혹은 비밀번호가 일치하는 것이 없습니다.", Toast.LENGTH_LONG).show();
                    Log.i("User", "응답코드 : " + statusCode);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(), "아이디 혹은 비밀번호가 일치하는 것이 없습니다.", Toast.LENGTH_LONG).show();
                Log.i("User", "로그인 에러내용 : " + t.getMessage());
                //finish();
            }
        });
        } catch (Exception e) {//오류처리
            Log.v("spla erro", e.toString());
        }
    }
}