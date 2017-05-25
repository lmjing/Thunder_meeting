package com.sopt.android.thunder.v3.Comment;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.sopt.android.thunder.R;
import com.sopt.android.thunder.detail.model.Comment;
import com.sopt.android.thunder.v3.Comment.Image.Task;

import java.util.ArrayList;

/**
 * Created by LG on 2015-10-31.
 */
/*ArrayList??view?*/
public class CommentAdapter extends BaseAdapter {

    CommentViewHolder viewHolder = new CommentViewHolder();
    Handler handler = new Handler();
    String dday = null;
    Context ctx;
    private ArrayList<Comment> itemDatas = null;
    private LayoutInflater layoutInflater=  null;

    public CommentAdapter(Context ctx){
        this.ctx = ctx;
        layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //layoutInflater = LayoutInflater.from(ctx);
    }

    public void setItemDatas(ArrayList<Comment> itemDatas){
        this.itemDatas = itemDatas;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return itemDatas != null ? itemDatas.size() : 0 ;
    }

    @Override
    public Object getItem(int position) {
        return (Comment) (itemDatas != null && (position >= 0 && position<itemDatas.size()) ? itemDatas.get(position) : null);
    }

    @Override
    public long getItemId(int position) {
        return (itemDatas != null && (position >= 0 && position<itemDatas.size())? position: 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.test_item, parent, false); //레이아웃정렬

            viewHolder.textViewMessage = (TextView)convertView.findViewById(R.id.text_view_message);
            viewHolder.textViewDate = (TextView)convertView.findViewById(R.id.text_view_date);
            viewHolder.textName = (TextView)convertView.findViewById(R.id.name);
            viewHolder.ProfileImg = (CircularImageView)convertView.findViewById(R.id.img);

        }
        else{ // convertView != null)
            viewHolder = (CommentViewHolder) convertView.getTag();
        }

        final Comment itemData_temp = itemDatas.get(position);
        //사진
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                Task task = new Task(viewHolder.ProfileImg);
                task.execute(itemData_temp.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();

        viewHolder.textViewMessage.setText(itemData_temp.getContents());
        viewHolder.textViewDate.setText(itemData_temp.getWrite_time());
        viewHolder.textName.setText(itemData_temp.getName());
        Log.i("comment", position +"번째 List : " + itemData_temp.getContents());

        convertView.setTag(viewHolder);

        return convertView;
    }
}
