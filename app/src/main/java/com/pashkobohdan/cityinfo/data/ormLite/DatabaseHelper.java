package com.pashkobohdan.cityinfo.data.ormLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.pashkobohdan.cityinfo.data.ormLite.dao.CountryDAO;
import com.pashkobohdan.cityinfo.data.ormLite.dao.CityDAO;
import com.pashkobohdan.cityinfo.data.ormLite.model.CityModel;
import com.pashkobohdan.cityinfo.data.ormLite.model.CountryModel;

import java.sql.SQLException;

/**
 * Created by bohdan on 01.04.17.
 */

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();

    private static final String DATABASE_NAME ="cities_info.db";

    private static final int DATABASE_VERSION = 1;

    private CountryDAO countryDAO = null;
    private CityDAO cityDAO = null;

    public DatabaseHelper(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource){
        try
        {
            TableUtils.createTable(connectionSource, CountryModel.class);
            TableUtils.createTable(connectionSource, CityModel.class);
        }
        catch (SQLException e){
            Log.e(TAG, "error creating DB " + DATABASE_NAME);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVer,
                          int newVer){
        try{
            TableUtils.dropTable(connectionSource, CountryModel.class, true);
            TableUtils.dropTable(connectionSource, CityModel.class, true);
            onCreate(db, connectionSource);
        }
        catch (SQLException e){
            Log.e(TAG,"error upgrading db "+DATABASE_NAME+"from ver "+oldVer);
            throw new RuntimeException(e);
        }
    }

    public CountryDAO getSourceDAO() throws SQLException{
        if(countryDAO == null){
            countryDAO = new CountryDAO(getConnectionSource(), CountryModel.class);
        }
        return countryDAO;
    }
    public CityDAO getPostDAO() throws SQLException{
        if(cityDAO == null){
            cityDAO = new CityDAO(getConnectionSource(), CityModel.class);
        }
        return cityDAO;
    }

    @Override
    public void close(){
        super.close();
        countryDAO = null;
        cityDAO = null;
    }
}
