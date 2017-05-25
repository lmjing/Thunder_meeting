package com.sopt.android.thunder.v3;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sopt.android.thunder.R;
import com.sopt.android.thunder.component.ApplicationController;
import com.sopt.android.thunder.detail.model.User;
import com.sopt.android.thunder.network.NetworkService;
import com.tsengvn.typekit.Typekit;
import com.tsengvn.typekit.TypekitContextWrapper;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class EditActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView actionbar_title_edit;
    private TextView edit_name, edit_year, edit_month, edit_day, edit_phone1, edit_phone2, edit_phone3; // 유저이름
    private Button modify_finish, modify_cancel;

    User userInfo;

    private NetworkService networkService;
    @Override
    protected void attachBaseContext(Context newBase) {//폰트 적용 함수
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Typekit.getInstance()//폰트적용
                .addNormal(Typekit.createFromAsset(this, "fonts/NanumBarunGothicLight.otf"));
        try {
            // 액션바 세팅
            settingActionBar();
            Log.i("Edit", "oncreate 0");
            Intent intent = getIntent();
            userInfo = (User) intent.getParcelableExtra("UserInfo");
            Log.i("Edit", "oncreate 1");
            // 정보받아와 넣어줌
            set_UserInfo();
            Log.i("Edit", "oncreate 2");

            ApplicationController application = ApplicationController.getInstance();
            application.buildNetworkService("54.200.153.39", 8080);
            initNetworkService();

            modify_cancel.setOnClickListener(this);
            modify_finish.setOnClickListener(this);

        }catch(Exception e){
            Log.v("EditActivity erro","에러내용 :"+e.toString());
        }

    }

    private void set_UserInfo() {
        edit_name = (TextView) findViewById(R.id.edit_name);
        edit_year = (TextView)findViewById(R.id.edit_year);
        edit_month = (TextView)findViewById(R.id.edit_month);
        edit_day = (TextView)findViewById(R.id.edit_day);
        edit_phone1 = (TextView)findViewById(R.id.edit_phone1);
        edit_phone2 = (TextView)findViewById(R.id.edit_phone2);
        edit_phone3 = (TextView)findViewById(R.id.edit_phone3);
        modify_cancel = (Button) findViewById(R.id.modify_cancel);
        modify_finish = (Button)findViewById(R.id.modify_finish);

        // 이름 값
        edit_name.setText(userInfo.getName());

        // 생일 값이 null이 아닐때면
        if(userInfo.getBirth()!= null){
            edit_year.setText(userInfo.getBirth().substring(0,4));
            edit_month.setText(userInfo.getBirth().substring(4,6));
            edit_day.setText(userInfo.getBirth().substring(6,8));
        }

        // 전화번호 값이 null이 아닐때면
        if(userInfo.getNumber()!=null) {
            edit_phone1.setText(userInfo.getNumber().substring(0,3));
            edit_phone2.setText(userInfo.getNumber().substring(3,7));
            edit_phone3.setText(userInfo.getNumber().substring(7,11));
        }

    }

    private void settingActionBar() {
        getSupportActionBar().setDisplayOptions(getSupportActionBar().DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.titlebar_edit);
        getSupportActionBar().setIcon(R.mipmap.btn_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    private void initNetworkService(){
        // TODO: 13. ApplicationConoller 객체를 이용하여 NetworkService 가져오기
        networkService = ApplicationController.getInstance().getNetworkService();
    }


    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.modify_cancel:
                Log.i("MyTag", "정보수정취소");
                Toast.makeText(getApplicationContext(),"정보가 수정되지 않았습니다.",Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.modify_finish:
                editUerInfo();
                break;
        }

    }

    private void editUerInfo() {

        int phoneLength = edit_phone1.length() + edit_phone2.length() + edit_phone3.length();
        if(phoneLength < 11) {
            Toast.makeText(getApplicationContext(), "휴대폰 번호를 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
      //폰번호만 수정가능
            // 나머지 하려면 회원탈퇴하세요
            // TODO: 정보 가져오기
            String phoneNumber = edit_phone1.getText().toString() +
                    edit_phone2.getText().toString() + edit_phone3.getText().toString();
            userInfo.setNumber(phoneNumber);
        userInfo.setPassword("0000");

            Call<User> editCall = networkService.editUser(userInfo);
            editCall.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Response<User> response, Retrofit retrofit) {
                    if (response.isSuccess()) { //정보수정
                        Toast.makeText(getApplicationContext(), "변경이 완료되었습니다.", Toast.LENGTH_LONG).show();
                        finish();
                        Log.i("MyTag", "정보수정완료");
                    } else {
                        int statusCode = response.code();
                        Toast.makeText(getApplicationContext(), "다시 시도하세요.", Toast.LENGTH_LONG).show();
                        Log.i("MyTag", "응답코드 : " + statusCode);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.i("MyTag", "정보수정 에러내용 : " + t.getMessage());
                    Toast.makeText(getApplicationContext(), "다시 시도하세요.", Toast.LENGTH_LONG).show();
                }
            });
    }
}
