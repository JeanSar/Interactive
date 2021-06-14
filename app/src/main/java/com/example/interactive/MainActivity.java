package com.example.interactive;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

     public void sendMessage( View V) {
         Intent intent = new Intent(this, GameActivity.class);
         String message = "La_Traque";
         intent.putExtra(EXTRA_MESSAGE, message);
         startActivity(intent);
         finish();
    }

    public void quit(View V) {
        finish();
        System.exit(0);
    }
}