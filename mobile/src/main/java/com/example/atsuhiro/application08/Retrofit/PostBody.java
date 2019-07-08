package com.example.atsuhiro.application08.Retrofit;

import android.util.Log;

public class PostBody {
    String sheetname,order;
    long actionRate[]={0,0,0,0};

    PostBody(String _name){
        this.sheetname = _name;
    }

    public void setBody(String _name,String _order,long[] _action){
        Log.d("PostBody","setBody");
        this.sheetname = _name;
        this.order=_order;
        this.actionRate = _action;
    }

}

