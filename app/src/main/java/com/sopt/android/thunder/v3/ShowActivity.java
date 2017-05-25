package com.sopt.android.thunder.v3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.sopt.android.thunder.GridAdapter;
import com.sopt.android.thunder.R;
import com.sopt.android.thunder.component.ApplicationController;
import com.sopt.android.thunder.detail.model.Comment;
import com.sopt.android.thunder.detail.model.Content;
import com.sopt.android.thunder.detail.model.Participant;
import com.sopt.android.thunder.detail.model.User;
import com.sopt.android.thunder.network.NetworkService;
import com.sopt.android.thunder.v3.Comment.CommentAdapter;
import com.sopt.android.thunder.v3.register.ShowDetailMap;
import com.tsengvn.typekit.Typekit;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class ShowActivity extends AppCompatActivity {

    //v2그냥 다 복붙
    private Content content;
    private User user = new User();
    private ImageView icon ,refresh;
    private NetworkService networkService;
    private TextView editTitle ,editYear, editMonth, editDay, editHour, editMinute, editMemo, editAddress, commentCount;
    private EditText editComment;
    private Button btn_people, btn_participate, btn_detail, btn_comment_send, btn_more, btn_share;

    long throwthunder;

    SharedPreferences setting;

    int cnt;

    //gridview를 위한 것들 4가지.
    List<Participant> personList;
    GridAdapter adapter;
    GridView gridview;
    TextView text_cnt;

    //번개 불참/참가/삭제 버튼 관리
    int check;

    //v2
    private String url;
    private ListView listView;
    private String user_id;

    //댓글 등록(1)/수정(2) 관리
    int comment_check=1;
    //댓글 더보기 몇번 눌렸는지
    int comment_more_check = 1;

    //댓글
    CommentAdapter adapter2;

    private void showInfo(){
        editTitle.setText(content.getTitle().toString());
        editAddress.setText(content.getAddress().toString());
        editMemo.setText(content.getContents().toString());
        editYear.setText(content.getDate().toString().substring(0, 4));
        editMonth.setText(content.getDate().toString().substring(4, 6));
        editDay.setText(content.getDate().toString().substring(6, 8));
        editHour.setText(content.getDate().toString().substring(8, 10));
        editMinute.setText(content.getDate().toString().substring(10, 12));
        setImage(content.getType());
    }

    private void setImage(int icon_flag)
    {
        //Log.i("MyTag", "icon_flag : " + icon_flag);
        switch (icon_flag)
        {
            case 1 : icon.setImageDrawable(getResources().getDrawable(R.drawable.beer_icon)); break;
            case 2 : icon.setImageDrawable(getResources().getDrawable(R.drawable.art_icon)); break;
            case 3 : icon.setImageDrawable(getResources().getDrawable(R.drawable.bolling_icon)); break;
        }
    }

    private void initNetworkService(){
        // TODO: 13. ApplicationConoller 객체를 이용하여 NetworkService 가져오기
        networkService = ApplicationController.getInstance().getNetworkService();
    }

    private void initView() {
        editTitle = (TextView) findViewById(R.id.title);
        editYear = (TextView) findViewById(R.id.year);
        editMonth = (TextView) findViewById(R.id.month);
        editDay = (TextView) findViewById(R.id.day);
        editHour = (TextView) findViewById(R.id.hour);
        editMinute = (TextView) findViewById(R.id.minute);
        editMemo = (TextView) findViewById(R.id.memo);
        editAddress = (TextView) findViewById(R.id.address);
        btn_participate = (Button) findViewById(R.id.btn_participate);
        btn_people = (Button) findViewById(R.id.btn_people);
        btn_detail = (Button) findViewById(R.id.btn_detail);
        icon = (ImageView)findViewById(R.id.group_icon);
        //댓글
        btn_comment_send = (Button)findViewById(R.id.button_send_message);
        editComment = (EditText)findViewById(R.id.edit_text_message);
        commentCount = (TextView)findViewById(R.id.comment_count);
        refresh = (ImageView)findViewById(R.id.comment_refresh);
        btn_more = (Button)findViewById(R.id.btn_more);
        btn_share = (Button)findViewById(R.id.btn_share);
        listView = (ListView)findViewById(R.id.expandableListView);
    }

    private void showUser(){ //이 번개에 참여한 인원을 dialog로 보여준다.
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_people, null);
        //멤버의 세부내역 입력 Dialog 생성 및 보이기
        //testing();
        //Log.i("Show", "showuser 0");
        text_cnt = (TextView) dialogView.findViewById(R.id.text_cnt);
        gridview = (GridView) dialogView.findViewById(R.id.gridView);
        //Log.i("Show","showuser 1");
        adapter = new GridAdapter(getApplicationContext(), R.layout.person, personList);
        //Log.i("Show","showuser 2");

        cnt = adapter.getCount();
        text_cnt.setText(String.valueOf(cnt));
        //Log.i("Show", "showuser 3");
        gridview.setAdapter(adapter);
        //Log.i("Show", "showuser 4");
        AlertDialog.Builder buider = new AlertDialog.Builder(ShowActivity.this); //AlertDialog.Builder 객체 생성
        AlertDialog dialog = buider.create();//설정한 값으로 AlertDialog 객체 생성
        buider.setView(dialogView); //위에서 inflater가 만든 dialogView 객체 세팅 (Customize)
        //버튼 제거
        buider.setPositiveButton(null,null);
        buider.setNegativeButton(null,null);
        //Dialog의 바깥쪽을 터치했을 때 Dialog를 없앨지 설정
        dialog.setCanceledOnTouchOutside(true);//없어지도록 설정
        //Dialog 보이기
        buider.show();
    }

    private void throwUserInfo(){
        try {//오류 처리
            //Log.i("Show", "throwUserInfo 0");
            // TODO: 정보 가져오기
            throwthunder = content.getThunderid();
            //필요한곳에서나 onCreate에서
            //무조건 선언
            setting = getSharedPreferences("setting", 0);
            //id값 가져옴
            String id = setting.getString("login_id","");
            //Log.i("Show", "throwUserInfo 00" + throwthunder);
            //Log.i("Show", "try in 1");
            Call<Content> userCall = networkService.pleaseThunder3(throwthunder, id);   //참여하기 누를 때
            if(check == 1)//등록자가 자신의 게시글을 봤을 때. = > "참여" 버튼을 "삭제" 로 바꾸기
                userCall = networkService.pleaseThunder1(throwthunder);   //참여하기 누를 때
            else if(check == 2)//그 게시글 참석자가 자신이 신청한 번개를 봤을 때 = > "참여" 버튼을 "불참" 으로 바꾸기
                userCall = networkService.pleaseThunder2(throwthunder, id);   //참여하기 누를 때
            else  //미 참석자가 번개보기를 봤을 때. => "참석" 버튼 그대로 냅 둬
                userCall = networkService.pleaseThunder3(throwthunder, id);   //참여하기 누를 때
            //Log.i("Show", "throwUserInfo 1");

            userCall.enqueue(new Callback<Content>() {
                @Override
                public void onResponse(Response<Content> response, Retrofit retrofit) {
                    Log.i("Show", "throwUserInfo 2");
                    if (response.isSuccess()) { //로그인 성공
                        if(check == 1) //등록자가 자신의 게시글을 봤을 때. = > "참여" 버튼을 "삭제" 로 바꾸기
                            Toast.makeText(getApplicationContext(), "등록하신 번개가 삭제되었습니다.", Toast.LENGTH_LONG).show();
                        else if(check == 2) //그 게시글 참석자가 자신이 신청한 번개를 봤을 때 = > "참여" 버튼을 "불참" 으로 바꾸기
                            Toast.makeText(getApplicationContext(), "선택하신 번개의 참여 신청이 취소되었습니다.", Toast.LENGTH_LONG).show();
                        else  //미 참석자가 번개보기를 봤을 때. => "참석" 버튼 그대로 냅 둬
                            Toast.makeText(getApplicationContext(), "해당 번개에 참여 신청이 되었습니다.", Toast.LENGTH_LONG).show();
                        finish();
                        Log.i("Show", "참여 성공");
                    } else {
                        int statusCode = response.code();
                        //Toast.makeText(getApplicationContext(), "아이디 혹은 비밀번호가 일치하는 것이 없습니다.", Toast.LENGTH_LONG).show();
                        Log.i("Show", "응답코드 : " + statusCode);
                    }
                }


                @Override
                public void onFailure(Throwable t) {
                    //Toast.makeText(getApplicationContext(), "아이디 혹은 비밀번호가 일치하는 것이 없습니다.", Toast.LENGTH_LONG).show();
                    Log.i("Show", "로그인 에러내용 : " + t.getMessage());
                    //finish();
                }
            });
        }
        catch(Exception e) {//오류처리
            Log.v("erro",e.toString());
        }
    }

    private void throwContentInfo(){   //참여 인원 확인 누를 때
        try {//오류 처리
            Log.i("Show", "throwUserInfo 0");
            // TODO: 정보 가져오기
            throwthunder = content.getThunderid();

            Call<List<Participant>> userCall = networkService.getParticipant(throwthunder);     //참여 인원 확인 누를 때
            userCall.enqueue(new Callback<List<Participant>>() {
                @Override
                public void onResponse(Response<List<Participant>> response, Retrofit retrofit) {
                    try {
                        Log.i("Show", "throwUserInfo 2");
                        if (response.isSuccess()) { //로그인 성공

                            personList = response.body();
                            showUser();//이 번개에 참여한 인원을 dialog로 보여준다.
                            Log.i("Show", "adapter 2");
                        } else {
                            int statusCode = response.code();
                            Toast.makeText(getApplicationContext(), "참여하는 인원이 없습니다.", Toast.LENGTH_LONG).show();
                            Log.i("Show", "응답코드 : " + statusCode);
                        }
                    }catch(Exception e) {//오류처리
                        Log.v("Show erro1",e.toString());
                    }

                }
                @Override
                public void onFailure(Throwable t) {
                    //Toast.makeText(getApplicationContext(), "아이디 혹은 비밀번호가 일치하는 것이 없습니다.", Toast.LENGTH_LONG).show();
                    Log.i("Show", "로그인 에러내용 : " + t.getMessage());
                    //finish();
                }
            });
        }
        catch(Exception e) {//오류처리
            Log.v("erro",e.toString());
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {//폰트 적용 함수
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_show);
        //v2 댓글
        Intent intent = getIntent();
        user = (User)intent.getParcelableExtra("UserInfo");

        //v2 id값 저장
        setting = getSharedPreferences("setting", 0);
        //id값 가져옴
        user_id = setting.getString("login_id", "");

        try {
            Typekit.getInstance()//폰트적용
                    .addNormal(Typekit.createFromAsset(this, "fonts/NanumBarunGothicLight.otf"));
            Intent s_intent = getIntent();
            content = (Content) s_intent.getParcelableExtra("showThunder");  //번개 보기 화면이 띄워지면 그 내용을 객체로 받는다
            check = s_intent.getIntExtra("check_number", 0);
            initView();
            //댓글
            initAdapter();
            //여기부터 main에서 vcheck받는거 확인하는 거
            if(check == 1)//등록자가 자신의 게시글을 봤을 때. = > "참여" 버튼을 "삭제" 로 바꾸기
                btn_participate.setText("삭제");
            else if(check == 2)//그 게시글 참석자가 자신이 신청한 번개를 봤을 때 = > "참여" 버튼을 "불참" 으로 바꾸기
                btn_participate.setText("불참");
            else  //미 참석자가 번개보기를 봤을 때. => "참석" 버튼 그대로 냅 둬
                btn_participate.setText("참여");
            gridview = (GridView) findViewById(R.id.gridView);//그리드 뷰를 위한 코드
            //Log.i("Show", "oncreate 3");

            //Log.i("Show", "oncreate 4");
            showInfo();
            //Log.i("Show", "oncreate 5");
            initNetworkService();
            getSupportActionBar().setDisplayOptions(getSupportActionBar().DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.titlebar_show);
            ApplicationController application = ApplicationController.getInstance();
            application.buildNetworkService("54.200.153.39", 8080);
            Log.i("Show", "oncreate 0");
            btn_participate.setOnClickListener(new View.OnClickListener() {//참여하기 버튼 클릭 시
                @Override
                public void onClick(View v) {
                    Log.i("Show", "참여하기 클릭");
                    throwUserInfo();
                }
            });
            btn_people.setOnClickListener(new View.OnClickListener() {//현재 참여 인원 버튼 클릭 시
                @Override
                public void onClick(View v) {
                    Log.i("Show", "adapter 0");
                    try {
                        throwContentInfo();
                    } catch (Exception e) {//오류처리
                        Log.v("Show erro 2", e.toString());
                    }
                }
            });
            btn_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ShowDetailMap.class);
                    intent.putExtra("lat", content.getLatitude());
                    Log.i("Map", "보냅니다 : " + content.getLatitude() + "," + content.getLongitude());
                    intent.putExtra("lng", content.getLongitude());
                    intent.putExtra("juso", content.getAddress());
                    //v2 url보내주는거
                    intent.putExtra("url", url);
                    Log.i("url", "url(show activity url보내줄때) : " + url);
                    Log.i("ShowActivity", "1");
                    startActivity(intent);
                }
            });
            //v2 댓글 등록
            comment_check = 1;//보통엔 1 ( 등록 )
            Comment lastcomment = (Comment) adapter2.getItem(adapter2.getCount());
            btn_comment_send_method(lastcomment);
            //v2 댓글 클릭시
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//해당 댓글 클릭시
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Log.i("comment", "index : " + position);

                    Comment comment_temp = (Comment) adapter2.getItem(position);

                    Log.i("comment", "댓글 id : " + comment_temp.getId());
                    Log.i("comment", "어플 id : " + user_id);
                    if (comment_temp.getId().equals(user_id))//내가 등록한 댓글일 경우에만
                    {
                        setCommnetSelect(comment_temp);
                        Log.i("comment", "내가 쓴 댓글");
                    } else Log.i("comment", "남의 댓글");
                }
            });
            //v2 댓글 새로고침
            refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowComment(1);
                    comment_more_check = 1;
                }
            });
            //v2 댓글 더보기
            btn_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    comment_more_check++;
                    ShowComment(comment_more_check);
                }
            });
            //v2 페이스북 공유
            btn_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FacebookSdk.sdkInitialize(getApplicationContext());
                    callbackManager = CallbackManager.Factory.create();
                    shareDialog = new ShareDialog(ShowActivity.this);
                    // this part is optional
                    if (ShareDialog.canShow(ShareLinkContent.class)) {
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentTitle("제목")
                                .setContentDescription(
                                        "공유공유")
                                .setContentUrl(
                                        Uri.parse("https://play.google.com/store/apps/details?id=com.sopt.android.thunder&hl=ko"))
                                .build();
                        shareDialog.show(linkContent);
                    }
                }
            });
        }catch(Exception e){
            Log.v("Show erro 3",e.toString());
        }
    }
    //v2 댓글 전송 버튼 클릭 메소드 - 매개변수에 comment값 넣기 위해 메소드 따로 만듦
    private void btn_comment_send_method(final Comment comment) {
        try {
            btn_comment_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (comment_check == 1) {
                        newcomment();
                        comment_more_check=1;
                        ShowComment(comment_more_check);
                    } else if (comment_check == 2) {
                        editcomment(comment);
                    }
                }
            });
        }
        catch(Exception e) {//오류처리
            Log.v("erro",e.toString());
            Log.i("Show","댓글 에러 : "+e.toString());
        }
    }
    //v2 댓글 수정
    private void editcomment(Comment comment) {
        String comment_text = editComment.getText().toString();

        Toast.makeText(getApplicationContext(), "댓글이 수정되었습니다.", Toast.LENGTH_LONG).show();

        comment.setContents(comment_text);

        Call<Comment> CommentCall = networkService.editComment(comment, content.getThunderid(), user_id, comment.getWrite_time()); // 0부터 시작
        Log.i("comment","comment_id : "+adapter2.getCount());
        CommentCall.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Response<Comment> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Comment comment_temp = response.body();
                } else {
                    int statusCode = response.code();
                    Log.i("comment", "응답코드 : " + statusCode);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("comment", "서버 onFailure 에러내용 : " + t.getMessage());
            }
        });
        ShowComment(comment_more_check);
        comment_check = 1;
    }

    private void newcomment() {
        String comment_text = editComment.getText().toString();

        //Date 형식을 정해줍니다.
        long now = System.currentTimeMillis();
        Date currentTime = new Date(now);
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        String mTime = mSimpleDateFormat.format(currentTime);
        Log.i("date", "date : " + mTime);

        //Comment-id값을 구합니다
        int comment_id;
        if(adapter2.getCount() == 0){ //등록된 댓글이 없을 경우
            comment_id = 0;
        }else{
            Comment comment_temp = (Comment) adapter2.getItem(adapter2.getCount()-1);
            comment_id = comment_temp.getComment_index()+1;
            Log.i("comment","계산된 comment_id : "+comment_id);
        }

        //Toast.makeText(getApplicationContext(), "댓글이 등록되었습니다.", Toast.LENGTH_LONG).show();

        Comment comment = new Comment();
        comment.setName(user.getName());
        comment.setId(user.getId());
        comment.setContents(comment_text);
        comment.setThunderid(content.getThunderid());
        comment.setWrite_time(mTime);
        comment.setComment_index(comment_id);

        Call<Comment> CommentCall = networkService.newComment(comment, user.getId(), content.getThunderid()); // 0부터 시작
        Log.i("comment", "comment_id : " + adapter2.getCount());
        CommentCall.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Response<Comment> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Comment comment_temp = response.body();
                    editComment.setText("");
                    Log.i("comment2", "댓글 내용 : " + comment_temp.getContents());
                    Log.i("comment2", "댓글 요청한 아이디:" + comment_temp.getId());
                } else {
                    int statusCode = response.code();
                    Log.i("comment2", "응답코드 : " + statusCode);
                    Log.i("comment2", "동일한 시간에 등록했어요");
                    Toast.makeText(ShowActivity.this, "연속으로 등록할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("comment", "서버 onFailure 에러내용 : " + t.getMessage());
            }
        });
    }

    //v2 댓글 클릭시 호출 함수
    private void setCommnetSelect(final Comment comment) {
        CharSequence info[] = new CharSequence[] {"댓글 수정", "댓글 삭제" };
        AlertDialog.Builder builder = new AlertDialog.Builder(ShowActivity.this);
        builder.setItems(info, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        comment_check = 2;//이 경우에만 2 ( 수정 )
                        // 댓글 수정
                        editComment.setText(comment.getContents());//원래 내용 띄워주고 수정하게 하기
                        editComment.requestFocus();
                        editComment.setSelection(editComment.length());
                        btn_comment_send_method(comment);
                        break;
                    case 1:
                        // 댓글 삭제
                        AlertDialog.Builder dlg = new AlertDialog.Builder(ShowActivity.this);
                        dlg.setMessage("댓글을 삭제하시겠습니까?");
                        // 탈퇴 실행
                        dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteComment(comment);
                            }
                        });
                        // no 그대로
                        dlg.setNegativeButton("아니오", null);
                        dlg.show();
                        break;
                }
                dialog.dismiss();
            }
        });
        builder.show();
    }

    //v2 댓글 삭제
    private void deleteComment(Comment comment) {
        //댓글 삭제 구현하기

        Call<Comment> CommentCall = networkService.deleteComment(comment.getThunderid(), user_id, comment.getWrite_time());
        CommentCall.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Response<Comment> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Comment comment_temp = response.body();
                    Toast.makeText(ShowActivity.this,"댓글이 삭제되었습니다.",Toast.LENGTH_SHORT).show();
                } else {
                    int statusCode = response.code();
                    Log.i("comment", "응답코드 : " + statusCode);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("comment", "서버 onFailure 에러내용 : " + t.getMessage());
            }
        });
        comment_more_check=1;
        ShowComment(comment_more_check);
    }

    //v2 댓글
    private void initAdapter() { // 초기화
        adapter2 = new CommentAdapter(getApplicationContext());
        listView.setAdapter(adapter2);
    }

    //v2 댓글 목록 띄우기
    @Override
    protected void onResume() {
        super.onResume();
        //네트워크
        ApplicationController application = ApplicationController. getInstance ();
        application.buildNetworkService("54.200.153.39", 8080);
        initNetworkService();

        //댓글 띄우기
        ShowComment(1);
        Log.i("comment", "OnResume");
    }

    //v2 댓글 띄우기
    private void ShowComment(final int su) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
        initAdapter();
        //댓글
        // TODO: 11. 서버에서 Thumbnail을 리스트로 받아오기 위한 GET 방식의 요청 구현
        Call<List<Comment>> CommentListCall = networkService.getCommentList(content.getThunderid());
        CommentListCall.enqueue(new Callback<List<Comment>>() {

            @Override
            public void onResponse(Response<List<Comment>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<Comment> commentList = response.body(); // 여기서 정렬
                    commentCount.setText(Integer.toString(commentList.size()));
                    Log.i("commentrefresh", "등록된 댓글 갯 수 : " + commentList.size());

                    if (commentList.size() > su * 5)//등록된 댓글이 원하는 수보다 많을 경우
                    {
                        List<Comment> showcommentrList = new ArrayList<Comment>();
                        for (int i = su * 5; i > 0; i--) {
                            showcommentrList.add(commentList.get(commentList.size() - i));
                            Log.i("comment", "showcommentList에 저장 : " + commentList.get(commentList.size() - i).getContents());
                        }
                        Log.i("comment", "showcommentList에 저장된 수 : " + showcommentrList.size());
                        //adapter2.setItemDatas((ArrayList) commentrList);
                        adapter2.setItemDatas((ArrayList) showcommentrList);
                        adapter2.notifyDataSetChanged();
                        setListViewHeightBasedOnItems(listView);
                    } else {//등록된 댓글이 원하는 수보다 작거나 같을 경우
                        adapter2.setItemDatas((ArrayList) commentList);
                        adapter2.notifyDataSetChanged();
                        setListViewHeightBasedOnItems(listView);
                    }
                    int ShowCommentCount = adapter2.getCount();//화면에 나와있는 댓글 수
                    int AllCommentCount = commentList.size();
                    Log.i("comment", "adapter에 저장된 수 : " + ShowCommentCount);

                    if (AllCommentCount > ShowCommentCount)//댓글이 더 있을 경우
                        btn_more.setVisibility(View.VISIBLE);
                    else btn_more.setVisibility(View.GONE);//댓글 더 없을 경우
                } else {
                    int statusCode = response.code();
                    Log.i("comment", "응답코드 : " + statusCode);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed to load thunderList",
                        Toast.LENGTH_SHORT).show();
                Log.i("comment", "에러내용 : " + t.getMessage());
            }
        });

            }
        }, 100);
    }
    //v2 댓글 높이 측정
    public void setListViewHeightBasedOnItems(ListView listView) {

        if (adapter2 == null)  return;

        int numberOfItems = adapter2.getCount();

        // Get total height of all items.
        int totalItemsHeight = 0;
        for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
            View item = adapter2.getView(itemPos, null, listView);
            item.measure(0, 0);
            totalItemsHeight += item.getMeasuredHeight();
        }

        // Get total height of all item dividers.
        int totalDividersHeight = listView.getDividerHeight() *  (numberOfItems - 1);

        // Set list height.
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalItemsHeight + totalDividersHeight;
        listView.setLayoutParams(params);
        listView.requestLayout();

        Log.i("commentsize", "댓글 갯수 : " + Integer.toString(numberOfItems));
        Log.i("commentsize", "댓글 총 height : " + Integer.toString(params.height));
    }

    //v2 페이스북 공유
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    @Override
    protected void onActivityResult(final int requestCode,
                                    final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            Toast.makeText(ShowActivity.this, "공유가 완료 되었습니다", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(ShowActivity.this, "공유가 취소/실패 되었습니다", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}