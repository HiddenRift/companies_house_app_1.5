package com.example.r4_fry.androidnodeapp.CompaniesDB;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Company.class, Officer.class}, version = 1, exportSchema = false)
public abstract class CompaniesRoomDatabase extends RoomDatabase {
    private static volatile CompaniesRoomDatabase mInstance;

    public static CompaniesRoomDatabase getDatabase(final Context context)
    {
        if(mInstance == null){
            synchronized (CompaniesRoomDatabase.class){
                if(mInstance == null){
                    mInstance = Room.databaseBuilder(context.getApplicationContext(),
                            CompaniesRoomDatabase.class,
                            "companies_database")
                            .build();
                }
            }
        }
        return mInstance;
    }

    public abstract CompaniesDAO companiesDao();

}
