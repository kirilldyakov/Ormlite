package com.example.user.ormlite.properties;

import android.app.Activity;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.ormlite.R;
import com.example.user.ormlite.database.HelperFactory;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.sql.SQLException;

/**
 * Created by user on 17.08.16.
 */
@EActivity(R.layout.activity_properties_list)
public class PropertiesList extends Activity {


    @AfterViews
    protected void init() {
        //    txtMain.setText("newText");
    }

    @Click(R.id.btnPropFilter)
    void btnGraphClick(){
        PropFilter_.intent(this).start();
    }

}
