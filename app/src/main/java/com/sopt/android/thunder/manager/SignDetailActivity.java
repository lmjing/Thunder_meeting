package com.sopt.android.thunder.manager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sopt.android.thunder.R;
import com.sopt.android.thunder.component.ApplicationController;
import com.sopt.android.thunder.detail.model.Content_member;
import com.sopt.android.thunder.detail.model.User;
import com.sopt.android.thunder.network.NetworkService;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class SignDetailActivity extends AppCompatActivity implements View.OnClickListener{

    User member;
    User userInfo;

    TextView member_name_sign_detail, member_id_sign_detail;
    TextView member_birthday_sign_detail, member_phoneNum_sign_detail;
    Button member_sign_cancel_btn, member_sign_finish_btn;

    Content_member memberSign;

    // 네트워크
    private NetworkService networkService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_detail);

        settingActionBar();

        initNetworkService();

        Intent intent = getIntent();
        member = (User) intent.getParcelableExtra("Member");
        userInfo = (User) intent.getParcelableExtra("UserInfo");
        memberSign = new Content_member();
        memberSign.setId(member.getId());
        memberSign.setGroupid(userInfo.getGroupid());
        memberSign.setName(member.getName());

        member_id_sign_detail = (TextView) findViewById(R.id.member_id_sign_detail);
        member_name_sign_detail = (TextView) findViewById(R.id.member_name_sign_detail);
        member_birthday_sign_detail = (TextView) findViewById(R.id.member_birthday_sign_detail);
        member_phoneNum_sign_detail = (TextView) findViewById(R.id.member_phoneNum_sign_detail);
        member_sign_cancel_btn = (Button) findViewById(R.id.member_sign_cancel_btn);
        member_sign_finish_btn = (Button) findViewById(R.id.member_sign_finish_btn);

        member_sign_cancel_btn.setOnClickListener(this);
        member_sign_finish_btn.setOnClickListener(this);

        member_name_sign_detail.setText(member.getName());
        member_id_sign_detail.setText("( " + member.getId() + " )");
        member_birthday_sign_detail.setText(member.birth.substring(0,4).toString()+"년 "
                +member.birth.substring(4, 6).toString() + "월 " + member.birth.substring(6, 8) + "일");
        member_phoneNum_sign_detail.setText(member.number.substring(0,3).toString()+"-"
                +member.number.substring(3,7)+"-"+member.number.substring(7,11));

    }

    private void settingActionBar() {
        getSupportActionBar().setDisplayOptions(getSupportActionBar().DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.titlebar_member_sign_detail);
        getSupportActionBar().setIcon(R.mipmap.btn_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initNetworkService(){
        // TODO: 13. ApplicationConoller 객체를 이용하여 NetworkService 가져오기
        networkService = ApplicationController.getInstance().getNetworkService();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.member_sign_cancel_btn:
                Toast.makeText(getApplicationContext(),"승인요청이 취소되었습니다", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.member_sign_finish_btn:
                sign_finish();
                break;
        }
    }

    // 가입 요청 승인
    private void sign_finish() {
        // 승인 요청
        Call<Content_member> memberSignOK = networkService.getMemberSign(memberSign);
        memberSignOK.enqueue(new Callback<Content_member>() {

            @Override
            public void onResponse(Response<Content_member> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Toast.makeText(getApplicationContext(),member.getName()+"가입 요청이 승인되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    int statusCode = response.code();
                    Toast.makeText(getApplicationContext(), "다시 시도하세요", Toast.LENGTH_SHORT).show();
                    Log.i("MyTag", "응답코드 : " + statusCode);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed to register for manager",
                        Toast.LENGTH_SHORT).show();
                Log.i("MyTag", "에러내용 : " + t.getMessage());

            }
        });
    }
}
