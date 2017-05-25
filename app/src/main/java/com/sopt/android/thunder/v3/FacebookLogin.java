package com.sopt.android.thunder.v3;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.sopt.android.thunder.GCM.RegistrationIntentService;
import com.sopt.android.thunder.MyGroupActivity;
import com.sopt.android.thunder.R;
import com.sopt.android.thunder.component.ApplicationController;
import com.sopt.android.thunder.detail.model.User;
import com.sopt.android.thunder.network.NetworkService;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class FacebookLogin extends Activity {

    /*
    <로그인 화면>
    로그인
     서버가 필요 없으니 지금 그대로 진행

    회원가입
     첫 로그인 시에 서버에 정보 저장
     현재 아이디 값 비교해서 이미 있을 경우 회원가입 되지 않음
        없을 경우 : 원래대로 저장, 회원가입 성공
            => intent로 어플 설명화면으로 이동하면 될 듯
        있을 경우 : 저장안됨, 아무 메세지도 띄우지 말 것
            => intent로 메인 화면으로 이동
     */

    Activity act;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    private NetworkService networkService;
    User n_user = new User(); //new_user 여기에 회원가입하는 놈 정보저장해
    User user = new User(); // 로그인할때
    SharedPreferences setting;
    SharedPreferences.Editor editor;

    public String name="", id="", password="";
    public String birth="";
    public String number="";//휴대폰 번호
    public String registerid="";

    // GCM 부분
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG="FacebookLogin";
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    public void getInstanceIdToken() {
        Log.i(TAG, "getInstanceIdToken 0");
        if (checkPlayServices()) {
            Log.i(TAG,"1");
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            Log.i(TAG,"2");
            startService(intent);
            Log.i(TAG, "3");
        }
    }

    //Google Play Service를 사용할 수 있는 환경이지를 체크한다.
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
                Log.i(TAG,"CheckPlayService complete");
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        Log.i(TAG, "This device is supported.");
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.test_facebook_login);

        //서버연결
       ApplicationController application = ApplicationController.getInstance();
        application.buildNetworkService("54.200.153.39", 8080);
        initNetworkService();

        //삭제해
        setting = getSharedPreferences("setting", 0);
        editor = setting.edit();
        editor.remove("login_id");
        editor.commit();

        act = this;
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(act);
        LoginButton loginButton = (LoginButton)findViewById(R.id.btn_facebook);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult result) {
                // TODO Auto-generated method stub
                Toast.makeText(act, "Succes", Toast.LENGTH_SHORT).show();

                //User에 정보저장
                GraphRequest request = GraphRequest.newMeRequest(result.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Application code
                        try {
                            Log.i("Facebook", "잘들어옴");

                            //Facebook정보 가져와서 저장
                            name = (String) response.getJSONObject().get("name");//페이스북 이름
                            id = (String) response.getJSONObject().get("id");

                            Log.i("MyTag", "FacebookLogin 이름 : "+name);
                            user.setId(id);
                            user.setBirth(birth);
                            user.setName(name);
                            user.setNumber(number);
                            user.setPassword("0000");

                            setting = getSharedPreferences("setting", 0);
                            if(setting.getString("login_id","").equals("")){
                                //회원가입 한적 없음

                                //GCM
                                Log.i(TAG, "getInstanceIdToken시작");
                                getInstanceIdToken();
                                Log.i(TAG, "getInstanceIdToken 끝");
                                registerid = setting.getString("Token", "");
                                Log.i(TAG, "getInstanceIdToken 완전끝");

                                //로그인 한적 있을 경우 로그인 요청 보냄(서버로)
                                user.setRegisterid(registerid);
                                Log.i("MyTag", "registerid : " + registerid);

                                Log.i("Facebook", "처음 로그인(회원가입)");

                                //회원가입 할때 id값 저장
                                Log.i("AutoLogin", "로그인 한적 없음");
                                setting = getSharedPreferences("setting", 0);
                                editor = setting.edit();
                                editor.putString("login_id", id);
                                editor.commit();
                                Log.i("AutoLogin", "로그인 한적 없음 " + id + "저장");

                                            Intent intent = new Intent(getApplicationContext(), joinActivity.class);
                                intent.putExtra("UserInfo", user); //user 정보가 들어있는 객체 전달
                                startActivity(intent);
                                            finish();
                            }else {
                                //한적 있음
                                Toast.makeText(getApplicationContext(), "환영합니다.", Toast.LENGTH_LONG).show();

                                //GCM
                                Log.i(TAG, "getInstanceIdToken시작");
                                getInstanceIdToken();
                                Log.i(TAG, "getInstanceIdToken 끝");
                                registerid = setting.getString("Token", "");
                                Log.i(TAG, "getInstanceIdToken 완전끝");

                                //로그인 한적 있을 경우 로그인 요청 보냄(서버로)
                                user.setRegisterid(registerid);
                                Log.i("MyTag", "registerid : " + registerid);

                                Call<User> userCall = networkService.loginUser(user);   //id,passwd가 저장된 user객체를 전달
                                userCall.enqueue(new Callback<User>() {
                                    @Override
                                    public void onResponse(Response<User> response, Retrofit retrofit) {
                                        if (response.isSuccess()) { //로그인 성공
                                            user = response.body();

                                            go_anotherclass(MyGroupActivity.class);
                                        } else {
                                            int statusCode = response.code();
                                            Toast.makeText(getApplicationContext(), "아이디 혹은 비밀번호가 일치하는 것이 없습니다.", Toast.LENGTH_LONG).show();
                                            Log.i("MyTag", "응답코드 : " + statusCode);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Throwable t) {
                                        Toast.makeText(getApplicationContext(), "아이디 혹은 비밀번호가 일치하는 것이 없습니다.", Toast.LENGTH_LONG).show();
                                        Log.i("MyTag", "로그인 에러내용 : " + t.getMessage());
                                        //finish();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            Log.i("Facebook", "1 : " + e.toString());
                        }
                    }
                });
                request.executeAsync();
            }

            @Override
            public void onError(FacebookException error) {
                // TODO Auto-generated method stub
                Toast.makeText(act, error + "", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                // TODO Auto-generated method stub
                Toast.makeText(act, "Cancel", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void go_anotherclass(final Class move_class){

        Call<User> userCall = networkService.getUser(id);   //id,passwd가 저장된 user객체를 전달
        Log.i("User", "id : " + id);
        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                if (response.isSuccess()) { //로그인 성공
                    Log.i("User", "성공");
                    User user = response.body();
                    Log.i("User", "아이디 : " + user.getId());
                    Log.i("User", "이름 : " + user.getName());
                    Log.i("User", "생일 : " + user.getBirth());
                    Log.i("User", "번호 : " + user.getNumber());

                    Intent intent = new Intent(getApplicationContext(), move_class);
                    intent.putExtra("UserInfo", user); //user 정보가 들어있는 객체 전달
                    startActivity(intent);
                    finish();
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
            }
        });
    }
    private void initNetworkService(){
        // TODO: 13. ApplicationConoller 객체를 이용하여 NetworkService 가져오기
        networkService = ApplicationController.getInstance().getNetworkService();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }
}
