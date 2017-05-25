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
import com.sopt.android.thunder.detail.model.User;
import com.sopt.android.thunder.network.NetworkService;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

// 관리자 탭
public class ManageActivity extends AppCompatActivity implements View.OnClickListener {

    TextView manage_group_manager, manage_group_name;

    Button manage_group_register_notice,manage_group_member_change, manage_group_sign_all, manage_group_info;
    Button manage_group_allNum, manage_group_sign_wait;
    User userInfo;

    // 네트워크
    private NetworkService networkService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        // 관리자 탭
        settingActionBar();
        initView();

        // 관리자이름
        Intent intent = getIntent();
        userInfo = (User)intent.getParcelableExtra("UserInfo");
        manage_group_manager.setText(userInfo.getName()+" / 관리자");

    }

    private void settingActionBar() {
        getSupportActionBar().setDisplayOptions(getSupportActionBar().DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.titlebar_group);
        getSupportActionBar().setIcon(R.mipmap.btn_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    // 버튼 관리
    public void initView(){

        // 관리자 이름
        manage_group_manager = (TextView) findViewById(R.id.manage_group_manager);
        // 소속 이름
        manage_group_name = (TextView) findViewById(R.id.manage_group_name);

        // notice 등록
        manage_group_register_notice = (Button) findViewById(R.id.manage_group_register_notice);
        // 동아리 기본 정보
        manage_group_info = (Button) findViewById(R.id.manage_group_info);
        // 가입 관리
        manage_group_sign_all = (Button) findViewById(R.id.manage_group_sign_all);
        // 회원 관리
        manage_group_member_change = (Button) findViewById(R.id.manage_group_member_change);

        manage_group_register_notice.setOnClickListener(this);
        manage_group_sign_all.setOnClickListener(this);
        manage_group_member_change.setOnClickListener(this);
        manage_group_info.setOnClickListener(this);

    }

    // 버튼 클릭 리스너
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            // notice 등록
            case R.id.manage_group_register_notice:
                registerNotice();
                Log.i("ManageActivity", "notice 등록");
                break;
            // 가입 관리 (승인처리)
            case R.id.manage_group_sign_all:
                signAll();
                Log.i("ManageActivity", "가입관리(승인처리)");
                break;
            // 회원 관리 (탈퇴관리)
            case R.id.manage_group_member_change:
                memberChange();
                Log.i("ManageActivity", "회원 관리(탈퇴/관리자관리)");
                break;
            // 동아리 기본 정보
            case R.id.manage_group_info:
                groupInfo();
                Log.i("ManageActivity", "동아리 기본정보");
                break;
        }

    }

    // notice 등록 버튼 누를 시
    private void registerNotice() {
        Intent intent = new Intent(getApplicationContext(), NoticeActivity.class);
        startActivity(intent);
        finish();
    }
    // 가입 관리(승인처리) 버튼 누를 시
    private void signAll(){
        Intent intent = new Intent(getApplicationContext(), SignActivity.class);
        startActivity(intent);
        finish();
    }
    // 회원 관리 (탈퇴 및 관리자권한 관리) 버튼 누를 시
    private void memberChange(){
        Intent intent = new Intent(getApplicationContext(), MemberChangeActivity.class);
        intent.putExtra("UserInfo",userInfo);
        startActivity(intent);
        finish();
    }
    // 동아리 기본정보 버튼 누를 시
    private void groupInfo(){
        Intent intent = new Intent(getApplicationContext(), GroupInfoActivity.class);
        startActivity(intent);
        finish();
    }

    private void initNetworkService(){
        // TODO: 13. ApplicationConoller 객체를 이용하여 NetworkService 가져오기
        networkService = ApplicationController.getInstance().getNetworkService();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // TODO: 11. 서버에서 member 숫자 받아오기 위한 GET 방식의 요청 구현
        Call<Integer> memberNum = networkService.getMemberNum(userInfo.groupid);
        memberNum.enqueue(new Callback<Integer>() {

            @Override
            public void onResponse(Response<Integer> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    int number = response.body(); // 여기서 정렬
                    manage_group_allNum.setText("전체 회원 수 " + Integer.toString(number) + " 명");

                } else {
                    int statusCode = response.code();
                    Log.i("MyTag", "응답코드 : " + statusCode);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed to load memberNumber",
                        Toast.LENGTH_SHORT).show();
                Log.i("MyTag", "에러내용 : " + t.getMessage());

            }
        });
    }
}
