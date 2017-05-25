package com.sopt.android.thunder.v3;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sopt.android.thunder.MyGroupActivity;
import com.sopt.android.thunder.R;
import com.sopt.android.thunder.component.ApplicationController;
import com.sopt.android.thunder.detail.model.User;
import com.sopt.android.thunder.network.NetworkService;
import com.tsengvn.typekit.Typekit;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.Calendar;
import java.util.GregorianCalendar;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class joinActivity extends AppCompatActivity {
    EditText input_year,input_month,input_day,input_phone1,input_phone2,input_phone3;
    TextView input_name;
    Button btn_join;
    private NetworkService networkService;
    User n_user = new User(); //new_user 여기에 회원가입하는 놈 정보저장해
    int year,month,day;

    private void initview(){
        input_name = (TextView)findViewById(R.id.input_name);
        input_year = (EditText)findViewById(R.id.input_year);
        input_month = (EditText)findViewById(R.id.input_month);
        input_day = (EditText)findViewById(R.id.input_day);
        input_phone1 = (EditText)findViewById(R.id.input_phone1);
        input_phone2 = (EditText)findViewById(R.id.input_phone2);
        input_phone3 = (EditText)findViewById(R.id.input_phone3);
        btn_join = (Button)findViewById(R.id.btn_join);
    }

    private void dataInsert(){
        n_user.birth = input_year.getText().toString();  //년 입력
        if (input_month.getText().toString().length() == 1) //월 입력
            n_user.birth += ("0" + input_month.getText().toString());
        else
            n_user.birth += input_day.getText().toString();

        if (input_day.getText().toString().length() == 1) //일 입력
            n_user.birth += ("0" + input_day.getText().toString());
        else
            n_user.birth += input_day.getText().toString();

        if(n_user.birth.length() < 8){
            Toast.makeText(getApplicationContext(), "생년월일을 모두 입력해주세요.", Toast.LENGTH_LONG).show();
            return;
        }
        if(0 == checkBirth())//미래에서 왔을 시
            return;
        n_user.number = input_phone1.getText().toString();
        n_user.number += input_phone2.getText().toString();
        n_user.number += input_phone3.getText().toString();
        if(n_user.number.length() < 11){
            Toast.makeText(getApplicationContext(), "휴대폰 번호를 입력해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

       Log.i("MyTag" ,"입력 다 저장된 뒤" );

        Log.i("MyTag", "저장 id : " + n_user.getId());
        Log.i("MyTag", "저장 name : "+n_user.getName());
        Log.i("MyTag", "저장 birth : "+n_user.getBirth());
        Log.i("MyTag", "저장 number : "+n_user.getNumber());
        Log.i("MyTag", "저장 registerid : "+n_user.getRegisterid());
        Log.i("MyTag", "저장 pw : "+n_user.getPassword());

        Call<User> userCall = networkService.newUser(n_user);
        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                if (response.isSuccess()) { //회원가입 성공

                    Call<User> userCall = networkService.loginUser(n_user);   //id,passwd가 저장된 user객체를 전달
                    userCall.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Response<User> response, Retrofit retrofit) {
                            if (response.isSuccess()) { //로그인 성공
                                n_user = response.body();

                                Toast.makeText(getApplicationContext(), "가입해주셔서 감사합니다.", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), MyGroupActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                int statusCode = response.code();
                                Toast.makeText(getApplicationContext(), "아이디 혹은 비밀번호가 일치하는 것이 없습니다.", Toast.LENGTH_LONG).show();
                                Log.i("MyTag", "응답코드 : " + statusCode);
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Toast.makeText(getApplicationContext(), "아이디 혹은 비밀번호가 일치하는 것이 없습니다.", Toast.LENGTH_LONG).show();
                            Log.i("MyTag", "로그인 에러내용 : " + t.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("MyTag", "회원가입 에러내용 : " + t.getMessage());
                Toast.makeText(getApplicationContext(), "이미 존재하는 아이디입니다.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initNetworkService(){
        // TODO: 13. ApplicationConoller 객체를 이용하여 NetworkService 가져오기
        networkService = ApplicationController.getInstance().getNetworkService();
    }

    @Override
    protected void attachBaseContext(Context newBase) {//폰트 적용 함수
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_join);
        Typekit.getInstance()//폰트적용
                .addNormal(Typekit.createFromAsset(this, "fonts/NanumBarunGothicLight.otf"));
        initview();
        getSupportActionBar().setDisplayOptions(getSupportActionBar().DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.titlebar_join);


        ApplicationController application = ApplicationController. getInstance();
        application.buildNetworkService("54.200.153.39", 8080);
        initNetworkService();

        GregorianCalendar calendar = new GregorianCalendar();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day= calendar.get(Calendar.DAY_OF_MONTH);

        Intent intent = getIntent();
        n_user = (User)intent.getParcelableExtra("UserInfo");
        input_name.setText(n_user.getName());

        Log.i("MyTag", "받아옴 id : " + n_user.getId());
        Log.i("MyTag", "받아옴 id : "+n_user.getName());
        Log.i("MyTag", "받아옴 id : "+n_user.getBirth());
        Log.i("MyTag", "받아옴 id : "+n_user.getNumber());
        Log.i("MyTag", "받아옴 id : "+n_user.getRegisterid());


        input_year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(joinActivity.this, dateSetListener, year, month, day).show();
                Log.i("MyTag","yearTouch1");
                //dialog.show();
            }
        });

        input_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(joinActivity.this, dateSetListener, year, month, day).show();
                Log.i("MyTag","yearTouch2");
                //dialog.show();
            }
        });
        input_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(joinActivity.this, dateSetListener, year, month, day).show();
                Log.i("MyTag","yearTouch3");
                //dialog.show();
            }
        });

        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataInsert();
            }
        });
    }

    private int checkBirth(){
        if((Integer.parseInt(input_year.getText().toString()) == year && Integer.parseInt(input_month.getText().toString()) == month && Integer.parseInt(input_day.getText().toString()) > day) ||
                (Integer.parseInt(input_year.getText().toString()) > year || (Integer.parseInt(input_year.getText().toString()) == year && Integer.parseInt(input_month.getText().toString()) > month+1))){
            Toast.makeText(getApplicationContext(), "미래에서 오셨나요?", Toast.LENGTH_SHORT).show();
            return 0;
        }

        input_year.getText();
        input_month.getText();
        input_day.getText();
        return 1;
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // TODO Auto-generated method stub
            input_year.setText(String.valueOf(year));
            input_month.setText(String.valueOf(monthOfYear+1));
            input_day.setText(String.valueOf(dayOfMonth));
            String msg = String.format("%d /%d / %d", year,monthOfYear+1, dayOfMonth);
            Toast.makeText(joinActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
    };





}
