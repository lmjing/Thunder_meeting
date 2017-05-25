package com.sopt.android.thunder.manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sopt.android.thunder.R;
import com.sopt.android.thunder.detail.model.User;

import java.util.ArrayList;

/**
 * Created by Yujin on 2016-02-01.
 */

// 회원목록 가져오는 어댑터
public class ManagerRegisterAdapter extends BaseAdapter {

    Context ctx;
    private LayoutInflater layoutInflater=  null;
    private ArrayList<User> itemDatas = null;


    public ManagerRegisterAdapter(Context ctx){
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


        ViewHolder_member_manager viewHolder_member_manager = new ViewHolder_member_manager();

        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.member_manager_list_item, parent, false); //레이아웃정렬

            viewHolder_member_manager.member_name_manager_register = (TextView) convertView.findViewById(R.id.member_name_manager_register);
            viewHolder_member_manager.member_id_manager_register = (TextView) convertView.findViewById(R.id.member_id_manager_register);
            viewHolder_member_manager.member_phoneNum_manager_register = (TextView) convertView.findViewById(R.id.member_phoneNum_manager_register);
            viewHolder_member_manager.manager_register_img = (ImageView) convertView.findViewById(R.id.manager_register_img);

        }
        else{ // convertView != null)
            viewHolder_member_manager = (ViewHolder_member_manager) convertView.getTag();
        }

        User itemData_temp = itemDatas.get(position);
        viewHolder_member_manager.member_name_manager_register.setText(itemData_temp.name);
        viewHolder_member_manager.member_id_manager_register.setText("("+ itemData_temp.id +")");
        viewHolder_member_manager.member_phoneNum_manager_register.setText(itemData_temp.number.substring(0,3).toString()+"-"
                +itemData_temp.number.substring(3,7)+"-"+itemData_temp.number.substring(7,11));

        if(itemData_temp.getAuthority() == 1){
            viewHolder_member_manager.manager_register_img.setVisibility(View.VISIBLE);
        } else if(itemData_temp.getAuthority() != 1){
            viewHolder_member_manager.manager_register_img.setVisibility(View.INVISIBLE);
        }

        convertView.setTag(viewHolder_member_manager);

        return convertView;
    }
}
