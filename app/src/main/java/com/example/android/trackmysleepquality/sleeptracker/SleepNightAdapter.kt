package com.example.android.trackmysleepquality.sleeptracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.HeaderBinding
import com.example.android.trackmysleepquality.databinding.ListItemSleepNightBinding
import com.example.android.trackmysleepquality.sleeptracker.SleepNightAdapter.DataItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_ITEM = 1

class SleepNightAdapter(private val onClick: (item: SleepNight) -> Unit) :
    ListAdapter<DataItem, RecyclerView.ViewHolder>(SleepNightDiffCallBack()) {

    private val recyclerViewScope = CoroutineScope(Dispatchers.Default)

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.SleepNightItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> TextViewHolder.binding(parent)
            ITEM_VIEW_TYPE_ITEM -> ViewHolder.binding(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val item = getItem(position) as DataItem.SleepNightItem
                holder.bind(item.sleepNight, onClick)
            }
        }
    }

    fun addHeaderAndSubmitList(list: List<SleepNight?>) {
        recyclerViewScope.launch {
            val items = when (list) {
                null -> listOf(DataItem.Header)
                else -> listOf(DataItem.Header) + list.map {
                    it?.let {
                        DataItem.SleepNightItem(it)
                    }
                }
            }

            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }

    }


    class TextViewHolder private constructor(val binding: HeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun binding(parent: ViewGroup): TextViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = HeaderBinding.inflate(layoutInflater, parent, false)
                return TextViewHolder(binding)
            }
        }
    }


    class ViewHolder private constructor(private val binding: ListItemSleepNightBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun binding(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemSleepNightBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(item: SleepNight, onClick: (item: SleepNight) -> Unit) {
            binding.sleep = item
            binding.root.setOnClickListener {
                onClick(item)
            }
            binding.executePendingBindings()
        }
    }

    class SleepNightDiffCallBack : DiffUtil.ItemCallback<DataItem>() {
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem == newItem
        }
    }


    sealed class DataItem {
        data class SleepNightItem(val sleepNight: SleepNight) : DataItem() {
            override val id: Long = sleepNight.nightId
        }
        object Header : DataItem() {
            override val id: Long = Long.MIN_VALUE
        }
        abstract val id: Long
    }

}