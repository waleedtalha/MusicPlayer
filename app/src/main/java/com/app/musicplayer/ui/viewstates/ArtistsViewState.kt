package com.app.musicplayer.ui.viewstates

import androidx.lifecycle.LiveData
import com.app.musicplayer.core.utils.DataLiveEvent
import com.app.musicplayer.interator.livedata.ArtistsLiveData
import com.app.musicplayer.models.Artist
import com.app.musicplayer.repository.artists.ArtistsRepository
import com.app.musicplayer.ui.list.ListViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ArtistsViewState @Inject constructor(
    artistsRepository: ArtistsRepository
) : ListViewState<Artist>() {

    private val artistsLiveData = artistsRepository.getArtists() as ArtistsLiveData
    val showItemEvent = DataLiveEvent<Artist>()

    override fun getItemsObservable(callback: (LiveData<List<Artist>>) -> Unit) {
        callback.invoke(artistsLiveData)
    }
    override fun setOnItemClickListener(item: Artist, position: Int) {
        super.setOnItemClickListener(item, position)
        showItemEvent.call(item)
    }

    override fun onFilterChanged(filter: String?) {
        super.onFilterChanged(filter)
        artistsLiveData.filter = filter
    }
}