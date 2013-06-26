package com.example.decision.controllers;

import android.app.Application;
import android.content.Context;
import com.example.decision.adapter.CardDbAdapter;

/**
 * Created by haihong.xiahh on 13-6-25.
 */
public class Controllers {
    Context mContext;
    CardDbAdapter mDbAdapter;
    Application mApplication;
    private Controllers(){

    }
    private static final class ControllersHolder{
        private final static Controllers INSTANCE = new Controllers();
        private ControllersHolder(){

        }
    }
    public static Controllers Instance(){
        return ControllersHolder.INSTANCE;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public CardDbAdapter getmDbAdapter() {
        if (mDbAdapter == null){
            mDbAdapter = CardDbAdapter.Instance(mContext);
        }
        return mDbAdapter;
    }

    public void setmDbAdapter(CardDbAdapter mDbAdapter) {
        this.mDbAdapter = mDbAdapter;
    }

    public Application getmApplication() {
        return mApplication;
    }

    public void setmApplication(Application mApplication) {
        this.mApplication = mApplication;
    }
}
