package com.example.atsuhiro.application08;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import com.github.mikephil.charting.charts.PieChart;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import com.example.atsuhiro.application08.Retrofit.RetrofitClient;

// TODO POSTの処理
public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, MessageApi.MessageListener{
    private static final String TAG = MainActivity.class.getName();
    private GoogleApiClient mGoogleApiClient;
    private Chart chart;
    TextView nameView,intervalView,timeView;
    TextView actionTextView;

    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    Date nowDate = null;
    Date startDate = null;
    Date termStartDate = null;

    private long interval=10;
    private long termTime=0;
    private long sendTime=0;

    private String BASE_URL= "URL for POST";
    private String NAME = "";
    private long INTERVAL =10;

    // output file of csv
    String FILE = "/data/data/com.example.atsuhiro.application08/test.csv";

    RetrofitClient client = new RetrofitClient(BASE_URL);

    int x,y,z;
    String GuessAction = "";
    double[] actionTime;

    String[] names = new String[]{"x-value", "y-value", "z-value"};
    int[] colors = new int[]{Color.RED, Color.GREEN, Color.BLUE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //GoogleApiClientインスタンス生成
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.d(TAG, "onConnectionFailed:" + connectionResult.toString());
                    }
                })
                .addApi(Wearable.API)
                .build();

        Intent intent = getIntent();
        NAME = intent.getStringExtra("NAME");
        Log.d("MainACtivity:oncreate:",NAME);
        INTERVAL = Integer.parseInt(intent.getStringExtra("INTERVAL"));
        Log.d("MainACtivity:oncreate:",NAME);

        // activity_main.xml にUIコンポーネントを配置する
        setContentView(R.layout.activity_main);

        nameView = findViewById(R.id.nameValue);
        intervalView = findViewById(R.id.intervalValue);
        timeView = findViewById(R.id.passTimeValue);
        actionTextView = findViewById(R.id.action);

        PieChart piechart = findViewById(R.id.chart);

        nameView.setText(NAME);
        intervalView.setText("Interval:"+interval);
        timeView.setText("経過時間：");

        chart = new Chart(piechart);
        chart.setupPieChart();
        chart.setInterval(INTERVAL);

        try {
            startDate=dateFormat.parse( dateFormat.format(new Date()) );
            termStartDate=startDate;
        } catch (ParseException e) {
            Log.d("parseError",e.getMessage());
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            Wearable.MessageApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //if (id == R.id.action_settings) {
        //    return true;
        //}
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        //GoogleApiClient 接続成功時に呼ばれます。
        Log.d(TAG, "onConnected");
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        //接続中断時に呼ばれます。
        Log.d(TAG, "onConnectionSuspended");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        //メッセージ（データ）が来たら呼ばれます。
//        xTextView.setText(messageEvent.getPath());
        Log.d(TAG, "DataReserved");
        //受け取ったデータmsgはコンマ区切りのcsv形式なので、value[]にそれぞれ格納します。
        String msg = messageEvent.getPath();
        String[] value = msg.split(",", 0);

//        xTextView.setText("X:" + value[0]);
//        yTextView.setText("Y:" + value[1]);
//        zTextView.setText("Z:" + value[2]);

        //String型のX,Y,Zをint型に変換
        double x_double = Double.valueOf(value[0]);
        double y_double = Double.valueOf(value[1]);
        double z_double = Double.valueOf(value[2]);

        //GuessAction取得
        GuessAction = getAction(x_double,y_double,z_double);
        actionTextView.setText(GuessAction);
        chart.addNum(GuessAction);

        upDateChart();

        // 一応csv出力
//        writeCSV(value);
    }

    public void upDateChart(){
        // test
        try {
            nowDate=dateFormat.parse( dateFormat.format(new Date()) );
            sendTime=getSubTime(nowDate,startDate);
            termTime=getSubTime(nowDate,termStartDate);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("parseError",e.getMessage());
        }

        if(termTime>=interval){
//            Toast toast = Toast.makeText(MainActivity.this, "Update termStart", Toast.LENGTH_LONG);
//            toast.show();

            // term
            try {
                termStartDate=dateFormat.parse( dateFormat.format(new Date()) );
            } catch (ParseException e) {
                Log.d("parseError",e.getMessage());
            }
            // 経過時間
            chart.setSpendTime(sendTime);
            chart.setRealTime();
            chart.setupPieChart();
            chart.setTermActionTime();
            chart.sendTermTime2Sheet(client,NAME);

        }

        timeView.setText("経過時間："+String.valueOf(sendTime)+" Second");

    }

    private long getSubTime(Date start,Date end){
        // 差分の時間を算出(Second)
        long subTime = start.getTime() - end.getTime() ;
        // 単位を秒に変換
        subTime = subTime/1000;
        return subTime;
    }

    public String getAction(double x_double,double y_double,double z_double){
        if (z_double < 8.755) {
            if (y_double < -3.672) {
                if (x_double < -1.076) {
                    GuessAction = "スマートフォン";
                } else GuessAction = "筆記";
            } else if (x_double < -4.136) {
                GuessAction = "通話";
            } else GuessAction = "PC操作";
        }

        else if (y_double < -4.275) {
            if (x_double < -0.044) {
                GuessAction = "PC操作";
            } else GuessAction = "筆記";
        }

        else if (z_double < 11.876) {
            GuessAction = "PC操作";
        } else GuessAction = "筆記";

        return GuessAction;
    }


    public void writeCSV(String[] accelvalue){
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(FILE, true), "UTF-8"));
            String write_int = nowDate + "," +
                    accelvalue[0] + "," +
                    accelvalue[1] + "," +
                    accelvalue[2] + "," + GuessAction + "\n";
            bw.write(write_int);
            bw.close();
        } catch (UnsupportedEncodingException k) {
            k.printStackTrace();
        } catch (FileNotFoundException k) {
            k.printStackTrace();
        } catch (IOException k) {
            k.printStackTrace();
        }
        Log.d(TAG, "WriteOK");
    }

}

