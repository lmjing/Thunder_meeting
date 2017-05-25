package com.sopt.android.thunder;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by LG on 2015-10-31.
 */


/* ViewHolder 은 없어도 됨 ConvertView 아이템 하나하나 */
public class ViewHolder {
    ImageView img_item;
    TextView txtTitle_item;
    TextView txtName_item;
    TextView txtDate_item;
    TextView txtAddress_item;
    TextView txtDday_item;
    ImageView img_bookmark;
    ImageView end_thunder;



    public ImageView getImg_item() {
        return img_item;
    }

    public void setImg_item(ImageView img_item) {
        this.img_item = img_item;
    }

    public TextView getTxtTitle_item() {
        return txtTitle_item;
    }

    public void setTxtTitle_item(TextView txtTitle_item) {
        this.txtTitle_item = txtTitle_item;
    }

    public TextView getTxtName_item() {
        return txtName_item;
    }

    public void setTxtName_item(TextView txtName_item) {
        this.txtName_item = txtName_item;
    }

    public TextView getTxtDate_item() {
        return txtDate_item;
    }

    public void setTxtDate_item(TextView txtDate_item) {
        this.txtDate_item = txtDate_item;
    }

    public TextView getTxtAddress_item() {
        return txtAddress_item;
    }

    public void setTxtAddress_item(TextView txtAddress_item) {
        this.txtAddress_item = txtAddress_item;
    }

    public TextView getTxtDday_item() {
        return txtDday_item;
    }

    public void setTxtDday_item(TextView txtDday_item) {
        this.txtDday_item = txtDday_item;
    }

    public ImageView getImg_bookmark() {
        return img_bookmark;
    }

    public void setImg_bookmark(ImageView img_bookmark) {
        this.img_bookmark = img_bookmark;
    }

    public ImageView getEnd_thunder() {
        return end_thunder;
    }

    public void setEnd_thunder(ImageView end_thunder) {
        this.end_thunder = end_thunder;
    }
}
