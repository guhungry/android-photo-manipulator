package com.guhungry.photomanipulator.demo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.guhungry.photomanipulator.demo.item.ExampleItem
import com.guhungry.photomanipulator.demo.item.ListItem
import com.guhungry.photomanipulator.demo.item.SectionItem

class ExampleRecyclerViewAdapter(private val context: Context) : RecyclerView.Adapter<ExampleRecyclerViewAdapter.RecyclerViewHolder>() {
    private var items = listOf<ListItem>()

    fun setItem(items: List<ListItem>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val rootView: View = LayoutInflater.from(context)
            .inflate(getItemView(viewType), parent, false)
        return RecyclerViewHolder(rootView)
    }

    private fun getItemView(viewType: Int): Int {
        return if (viewType == 0) R.layout.item_example_section
        else R.layout.item_example_item
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = items[position]

        if (item is SectionItem) {
            setText(holder.title, item.title)
        } else if (item is ExampleItem) {
            setText(holder.title, item.title)
            setText(holder.kotlin, item.kotlin)

            holder.image?.setImageBitmap(item.image)
        }
    }

    private fun setText(textView: TextView?, value: String) {
        if (value.isBlank()) {
            textView?.visibility = View.GONE
        } else {
            textView?.text = value
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class RecyclerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var title: TextView? = itemView.findViewById(R.id.title)
        var image: ImageView? = itemView.findViewById(R.id.image)
        var kotlin: TextView? = itemView.findViewById(R.id.kotlin)
    }
}
