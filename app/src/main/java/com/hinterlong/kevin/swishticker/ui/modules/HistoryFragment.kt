package com.hinterlong.kevin.swishticker.ui.modules

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.hinterlong.kevin.swishticker.R
import com.hinterlong.kevin.swishticker.service.AppDatabase
import com.hinterlong.kevin.swishticker.service.gameScores
import com.hinterlong.kevin.swishticker.ui.adapters.GameItem
import com.hinterlong.kevin.swishticker.utilities.Prefs
import eu.davidea.flexibleadapter.FlexibleAdapter
import kotlinx.android.synthetic.main.fragment_game_list.*
import kotlinx.android.synthetic.main.game_item.view.*


/**
 * A fragment representing a list of Items.
 */
class HistoryFragment : Fragment() {
    private val adapter = FlexibleAdapter<GameItem>(null)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_game_list, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gamesList.adapter = adapter

        Transformations.map(AppDatabase.getInstance(view.context)
            .gameDao()
            .getGamesAndActions(), ::gameScores)
            .observe(viewLifecycleOwner, Observer {
                adapter.notifyItemRangeRemoved(0, adapter.itemCount)
                adapter.updateDataSet(it.map { GameItem(it.game, it.score, viewLifecycleOwner) })
                adapter.notifyItemRangeChanged(0, it.size)

                if (it.size == 1 && Prefs.isFirstDemoDeleteGame) {
                    demoDeleteAction()
                    Prefs.isFirstDemoDeleteGame = false
                }
            })
    }

    private fun demoDeleteAction() {
        val handler = Handler()
        handler.postDelayed({
            val swipeLayout = gamesList.findViewHolderForLayoutPosition(0)?.itemView?.swipeLayout
            swipeLayout?.animateSwipeRight()
            Toast.makeText(context, "Swipe right to delete games", Toast.LENGTH_SHORT).show()
            handler.postDelayed({
                swipeLayout?.animateReset()
            }, 800)

        }, 500)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        adapter.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter.onRestoreInstanceState(savedInstanceState)
    }
}
