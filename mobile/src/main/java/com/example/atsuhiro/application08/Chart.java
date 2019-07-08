package com.example.atsuhiro.application08;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import com.example.atsuhiro.application08.Retrofit.RetrofitClient;

public class Chart {
    String actionNames[] = {"スマートフォン", "PC操作", "通話", "筆記"};

    // 円グラフ表示用（全区間分）
    int actionNum[]={0,0,0,0};
    long actionTime[] = {0,0,0,0};

    // termごとの
    double termRate[]={0,0,0,0};
    long termActionTime[]={0,0,0,0};

    long interval=0;
    long spendtime=0;

    PieChart piechart;

    public Chart(PieChart _piechart){
        this.piechart=_piechart;
    }

    public void setInterval(long _interval){
        this.interval = _interval;
    }

    // 経過時間
    public void setSpendTime(long timeSub){
        this.spendtime=timeSub;
    }

    public void addNum(String action){
        for(int i=0;i<actionNum.length;i++){
            if(actionNames[i].equals(action)){
                actionNum[i]++;
                termRate[i]++;
            }
        }
        Log.d("addNum:",String.valueOf(actionNum));
    }

    // 実時間を算出（円グラフ）
    public void setRealTime(){
        Log.d("setRealTime:","こっから処理");

        int totalNum = 0;
        for(int i=0;i<actionNum.length;i++){
            totalNum+=actionNum[i];
        }
        for(int i=0;i<actionTime.length;i++){
            actionTime[i]=spendtime*actionNum[i]/totalNum;
        }
        Log.d("setRealTime-sendTime:",String.valueOf(spendtime));
        Log.d("setRealTime-actionNum:",actionNum[0]+","+actionNum[1]+","+actionNum[2]+","+actionNum[3]);
        Log.d("setRealTime-actionTime:",actionTime[0]+","+actionTime[1]+","+actionTime[2]+","+actionTime[3]);
    }

    // 区間ごとの各動作の比率を算出
    public void setTermActionTime(){
        Log.d("termRate:","こっから処理");
        Log.d("termRate",termRate[0]+","+termRate[1]+","+termRate[2]+","+termRate[3]);

        int totalNum = 0;
        for(int i=0;i<termRate.length;i++){
            totalNum+=termRate[i];
        }
        for(int i=0;i<termRate.length;i++){
            this.termActionTime[i]=(long)(termRate[i]*interval/totalNum);
        }
        // termRateの初期化
        for(int i=0;i<termRate.length;i++)
            this.termRate[i]=0;
        Log.d("termActionTime:",termActionTime[0]+","+termActionTime[1]+","+termActionTime[2]+","+termActionTime[3]);
    }

    // draw graph
    public void setupPieChart() {
        //PieEntriesのリストを作成する:
        List<PieEntry> pieEntries = new ArrayList<>();
        for (int i = 0; i < actionTime.length; i++) {
            pieEntries.add(new PieEntry(actionTime[i], actionNames[i]));
        }

        PieDataSet dataSet = new PieDataSet(pieEntries, "Behavior Analysis");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data = new PieData(dataSet);

        //PieChartを取得する:
        piechart.setData(data);
        piechart.invalidate();
    }

    public void sendTermTime2Sheet(RetrofitClient client,String NAME){
        Log.d("termActionTime:in send",termActionTime[0]+","+termActionTime[1]+","+termActionTime[2]+","+termActionTime[3]);
        client.post(NAME,"append-row",termActionTime);
    }

}
