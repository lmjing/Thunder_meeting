package com.sopt.android.thunder.v3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.sopt.android.thunder.CustomAdapter;
import com.sopt.android.thunder.GroupActivity;
import com.sopt.android.thunder.R;
import com.sopt.android.thunder.component.ApplicationController;
import com.sopt.android.thunder.detail.model.Content;
import com.sopt.android.thunder.detail.model.User;
import com.sopt.android.thunder.detail.model.ViewCheck;
import com.sopt.android.thunder.manager.ManageActivity;
import com.sopt.android.thunder.network.NetworkService;
import com.sopt.android.thunder.v3.Comment.Image.ImageCache;
import com.sopt.android.thunder.v3.Comment.Image.ImageCacheFactory;
import com.sopt.android.thunder.v3.register.Register;
import com.tsengvn.typekit.Typekit;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

// 번개 목룍 Activity
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //v2 네비게이션 드로우 사진
    Handler handler = new Handler();

    ListView listView; // 번개 리스트
    FloatingActionButton fab;

    CustomAdapter adapter; // 리스트뷰에 꼭 필요한 것

    // intent 넘겨주는
    User user = new User();

    // 네트워크
    private NetworkService networkService;

    // myPage 큰 이름
    TextView userName;
    // 정보 수정

    //로그인된 아이디
    private String id = null;
    SharedPreferences setting;
    SharedPreferences.Editor editor;
    ImageView thunder_bookmark;

    //내가 추가한 부분 2/21(일) 관리자 권한 1 or 0
    int authority_check,groupid;


    //끗
    //번개 보기 할 때 서버에서 받을 check를 위하여 !
    ViewCheck vCheck = new ViewCheck();
    @Override
    protected void attachBaseContext(Context newBase) {//폰트 적용 함수
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Typekit.getInstance()//폰트적용
                .addNormal(Typekit.createFromAsset(this, "fonts/NanumBarunGothicLight.otf"));
        //내가 추가한 부분 2/21(일)
        Intent intent_authority  = getIntent();
        authority_check = intent_authority.getIntExtra("authority check",0);
        groupid = intent_authority.getIntExtra("groupid",0);

        // 끗
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        settingActionBar();

        //v2 네트워크
        ApplicationController application = ApplicationController. getInstance ();
        application.buildNetworkService("54.200.153.39", 8080);
        initNetworkService();

        //v2 페이스북
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        //v2 id 저장
        setting = getSharedPreferences("setting", 0);
        id = setting.getString("login_id","");

        // 네비게이션 드로어
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Log.i("Main","oncreat 1");
        navigationView.setNavigationItemSelectedListener(this);

        // 리스트뷰 설정
        initView();
        initAdapter();
        // 리스트뷰 각 하나씩 클릭시 번개보기
        Log.i("Main","oncreate");
        aboutView();
        // 번개 등록
        registerThunder();


        // mypage 정보 보이게
        View header = navigationView.getHeaderView(0);
        userName = (TextView) header.findViewById(R.id.userName_mypage);

         /*
        v2 User정보 가져오기
        intent로 받아온 정보는 수정 후 갱신되지 않아
        그냥 서버로 부터 받아옴
         */

        getUser();
        Log.i("User", "User name1" + user.getName());


        //v2 사진 화면에 띄우기
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://graph.facebook.com/" + id + "/picture?type=normal");
                    InputStream is = url.openStream();
                    final Bitmap bm = BitmapFactory.decodeStream(is);

                    // 프로필 사진 캐시에 저장 -> 나중에 댓글에선 캐시에 저장된것만 꺼내올거임
                    ImageCache imageCache = ImageCacheFactory.getInstance().getCache("여여붙");
                    imageCache.addBitmap(id, bm);
                    Log.i("Image","메인 : 이미지 저장완료");

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //ImageView img = (ImageView) findViewById(R.id.myPicture);
                            //img.setImageBitmap(bm);
                            CircularImageView circularImageView = (CircularImageView) findViewById(R.id.myPicture);
                            circularImageView.setImageBitmap(bm);
                            circularImageView.setBorderColor(Color.parseColor("#FFFFFF"));
                            circularImageView.setBorderWidth(4);
                            circularImageView.addShadow();
                        }
                    });
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    private void settingActionBar() {
        getSupportActionBar().setDisplayOptions(getSupportActionBar().DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.titlebar_mypage);
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.listView);
    }
    private void toServer_thunderid(int position){  //특정 번개를 클릭했을 때 서버에 넘겨 줌
        final Content itemData_temp = (Content) adapter.getItem(position);
        Log.i("Main toServer", "position:" + position);
        Call<ViewCheck> userCall = networkService.toServer(itemData_temp.getThunderid(), user.getId());   //id,passwd가 저장된 user객체를 전달

        userCall.enqueue(new Callback<ViewCheck>() {
            @Override
            public void onResponse(Response<ViewCheck> response, Retrofit retrofit) {
                if (response.isSuccess()) { //로그인 성공
                    vCheck = response.body();
                    Log.i("Main 로그인 성공 1", "vCheck.getCheck():" + vCheck.getCheck());
                    Log.i("Main 로그인 성공 1", "itemData_temp.getTitle():" + itemData_temp.getTitle());
                    Intent intent = new Intent(getApplicationContext(), ShowActivity.class); //"번개보기 엑티비티" // 번개 자세히 보기 창
                    intent.putExtra("check_number", vCheck.getCheck());//check넘버를 보낸다
                    intent.putExtra("showThunder", itemData_temp); // 누른 번개 보기
                    intent.putExtra("UserInfo",user);
                    intent.putExtra("url", itemData_temp.getUrl());
                    Log.i("url", "url(main activity url보내줄때) : " + itemData_temp.getUrl());
                    Log.i("Main 로그인 성공 2", "..정말?");
                    startActivity(intent);
                    Log.i("Main 로그인 성공 3", "..정말?");
                } else {
                    int statusCode = response.code();
                    Log.i("Show toServer", "응답코드 : " + statusCode);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(), "특정 번개 클릭하는게 서버에서 아에 오류반응이 났어요.", Toast.LENGTH_LONG).show();
                Log.i("Show toServer", "번개보기 띄우는거 에러내용 : " + t.getMessage());
                //finish();
            }
        });
    }

    private void aboutView() {
        Log.i("Main","aboutView 0");
        // 리스트뷰 클릭
        // 번개보기 넘겨주기
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    //Content itemData_temp = (Content) adapter.getItem(position);
                    toServer_thunderid(position);
                    Log.i("Main", "aboutView 1");
                    //view.setBackgroundColor(Color.GRAY);
                    // 번개보기
                    //Intent intent = new Intent(getApplicationContext(), ShowActivity.class); //"번개보기 엑티비티" // 번개 자세히 보기 창
                    //intent.putExtra("showThunder", itemData_temp); // 누른 번개 보기
                    //startActivity(intent);
                    Log.i("Main", "aboutView 4");


                } catch (Exception e) {//오류처리
                    Log.v("Main erro", e.toString());
                }
                //
                //
                //Toast.makeText(getApplicationContext(), "" + itemData_temp.title + "\n", Toast.LENGTH_SHORT).show();
            }
        });

        // 즐겨찾기 기능 아직 덜 구현
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                thunder_bookmark = (ImageView) findViewById(R.id.thunder_bookmark);
                thunder_bookmark.setVisibility(View.VISIBLE);
                //itemDatas.remove(position);
                //adapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "즐겨찾기에 추가되었습니다", Toast.LENGTH_SHORT).show();
                return true;
            }
        });


    }

    private void initAdapter() { // 초기화
        adapter = new CustomAdapter(getApplicationContext());
        listView.setAdapter(adapter);

    }

    // 번개등록 창
    // AddActivity.class
    private void registerThunder() {
        fab = (FloatingActionButton) findViewById(R.id.add_button);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                intent.putExtra("UserInfo", user); //user 정보가 들어있는 객체 전달
                intent.putExtra("groupid", groupid);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        try {


            Intent intent;
            int id = item.getItemId();

            // 네비게이션 메뉴 하나씩 작동되는 곳곳
            // 개인정보 수정
            if (id == R.id.nav_edit) {
                Log.i("Main", "네비드로워: 개인정보 수정");
                intent = new Intent(getApplicationContext(), EditActivity.class);
                intent.putExtra("UserInfo", user);
                startActivity(intent);
            }
            // 소속 변경
            else if (id == R.id.nav_group) {
                Log.i("Main", "네비드로워: 소속 변경");
                intent = new Intent(getApplicationContext(), GroupActivity.class);
                startActivity(intent);
            }
            // 탈퇴
            else if (id == R.id.nav_delete) {
                Log.i("Main", "네비드로워: 탈퇴");
                deleteDialog();
            }
            // 로그아웃
            else if (id == R.id.nav_logout) {
                Log.i("Main", "네비드로워: 로그아웃");
                signout();
            }
            // 관리자
            else if (id== R.id.nav_manage){
                Log.i("Main", "네비드로워: 관리자");
                manage();
            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);


        } catch(Exception e){
            Log.v("Main error","네비드러우워 파트야."+e.toString());
        }
        return true;
    }

    //v2 탈퇴
    private void deleteDialog() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setMessage("정말 탈퇴하시겠습니까?");
        // 탈퇴 실행
        dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder dlg2 = new AlertDialog.Builder(MainActivity.this);
                dlg2.setMessage("이용해주셔서 감사합니다.\n페이스북 연동의 경우 \n어플 내에서 연동 해제를\n도와드리기 어렵습니다.\n" +
                        "\t페이스북 계정에서 '설정 > 앱\n>여기여기 붙어라 >연동 해제'를\n클릭하여 해제를 하시면\n연동이 해제 됩니다.");
                dlg2.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteInfo();
                    }
                });
                dlg2.show();
            }
        });
        // no 그대로
        dlg.setNegativeButton("아니오", null);
        dlg.show();

        //탈퇴할때 캐쉬삭제
        ImageCacheFactory.getInstance().getCache("여여붙").clear();
    }

    //v2 회원탈퇴
    private void deleteInfo() {
        // 회원탈퇴
        Call<User> delete = networkService.getDelete(id);
        delete.enqueue(new Callback<User>() {

            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    //Toast.makeText(getApplicationContext(), "그동안 이용해주셔서 감사합니다", Toast.LENGTH_SHORT);
                    setting = getSharedPreferences("setting", 0);
                    editor = setting.edit();
                    editor.remove("login_id");
                    editor.commit();
                    LoginManager.getInstance().logOut();
                    //탈퇴(연동해제)를 제공하는 API존재하지 않음. 야매로 로그아웃만 시키고 페이스북에 들어가서 해제하게 해야할 듯.
                    Intent intent2 = new Intent(getApplicationContext(), FacebookLogin.class);
                    startActivity(intent2);
                    finish();
                } else {
                    int statusCode = response.code();
                    Toast.makeText(getApplicationContext(), "다시 시도하세요", Toast.LENGTH_SHORT);
                    Log.i("MyTag", "응답코드 : " + statusCode);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed to logout",
                        Toast.LENGTH_SHORT).show();
                Log.i("MyTag", "에러내용 : " + t.getMessage());

            }
        });
    }

    //v2 로그아웃
    public void signout() {

        // TODO: 11. 로그아웃
        //Call<String> logout = networkService.newString("logout");
        Call<User> logout = networkService.getString(id);
        logout.enqueue(new Callback<User>() {

            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Toast.makeText(getApplicationContext(), "로그아웃되었습니다", Toast.LENGTH_SHORT);
                    LoginManager.getInstance().logOut();
                    Intent intent2 = new Intent(getApplicationContext(), FacebookLogin.class);
                    startActivity(intent2);
                    finish();
                } else {
                    int statusCode = response.code();
                    Toast.makeText(getApplicationContext(), "다시 시도하세요", Toast.LENGTH_SHORT);
                    Log.i("MyTag", "응답코드 : " + statusCode);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed to logout",
                        Toast.LENGTH_SHORT).show();
                Log.i("MyTag", "에러내용 : " + t.getMessage());

            }
        });
    }

    // 관리자
    public void manage(){

        // 관리자가 아니면
        if(authority_check == 0){
            Toast.makeText(getApplicationContext(),"권한이 없습니다.", Toast.LENGTH_SHORT).show();
        }
        // 관리자이면
        else {
            Intent intent = new Intent(getApplicationContext(), ManageActivity.class);
            intent.putExtra("UserInfo", user);
            startActivity(intent);
            finish();
        }
    }

    private void initNetworkService(){
        // TODO: 13. ApplicationConoller 객체를 이용하여 NetworkService 가져오기
        networkService = ApplicationController.getInstance().getNetworkService();
    }


    @Override
    protected void onResume() {
        super.onResume();

        //v2 user정보 갱신
        getUser();
        // TODO: 11. 서버에서 Thumbnail을 리스트로 받아오기 위한 GET 방식의 요청 구현
        Call<List<Content>> thunderListCall = networkService.getThunderList(groupid);
        thunderListCall.enqueue(new Callback<List<Content>>() {

            @Override
            public void onResponse(Response<List<Content>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<Content> thunderList = response.body(); // 여기서 정렬

                    //adapter.sort(thunderList);
                    adapter.setItemDatas((ArrayList)thunderList);
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

    public void getUser(){
        //회원 정보 가져오기 (수정되면 새로운 정보 가져와야해서 만듦)
        Call<User> userCall = networkService.getUser(id);   //id,passwd가 저장된 user객체를 전달
        Log.i("User", "id : " + id);
        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                if (response.isSuccess()) { //로그인 성공
                    Log.i("User", "성공");
                    user = response.body();
                    //v2 사진을 위한 이름 설정
                    String name = user.getName();
                    Log.i("Facebook", "(메인)페이스북 이름 : "+name);
                    Log.i("User","User name2"+user.getName());
                    userName.setText(name);
                    Log.i("User","User name (함수):"+user.getName());
                } else {
                    int statusCode = response.code();
                    Toast.makeText(getApplicationContext(), "아이디 혹은 비밀번호가 일치하는 것이 없습니다.", Toast.LENGTH_LONG).show();
                    Log.i("User", "응답코드 : " + statusCode);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(), "아이디 혹은 비밀번호가 일치하는 것이 없습니다.", Toast.LENGTH_LONG).show();
                Log.i("User", "로그인 에러내용 : " + t.getMessage());
            }
        });
    }
}