package com.sopt.android.thunder.manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sopt.android.thunder.R;
import com.sopt.android.thunder.detail.model.User;

import java.util.ArrayList;

/**
 * Created by Yujin on 2016-02-01.
 */

// 회원목록 가져오는 어댑터
public class MemberSignAdapter extends BaseAdapter {

    Context ctx;
    private LayoutInflater layoutInflater=  null;
    private ArrayList<User> itemDatas = null;


    public MemberSignAdapter(Context ctx){
        this.ctx= ctx;
        layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setItemDatas(ArrayList<User> itemDatas){
        this.itemDatas = itemDatas;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return itemDatas != null ? itemDatas.size() : 0 ;
    }

    @Override
    public Object getItem(int position) {
        return (User) (itemDatas != null && (position >= 0 && position<itemDatas.size()) ? itemDatas.get(position) : null);

    }

    @Override
    public long getItemId(int position) {
        return (itemDatas != null && (position >= 0 && position<itemDatas.size())? position: 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHolder_member_sign viewHolder_member_sign = new ViewHolder_member_sign();

        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.member_sign_list_item, parent, false); //레이아웃정렬

            viewHolder_member_sign.member_name_sign = (TextView) convertView.findViewById(R.id.member_name_sign);
            viewHolder_member_sign.member_id_sign = (TextView) convertView.findViewById(R.id.member_id_sign);

        }
        else{ // convertView != null)
            viewHolder_member_sign = (ViewHolder_member_sign) convertView.getTag();
        }

        User itemData_temp = itemDatas.get(position);
        viewHolder_member_sign.member_name_sign.setText(itemData_temp.name);
        viewHolder_member_sign.member_id_sign.setText("("+ itemData_temp.id +")");

        convertView.setTag(viewHolder_member_sign);

        return convertView;
    }
}
