package com.sopt.android.thunder;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sopt.android.thunder.component.ApplicationController;
import com.sopt.android.thunder.detail.model.Content_Mygroup;
import com.sopt.android.thunder.detail.model.Content_group;
import com.sopt.android.thunder.detail.model.User;
import com.sopt.android.thunder.detail.model.ViewCheck;
import com.sopt.android.thunder.network.NetworkService;
import com.sopt.android.thunder.v3.MainActivity;
import com.tsengvn.typekit.Typekit;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MyGroupActivity extends AppCompatActivity {
    EditText input_id,input_pw, input_search;
    Button btn_join,btn_login,dialog_groupbtn;
    private NetworkService networkService;
    User user = new User();
    Content_Mygroup groupInfo = new Content_Mygroup();
    SharedPreferences setting;
    private String id = null;
    CustomAdapter_Mygroups adapter; // 리스트뷰에 꼭 필요한 것
    ListView listView; // 번개 리스트
    FloatingActionButton fab;
    TextView dialog_groupContent,dialog_groupName,dialog_rootid;


    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable edit) {    //edittext 변경
            String s = edit.toString();

            Call<List<Content_group>> groupListCall = networkService.getGroupList(id);
            groupListCall.enqueue(new Callback<List<Content_group>>() {
                @Override
                public void onResponse(Response<List<Content_group>> response, Retrofit retrofit) {
                    if (response.isSuccess()) {
                        List<Content_group> groupList = response.body();
                        //adapter.sort(thunderList);
                        adapter.setItemDatas((ArrayList) groupList);
                        adapter.notifyDataSetChanged();
                    } else {
                        int statusCode = response.code();
                        Log.i("MyTag", "응답코드 : " + statusCode);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(getApplicationContext(), "Failed to load thunderList",
                            Toast.LENGTH_SHORT).show();
                    Log.i("MyTag", "에러내용 : " + t.getMessage());

                }
            });


        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {   }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {   }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);//커스텀 타이틀 바
        setContentView(R.layout.layout_mygroup);
        Typekit.getInstance()//폰트적용
                .addNormal(Typekit.createFromAsset(this, "fonts/NanumBarunGothicLight.otf"));
        initView();
        Log.i("MyGroup", "main 1");
        getSupportActionBar().setDisplayOptions(getSupportActionBar().DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.titlebar_mygroup);        //커스텀 액션 바
        Log.i("MyGroup", "main 2");
        ApplicationController application = ApplicationController. getInstance ();//서버 연결
        application.buildNetworkService("54.200.153.39", 8080);
        initNetworkService();
        Log.i("MyGroup", "main 3");

        Intent intent = getIntent();
        user = (User)intent.getParcelableExtra("UserInfo");


        //sharedPreference
        setting = getSharedPreferences("setting", 0);
        id = setting.getString("login_id", "");
        Log.i("MyGroup","main 4");
        try {
            initAdapter();//어댑터 초기
            aboutView();//리스트 뷰, 즉 소속 리스트 중 특정 뷰 클릭시 작동하는 함수
            registerGroup();//플로팅 버튼. 새로운 그룹 등록시 작동
        }catch(Exception e){
            Log.v("MyGroup erro", e.toString());
        }
///////////////
    }



    private void initNetworkService(){
        // TODO: 13. ApplicationConoller 객체를 이용하여 NetworkService 가져오기
        networkService = ApplicationController.getInstance().getNetworkService();
    }

    private void initView(){
        input_id = (EditText)findViewById(R.id.input_id);
        input_pw = (EditText)findViewById(R.id.input_pw);
        input_search = (EditText)findViewById(R.id.searchGroup);
        btn_join = (Button)findViewById(R.id.btn_join);
        btn_login = (Button)findViewById(R.id.btn_login);
        listView = (ListView)findViewById(R.id.groupList_my);
    }
    private void initAdapter() { // 초기화
        Log.i("MyGroup","initAdapter 1");
        adapter = new CustomAdapter_Mygroups(getApplicationContext());
        Log.i("MyGroup","initAdapter 2");
        listView.setAdapter(adapter);
        Log.i("MyGroup", "initAdapter 3");
    }
    private void aboutView() {
        Log.i("selectG", "aboutView 0");
        // 리스트뷰 클릭
        // 번개보기 넘겨주기
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    //Content itemData_temp = (Content) adapter.getItem(position);
                    groupInfo = (Content_Mygroup) adapter.getItem(position);
                    //groupInfo.setGroupid(position);
                    dialog_group();
                    Log.i("selectG", "aboutView 1");
                } catch (Exception e) {//오류처리
                    Log.v("selectG erro", e.toString());
                }
                //Toast.makeText(getApplicationContext(), "" + itemData_temp.title + "\n", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toServer_userid(){
        Call<ViewCheck> groupListCall = networkService.choice_MyGroup(id, groupInfo.getGroupid());
        groupListCall.enqueue(new Callback<ViewCheck>() {
            @Override
            public void onResponse(Response<ViewCheck> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    ViewCheck vCheck = response.body();
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class); //로그인 성공 시 메인화면을 띄운다.
                        intent.putExtra("authority check", vCheck.getCheck());//관리자인지 아닌지 보낸다. 관리자이면 1, 아니면 0
                        intent.putExtra("groupid", groupInfo.getGroupid());//그룹id
                        intent.putExtra("UserInfo",user); //user 정보가 들어있는 객체 전달
                    Log.i("MyGroup", "authority value : "+vCheck.getCheck());
                        startActivity(intent); //main_intent 회원가입창을 띄운다
                        //finish();
                } else {
                    int statusCode = response.code();
                    Toast.makeText(getApplicationContext(), groupInfo.getGroupname().toString() + "의 일원이 아니십니다.", Toast.LENGTH_SHORT);
                    Log.i("dialog", "소속선택 응답코드 : " + statusCode);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                //Toast.makeText(getApplicationContext(), "Failed",Toast.LENGTH_SHORT).show();
                Log.i("dialog", "소속선택 에러내용 : " + t.getMessage());

            }
        });
    }



    private void dialog_group(){
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_group, null);
        //멤버의 세부내역 입력 Dialog 생성 및 보이기
        //testing();
        Log.i("Show", "showuser 0");
        dialog_groupName = (TextView) dialogView.findViewById(R.id.dialog_groupName);
        //dialog_groupContent = (TextView) dialogView.findViewById(R.id.dialog_groupContent);
        //dialog_rootid = (TextView) dialogView.findViewById(R.id.dialog_rootid);
        dialog_groupbtn = (Button) dialogView.findViewById(R.id.dialog_groupbtn);
        Log.i("Show", "showuser 1");
        dialog_groupName.setText(groupInfo.getGroupname().toString());
        //dialog_groupContent.setText(groupInfo.getGroupcontents().toString());
        //dialog_rootid.setText(groupInfo.getRootid().toString());

        AlertDialog.Builder buider = new AlertDialog.Builder(MyGroupActivity.this); //AlertDialog.Builder 객체 생성
        AlertDialog dialog = buider.create();//설정한 값으로 AlertDialog 객체 생성
        buider.setView(dialogView); //위에서 inflater가 만든 dialogView 객체 세팅 (Customize)
        dialog_groupbtn.setOnClickListener(new View.OnClickListener() {//그룹 선택 눌렀을 때
            @Override
            public void onClick(View v) {
                toServer_userid();
            }
        });
        //버튼 제거
        buider.setPositiveButton(null,null);
        buider.setNegativeButton(null, null);
        //Dialog의 바깥쪽을 터치했을 때 Dialog를 없앨지 설정
        dialog.setCanceledOnTouchOutside(true);//없어지도록 설정
        //Dialog 보이기
        buider.show();

    }

    private void registerGroup() {
        fab = (FloatingActionButton) findViewById(R.id.addGroup_my);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), selectGroupActivity.class);
                intent.putExtra("groupid", groupInfo.getGroupid());//그룹id
                startActivity(intent);
            }
        });

    }

    private void bringGroupList(){
        // TODO: 11. 서버에서 Thumbnail을 리스트로 받아오기 위한 GET 방식의 요청 구현
        Call<List<Content_Mygroup>> groupListCall = networkService.getMyGroupList(id);
        groupListCall.enqueue(new Callback<List<Content_Mygroup>>() {
            @Override
            public void onResponse(Response<List<Content_Mygroup>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<Content_Mygroup> groupList = response.body();
                    //adapter.sort(thunderList);
                    adapter.setItemDatas((ArrayList) groupList);
                    adapter.notifyDataSetChanged();
                } else {
                    int statusCode = response.code();
                    Log.i("MyTag", "응답코드 : " + statusCode);
                }
            }
            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed to load thunderList",
                        Toast.LENGTH_SHORT).show();
                Log.i("MyTag", "에러내용 : " + t.getMessage());
            }
        });
    }

    @Override
    protected void onResume() {//액티비티가 실행되기 직전에 항상 리스트를 받아온다.
        super.onResume();
        bringGroupList();
    }

}














