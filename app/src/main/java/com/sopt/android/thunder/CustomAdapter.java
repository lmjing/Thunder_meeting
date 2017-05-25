package com.sopt.android.thunder;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sopt.android.thunder.detail.model.Content;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by LG on 2015-10-31.
 */
/*ArrayList??view?*/
public class CustomAdapter extends BaseAdapter {

    String dday = null;
    Context ctx;
    private ArrayList<Content> itemDatas = null;
    private LayoutInflater layoutInflater=  null;

    public CustomAdapter(Context ctx){
        this.ctx = ctx;
        layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //layoutInflater = LayoutInflater.from(ctx);
    }

    public void setItemDatas(ArrayList<Content> itemDatas){
        this.itemDatas = itemDatas;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return itemDatas != null ? itemDatas.size() : 0 ;
    }

    @Override
    public Object getItem(int position) {
        return (Content) (itemDatas != null && (position >= 0 && position<itemDatas.size()) ? itemDatas.get(position) : null);
    }

    @Override
    public long getItemId(int position) {
        return (itemDatas != null && (position >= 0 && position<itemDatas.size())? position: 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();

        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.list_item, parent, false); //레이아웃정렬

            viewHolder.img_item = (ImageView) convertView.findViewById(R.id.img_item);
            viewHolder.txtTitle_item = (TextView) convertView.findViewById(R.id.textTitle_item);
            viewHolder.txtName_item = (TextView) convertView.findViewById(R.id.textName_item);
            viewHolder.txtAddress_item = (TextView) convertView.findViewById(R.id.textAddress_item);
            viewHolder.txtDate_item = (TextView) convertView.findViewById(R.id.textDate_item);
            viewHolder.txtDday_item = (TextView) convertView.findViewById(R.id.textDday_item);
            viewHolder.end_thunder = (ImageView) convertView.findViewById(R.id.end_thunder);


        }
        else{ // convertView != null)
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Content itemData_temp = itemDatas.get(position);
        switch(itemData_temp.type){
            case 1:
                viewHolder.img_item.setImageResource(R.mipmap.beer);
                break;
            case 2:
                viewHolder.img_item.setImageResource(R.mipmap.art);
                break;
            case 3:
                viewHolder.img_item.setImageResource(R.mipmap.bowling);
                break;
        }
        viewHolder.txtTitle_item.setText(itemData_temp.title);
        viewHolder.txtName_item.setText(itemData_temp.host);
        viewHolder.txtAddress_item.setText(itemData_temp.address);
        viewHolder.txtDate_item.setText(itemData_temp.date.substring(0,4)+" 년 "+itemData_temp.date.substring(4,6)+" 월 "+
                itemData_temp.date.substring(6,8)+" 일 "+itemData_temp.date.substring(8,10)+" 시 "+itemData_temp.date.substring(10,12)+" 분");

        // 장소 보기좋게 보이게 정리
        //String d = date_sort(itemData_temp.date); // 시간 받아와서
        //Log.i("MyTag", date);
        //viewHolder.txtDate_item.setText(d);
        //viewHolder.txtDate_item.setText(itemData_temp.date);

        int d_day = calculateDday(itemData_temp.date);
        Log.i("MyTag", "d_day : "+itemDatas.get(position).getTitle()+":"+Integer.toString(d_day));
        if(d_day==0){
            dday = "D - day";
            viewHolder.txtDday_item.setText(dday);
            viewHolder.txtDday_item.setTextColor(Color.parseColor("#fcaf17"));
            viewHolder.end_thunder.setVisibility(View.GONE);
        }else if(d_day>0) {
            dday = "D - "+Integer.toString(d_day);
            viewHolder.txtDday_item.setText(dday);
            viewHolder.txtDday_item.setTextColor(Color.parseColor("#333333"));
            viewHolder.end_thunder.setVisibility(View.GONE);
        } else if(d_day<0){
            dday = "종료";
            viewHolder.txtDday_item.setText(dday);
            viewHolder.txtDday_item.setTextColor(Color.parseColor("#fcaf17"));
            viewHolder.end_thunder.setVisibility(View.VISIBLE);
        }
        Log.i("MyTag", dday);


        convertView.setTag(viewHolder);

        return convertView;
    }

    private String date_sort(String date) {;
        String year = date.substring(2,4);
        String month = Integer.toString(Integer.parseInt(date.substring(4,6))+1);
        String day = date.substring(6,8);
        String hour = date.substring(8,10);
        String minute = date.substring(10,12);
        // 오후 오전 나누기
        int noTime = Integer.parseInt(hour);
        if(noTime<12){
            hour = "AM "+Integer.toString(noTime%12);
        } else if(noTime == 12){
            hour = "PM "+Integer.toString(12);
        } else if(noTime>12){ // noTime>=12 ==1 이면
            hour = "PM "+Integer.toString(noTime%12);
        }
        String result = "20"+year+"년 "+month+"월 "+day+"일 "+hour+"시 "+minute+"분 ";
        return result;
    }

    private int calculateDday(String date) {
        DecimalFormat decimalFormat = new DecimalFormat("00");// decimalformat은
        // 숫자에 대한 자바 포멧
        // 00은 두자리로표현
        DecimalFormat NumFormat = new DecimalFormat("0000");// 4자리로 표현 한다.

        //현재 날짜 가져오기 시간 분 까지 가능
        Calendar rightNow = Calendar.getInstance();// 날짜 불러오는 함수
        int year = rightNow.get(Calendar.YEAR) % 100;// 100을 나눠서 년도표시를 2009->9지만
        // decimal포멧으로 09로 표현
        int month = rightNow.get(Calendar.MONTH);// 달
        int day = rightNow.get(Calendar.DATE);// 일
        String result = decimalFormat.format(year)
                + decimalFormat.format(month + 1) + decimalFormat.format(day);
        int dday_num = Integer.parseInt(date.substring(2, 8)) - Integer.parseInt(result);

        return dday_num;
    }
}
