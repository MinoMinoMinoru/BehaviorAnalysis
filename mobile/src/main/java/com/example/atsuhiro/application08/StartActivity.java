package com.example.atsuhiro.application08;

import android.content.Intent;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.example.atsuhiro.application08.Retrofit.RetrofitClient;

public class StartActivity extends AppCompatActivity {
    private String BASE_URL= "https://script.google.com/macros/s/AKfycbzqRy8L25yaV_YzaaeSqNBnHXljIM3OkTrIi-O1sCVRHZKeWO4/";
    private String NAME = "";
    private String TAG = "";
    private String INTERVAL="";
    private String[] order = {"make-sheet","append-row"};

    RetrofitClient client = new RetrofitClient(BASE_URL);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        // set Button
        Button getIPBt = findViewById(R.id.setIP_button);
        Button PostBt = findViewById(R.id.post_button);
        Button NextBt = findViewById(R.id.next_button);
        // setEditor
        final EditText name_text = findViewById(R.id.name_editor);
        final EditText tag_text = findViewById(R.id.tag_editor);
        final EditText interval_text = findViewById(R.id.interval_editor);

        // Set Listener
        getIPBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NAME = name_text.getText().toString();
                TAG = tag_text.getText().toString();
                INTERVAL=interval_text.getText().toString();
                NAME=NAME+"_"+TAG;
                Toast.makeText(StartActivity.this, "Set Sheet Name as:"+NAME, Toast.LENGTH_SHORT).show();
                client.setURL(BASE_URL);
                Log.d("StartActivity:INTERVAL",INTERVAL);
            }
        });

        PostBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(StartActivity.this, "TestPost", Toast.LENGTH_SHORT).show();
                Log.d("post","Done");
                long[] action={1,2,3,11};
                // sheet 作成
                Log.d("post",order[0]);
                client.post(NAME,order[1],action);
                client.post(NAME,order[0],action);
            }
        });

        NextBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // インテントへのインスタンス生成
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                //　インテントに値をセット
                intent.putExtra("URL", BASE_URL);
                intent.putExtra("NAME", NAME);
                intent.putExtra("INTERVAL", INTERVAL);
                // サブ画面の呼び出し
                startActivity(intent);
            }
        });


    }
}
