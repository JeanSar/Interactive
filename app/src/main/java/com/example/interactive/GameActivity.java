package com.example.interactive;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    private String currentText = "0";
    private String story = "";
    private boolean isSaved = false;

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

        story = msgtitle;

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
                        finish();
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

        //storing the current position in the story in a variable
        currentText = Integer.toString(nbtext);
        //Progression is not saved
        isSaved = false;
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

    @Override
    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_favorite:
                save();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void save() {
        File dir = new File(this.getFilesDir(), "saves");
        if(!dir.exists()){
            boolean created = dir.mkdir();
            if (created) {
                //TODO faire qqc
            }
        }
        try {
            File gpxfile = new File(dir, story);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(currentText);
            writer.flush();
            writer.close();
            isSaved = true;
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(android.R.string.dialog_alert_title);
        if(isSaved) {
            builder.setMessage("Partie sauvegardée - Voulez-vous vraiment quitter ?");
        } else {
            builder.setMessage("Vous allez quitter la partie sans sauvegarder votre progression");
            builder.setNegativeButton("Sauver", (dialog, which) -> {
                save();
                GameActivity.super.onBackPressed();
            });
        }
        builder.setPositiveButton(android.R.string.yes,
                (dialog, which) -> super.onBackPressed());
        builder.setNeutralButton(android.R.string.cancel, (dialog, which) -> {});

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }
}

