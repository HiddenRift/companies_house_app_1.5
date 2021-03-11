package com.example.r4_fry.androidnodeapp.CompaniesDB;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface CompaniesDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCompany(Company company);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCompanies(ArrayList<Company> company);

    @Query("delete from company_table")
    void deleteAllCompanies();

    @Query("SELECT * from company_table ORDER BY title ASC")
    LiveData<List<Company>> getAllCompanies();

    @Query("SELECT * FROM company_table WHERE companyId = :id")
    LiveData<List<Company>> getCompany(String id);

    @Query("delete from officer_table")
    void deleteAllOfficers();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOfficer(Officer officer);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOfficers(ArrayList<Officer> officers);

    @Query("SELECT  * FROM officer_table")
    LiveData<List<Officer>> getAllOfficers();

    @Query("SELECT * FROM officer_table WHERE officerId = :id")
    LiveData<List<Officer>> getOfficer(String id);


}
