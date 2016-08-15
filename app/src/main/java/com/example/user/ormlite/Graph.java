package com.example.user.ormlite;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.ormlite.Database.HelperFactory;
import com.example.user.ormlite.Database.Pick;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jjoe64.graphview.GraphView;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@EActivity(R.layout.activity_graph)
@OptionsMenu(R.menu.menu_graph)
public class Graph extends Activity {
    private GraphView graphView;

    private DataPoint[] data = null;

    private final int TYPE_BAR = 1101;
    private final int TYPE_LINE = 1102;

    private final int GRAPH_POWER = 1201;
    private final int GRAPH_ENERGY = 1202;
    private final int GRAPH_DURTY = 1203;

    @InstanceState
    int GraphState = 1201;


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

        switch (GraphState) {
            case GRAPH_ENERGY:
                showEnergyGraph();
                break;
            case GRAPH_POWER:
                showPowerGraph();
                break;
            case GRAPH_DURTY:
                showDurtyDataGraph();
                break;
            default:
                showPowerGraph();
        }

    }

    @OptionsItem(R.id.mi_graph_Power)
    void showPowerGraph() {
        //DataPoint[] data = new DataPoint[0];
        GraphState = GRAPH_POWER;
        txtGraphLabel.setText(getText(R.string.graphLabelPower));
        if (calcPower())
            drawGraph(TYPE_LINE, Color.BLUE);
        else
            Toast.makeText(this, "Нет данных", Toast.LENGTH_SHORT).show();
    }

    @OptionsItem(R.id.mi_graph_Energy)
    void showEnergyGraph() {
        data = null;
        GraphState = GRAPH_ENERGY;

        txtGraphLabel.setText(getText(R.string.graphLabelEnergy));

        if (calcEnergy())
            drawGraph(TYPE_LINE, Color.RED);
        else
            Toast.makeText(this, getText(R.string.noneData), Toast.LENGTH_SHORT).show();

    }

    @OptionsItem(R.id.mi_graph_DurtyData)
    void showDurtyDataGraph() {
        data = null;
        GraphState = GRAPH_DURTY;
        txtGraphLabel.setText(getText(R.string.graphLabelDurty));
        if (calcDurtyData()) {
            drawFilterLine(Color.CYAN);
            drawGraph(TYPE_BAR, Color.GREEN);

        } else
            Toast.makeText(this, getText(R.string.noneData), Toast.LENGTH_SHORT).show();
    }


    //Расчет мощности потребления по имеющимся данным
    public boolean calcEnergy() {

        if (!calcPower()) return false;

        for (int i = 1; i < data.length; i++) {
            double x_0 = data[i].getX();
            double y_0 = data[i].getY();
            double y_m1 = data[i - 1].getY();
            data[i] = new DataPoint(x_0, y_0 + y_m1);
        }

        return true;
    }

    //Расчет количества потребленной ЭЭ за время измерений
    public boolean calcPower() {
        long minDT = HelperFactory.getHelper().getPickDAO().getMinDT();


        int filter = 0;
        try {
            filter = HelperFactory.getHelper().getSettingDAO().getValByName("FILTER");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int tickCount = 0;
        try {
            tickCount = HelperFactory.getHelper().getSettingDAO().getValByName("TICK_COUNT");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int minDuration = 0;
        try {
            minDuration = HelperFactory.getHelper().getSettingDAO().getValByName("MIN_DURATION");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<Pick> picks = null;
        try {
            picks = HelperFactory.getHelper().getPickDAO().getAllPicksWhereAmplitudeGE(filter);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (picks == null) return false;

        ArrayList<Pick> tmpPicks = new ArrayList<>();
        compressPicks(minDuration, picks, tmpPicks);


        this.data = new DataPoint[tmpPicks.size()];
        for (int i = 0; i < tmpPicks.size(); i++) {
            Date x = new Date(tmpPicks.get(i).getDateTimeLong()-minDT);
            double y = 0;
            if (i > 0) {
                double interval = 1.0 * (tmpPicks.get(i).getDateTimeLong()
                                       - tmpPicks.get(i - 1).getDateTimeLong()) / 1000;
                y = 3600.0 / (tickCount * interval);
            }
            data[i] = new DataPoint(x, y);
        }
        return true;
    }

    private void compressPicks(int minDuration, List<Pick> picks, ArrayList<Pick> tmpPicks) {
        for (int i = 0; i < picks.size(); i++) {
            Pick p = new Pick(picks.get(i).getDateTimeLong(), picks.get(i).getAmplitude());
            tmpPicks.add(p);
            int lastPickIdx = tmpPicks.size() - 1;
            if (lastPickIdx > 0) {
                Pick pick_m1 = tmpPicks.get(lastPickIdx);
                Pick pick_m2 = tmpPicks.get(lastPickIdx - 1);

                if (pick_m1.getDateTimeLong() - pick_m2.getDateTimeLong() < minDuration * 1.0) {
                    if (pick_m1.getAmplitude() < pick_m2.getAmplitude())
                        tmpPicks.remove(lastPickIdx);
                    else
                        tmpPicks.remove(lastPickIdx - 1);
                }
            }
        }
    }


    public double power(int tickCount, double period) {
        return 3600.0 / (tickCount * period);
    }

    //Подготова поступивших данных (пары [время: уровень сигнала])
    public boolean calcDurtyData() {
        long minDT = 0;
        minDT = HelperFactory.getHelper().getPickDAO().getMinDT();

        List<Pick> picks = null;

        try {
            picks = HelperFactory.getHelper().getPickDAO().getAllPicks();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        if (picks== null) return false;


        this.data = new DataPoint[picks.size()];

        int i = 0;
        for (Pick pick : picks) {
            //Date x = new Date(pick.getDateTimeLong());
            double x= 1.0*i;
            double y = pick.getAmplitude();
            this.data[i++] = new DataPoint(x, y);
        }
        return true;
    }

    //Отрисовка данных на графике
    boolean drawGraph(int GraphType, int color) {
        graphView.removeAllSeries();


        //graphView.getGridLabelRenderer().setHorizontalLabelsVisible(true);
        //graphView.getGridLabelRenderer().setVerticalLabelsVisible(true);
        //graphView.getGridLabelRenderer().setHumanRounding(false);


        graphView.getGridLabelRenderer().setNumHorizontalLabels(3);

        if (GraphType == TYPE_BAR) {
            BarGraphSeries<DataPoint> series = new BarGraphSeries<>(this.data);
            series.setColor(color);
            graphView.addSeries(series);
            //graphView.getViewport().setYAxisBoundsManual(true);
            //graphView.getViewport().setMinY(0.0);
            //graphView.getViewport().setYAxisBoundsManual(false);
            //graphView.getViewport().setMinY(0.0);
        }

        if (GraphType == TYPE_LINE) {
            graphView.getGridLabelRenderer().setHorizontalAxisTitle("Date");
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(this.data);
            series.setDrawDataPoints(true);
            series.setDataPointsRadius(2);
            series.setColor(color);
            graphView.addSeries(series);

            graphView.getGridLabelRenderer().setLabelFormatter(
                    new DateAsXAxisLabelFormatter(this, new SimpleDateFormat(getString(R.string.AxisXFormat))));// hh:mm:ss


            graphView.computeScroll();



        }
        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScalable(true);



        graphView.getGridLabelRenderer().setHumanRounding(false);


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

        try {
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

        } catch (Exception e) {
            return false;
        }

        return true;
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
