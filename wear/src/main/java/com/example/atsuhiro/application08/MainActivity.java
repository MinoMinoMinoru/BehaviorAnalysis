package com.example.atsuhiro.application08;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
//import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Date;

//SensorEventListenerインタフェースの実装
public class MainActivity extends Activity implements SensorEventListener {

    private final String TAG = MainActivity.class.getName();
    private TextView mTextView;
    private SensorManager mSensorManager;
    private GoogleApiClient mGoogleApiClient;
    private String mNode;
    private float x,y,z;
    private float heatrate;
    //private String str;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mTextView = findViewById(R.id.text);
        mTextView.setTextSize(24.0f);

        //センサーマネージャーを取得
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        this.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Log.d(TAG, "onConnected");
                        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                            @Override
                            public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                                if (nodes.getNodes().size() > 0) {
                                    mNode = nodes.getNodes().get(0).getId();
                                }
                            }
                        });
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.d(TAG, "onConnectionSuspended");

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.d(TAG, "onConnectionFailed : " + connectionResult.toString());
                    }
                })
                .addApi(Wearable.API)
                .build();
        this.mGoogleApiClient.connect();
    }

    @Override
    public void onResume(){
        // TODO make HEARTRATE SENSOR
        super.onResume();
        Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        mGoogleApiClient.disconnect();
    }

    @Override
    //加速度センサの値に変更があった場合に実行される
    //加速度データを送信するプログラムを記述
    public void onSensorChanged(SensorEvent event) {
        if(count>= 10) {
            count = 0;
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];
                mTextView.setText(String.format("X : %f\nY : %f\nZ : %f" , x, y, z));
                //メッセージとして加速度をmobileに送信する処理
                String SEND_DATA = x + "," + y + "," + z;
                if (mNode != null) {
                    Wearable.MessageApi.sendMessage(mGoogleApiClient, mNode, SEND_DATA, null).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult result) {
                            if (!result.getStatus().isSuccess()) {
                                Log.d(TAG, "ERROR : failed to send Message" + result.getStatus());
                            }

                        }
                    });
                }
            }

            if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
                heatrate = event.values[0];

            }

            // setText in wear
//            mTextView.setText(String.format("X : %f\nY : %f\nZ : %f, x, y, z"));
//            mTextView.setText(String.format("X : %f\nY : %f\nZ : %f\nHR：%f" , x, y, z,heatrate));

            //メッセージとして加速度をmobileに送信する処理
            String SEND_DATA = x + "," + y + "," + z;
//            String SEND_DATA = x + "," + y + "," + z+","+heatrate;
            if (mNode != null) {
                Wearable.MessageApi.sendMessage(mGoogleApiClient, mNode, SEND_DATA, null).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult result) {
                        if (!result.getStatus().isSuccess()) {
                            Log.d(TAG, "ERROR : failed to send Message" + result.getStatus());
                        }

                    }
                });
            }

        }else count++;
    }

    @Override
    //センサの精度に変更があった場合に実行される
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
