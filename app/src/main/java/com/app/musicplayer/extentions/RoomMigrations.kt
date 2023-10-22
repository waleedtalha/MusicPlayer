package com.app.musicplayer.extentions

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_0_1 = object : Migration(0, 1) {
    override fun migrate(database: SupportSQLiteDatabase) {
    }
}