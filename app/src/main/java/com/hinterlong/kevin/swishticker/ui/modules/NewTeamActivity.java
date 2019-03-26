package com.hinterlong.kevin.swishticker.ui.modules;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.hinterlong.kevin.swishticker.R;

public class NewTeamActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_team);
        setTitle(R.string.new_team);
    }
}
