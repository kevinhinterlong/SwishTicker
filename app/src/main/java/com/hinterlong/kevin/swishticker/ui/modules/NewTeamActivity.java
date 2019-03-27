package com.hinterlong.kevin.swishticker.ui.modules;

import android.os.Bundle;

import com.hinterlong.kevin.swishticker.R;

import androidx.appcompat.app.AppCompatActivity;

public class NewTeamActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_team);
        setTitle(R.string.new_team);
    }
}
