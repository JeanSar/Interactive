package com.example.interactive;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Transition;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "Story/nb";
    public static final String STORY ="La_Traque";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Transition fade = new Fade();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setExitTransition(fade);
        getWindow().setEnterTransition(fade);

        final Button button_play = findViewById(R.id.play_button);

        button_play.setOnClickListener(v -> sendMessage(STORY + "/0"));

        final Button button_continue = findViewById(R.id.continue_button);
        if(isSavesEmpty()) {
            button_continue.setEnabled(false);
            button_continue.setTextColor(Color.GRAY);
        }
        button_continue.setOnClickListener(v -> sendMessage(getSave()));

        final Button button_quit = findViewById(R.id.quit_button);
        button_quit.setOnClickListener(this::quit);
    }


     public void sendMessage( String message)  {
         Intent intent = new Intent(this, GameActivity.class);

         intent.putExtra(EXTRA_MESSAGE, message);
         startActivity(intent);
         finish();
    }

    public String getSave() {
        String message = "";

        File dir = new File(this.getFilesDir(), "saves");

        if(!dir.exists()){
            boolean created = dir.mkdir();
            if (created) {
                System.out.println("Directory saves successfully created");
            }
        }

        try {
            File myObj = new File(dir, STORY);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                message = STORY + "/" + data;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } finally {
            if(message.equals(""))
                message = STORY + "/0";
        }

        return message;
    }

    public void quit(View V) {
        finish();
        System.exit(0);
    }

    private boolean isSavesEmpty() {
        File dir = new File(this.getFilesDir(), "saves");

        if(!dir.exists()){
            boolean created = dir.mkdir();
            if (created) {
                System.out.println("Directory saves successfully created");
            }
            return true;
        }
        else {
            File[] contents = dir.listFiles();
            // False if folder contains files
            if(contents != null) {
                return contents.length == 0;
            }
            else {
                return true;
            }
        }

    }


}