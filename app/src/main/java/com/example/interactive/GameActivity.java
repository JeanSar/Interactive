package com.example.interactive;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.MenuItem;
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

        // toolbar2 is defined in the layout file
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        assert ab != null;

        // Disable de title

        ab.setDisplayShowTitleEnabled(false);

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);



        //getting access to assets via AssetManager
        AssetManager am = getAssets();

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String msg = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        //Cutting the message to get ony the title and the nbtext
        int idx = msg.indexOf("/");
        String msgtitle = "";
        String nbtext = "";
        if(idx != -1) {
            msgtitle =  msg.substring(0,idx);
            nbtext = msg.substring(idx + 1);
        }

        // Capture the layout's TextView and set the string as its text
        TextView title = findViewById(R.id.title);
        title.setText(msgtitle.replace("_"," "));

        launchText(msgtitle, Integer.parseInt(nbtext), am);

    }

    //display the right text from the story
    public void launchText(String msgtitle, int nbtext, AssetManager am) {

        TextView paragraphe = findViewById(R.id.paragraphe);
        StringBuilder text = new StringBuilder();

        //opening the chosen file
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(am.open(msgtitle + "/" + nbtext)));
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

        ArrayList< Pair<Pair< Integer, Integer>, Integer>> analysis =
                new ArrayList<>();
        String newtext = textAnalyser(text, analysis);

        SpannableString ss = new SpannableString(newtext);

        for(int i= 0; i < analysis.size(); i++) {
            int finalI = i;
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    if(analysis.get(finalI).getSecond() != -1) {
                        launchText(msgtitle, analysis.get(finalI).getSecond(), am);
                    }else {
                        quitToMenu();
                    }
                }
                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            };
            ss.setSpan(clickableSpan, analysis.get(finalI).getFirst().getFirst(), analysis.get(finalI).getFirst().getSecond(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }


        paragraphe.setText(ss);
        paragraphe.setMovementMethod(LinkMovementMethod.getInstance());
        paragraphe.setHighlightColor(Color.TRANSPARENT);
    }

    //get the possible choices and the indexes of the clickable texts while removing the encoding information
    public String textAnalyser (StringBuilder text,
                              ArrayList<Pair<Pair<Integer,Integer>,Integer>> analysis) {
        String newtext = text.toString();
        Pair< Integer, Integer> tmp1 = new Pair<>(); //clickable zone
        int tmp2; //choice zone
        int tmp3; //value of the choice zone

        int start;
        int index = newtext.indexOf("[");

        while(index != -1) { //while there is choices in the text with right format
            tmp1.setFirst(index);
            start = index;
            newtext = newtext.replaceFirst("\\[","");

            index = newtext.indexOf("]", start) ;
            tmp1.setSecond(index);
            newtext = newtext.replaceFirst("]","");


            index = newtext.indexOf("]", start);
            tmp2 = index;
            start = index;

            tmp3 = Integer.parseInt(newtext.substring(tmp1.getSecond() + 1, tmp2)); //get the right choice's value

            newtext = newtext.replaceFirst("\\[" + tmp3 + "]","");

            analysis.add(new Pair<>(new Pair<>(tmp1.getFirst(), tmp1.getSecond()),tmp3));
            index = newtext.indexOf("[", start - (tmp2 - tmp1.getSecond()));
        }

        return newtext;
    }
    /*
    private void addIdToBackButton(Toolbar toolbar) {
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View child = toolbar.getChildAt(i);
            if (child instanceof AppCompatImageButton) {
                child.setId(R.id.toolbar_back_button);
                return;
            }
        }
    }*/

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                quitToMenu();
                return true;
/*
            case :
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;*/

            default:
                return super.onOptionsItemSelected(item);

        }
    }


    public void quitToMenu() {
        finish();
    }
}