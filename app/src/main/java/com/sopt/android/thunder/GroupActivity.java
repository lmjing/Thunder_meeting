package com.sopt.android.thunder;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tsengvn.typekit.Typekit;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;

// 소속 관리 Activity
public class GroupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener{

    // 그룹이름 저장 스트링
    String saveGroup = null;
    // test 배열
    //String fruit[] = {"사과", "딸기"};
    String sopt[] = {"sopt"};
    ArrayList<String> spinner = new ArrayList<>(); // 소속 서버 겟해서 추가하는 부분
    Spinner spinner_group;

    View view; // View를 얻오옴

    TextView actionbar_title_group;

    // 그룹 관리 버튼
    Button addGroupBtn; // 소속 추가
    Button deleteGroupBtn; // 소속 삭제
    Button finishBtn; // 변경완료

    @Override
    protected void attachBaseContext(Context newBase) {//폰트 적용 함수
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Typekit.getInstance()//폰트적용
                .addNormal(Typekit.createFromAsset(this, "fonts/NanumBarunGothicLight.otf"));
        settingActionBar();

        // 소속추가하면 소속추가 레이아웃 보이게
        view = (View) findViewById(R.id.group_change); // View를 얻어옴
        view.setVisibility(View.GONE);

        // 스피너 추가하는 부분
        spinner_group = (Spinner) findViewById(R.id.spinner_group);

        ArrayAdapter<String> adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item, sopt);
        spinner_group.setAdapter(adapter);
        spinner_group.setOnItemSelectedListener(this);

        // 소속 추가 삭제 부분
        addGroupBtn = (Button) findViewById(R.id.btn_group_plus);
        deleteGroupBtn = (Button)findViewById(R.id.btn_group_delete);
        finishBtn = (Button) findViewById(R.id.btn_group_finish);

        addGroupBtn.setOnClickListener(this);
        deleteGroupBtn.setOnClickListener(this);

        //  변경완료
        finishBtn.setOnClickListener(this);

    }

    private void settingActionBar() {
        getSupportActionBar().setDisplayOptions(getSupportActionBar().DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.titlebar_edit);
        getSupportActionBar().setIcon(R.mipmap.btn_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    // 스피너 클릭이벤트
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        saveGroup = (String) spinner_group.getSelectedItem();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    // 버튼 리스너 이벤트
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            // 변경완료
            case R.id.btn_group_finish:
                changeGroup();
                finish();
                break;
            // 소속 추가
            case R.id.btn_group_plus:
                addGroup();
                break;
            // 소속 제거
            case R.id.btn_group_delete:
                deleteGroup();
                break;
        }

    }

    // 소속제거함수
    private void deleteGroup() {
        view.setVisibility(View.GONE);
    }


    // 변경완료되면 서버에 소속 변경 추가해준다.
    private void changeGroup() {
        Toast.makeText(getApplicationContext(), saveGroup+" 변경이 완료되었습니다.", Toast.LENGTH_SHORT).show();
    }

    // 소속추가 - 우선 하나만
    private void addGroup() {
        view.setVisibility(View.VISIBLE);
    }

}