package com.sopt.android.thunder.manager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sopt.android.thunder.R;
import com.sopt.android.thunder.component.ApplicationController;
import com.sopt.android.thunder.detail.model.User;
import com.sopt.android.thunder.network.NetworkService;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

// 관리자 탭 가입 관리 액티비티 (승인요청)
public class SignActivity extends AppCompatActivity {

    TextView text;
    ImageView img;
    ListView member_sign_list;
    MemberSignAdapter adapter;
    TextView member_sign_num;
    int signmember;


    // 네트워크
    private NetworkService networkService;

    User userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        settingActionBar();

        initNetworkService();

        member_sign_num = (TextView) findViewById(R.id.member_sign_num);
        text = (TextView) findViewById(R.id.no_wait_text);
        img = (ImageView) findViewById(R.id.no_wait_img);

        Intent intent = getIntent();
        userInfo =  (User) intent.getParcelableExtra("UserInfo");

        initListView();
        initAdapter();

        // 리스트뷰 클릭시
        aboutView();

    }


    private void initNetworkService(){
        // TODO: 13. ApplicationConoller 객체를 이용하여 NetworkService 가져오기
        networkService = ApplicationController.getInstance().getNetworkService();
    }

    private void settingActionBar() {
        getSupportActionBar().setDisplayOptions(getSupportActionBar().DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.titlebar_member_sign);
        getSupportActionBar().setIcon(R.mipmap.btn_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initListView() {
        member_sign_list = (ListView) findViewById(R.id.member_sign_list);
    }

    private void initAdapter() { // 초기화
        adapter = new MemberSignAdapter(getApplicationContext());
        member_sign_list.setAdapter(adapter);
    }

    private void aboutView() {
        // 리스트뷰 선택
        member_sign_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User member = (User) adapter.getItem(position);
                Intent intent = new Intent(getApplication(),SignDetailActivity.class);
                intent.putExtra("Member", member);
                intent.putExtra("UserInfo",userInfo);
                startActivity(intent);
            }
        });
    }

    // 가입 승인 대기자 띄우기
    @Override
    protected void onResume() {
        super.onResume();

        // TODO: 11. 서버에서 member 리스트로 받아오기 위한 GET 방식의 요청 구현
        Call<List<User>> memberQueueListCall = networkService.getMemberQueueList(userInfo.groupid);
        memberQueueListCall.enqueue(new Callback<List<User>>() {

            @Override
            public void onResponse(Response<List<User>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<User> memberQueueList = response.body(); // 여기서 정렬
                    signmember = memberQueueList.size();

                    adapter.setItemDatas((ArrayList) memberQueueList);
                    adapter.notifyDataSetChanged();

                    if(signmember>0){
                        member_sign_num.setText(Integer.toString(signmember)+" ");
                        text.setVisibility(View.INVISIBLE);
                        img.setVisibility(View.INVISIBLE);
                    }

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
}