package com.sopt.android.thunder.manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sopt.android.thunder.R;
import com.sopt.android.thunder.detail.model.Content_notice;

import java.util.ArrayList;

/**
 * Created by Yujin on 2016-02-01.
 */

// 회원목록 가져오는 어댑터
public class NoticeAdapter extends BaseAdapter {

    Context ctx;
    private LayoutInflater layoutInflater=  null;
    private ArrayList<Content_notice> itemDatas = null;


    public NoticeAdapter(Context ctx){
        this.ctx= ctx;
        layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setItemDatas(ArrayList<Content_notice> itemDatas){
        this.itemDatas = itemDatas;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return itemDatas != null ? itemDatas.size() : 0 ;
    }

    @Override
    public Object getItem(int position) {
        return (Content_notice) (itemDatas != null && (position >= 0 && position<itemDatas.size()) ? itemDatas.get(position) : null);

    }

    @Override
    public long getItemId(int position) {
        return (itemDatas != null && (position >= 0 && position<itemDatas.size())? position: 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHolder_notice viewHolder_notice = new ViewHolder_notice();

        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.member_list_item, parent, false); //레이아웃정렬

            viewHolder_notice.notice_title = (TextView) convertView.findViewById(R.id.notice_title);
            viewHolder_notice.notice_date = (TextView) convertView.findViewById(R.id.notice_date);
            viewHolder_notice.img = (ImageView) convertView.findViewById(R.id.notice_check);

        }
        else{ // convertView != null)
            viewHolder_notice = (ViewHolder_notice) convertView.getTag();
        }

        Content_notice itemData_temp = itemDatas.get(position);
        viewHolder_notice.notice_date.setText(itemData_temp.getDate().toString());
        viewHolder_notice.notice_title.setText(itemData_temp.getTitle().toString());
        if(itemData_temp.getCheck() == 0){
            viewHolder_notice.img.setVisibility(View.INVISIBLE);
        }


        convertView.setTag(viewHolder_notice);

        return convertView;
    }
}
