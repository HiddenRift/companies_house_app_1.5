package com.example.r4_fry.androidnodeapp.CompaniesDB;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@Entity(tableName = "officer_table")
public class Officer {

    public Officer(String officerId, String name, String appointedOnDate, String officerRole, String nationality, String occupation) {
        mOfficerId = officerId;
        mName = name;
        mAppointedOnDate = appointedOnDate;
        mOfficerRole = officerRole;
        mNationality = nationality;
        mOccupation = occupation;
    }

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "officerId")
    private String mOfficerId;

    @Nullable
    @ColumnInfo(name = "name")
    private String mName;

    @Nullable
    @ColumnInfo(name = "appointedOnDate")
    private String mAppointedOnDate;

    @Nullable
    @ColumnInfo(name = "officerRole")
    private String mOfficerRole;

    @Nullable
    @ColumnInfo(name = "nationality")
    private String mNationality;

    @Nullable
    @ColumnInfo(name = "occupation")
    private String mOccupation;

    public String getOfficerId() {
        return mOfficerId;
    }

    @Nullable
    public String getName() {
        return mName;
    }

    @Nullable
    public String getAppointedOnDate() {
        return mAppointedOnDate;
    }

    @Nullable
    public String getOfficerRole() {
        return mOfficerRole;
    }

    @Nullable
    public String getNationality() {
        return mNationality;
    }

    @Nullable
    public String getOccupation() {
        return mOccupation;
    }
}