package com.app.musicplayer.ui.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.app.musicplayer.ui.fragments.AlbumsFragment
import com.app.musicplayer.ui.fragments.AllMusicFragment
import com.app.musicplayer.ui.fragments.ArtistsFragment
import com.app.musicplayer.ui.fragments.FavoritesFragment
import com.app.musicplayer.ui.fragments.PlaylistFragment
import com.app.musicplayer.ui.fragments.RecentlyPlayedFragment

internal class ViewPagerAdapter(
    var context: Context,
    fm: FragmentManager,
    var totalTabs: Int
) :
    FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                AllMusicFragment()
            }

            1 -> {
                AlbumsFragment()
            }

            2 -> {
                ArtistsFragment()
            }

            3 -> {
                RecentlyPlayedFragment()
            }

            4 -> {
                FavoritesFragment()
            }

            5 -> {
                PlaylistFragment()
            }

            else -> getItem(position)
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }
}
//    fragmentManager: FragmentManager,
//    lifecycle: Lifecycle
//) : FragmentStateAdapter(fragmentManager, lifecycle) {
//    override fun getItemCount(): Int = 5
//
//    private var fragmentList = arrayListOf<Fragment>()
//
//    @RequiresApi(Build.VERSION_CODES.N)
//    override fun createFragment(position: Int): Fragment = fragmentList[position]
//
//
//    fun addFragment(fragment: Fragment) {
//        fragmentList.add(fragment)
//    }
//
//}