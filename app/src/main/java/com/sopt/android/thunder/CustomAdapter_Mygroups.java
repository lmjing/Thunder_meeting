package com.sopt.android.thunder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sopt.android.thunder.detail.model.Content_Mygroup;

import java.util.ArrayList;

/**
 * Created by LG on 2015-10-31.
 */
/*ArrayList??view?*/
public class CustomAdapter_Mygroups extends BaseAdapter {

    String dday = null;
    Context ctx;
    private ArrayList<Content_Mygroup> groupDatas = null;
    private LayoutInflater layoutInflater=  null;

    public CustomAdapter_Mygroups(Context ctx){
        this.ctx = ctx;
        layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //layoutInflater = LayoutInflater.from(ctx);
    }

    public void setItemDatas(ArrayList<Content_Mygroup> groupDatas){
        this.groupDatas = groupDatas;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return groupDatas != null ? groupDatas.size() : 0 ;
    }

    @Override
    public Object getItem(int position) {
        return (Content_Mygroup) (groupDatas != null && (position >= 0 && position<groupDatas.size()) ? groupDatas.get(position) : null);
    }

    @Override
    public long getItemId(int position) {
        return (groupDatas != null && (position >= 0 && position<groupDatas.size())? position: 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder_Mygroups viewHolder = new ViewHolder_Mygroups();

        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.list_item_selectgroup, parent, false); //레이아웃정렬

            viewHolder.groupname = (TextView) convertView.findViewById(R.id.groupname);
            //viewHolder.groupcontents = (TextView) convertView.findViewById(R.id.groupContents);
            //viewHolder.rootid = (TextView) convertView.findViewById(R.id.groupMaster);

        }
        else{ // convertView != null)
            viewHolder = (ViewHolder_Mygroups) convertView.getTag();
        }

        Content_Mygroup groupData_temp = groupDatas.get(position);
        String name = groupData_temp.groupname;
        viewHolder.groupname.setText(groupData_temp.groupname);
        Log.i("그룹어댑터 groupname: :","" + name);


        // 장소 보기좋게 보이게 정리
        //String d = date_sort(itemData_temp.date); // 시간 받아와서
        //Log.i("MyTag", date);
        //viewHolder.txtDate_item.setText(d);
        //viewHolder.txtDate_item.setText(itemData_temp.date);

        convertView.setTag(viewHolder);

        return convertView;
    }
}
