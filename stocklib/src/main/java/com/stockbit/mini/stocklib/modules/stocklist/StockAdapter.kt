package com.stockbit.mini.stocklib.modules.stocklist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.stockbit.mini.stocklib.R
import com.stockbit.mini.stocklib.repositories.cache.storage.entity.Stock

class StockAdapter() :
    RecyclerView.Adapter<StockAdapter.ViewHolder>() {
    var stocks = mutableListOf<Stock>()
    
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        val tvFullname: TextView = view.findViewById(R.id.tvFullname)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.stock_row, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.tvName.text = stocks[position].name
        viewHolder.tvPrice.text = stocks[position].price
        viewHolder.tvFullname.text = stocks[position].fullname
        viewHolder.tvStatus.text = stocks[position].status
    }

    override fun getItemCount() = stocks.size
}