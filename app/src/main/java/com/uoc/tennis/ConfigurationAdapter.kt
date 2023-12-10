package com.uoc.tennis

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.widget.WearableRecyclerView

data class MenuItem(val text: String)
class ConfigurationAdapter(
    context: Context,
    dataArgs: ArrayList<MenuItem>,
    callback: AdapterCallback?
) :
    RecyclerView.Adapter<ConfigurationAdapter.RecyclerViewHolder>() {
    private var dataSource = ArrayList<MenuItem>()
    interface AdapterCallback {
        fun onItemClicked(menuPosition: Int?)
    }

    private val callback: AdapterCallback?
    private val context: Context

    init {
        this.context = context
        this.dataSource = dataArgs
        this.callback = callback
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.configuration_menu_item, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val dataProvider = dataSource[position]
        holder.menuItem.text = dataProvider.text
        holder.menuContainer.setOnClickListener { callback?.onItemClicked(position) }
    }

    class RecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var menuContainer: RelativeLayout
        var menuItem: TextView
        init {
            menuContainer = view.findViewById(R.id.menu_item)
            menuItem = view.findViewById(R.id.menu_element)
        }
    }

    }