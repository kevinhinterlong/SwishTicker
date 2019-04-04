package com.hinterlong.kevin.swishticker.ui.modules

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.hinterlong.kevin.swishticker.R
import com.hinterlong.kevin.swishticker.service.AppDatabase
import com.hinterlong.kevin.swishticker.ui.adapters.GameItem
import eu.davidea.flexibleadapter.FlexibleAdapter

/**
 * A fragment representing a list of Items.
 */
class HistoryFragment : Fragment() {
    private val adapter = FlexibleAdapter<GameItem>(null)
    @BindView(R.id.game_list)
    lateinit var recyclerView: RecyclerView
    private lateinit var unbinder: Unbinder

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_game_list, container, false)
        unbinder = ButterKnife.bind(this, view)
        recyclerView.adapter = adapter

        return view
    }

    override fun onResume() {
        super.onResume()
        context?.let {
            AppDatabase.getInstance(it)
                .gameDao()
                .getGames()
                .observe(viewLifecycleOwner, Observer {
                    adapter.updateDataSet(it.map(::GameItem).toList())
                })
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        adapter.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter.onRestoreInstanceState(savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder.unbind()
    }
}
