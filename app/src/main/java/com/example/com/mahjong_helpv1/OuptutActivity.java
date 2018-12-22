package com.example.com.mahjong_helpv1;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OuptutActivity extends AppCompatActivity  {
    private TextView outputTextView;
    private ArrayList<String> listen = new ArrayList<>() ;
    private ArrayList<Integer> pic =new ArrayList<>();
    private ArrayList<String> finalOutput = new ArrayList<>() ;
    private Button speechBtn; // 按钮控制开始朗读
    private EditText speechTxt; // 需要朗读的内容
    private TextToSpeech textToSpeech;
    private Button speech ;
    private String a="";
    private  int  count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.output);
        outputTextView = (TextView)findViewById(R.id.outputTextView);
        speech = (Button)findViewById(R.id.button2);
        pic.add(R.drawable.t_1);
        pic.add(R.drawable.t_2);
        pic.add(R.drawable.t_3);
        pic.add(R.drawable.t_4);
        pic.add(R.drawable.t_5);
        pic.add(R.drawable.t_6);
        pic.add(R.drawable.t_7);
        pic.add(R.drawable.t_8);
        pic.add(R.drawable.t_9);
        pic.add(R.drawable.o_1);
        pic.add(R.drawable.o_2);
        pic.add(R.drawable.o_3);
        pic.add(R.drawable.o_4);
        pic.add(R.drawable.o_5);
        pic.add(R.drawable.o_6);
        pic.add(R.drawable.o_7);
        pic.add(R.drawable.o_8);
        pic.add(R.drawable.o_9);
        pic.add(R.drawable.w_1);
        pic.add(R.drawable.w_2);
        pic.add(R.drawable.w_3);
        pic.add(R.drawable.w_4);
        pic.add(R.drawable.w_5);
        pic.add(R.drawable.w_6);
        pic.add(R.drawable.w_7);
        pic.add(R.drawable.w_8);
        pic.add(R.drawable.w_9);
        pic.add(R.drawable.dong_1);
        pic.add(R.drawable.xi_1);
        pic.add(R.drawable.nan_1);
        pic.add(R.drawable.bei_1);
        pic.add(R.drawable.zhog_1);
        pic.add(R.drawable.fa_1);
        pic.add(R.drawable.bai_1);

        Intent reIntentObj = getIntent(); /* 取得傳入的 Intent 物件 */
        Bundle bundle = reIntentObj.getExtras();

        if (bundle != null && bundle.containsKey("listen") ){
            listen = bundle.getStringArrayList("listen");
        }

        for(int i =0 ;i<listen.size();i++)
        {
            finalOutput.add(parserChinese(listen.get(i)));
            count+=200;
        }

        for(int i =0 ;i<finalOutput.size();i++)
        {
            a+=finalOutput.get(i)+" ";
        }

        outputTextView.setText("你聽的是"+a);
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == textToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.CHINA);
                    if (result != TextToSpeech.LANG_COUNTRY_AVAILABLE
                            && result != TextToSpeech.LANG_AVAILABLE){
                        Toast.makeText(OuptutActivity.this, "TTS暂时不支持这种语音的朗读！",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });



        speech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech.speak("你聽的是"+a,
                        TextToSpeech.QUEUE_ADD, null);
            }
        });

    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null)
            textToSpeech.shutdown();
        super.onDestroy();
    }



    private  void setPic(int num)
    {
        RelativeLayout relativeLayout =  (RelativeLayout) findViewById(R.id.relativeLayout);
        ImageView imageview= new ImageView(getApplicationContext());
        imageview.setImageResource(pic.get(num)-1);
//這一行是你想要讓你的圖片大小是多大，我想要的是30*30
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(200, 200);
//下面是你想要讓你的圖片的位置在哪邊，我想要的是距離上面100，距離左邊100
        params.leftMargin = 200+count;
        params.topMargin = 100;
        relativeLayout.addView(imageview,params);
    }
    private String parserChinese(String card) {
        if (card.indexOf("T") != -1) {
            String temp ;
            temp = card.substring(0,1);
            setPic(Integer.parseInt(temp));
            return card.charAt(0) + "條";
        }
        if (card.indexOf("O") != -1) {
            String temp ;
            temp = card.substring(0,1);
            setPic(9+(Integer.parseInt(temp)));
            return card.charAt(0) + "筒";
        }
        if (card.indexOf("W") != -1) {
            String temp ;
            temp = card.substring(0,1);
            setPic(18+(Integer.parseInt(temp)));
            return card.charAt(0) + "萬";
        }
        if (card == "DONG") {
            setPic(28);
            return "東";
        }

        if (card == "XI") {
            setPic(29);
            return "西";
        }
        if (card == "NAN") {
            setPic(30);
            return "南";
        }
        if (card == "BEI") {
            setPic(31);
            return "北";
        }
        if (card == "ZHONG") {
            setPic(32);
            return "中";
        }
        if (card == "FA") {
            setPic(33);
            return "發";
        }
        if (card == "BAI") {
            setPic(34);
            return "白";
        }
        return "";
    }


}
