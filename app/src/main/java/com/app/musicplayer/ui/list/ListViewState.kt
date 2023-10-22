package com.app.musicplayer.ui.list

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.musicplayer.core.utils.DataLiveEvent
import com.app.musicplayer.ui.base.BaseViewState

abstract class ListViewState<ItemType>:BaseViewState() {
    var filter = MutableLiveData<String>()
    val itemsChangedEvent = DataLiveEvent<List<ItemType>>()
    val itemChangedEvent = DataLiveEvent<Int>()
    val _onItemClickListener = DataLiveEvent<ItemType>()
    var isEmpty = MutableLiveData(true)

    abstract fun getItemsObservable(callback:(LiveData<List<ItemType>>)->Unit)

    open fun onItemsChanged(items:List<ItemType>){
        isEmpty.value = items.isEmpty()
        itemsChangedEvent.call(items)
    }
    open fun setOnItemClickListener(item: ItemType, position: Int) {
        itemChangedEvent.call(position)
        _onItemClickListener.call(item)
    }
    open fun setOnMenuClickListener(item: ItemType,position:Int,view:View) {
        itemChangedEvent.call(position)
        _onItemClickListener.call(item)
    }
    open fun setOnFavoriteClickListener(item: ItemType) {
//        itemChangedEvent.call(position)
        _onItemClickListener.call(item)
    }
    open fun onFilterChanged(filter: String?) {
        this.filter.value = filter
    }
}