package com.app.musicplayer.di

import android.content.Context
import com.app.musicplayer.ui.base.BaseActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext

@InstallIn(ActivityComponent::class)
@Module(includes = [ActivityModule.BindsModule::class])
class ActivityModule {

    @Provides
    fun provideBaseActivity(@ActivityContext context: Context): BaseActivity<*> =
        context as BaseActivity<*>

    @Module
    @InstallIn(ActivityComponent::class)
    interface BindsModule
}