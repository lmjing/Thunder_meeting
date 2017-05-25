package com.sopt.android.thunder;

import android.widget.TextView;

/**
 * Created by LG on 2015-10-31.
 */


/* ViewHolder 은 없어도 됨 ConvertView 아이템 하나하나 */
public class ViewHolder_groups {
        TextView groupname, rootid,groupcontents;

    public TextView getGroupname() {
        return groupname;
    }

    public void setGroupname(TextView groupname) {
        this.groupname = groupname;
    }

    public TextView getRootid() {
        return rootid;
    }

    public void setRootid(TextView rootid) {
        this.rootid = rootid;
    }

    public TextView getGroupcontents() {
        return groupcontents;
    }

    public void setGroupcontents(TextView groupcontents) {
        this.groupcontents = groupcontents;
    }
}
