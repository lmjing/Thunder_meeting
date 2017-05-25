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
public class MemberAdapter extends BaseAdapter {

    Context ctx;
    private LayoutInflater layoutInflater=  null;
    private ArrayList<User> itemDatas = null;


    public MemberAdapter(Context ctx){
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


        ViewHolder_member viewHolder_member = new ViewHolder_member();

        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.member_list_item, parent, false); //레이아웃정렬

            viewHolder_member.member_name = (TextView) convertView.findViewById(R.id.member_name);
            viewHolder_member.member_id = (TextView) convertView.findViewById(R.id.member_id);
            viewHolder_member.member_phoneNum = (TextView) convertView.findViewById(R.id.member_phoneNum);

        }
        else{ // convertView != null)
            viewHolder_member = (ViewHolder_member) convertView.getTag();
        }

        User itemData_temp = itemDatas.get(position);
        viewHolder_member.member_name.setText(itemData_temp.name);
        viewHolder_member.member_id.setText("("+ itemData_temp.id +")");
        viewHolder_member.member_phoneNum.setText(itemData_temp.number.substring(0,3).toString()+"-"
                +itemData_temp.number.substring(3,7)+"-"+itemData_temp.number.substring(7,11));

        convertView.setTag(viewHolder_member);

        return convertView;
    }
}
