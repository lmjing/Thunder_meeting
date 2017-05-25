package com.sopt.android.thunder.manager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sopt.android.thunder.R;
import com.sopt.android.thunder.component.ApplicationController;
import com.sopt.android.thunder.detail.model.Content_notice;
import com.sopt.android.thunder.detail.model.User;
import com.sopt.android.thunder.network.NetworkService;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class NoticeRegisterActivity extends AppCompatActivity implements View.OnClickListener {

    boolean check = false;
    int check_int = 0;
    EditText notice_register_title, notice_register_context;
    Button notice_check_btn, notice_register_cancel, notice_register_finish;

    User userInfo;

    // 네트워크
    private NetworkService networkService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_register);

        settingActionBar();

        initNetworkService();

        Intent intent = getIntent();
        userInfo = (User)intent.getParcelableExtra("UserInfo"); // User 정보

        notice_register_title = (EditText) findViewById(R.id.notice_register_title);
        notice_register_context = (EditText) findViewById(R.id.notice_register_context);

        notice_check_btn = (Button) findViewById(R.id.notice_check_btn);
        notice_register_cancel = (Button) findViewById(R.id.notice_register_cancel);
        notice_register_finish = (Button) findViewById(R.id.notice_register_finish);

        notice_check_btn.setOnClickListener(this);
        notice_register_cancel.setOnClickListener(this);
        notice_register_finish.setOnClickListener(this);


    }

    private void settingActionBar() {
        getSupportActionBar().setDisplayOptions(getSupportActionBar().DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.titlebar_notice_register);
        getSupportActionBar().setIcon(R.mipmap.btn_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.notice_check_btn:
                checkBtn();
                break;
            case R.id.notice_register_cancel:
                cancelBtn();
                break;
            case R.id.notice_register_finish:
                finishBtn();
                break;

        }

    }

    private void checkBtn() {
        if(check == false){
            notice_check_btn.setBackgroundResource(R.drawable.check_notice_ok);
            check = true;
            check_int = 1;
        }
        else if(check == true){
            notice_check_btn.setBackgroundResource(R.drawable.check_notice);
            check = false;
            check_int = 0;
        }
    }

    private void cancelBtn() {
        Toast.makeText(getApplicationContext(), "등록이 취소되었습니다.", Toast.LENGTH_SHORT).show();
        finish();

    }

    private void finishBtn() {
        // 서버에 보낼 공지사항 내용들
        Content_notice notice = new Content_notice();
        notice.setGroupid(userInfo.groupid);
        notice.setTitle(notice_register_title.getText().toString());
        notice.setContext(notice_register_context.getText().toString());
        java.util.Calendar cal = java.util.Calendar.getInstance();
        //현재 년도, 월, 일
        int year = cal.get ( cal.YEAR );
        int month = cal.get ( cal.MONTH ) + 1 ;
        int date = cal.get ( cal.DATE ) ;
        notice.setDate(Integer.toString(year) + Integer.toString(month) + Integer.toString(date));
        notice.setCheck(check_int);

        // 서버연동
        Call<Content_notice> noticeCall = networkService.createNotice(notice);
        noticeCall.enqueue(new Callback<Content_notice>() {
            @Override
            public void onResponse(Response<Content_notice> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Toast.makeText(getApplicationContext(), "공지사항이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    int statusCode = response.code();
                    Log.i("MyTag", "응답코드 : " + statusCode);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("MyTag", "서버 onFailure 에러내용 : " + t.getMessage());
            }
        });

        finish();
    }

    private void initNetworkService(){
        // TODO: 13. ApplicationConoller 객체를 이용하여 NetworkService 가져오기
        networkService = ApplicationController.getInstance().getNetworkService();
    }

}
