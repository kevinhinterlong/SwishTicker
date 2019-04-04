package com.hinterlong.kevin.swishticker.ui.modules

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hinterlong.kevin.swishticker.R

class NewTeamActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_team)
        setTitle(R.string.new_team)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
