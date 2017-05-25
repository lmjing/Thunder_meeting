package com.sopt.android.thunder.manager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sopt.android.thunder.R;
import com.sopt.android.thunder.detail.model.Content_notice;

public class NoticeShowActivity extends AppCompatActivity {

    Content_notice notice;
    TextView notice_show_title, notice_show_context, notice_show_date;
    ImageView notice_show_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_show);

        Intent intent= getIntent();
        notice = (Content_notice) intent.getSerializableExtra("Notice");

        notice_show_context = (TextView) findViewById(R.id.notice_show_context);
        notice_show_date = (TextView) findViewById(R.id.notice_show_date);
        notice_show_title = (TextView) findViewById(R.id.notice_title);
        notice_show_check = (ImageView) findViewById(R.id.notice_show_check);

        notice_show_title.setText(notice.getTitle());
        notice_show_context.setText(notice.getContext());
        notice_show_date.setText(notice.getDate().substring(0,4)+"."+notice.getDate().substring(4,6)+"."+notice.getDate().substring(6,8));

        if(notice.getCheck()==0){
            notice_show_check.setVisibility(View.GONE);
        }
    }
}
