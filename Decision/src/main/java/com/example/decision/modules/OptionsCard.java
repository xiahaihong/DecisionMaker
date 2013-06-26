package com.example.decision.modules;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by haihong.xiahh on 13-6-25.
 */
public class OptionsCard implements Serializable{
    String mID;
    String mTitle;
    String mContent;
    List<String> mItemList;

    public OptionsCard(){
        mID = UUID.randomUUID().toString();
        mTitle = mID;
        mContent = mID;
        mItemList = new ArrayList<String>();
        for (int i = 0; i < 10; i++){
            mItemList.add(mID + "_" + i);
        }
    }

    public OptionsCard(String id, String title, String content, List<String> itemList){
        mID = id;
        mTitle = title;
        mContent = content;
        mItemList = new ArrayList<String>();
        mItemList.addAll(itemList);
    }

    public String getmID() {
        return mID;
    }

    public void setmID(String mID) {
        this.mID = mID;
    }


    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public List<String> getmItemList() {
        return mItemList;
    }

    public void setmItemList(ArrayList<String> mItemList) {
        this.mItemList = mItemList;
    }

    public String getmContent() {
        return mContent;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

}
