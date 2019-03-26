package com.hinterlong.kevin.swishticker.ui.modules;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.hinterlong.kevin.swishticker.R;

public class NewGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        setTitle(R.string.new_game);
    }
}
