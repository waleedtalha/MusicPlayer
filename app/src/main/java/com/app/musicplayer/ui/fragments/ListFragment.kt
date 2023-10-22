package com.app.musicplayer.ui.fragments

import com.app.musicplayer.databinding.ItemsBinding
import com.app.musicplayer.extentions.beVisibleIf
import com.app.musicplayer.ui.adapters.ListAdapter
import com.app.musicplayer.ui.base.BaseFragment
import com.app.musicplayer.ui.list.ListViewState

abstract class ListFragment<ItemType : Any, VS : ListViewState<ItemType>> : BaseFragment<VS>() {
    protected val binding by lazy { ItemsBinding.inflate(layoutInflater) }
    override val contentView by lazy { binding.root }
    abstract val listAdapter: ListAdapter<ItemType>

    override fun onSetup() {
        binding.tracksRv.apply {
            this.layoutManager = manager
            this.adapter = listAdapter
        }
        viewState.apply {
            isEmpty.observe(this@ListFragment, this@ListFragment::showEmpty)

            itemsChangedEvent.observe(this@ListFragment) { event ->
                event.ifNew?.let {
                    listAdapter.items = it
                    showEmpty(listAdapter.items.isEmpty())
                }
            }
            itemChangedEvent.observe(this@ListFragment) { event ->
                event.ifNew?.let { position ->
                    listAdapter.notifyItemChanged(position)
                }
            }
            getItemsObservable { it.observe(this@ListFragment, viewState::onItemsChanged) }
        }
        listAdapter.apply {
            setOnItemClickListener { item, position ->
                viewState.setOnItemClickListener(item, position)
            }
            setOnMenuClickListener { item, position, view ->
                viewState.setOnMenuClickListener(item, position, view)
            }
            setOnFavoriteClickListener { item ->
                viewState.setOnFavoriteClickListener(item)
            }
        }
    }

    protected open fun showEmpty(isShow: Boolean) {
        binding.apply {
            empty.emptyImage.beVisibleIf(isShow)
            empty.emptyText.beVisibleIf(isShow)
            tracksRv.beVisibleIf(!isShow)
        }
    }

}