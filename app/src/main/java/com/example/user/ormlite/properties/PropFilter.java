package com.example.user.ormlite.properties;

import android.app.Activity;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.user.ormlite.R;
import com.example.user.ormlite.database.HelperFactory;
import com.example.user.ormlite.graph.Graph_Power_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.sql.SQLException;

/**
 * Created by user on 17.08.16.
 */
@EActivity(R.layout.activity_prop_filter)
public class PropFilter extends Activity {

    @ViewById
    EditText edText;

    @AfterViews
    protected void init() {
    //    txtMain.setText("newText");
    }

    @Click(R.id.btnPropFilterOK)
    void btnGraphClick(){
        int  val = Integer.parseInt(edText.getText().toString());
        try {
            HelperFactory.getHelper().getSettingDAO().setValByName("FILTER",val);
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(this, "Неудача "+e.getMessage(),Toast.LENGTH_LONG);
        }
        Toast.makeText(this, "Все в прорядке ",Toast.LENGTH_LONG);
    }
}
