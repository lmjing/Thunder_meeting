package com.sopt.android.thunder.manager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.sopt.android.thunder.R;
import com.sopt.android.thunder.component.ApplicationController;
import com.sopt.android.thunder.detail.model.Content_member;
import com.sopt.android.thunder.detail.model.User;
import com.sopt.android.thunder.network.NetworkService;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

// 관리자 탭 - 회원관리 액티비티
public class MemberChangeActivity extends AppCompatActivity {

    ListView out_list, register_list;
    TextView manager_member_all_number;
    MemberAdapter adapter; // 리스트뷰에 꼭 필요한 것
    ManagerRegisterAdapter manager_adapter;
    User userInfo;
    Content_member member;
    ArrayList<User> delete;

    // 네트워크
    private NetworkService networkService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_change);

        settingActionBar();

        initNetworkService();

        manager_member_all_number = (TextView) findViewById(R.id.manager_member_all_number);

        TabHost tabHost = (TabHost)findViewById(R.id.tabHost);
        tabHost.setup();

        // 관리자이름
        Intent intent = getIntent();
        userInfo = (User)intent.getParcelableExtra("UserInfo"); // User 정보

        // 맴버 보내는
        member = new Content_member();

        // 리스트뷰 클릭시 삭제될 리스트뷰 옮겨넣기
        delete = new ArrayList<>();

        // "회원 탈퇴" 탭
        TabHost.TabSpec out_tab = tabHost.newTabSpec("out").setIndicator("회원 탈퇴");
        out_tab.setContent(R.id.manager_member_out);
        tabHost.addTab(out_tab);

        // "관리자 지정" 탭
        TabHost.TabSpec register_tab = tabHost.newTabSpec("register").setIndicator("관리자 지정");
        register_tab.setContent(R.id.manager_member_register);
        tabHost.addTab(register_tab);

        tabHost.setCurrentTab(0);


        initListView();
        initAdapter();

        //callMemberList();

        // 리스트 클릭했을 때
        aboutView();
    }

    private void settingActionBar() {
        getSupportActionBar().setDisplayOptions(getSupportActionBar().DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.titlebar_member_change);
        getSupportActionBar().setIcon(R.mipmap.btn_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initListView() {
        out_list = (ListView) findViewById(R.id.out_list);
        register_list = (ListView) findViewById(R.id.register_list);
    }

    private void initAdapter() { // 초기화
        adapter = new MemberAdapter(getApplicationContext());
        manager_adapter = new ManagerRegisterAdapter(getApplicationContext());
        out_list.setAdapter(adapter);
        register_list.setAdapter(adapter);
    }

    private void initNetworkService(){
        // TODO: 13. ApplicationConoller 객체를 이용하여 NetworkService 가져오기
        networkService = ApplicationController.getInstance().getNetworkService();
    }

    // 회원 정보 띄우기
    @Override
    protected void onResume() {
        super.onResume();

        // TODO: 11. 서버에서 member 리스트로 받아오기 위한 GET 방식의 요청 구현
        Call<List<User>> memberListCall = networkService.getMemberList(userInfo.groupid);
        memberListCall.enqueue(new Callback<List<User>>() {

            @Override
            public void onResponse(Response<List<User>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<User> memberList = response.body(); // 여기서 정렬

                    manager_member_all_number.setText(Integer.toString(memberList.size()));


                    adapter.setItemDatas((ArrayList) memberList);
                    manager_adapter.setItemDatas((ArrayList)memberList);
                    adapter.notifyDataSetChanged();
                    manager_adapter.notifyDataSetChanged();
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


    void callMemberList(){
        // TODO: 11. 서버에서 member 리스트로 받아오기 위한 GET 방식의 요청 구현
        Call<List<User>> memberListCall = networkService.getMemberList(userInfo.groupid);
        memberListCall.enqueue(new Callback<List<User>>() {

            @Override
            public void onResponse(Response<List<User>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<User> memberList = response.body(); // 여기서 정렬

                    manager_member_all_number.setText(Integer.toString(memberList.size()));

                    adapter.setItemDatas((ArrayList) memberList);
                    manager_adapter.setItemDatas((ArrayList) memberList);
                    adapter.notifyDataSetChanged();
                    manager_adapter.notifyDataSetChanged();

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

    private void aboutView() {

        // 회원탈퇴 리스트 클릭했을 때
        out_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User memberId = (User) adapter.getItem(position);
                member.setGroupid(userInfo.getGroupid());
                member.setId(memberId.getId());
                member.setName(memberId.getName());
                // 리스트뷰 클릭시에 탈퇴를 묻는 다이알로그를 띄운다.
                dialog_show(position, member);
            }

        });

        // 관리자지정 리스트 클릭했을 때
        register_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final User memberRegister = (User) manager_adapter.getItem(position);
                member.setGroupid(userInfo.getGroupid());
                member.setId(memberRegister.getId());
                member.setName(memberRegister.getName());

                dialog_show_register(position, member);

            }
        });
    }

    // 관리자 지정 다이알로그
    private void dialog_show_register(final int position, final Content_member member) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(MemberChangeActivity.this);
        dlg.setMessage(member.getName()+"님의 관리자 권한 지정");
        // 탈퇴 실행
        dlg.setPositiveButton("관리자 등록", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // 관리자 지정
                Call<Content_member> managerRegister = networkService.getMemberManager(member);
                managerRegister.enqueue(new Callback<Content_member>() {

                    @Override
                    public void onResponse(Response<Content_member> response, Retrofit retrofit) {
                        if (response.isSuccess()) {
                            Toast.makeText(getApplicationContext(), member.getName() + " 님이 관리자로 지정되었습니다.", Toast.LENGTH_SHORT).show();
                            callMemberList();

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
        });
        // no 그대로
        dlg.setNegativeButton("관리자 해제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 관리자 해제
                Call<Content_member> memberRegisterCancel = networkService.getMemberSign(member);
                memberRegisterCancel.enqueue(new Callback<Content_member>() {

                    @Override
                    public void onResponse(Response<Content_member> response, Retrofit retrofit) {
                        if (response.isSuccess()) {
                            Toast.makeText(getApplicationContext(),member.getName()+"님 관리자 해제되었습니다.", Toast.LENGTH_SHORT).show();
                            callMemberList();
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
        });
        dlg.show();
    }

    // 탈퇴 다이알로그
    private void dialog_show(final int position, final Content_member member) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(MemberChangeActivity.this);
        dlg.setMessage(member.getName()+"님을 정말 탈퇴시키시겠습니까?");
        // 탈퇴 실행
        dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // 회원탈퇴
                Call<Content_member> memberDelete = networkService.getMemberName(member);
                memberDelete.enqueue(new Callback<Content_member>() {

                    @Override
                    public void onResponse(Response<Content_member> response, Retrofit retrofit) {
                        if (response.isSuccess()) {
                            Toast.makeText(getApplicationContext(), member.getName() + " 님을 탈퇴시키셨습니다.", Toast.LENGTH_SHORT).show();
                            callMemberList();

                        } else {
                            int statusCode = response.code();
                            Toast.makeText(getApplicationContext(), "다시 시도하세요", Toast.LENGTH_SHORT).show();
                            Log.i("MyTag", "응답코드 : " + statusCode);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(getApplicationContext(), "Failed to delete member",
                                Toast.LENGTH_SHORT).show();
                        Log.i("MyTag", "에러내용 : " + t.getMessage());

                    }
                });

            }
        });
        // no 그대로
        dlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(),"취소되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        dlg.show();
    }

}
