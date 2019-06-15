package com.cheadtech.example.bakingtime.database;

import android.content.Context;

import androidx.room.Room;

import com.cheadtech.example.bakingtime.R;

public class DatabaseLoader {
    private static BakingTimeDB dbInstance;
    public static BakingTimeDB getDbInstance(Context context) {
        if (dbInstance == null) {
            dbInstance = Room.databaseBuilder(context,
                    BakingTimeDB.class, context.getString(R.string.app_db_name)).build();
        }
        return dbInstance;
    }
}
