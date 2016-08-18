package com.example.user.ormlite.measures;

        import android.app.Activity;
        import android.widget.Button;
        import android.widget.TextView;

        import com.example.user.ormlite.R;
        import com.example.user.ormlite.database.HelperFactory;
        import com.example.user.ormlite.database.Pick;
        import com.jjoe64.graphview.GraphView;
        import com.jjoe64.graphview.Viewport;
        import com.jjoe64.graphview.series.BarGraphSeries;
        import com.jjoe64.graphview.series.DataPoint;

        import org.androidannotations.annotations.AfterViews;
        import org.androidannotations.annotations.Click;
        import org.androidannotations.annotations.EActivity;
        import org.androidannotations.annotations.ViewById;

        import java.sql.SQLException;
        import java.util.Calendar;

@EActivity(R.layout.activity_noicemeter)
public class Noice extends Activity implements NoiceMeter.NoiceMeterListener {
    String LOG_TAG = "myLogs";

    BarGraphSeries<DataPoint> series;
    GraphView graph = null;

    private NoiceMeter nMeter = null;
    //private DBH dbh=null;

    private long lastDTl=0;
    private volatile double lastPower = 0.0;

    @ViewById(R.id.txtNmValue)
    TextView txtNmValue;

    @ViewById(R.id.btnNmStartStop)
    Button btnNmStartStop;



    @AfterViews
    void init(){
        if (nMeter==null)
            nMeter= new NoiceMeter();
        nMeter.addListener(this);
        graph = (GraphView) findViewById(R.id.graph);
        series = new BarGraphSeries<>();
        graph.addSeries(series);
        Viewport viewport = graph.getViewport();
        viewport.setScrollable(true);
        //if (dbh==null) dbh = new DBH(getApplicationContext());
    }



    @Click(R.id.btnNmStartStop)
    void btnNmStartStopClick(){
        if(btnNmStartStop.getText().toString()==getText(R.string.start)){
            nMeter.initMediaRecoder();
            nMeter.runRecording();
            btnNmStartStop.setText(getText(R.string.stop));
        }
        else{
            nMeter.stop_rec();
            btnNmStartStop.setText(getText(R.string.start));
        }
    }

    double MomentPower(){
        double res = 0.0;
        if(this.lastDTl==0) return res;
        long DTL = Calendar.getInstance().getTimeInMillis();
        double period = (DTL - this.lastDTl)/1000;
        res = 3600/(200*period);
        return res;
    }

    @Override
    public void onImpuls(final int amplitude)  { //TODO Разобрать метод на модули
        if ((amplitude>1000)&(amplitude<25000)) {
            //dbh.impuls(amplitude);
            try {
                HelperFactory.getHelper().getPickDAO().create(new Pick(System.currentTimeMillis(),amplitude));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            this.lastDTl = Calendar.getInstance().getTimeInMillis();
            this.lastPower = MomentPower();
            series.appendData(new DataPoint(System.currentTimeMillis(),amplitude), true, 20);

        }

        txtNmValue.post(new Runnable() {
            public void run() {
                txtNmValue.setText("" + amplitude + " dB " + lastPower);

            }
        });
    }
}
//http://www.ssaurel.com/blog/create-a-real-time-line-graph-in-android-with-graphview/

