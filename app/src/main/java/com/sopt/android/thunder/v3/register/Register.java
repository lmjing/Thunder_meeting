package com.sopt.android.thunder.v3.register;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.sopt.android.thunder.R;
import com.sopt.android.thunder.component.ApplicationController;
import com.sopt.android.thunder.detail.model.Content;
import com.sopt.android.thunder.detail.model.User;
import com.sopt.android.thunder.network.NetworkService;

import java.util.Calendar;
import java.util.GregorianCalendar;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by lmjin_000 on 2016-01-08.
 */

public class Register extends AppCompatActivity {

    private NetworkService networkService;
    private EditText editTitle ,editYear, editMonth, editDay, editHour, editMinute, editMemo;
    private Button sendBtn ,leftBtn, rightBtn;
    private TextView txtAddress;
    private FrameLayout map_search;
    private ImageView icon;
    private int icon_flag = 1;
    private String juso = null, lat = null, lng = null;
    private User userInfo;
    SharedPreferences setting;
    SharedPreferences.Editor editor;
    int n_year, n_month, n_day,hour,minute;
    int today_year, today_month, today_day;
    int groupid;

    Content content = new Content();

    private void initNetworkService(){
        // TODO: 13. ApplicationConoller 객체를 이용하여 NetworkService 가져오기
        networkService = ApplicationController.getInstance().getNetworkService();
    }

