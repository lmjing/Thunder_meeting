package com.sopt.android.thunder;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sopt.android.thunder.component.ApplicationController;
import com.sopt.android.thunder.detail.model.Content_group;
import com.sopt.android.thunder.network.NetworkService;
import com.tsengvn.typekit.Typekit;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class CreateGroupActivity extends AppCompatActivity {
    EditText input_groupName, input_groupContent;
    Button btn_createGroup;
    Content_group group = new Content_group();
    private NetworkService networkService;
    SharedPreferences setting;
    private String id = null;



    private void initNetworkService(){
        // TODO: 13. ApplicationConoller 객체를 이용하여 NetworkService 가져오기
        networkService = ApplicationController.getInstance().getNetworkService();
    }
    private void initView(){
        input_groupName = (EditText)findViewById(R.id.input_groupName);
        input_groupContent = (EditText)findViewById(R.id.input_groupContent);
        btn_createGroup = (Button)findViewById(R.id.btn_createGroup);
    }

    private void createNewGroup(){  //새로운 소속 생성할 때

        try {
            Call<Content_group> groupCall = networkService.createGroup(group, id);   //id,passwd가 저장된 user객체를 전달
            groupCall.enqueue(new Callback<Content_group>() {
                @Override
                public void onResponse(Response<Content_group> response, Retrofit retrofit) {
                    if (response.isSuccess()) { //로그인 성공
                        finish();
                    } else {
                        int statusCode = response.code();
                        Log.i("createNewGroup", "응답코드 : " + statusCode);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(getApplicationContext(), "특정 번개 클릭하는게 서버에서 아에 오류반응이 났어요.", Toast.LENGTH_LONG).show();
                    Log.i("createNewGroup", "소속 생성 에러내용 : " + t.getMessage());
                    //finish();
                }
            });
        }catch(Exception e) {//오류처리
            Log.v("createNewGroup erro",e.toString());
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);//커스텀 타이틀 바
        setContentView(R.layout.layout_creategroup);
        Typekit.getInstance()//폰트적용
                .addNormal(Typekit.createFromAsset(this, "fonts/NanumBarunGothicLight.otf"));
        initView();
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_login);//커스텀 타이틀 바
////////
        //커스텀 액션 바
        getSupportActionBar().setDisplayOptions(getSupportActionBar().DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.titlebar_creategroup);
////////
        //서버 연결
        ApplicationController application = ApplicationController. getInstance ();
        application.buildNetworkService("54.200.153.39", 8080);
        initNetworkService();

        //sharedpreference
        setting = getSharedPreferences("setting", 0);
        id = setting.getString("login_id","");

        btn_createGroup.setOnClickListener(new View.OnClickListener() { //그룹 생성
            @Override
            public void onClick(View v) {
                group.setGroupname(input_groupName.getText().toString());
                group.setGroupcontents(input_groupContent.getText().toString());
                createNewGroup();
            }
        });
    }
}














