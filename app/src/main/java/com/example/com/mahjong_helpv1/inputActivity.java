package com.example.com.mahjong_helpv1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class inputActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText inputText;
    private  Button divNumButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input);
        inputText =(EditText)findViewById(R.id.inputText);
        divNumButton = (Button) findViewById(R.id.inputTextButton);
        divNumButton.setOnClickListener(this);




    }
    @Override
    public void onClick(View v) {
        Pattern pattern = Pattern.compile("\\d+");
        String num =inputText.getText().toString();
        Matcher matcher_m = pattern.matcher(num);
        switch (v.getId()) {

            case R.id.inputTextButton:
                if(inputText.getText().toString().isEmpty()|| Integer.parseInt(inputText.getText().toString())<1|| Integer.parseInt(inputText.getText().toString())>16 )
                {
                    Toast toast = Toast.makeText(inputActivity.this,
                            "張數錯誤", Toast.LENGTH_LONG);
                    //顯示Toast
                    toast.show();
                }
                else
                {
                    Intent intent = new Intent();
                    intent.setClass(inputActivity.this,MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("num",num);
                    intent.putExtras(bundle);   // 記得put進去，不然資料不會帶過去哦
                    startActivity(intent);

                }
        }
    }


}
