package com.sopt.android.thunder.manager;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.sopt.android.thunder.R;
import com.sopt.android.thunder.component.ApplicationController;
import com.sopt.android.thunder.detail.model.Content_notice;
import com.sopt.android.thunder.detail.model.User;
import com.sopt.android.thunder.network.NetworkService;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

// 관리자 탭 - notice 등록 액티비티
public class NoticeActivity extends AppCompatActivity {

    ListView notice_list;
    FloatingActionButton fab;
    NoticeAdapter adapter;

    User userInfo;

    // 네트워크
    private NetworkService networkService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        settingActionBar();

        // 관리자이름
        Intent intent = getIntent();
        userInfo = (User)intent.getParcelableExtra("UserInfo"); // User 정보

        initNetworkService();

        initListView();
        initAdapter();

        // 리스트 클릭했을 때
        aboutView();

        // 노티스 등록
        fab = (FloatingActionButton) findViewById(R.id.notice_add_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NoticeRegisterActivity.class);
                intent.putExtra("UserInfo", userInfo);
                startActivity(intent);
            }
        });

    }
    private void initListView() {
        notice_list = (ListView) findViewById(R.id.notice_list);
    }

    private void initAdapter() { // 초기화
        adapter = new NoticeAdapter(getApplicationContext());
        notice_list.setAdapter(adapter);
    }

    private void aboutView(){

        notice_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Content_notice notice = (Content_notice) adapter.getItem(position);
                Intent intent = new Intent(getApplication(), NoticeShowActivity.class);
                intent.putExtra("Notice", notice);
                startActivity(intent);
            }
        });

    }

    // 회원 정보 띄우기
    @Override
    protected void onResume() {
        super.onResume();

        // TODO: 11. 서버에서 member 리스트로 받아오기 위한 GET 방식의 요청 구현
        Call<List<Content_notice>> noticeListCall = networkService.getNoticeList(userInfo.groupid);
        noticeListCall.enqueue(new Callback<List<Content_notice>>() {

            @Override
            public void onResponse(Response<List<Content_notice>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<Content_notice> noticeList = response.body(); // 여기서 정렬

                    adapter.setItemDatas((ArrayList) noticeList);
                    adapter.notifyDataSetChanged();
                } else {
                    int statusCode = response.code();
                    Log.i("MyTag", "응답코드 : " + statusCode);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed to load memberList",
                        Toast.LENGTH_SHORT).show();
                Log.i("MyTag", "에러내용 : " + t.getMessage());

            }
        });

    }

    private void initNetworkService(){
        // TODO: 13. ApplicationConoller 객체를 이용하여 NetworkService 가져오기
        networkService = ApplicationController.getInstance().getNetworkService();
    }

    private void settingActionBar() {
        getSupportActionBar().setDisplayOptions(getSupportActionBar().DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.titlebar_notice);
        getSupportActionBar().setIcon(R.mipmap.btn_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }
}
