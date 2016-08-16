package com.example.user.ormlite.graph;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.ormlite.Main_;
import com.example.user.ormlite.database.HelperFactory;
import com.example.user.ormlite.database.Pick;
import com.example.user.ormlite.R;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@EActivity(R.layout.activity_graph)
@OptionsMenu(R.menu.menu_graph)
public class Graph_Durty extends Activity {
    String LOG_TAG = "LOG_TAG";
    private GraphView graphView;

    private DataPoint[] data = null;


    @ViewById(R.id.txtGraphLabel)
    TextView txtGraphLabel;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @AfterViews
    void init() {
        graphView = new GraphView(this);
        LinearLayout graphLayout = (LinearLayout) findViewById(R.id.GraphLayout);
        graphLayout.addView(graphView);

        data = null;
        txtGraphLabel.setText(getText(R.string.Graph_Durty_Name));
        if (calcDurtyData()) {
            drawBarGraph(Color.GREEN);
            drawFilterLine(Color.CYAN);
        } else
            Toast.makeText(this, getText(R.string.noneData), Toast.LENGTH_SHORT).show();


    }

    @OptionsItem(R.id.mi_graph_Power)
    void showPowerGraph() {
        Graph_Power_.intent(this).start();
    }

    @OptionsItem(R.id.mi_graph_Energy)
    void showEnergyGraph() {
        Graph_Energy_.intent(this).start();
    }

    @OptionsItem(R.id.mi_graph_DurtyData)
    void showDurtyDataGraph() {
        Graph_Durty_.intent(this).start();
    }

    //Подготовка поступивших данных (пары [время: уровень сигнала])
    public boolean calcDurtyData() {
        long minDT = 0;
        minDT = HelperFactory.getHelper().getPickDAO().getMinDT();

        List<Pick> picks = null;

        try {
            picks = HelperFactory.getHelper().getPickDAO().getAllPicks();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        if (picks == null) return false;


        this.data = new DataPoint[picks.size()];

        int i = 0;
        for (Pick pick : picks) {
            //Date x = new Date(pick.getDateTimeLong());
            double x = 1.0 * i;
            double y = pick.getAmplitude();
            this.data[i++] = new DataPoint(x, y);
        }
        return true;
    }

    //Отрисовка данных на графике
    boolean drawBarGraph(int color) {
        graphView.removeAllSeries();
        graphView.getGridLabelRenderer().setHorizontalLabelsVisible(true);
        graphView.getGridLabelRenderer().setVerticalLabelsVisible(true);
        graphView.getGridLabelRenderer().setHumanRounding(true);
        graphView.getGridLabelRenderer().setHorizontalAxisTitle(getString(R.string.Graph_Durty_HorizontalAxisTitle));
        graphView.getGridLabelRenderer().setVerticalAxisTitle(getString(R.string.Graph_Durty_VerticalAxisTitle));


        //graphView.getGridLabelRenderer().setNumHorizontalLabels(3);
        BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>(this.data);
        series.setColor(color);
        series.setSpacing(20);
        //series.setDrawValuesOnTop(true);
        //series.setValuesOnTopColor(Color.RED);

        graphView.addSeries(series);
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(0.0);
        graphView.getViewport().setMaxY(1000.0);

        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                    return (data.getY()>400)?Color.GREEN:Color.YELLOW;
                //return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
            }
        });

        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScalable(true);


        //graphView.getGridLabelRenderer().setHumanRounding(false);


        return true;
    }


    boolean drawFilterLine(int color) {
        int filter = 0;
        try {
            filter = HelperFactory.getHelper().getSettingDAO().getValByName("FILTER");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return drawFilterLine(filter, color);
    }

    boolean drawFilterLine(int value, int color) {


        DataPoint[] filterData = new DataPoint[2];
        DataPoint BeginPoint = new DataPoint(0, value);
        DataPoint lastData = this.data[this.data.length - 1];
        DataPoint EndPoint = new DataPoint(Math.ceil(lastData.getX()), value);
        filterData[0] = BeginPoint;
        filterData[1] = EndPoint;

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(filterData);
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(2);
        series.setThickness(2);
        series.setColor(color);
        graphView.addSeries(series);


        return true;
    }



    @Override
    public void onBackPressed() {
        Main_.intent(this).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Graph Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.user.ormlite/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Graph Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.user.ormlite/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