    private void initView() {
        editTitle = (EditText) findViewById(R.id.title);
        sendBtn = (Button)findViewById(R.id.sendBtn);
        leftBtn = (Button)findViewById(R.id.left_arrow);
        rightBtn = (Button)findViewById(R.id.right_arrow);
        icon = (ImageView)findViewById(R.id.group_icon);
        editYear = (EditText) findViewById(R.id.year);
        editMonth = (EditText) findViewById(R.id.month);
        editDay = (EditText) findViewById(R.id.day);
        editHour = (EditText) findViewById(R.id.hour);
        editMinute = (EditText) findViewById(R.id.minute);
        editMemo = (EditText) findViewById(R.id.memo);
        map_search = (FrameLayout)findViewById(R.id.map_search);
        txtAddress = (TextView)findViewById(R.id.address);
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // TODO Auto-generated method stub



            editYear.setText(String.valueOf(year));
            editMonth.setText(String.valueOf(monthOfYear+1));
            editDay.setText(String.valueOf(dayOfMonth));
            String msg = String.format("%d /%d / %d", year,monthOfYear+1, dayOfMonth);
            Toast.makeText(Register.this, msg, Toast.LENGTH_SHORT).show();
        }
    };

    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            editHour.setText(String.valueOf(hourOfDay));
            editMinute.setText(String.valueOf(minute));
            String msg = String.format("%d 시 %d 분을 선택했습니다", hourOfDay,minute);
            Toast.makeText(Register.this, msg, Toast.LENGTH_SHORT).show();

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // ip, port 연결
        ApplicationController application = ApplicationController. getInstance();
        application.buildNetworkService("54.200.153.39", 8080);

        // 타이틀 바
        getSupportActionBar().setDisplayOptions(getSupportActionBar().DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.titlebar_register);

        initNetworkService();
        initView();

        //
        Intent u_intent = getIntent();

        //추가된 내용 2/21(일)
        groupid = u_intent.getIntExtra("groupid",0);
        content.setGroupid(groupid);
        //
        userInfo = (User)u_intent.getParcelableExtra("UserInfo");//user 객체를 받는다.
        GregorianCalendar calendar = new GregorianCalendar();
        n_year = calendar.get(Calendar.YEAR);
        n_month = calendar.get(Calendar.MONTH);
        n_day = calendar.get(Calendar.DAY_OF_MONTH);
        today_year = calendar.get(Calendar.YEAR);
        today_month = calendar.get(Calendar.MONTH);
        today_day = calendar.get(Calendar.DAY_OF_MONTH);



        editYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DatePickerDialog(Register.this, dateSetListener, n_year, n_month, n_day).show();
                Log.i("MyTag","yearTouch1");
                //dialog.show();
            }
        });

        editMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Register.this, dateSetListener, n_year, n_month, n_day).show();
                Log.i("MyTag","yearTouch2");
                //dialog.show();
            }
        });
        editDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Register.this, dateSetListener, n_year, n_month, n_day).show();
                Log.i("MyTag","yearTouch3");
                //dialog.show();
            }
        });
        editHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(Register.this, timeSetListener, hour, minute,false).show();
                Log.i("MyTag","timepicker 1");
                //dialog.show();
            }
        });
        editMinute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(Register.this, timeSetListener, hour, minute,false).show();
                Log.i("MyTag", "timepicker 2");
                //dialog.show();
            }
        });




        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("Test","editTitle : "+editTitle.getText());
                Log.i("Test","editYear : "+editYear.getText());
                Log.i("Test", "editMonth : " + editMonth.getText());
                Log.i("Test", "editDay : " + editDay.getText());
                Log.i("Test", "editHour : " + editHour.getText());
                Log.i("Test", "editMinute : " + editMinute.getText());
                Log.i("Test","txtAddress : "+txtAddress.getText());
                /*if (editTitle.getText().equals("") || editYear.getText().equals("") || editMonth.getText().equals("") ||
                        editDay.getText().equals("") || editHour.getText().equals("") || editMinute.getText().equals("") ||
                        txtAddress.getText().equals("") ) {*/
                if(editTitle.getText().length()==0 || editYear.getText().length()==0 || editMonth.getText().length()==0 ||
                        editDay.getText().length()==0 || editHour.getText().length()==0 || editMinute.getText().length()==0 ||
                        txtAddress.getText().length()==0 || editMemo.getText().length() ==0 ){
                    Log.i("Test","비어있는거 하나 라도 존재 ");
                    Toast.makeText(getApplicationContext(), "미입력된 사항이 있습니다.", Toast.LENGTH_LONG).show();
                } else {
                    String year = editYear.getText().toString();
                    String month = editMonth.getText().toString();
                    String day = editDay.getText().toString();
                    String hour = editHour.getText().toString();
                    String minute = editMinute.getText().toString();

                    Log.i("Test","오늘 : "+today_year + "년 "+(today_month+1) +"월 "+ today_day + "일 ");
                    Log.i("Test","your select : "+year + "년 "+(month+1)+"월 "+ day + "일 ");

                    if((Integer.parseInt(year) == today_year && Integer.parseInt(month) == today_month && Integer.parseInt(day) < today_day) ||
                            (Integer.parseInt(year) < today_year || Integer.parseInt(month)  < today_month)){
                        Toast.makeText(getApplicationContext(), "과거로는 등록할 수는 없습니다(단호).", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    Log.i("Test","하나도 안비었음");
                    Toast.makeText(getApplicationContext(), "번개가 등록되었습니다.", Toast.LENGTH_LONG).show();
                    ////날짜랑 시간은 나중에 date형식으로 바꿔보자 예외처리하기 수월하도록 저장하기도 좋고

                    if (month.length() == 1)
                        month = "0" + month;
                    if (day.length() == 1)
                        day = "0" + day;
                    if (hour.length() == 1)
                        hour = "0" + hour;
                    if (minute.length() == 1)
                        minute = "0" + minute;

                    //날짜 저장 (형식 1501210230 = 15년1월21일2시30분)
                    String date = year + month + day + hour + minute;

                    //v2 웹뷰를 위한 url 가져와 저장
                    setting = getSharedPreferences("setting", 0);
                    String url = setting.getString("url", "");

                    content.setTitle(editTitle.getText().toString());
                    content.setContents(editMemo.getText().toString());
                    content.setDate(date);
                    content.setType(icon_flag);
                    content.setAddress(juso);
                    content.setLatitude(lat);
                    content.setLongitude(lng);
                    content.setHost(userInfo.getName());

                    //v2 url저장 후 지워주기 그래야 임의의 장소일때 ""저장되지
                    content.setUrl(url);

                    editor = setting.edit();
                    editor.remove("url");
                    editor.commit();

                    Call<Content> thumbnailCall = networkService.newContent(content, setting.getString("login_id",""));
                    thumbnailCall.enqueue(new Callback<Content>() {
                        @Override
                        public void onResponse(Response<Content> response, Retrofit retrofit) {
                            if (response.isSuccess()) {
                                Content content_temp = response.body();
                                editTitle.setText("");
                                Log.i("MyTag", "썸네일 제목 : " + content_temp.title );
                                Log.i("MyTag","Register.java의 요청한 아이디:"+setting.getString("login_id",""));
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
            }
        });

        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(icon_flag==3)
                    icon_flag = 1;
                else icon_flag ++;

                setImage(icon_flag);
            }
        });

        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(icon_flag==1)
                    icon_flag = 3;
                else icon_flag --;

                setImage(icon_flag);
            }
        });

        map_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchDemoActivity.class);
                intent.putExtra("Map_call",1);
                startActivity(intent);
            }
        });
    }

    private void setImage(int icon_flag)
    {
        Log.i("MyTag", "icon_flag : " + icon_flag);
        switch (icon_flag)
        {
            case 1 : icon.setImageDrawable(getResources().getDrawable(R.drawable.beer_icon)); break;
            case 2 : icon.setImageDrawable(getResources().getDrawable(R.drawable.art_icon)); break;
            case 3 : icon.setImageDrawable(getResources().getDrawable(R.drawable.bolling_icon)); break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        setting = getSharedPreferences("setting", 0);
        juso = setting.getString("address", null);
        lat = setting.getString("lat", null);
        lng = setting.getString("lng", null);
        txtAddress.setText(juso);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        editor= setting.edit();
        editor.remove("address");
        editor.remove("lat");
        editor.remove("lng");
        editor.commit();
    }
}