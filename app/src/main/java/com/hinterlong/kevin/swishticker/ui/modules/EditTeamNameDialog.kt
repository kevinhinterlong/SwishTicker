package com.hinterlong.kevin.swishticker.ui.modules

import android.app.Dialog
import android.content.Context
import com.hinterlong.kevin.swishticker.R
import com.hinterlong.kevin.swishticker.utilities.setFullScreen
import kotlinx.android.synthetic.main.dialog_edit_team_name.*


class EditTeamNameDialog(context: Context, val name: String, val listener: (String) -> Unit = {}) : Dialog(context) {

    init {
        setContentView(R.layout.dialog_edit_team_name)
        setFullScreen(window)
    }

    override fun onStart() {
        super.onStart()

        saveTeamName.setOnClickListener {
            listener(teamName.text.toString())
            dismiss()
        }
        cancel.setOnClickListener {
            dismiss()
        }
    }
}