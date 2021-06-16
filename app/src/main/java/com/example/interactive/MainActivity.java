package com.example.interactive;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "Story/nb";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button_play = findViewById(R.id.button);

        button_play.setOnClickListener(this::sendMessage);

        final Button button_quit = findViewById(R.id.button4);
        button_quit.setOnClickListener(this::quit);
    }


     public void sendMessage( View V) {
         Intent intent = new Intent(this, GameActivity.class);
         String message = "La_Traque/0";
         intent.putExtra(EXTRA_MESSAGE, message);
         startActivity(intent);
         finish();
    }


    public void quit(View V) {
        finish();
        System.exit(0);
    }
}