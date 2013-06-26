package com.example.decision;

import android.app.Application;
import com.example.decision.controllers.Controllers;

/**
 * Created by haihong.xiahh on 13-6-25.
 */
public class BootstrapApp extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Controllers.Instance().setmContext(getBaseContext());
        Controllers.Instance().setmApplication(this);
    }
}
