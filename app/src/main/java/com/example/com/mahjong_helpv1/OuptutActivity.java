package com.example.com.mahjong_helpv1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class OuptutActivity extends AppCompatActivity  implements View.OnClickListener {
    private TextView outputTextView;
    private ArrayList<String> listen = new ArrayList<>() ;
    private ArrayList<String> finalOutput = new ArrayList<>() ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.output);
        outputTextView = (TextView)findViewById(R.id.outputTextView);

        Intent reIntentObj = getIntent(); /* 取得傳入的 Intent 物件 */
        Bundle bundle = reIntentObj.getExtras();

        if (bundle != null && bundle.containsKey("listen") ){
            listen = bundle.getStringArrayList("listen");

        }

        for(int i =0 ;i<listen.size();i++)
        {
            finalOutput.add(parserChinese(listen.get(i)));
        }
        String a="";
        for(int i =0 ;i<finalOutput.size();i++)
        {


            a+=finalOutput.get(i);
        }
        outputTextView.setText(a);
    }
    private String parserChinese(String card) {
        if (card.indexOf("T") != -1) {
            return card.charAt(0) + "條";
        }
        if (card.indexOf("O") != -1) {
            return card.charAt(0) + "筒";
        }
        if (card.indexOf("W") != -1) {
            return card.charAt(0) + "萬";
        }
        if (card == "DONG") {
            return "東";
        }

        if (card == "XI") {
            return "西";
        }
        if (card == "NAN") {
            return "南";
        }
        if (card == "BEI") {
            return "北";
        }
        if (card == "ZHONG") {
            return "中";
        }
        if (card == "FA") {
            return "發";
        }
        if (card == "BAI") {
            return "白";
        }
        return "";8
    }
    @Override
    public void onClick(View v) {

    }
}
