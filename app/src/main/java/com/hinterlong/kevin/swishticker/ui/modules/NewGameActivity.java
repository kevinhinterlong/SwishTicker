package com.hinterlong.kevin.swishticker.ui.modules;

import android.os.Bundle;

import com.hinterlong.kevin.swishticker.R;

import androidx.appcompat.app.AppCompatActivity;

public class NewGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        setTitle(R.string.new_game);
    }
}
