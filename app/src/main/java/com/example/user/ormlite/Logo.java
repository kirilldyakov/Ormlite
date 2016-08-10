package com.example.user.ormlite;



import android.app.Activity;
import android.os.SystemClock;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_logo)
public class Logo extends Activity {
    @AfterViews
    protected void init()
    {
        waitInBackGrond();
    }

    @Background
    void waitInBackGrond(){
        SystemClock.sleep(1000);
        Main_.intent(this).start();
    }

}