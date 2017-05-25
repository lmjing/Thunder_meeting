package com.sopt.android.thunder.manager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sopt.android.thunder.R;
import com.sopt.android.thunder.component.ApplicationController;
import com.sopt.android.thunder.detail.model.Content_group;
import com.sopt.android.thunder.network.NetworkService;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

// 관리자 탭 - 동아리 기본 정보 수정 액티비티
public class GroupInfoActivity extends AppCompatActivity implements View.OnClickListener{

    Button manage_modify_cancel, manage_modify_finish;
    TextView manage_input_groupName, manage_input_groupContent;

    Content_group group;

    // 네트워크
    private NetworkService networkService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        settingActionBar();

        initNetworkService();

        manage_modify_cancel=(Button) findViewById(R.id.manage_modify_cancel);
        manage_modify_finish = (Button) findViewById(R.id.manage_modify_finish);

        manage_input_groupContent = (TextView) findViewById(R.id.manage_input_groupContent);
        manage_input_groupName = (TextView) findViewById(R.id.manage_input_groupName);

        manage_modify_finish.setOnClickListener(this);
        manage_modify_cancel.setOnClickListener(this);
    }

    private void settingActionBar() {
        getSupportActionBar().setDisplayOptions(getSupportActionBar().DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.titlebar_group_info);
        getSupportActionBar().setIcon(R.mipmap.btn_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.manage_modify_cancel:
                finish();
                break;
            case R.id.manage_modify_finish:
                manageChangeInfo();
                break;
        }
    }

    private void manageChangeInfo() {

        group.setGroupcontents(manage_input_groupContent.getText().toString());
        group.setGroupname(manage_input_groupName.getText().toString());

        // TODO: 정보 가져오기
        Call<Content_group> editCall = networkService.editGroupInfo(group);
        editCall.enqueue(new Callback<Content_group>() {
            @Override
            public void onResponse(Response<Content_group> response, Retrofit retrofit) {
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

    private void initNetworkService(){
        // TODO: 13. ApplicationConoller 객체를 이용하여 NetworkService 가져오기
        networkService = ApplicationController.getInstance().getNetworkService();
    }
}
