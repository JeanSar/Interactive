package com.example.interactive;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //getting access to assets via AssetManager
        AssetManager am = getAssets();

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String msg = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        //Cutting the message to get ony the title if possible
        int idx = msg.indexOf("/");
        String msgtitle = "";
        if(idx != -1) {
            msgtitle =  msg.substring(0,idx);
        }
        // Capture the layout's TextView and set the string as its text
        TextView title = findViewById(R.id.title);
        title.setText(msgtitle);

        TextView paragraphe = findViewById(R.id.paragraphe);
        StringBuilder text = new StringBuilder();

        //opening the chosen file
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(am.open(msg)));
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                text.append(mLine);
                text.append(System.getProperty("line.separator"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ERR ", e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("ERR ", e.getMessage());
                }
            }
        }

        ArrayList< Pair<Pair< Integer, Integer>, Pair< Integer, Integer>>> analysis =
            new ArrayList<>();
        textAnalyser(text, analysis);

        //System.out.println("XXXXXXXXXXXX-----------> " + analysis.get(0).getSecond().getSecond());

        SpannableString ss = new SpannableString(text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                startActivity(new Intent(GameActivity.this, MainActivity.class));
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, analysis.get(0).getFirst().getFirst(), analysis.get(0).getFirst().getSecond(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        paragraphe.setText(ss);
        paragraphe.setMovementMethod(LinkMovementMethod.getInstance());
        paragraphe.setHighlightColor(Color.TRANSPARENT);
    }

    //va récupérer les choix possibles dans le texte également modifié et les ordonner dans une liste
    public void textAnalyser (StringBuilder text,
                              ArrayList<Pair<Pair<Integer,Integer>,Pair<Integer,Integer>>> analysis) {
        Pair< Integer, Integer> tmp1 = new Pair<>();
        Pair< Integer, Integer> tmp2= new Pair<>();
        int start;
        int index = text.indexOf("[");

        while(index != -1) {
            tmp1.setFirst(index);
            start = index;

            index = text.indexOf("]", start);
            tmp1.setSecond(index);

            index++;
            tmp2.setFirst(index);
            start = index;

            index = text.indexOf("]", start);
            tmp2.setSecond(index);
            start = index;

            analysis.add(new Pair<>(tmp1,tmp2));

            index = text.indexOf("[", start);
        }
    }
}