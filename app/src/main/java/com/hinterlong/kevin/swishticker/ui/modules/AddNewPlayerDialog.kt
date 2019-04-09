package com.hinterlong.kevin.swishticker.ui.modules

import android.app.Dialog
import android.content.Context
import com.hinterlong.kevin.swishticker.R
import com.hinterlong.kevin.swishticker.utilities.setFullScreen
import kotlinx.android.synthetic.main.dialog_add_new_player.*


class AddNewPlayerDialog(context: Context, val listener: (String) -> Unit = {}) : Dialog(context) {

    init {
        setContentView(R.layout.dialog_add_new_player)
        setFullScreen(window)
    }

    override fun onStart() {
        super.onStart()

        addToTeam.setOnClickListener {
            listener(playerName.text.toString())
            dismiss()
        }
        cancel.setOnClickListener {
            dismiss()
        }
    }
}