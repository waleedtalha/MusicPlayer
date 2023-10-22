package com.app.musicplayer.di

import android.content.Context
import androidx.room.Room
import com.app.musicplayer.db.MusicDB
import com.app.musicplayer.di.factory.contentresolver.ContentResolverFactory
import com.app.musicplayer.di.factory.contentresolver.ContentResolverFactoryImpl
import com.app.musicplayer.di.factory.livedata.LiveDataFactory
import com.app.musicplayer.di.factory.livedata.LiveDataFactoryImpl
import com.app.musicplayer.extentions.MIGRATION_0_1
import com.app.musicplayer.interator.albums.AlbumsInteractorImpl
import com.app.musicplayer.interator.albums.AlbumsInterator
import com.app.musicplayer.interator.player.PlayerInteractor
import com.app.musicplayer.interator.player.PlayerInteractorImpl
import com.app.musicplayer.interator.playlist.PlaylistInteractor
import com.app.musicplayer.interator.playlist.PlaylistInteractorImpl
import com.app.musicplayer.interator.tracks.TracksInteractor
import com.app.musicplayer.interator.tracks.TracksInteractorImpl
import com.app.musicplayer.interator.string.StringsInteractor
import com.app.musicplayer.interator.string.StringsInteratorImpl
import com.app.musicplayer.repository.albums.AlbumsRepository
import com.app.musicplayer.repository.albums.AlbumsRepositoryImpl
import com.app.musicplayer.repository.artists.ArtistsRepository
import com.app.musicplayer.repository.artists.ArtistsRepositoryImpl
import com.app.musicplayer.repository.tracks.TracksRepository
import com.app.musicplayer.repository.tracks.TracksRepositoryImpl
import com.app.musicplayer.utils.ROOM_DB_NAME
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Singleton

@Module(includes = [AppModule.BindModule::class])
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideDisposable(): CompositeDisposable = CompositeDisposable()

    @Provides
    @Singleton
    fun provideRoomDb(@ApplicationContext context: Context): MusicDB {
        return Room.databaseBuilder(context, MusicDB::class.java, ROOM_DB_NAME)
            .addMigrations(MIGRATION_0_1).build()
    }

    @Module
    @InstallIn(SingletonComponent::class)
    interface BindModule {

        @Binds
        fun bindsTracksInteractor(tracksInteractorImpl: TracksInteractorImpl): TracksInteractor

        @Binds
        fun bindsPlaylistInteractor(playlistInteractorImpl: PlaylistInteractorImpl):PlaylistInteractor

        @Binds
        fun bindsAlbumsInteractor(albumsInteractorImpl: AlbumsInteractorImpl): AlbumsInterator

        @Binds
        fun bindsPlayersInteractor(playerInteractorImpl: PlayerInteractorImpl): PlayerInteractor

        @Binds
        fun bindsContentResolverFactory(contentResolverFactoryImpl: ContentResolverFactoryImpl): ContentResolverFactory

        @Binds
        fun bindsTracksRepository(tracksRepositoryImpl: TracksRepositoryImpl): TracksRepository

        @Binds
        fun bindsAlbumsRepository(albumsRepositoryImpl: AlbumsRepositoryImpl): AlbumsRepository

        @Binds
        fun bindsArtistsRepository(artistsRepositoryImpl: ArtistsRepositoryImpl): ArtistsRepository

        @Binds
        fun bindsLiveDataFactory(liveDataFactoryImpl: LiveDataFactoryImpl): LiveDataFactory

        @Binds
        fun bindsStringsInterator(stringInteractorImpl: StringsInteratorImpl): StringsInteractor
    }
}