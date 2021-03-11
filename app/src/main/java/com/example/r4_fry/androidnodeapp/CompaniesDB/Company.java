package com.example.r4_fry.androidnodeapp.CompaniesDB;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@Entity(tableName = "company_table")
public class Company {

    @NonNull
    public String getTitle() {
        return mTitle;
    }

    @Nullable
    public String getCompanyStatus() {
        return mCompanyStatus;
    }

    @Nullable
    public String getDescription() {
        return mDescription;
    }

    @Nullable
    public String getAddressSnippet() {
        return mAddressSnippet;
    }

    public String getCompanyId() {
        return mCompanyId;
    }

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "companyId")
    private String mCompanyId; //companyNumber(can contain letters rather misleadingly)

    @NonNull
    @ColumnInfo(name = "title")
    private String mTitle;

    @Nullable
    @ColumnInfo(name = "companyStatus")
    private String mCompanyStatus;

    @Nullable
    @ColumnInfo(name = "description")
    private String mDescription;

    @Nullable
    @ColumnInfo(name = "addressSnippet")
    private String mAddressSnippet;

    public Company(String companyId,
                   String title,
                   String companyStatus,
                   String description,
                   String addressSnippet){

        mCompanyId = companyId;
        mTitle = title;
        mCompanyStatus = companyStatus;
        mDescription = description;
        mAddressSnippet = addressSnippet;


    }

}
