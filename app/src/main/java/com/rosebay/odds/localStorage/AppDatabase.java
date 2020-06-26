package com.rosebay.odds.localStorage;

import com.rosebay.odds.model.Favorite;
import com.rosebay.odds.model.Vote;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Favorite.class, Vote.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract FavoriteDao getFavoriteDao();
    public abstract VoteDao getVoteDao();

}
