package com.example.interactive;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
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
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Capture the layout's TextView and set the string as its text
        TextView title = findViewById(R.id.title);
        title.setText(message);

        TextView paragraphe = findViewById(R.id.paragraphe);
        StringBuilder text = new StringBuilder();

        //opening the chosen file
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(am.open("La_Traque/0")));
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

        paragraphe.setText(text);
        System.out.println(text);
    }

    public void textAnalyser (StringBuilder text,
                              ArrayList<Pair<Pair<Integer,Integer>,Pair<Integer,Integer>>> analysis) {
        Pair< Integer, Integer> tmp1 = new Pair<>();
        Pair< Integer, Integer> tmp2= new Pair<>();
        int start;
        int index = text.indexOf("[");

        while(index != -1) {
            tmp1.setFirst(index);
            start = index;
            index = text.indexOf("[", start);
        }

    }
}