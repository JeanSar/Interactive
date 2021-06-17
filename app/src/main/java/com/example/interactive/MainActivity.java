package com.example.interactive;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
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

        final Button button_play = findViewById(R.id.button);
        button_play.setOnClickListener(this::sendMessage);

        final Button button_quit = findViewById(R.id.button4);
        button_quit.setOnClickListener(this::quit);
    }


     public void sendMessage( View V)  {
         Intent intent = new Intent(this, GameActivity.class);
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
         }


         intent.putExtra(EXTRA_MESSAGE, message);
         startActivity(intent);
    }


    public void quit(View V) {
        finish();
        System.exit(0);
    }
}