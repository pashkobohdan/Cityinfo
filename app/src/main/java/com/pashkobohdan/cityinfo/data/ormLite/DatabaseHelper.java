package com.pashkobohdan.cityinfo.data.ormLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.pashkobohdan.cityinfo.data.model.CityModel;
import com.pashkobohdan.cityinfo.data.model.CountryModel;
import com.pashkobohdan.cityinfo.data.ormLite.dao.CityDAO;
import com.pashkobohdan.cityinfo.data.ormLite.dao.CountryDAO;

import java.sql.SQLException;

/**
 * Created by bohdan on 01.04.17.
 */

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();

    //имя файла базы данных который будет храниться в /data/data/APPNAME/DATABASE_NAME.db
    private static final String DATABASE_NAME ="data.db";

    //с каждым увеличением версии, при нахождении в устройстве БД с предыдущей версией будет выполнен метод onUpgrade();
    private static final int DATABASE_VERSION = 1;

    //ссылки на DAO соответсвующие сущностям, хранимым в БД
    private CountryDAO sourceDao = null;
    private CityDAO postDao = null;

    public DatabaseHelper(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Выполняется, когда файл с БД не найден на устройстве
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

    //Выполняется, когда БД имеет версию отличную от текущей
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVer,
                          int newVer){
        try{
            //Так делают ленивые, гораздо предпочтительнее не удаляя БД аккуратно вносить изменения
            TableUtils.dropTable(connectionSource, CountryModel.class, true);
            TableUtils.dropTable(connectionSource, CityModel.class, true);
            onCreate(db, connectionSource);
        }
        catch (SQLException e){
            Log.e(TAG,"error upgrading db "+DATABASE_NAME+"from ver "+oldVer);
            throw new RuntimeException(e);
        }
    }

    //синглтон для GoalDAO
    public CountryDAO getCountryDAO() throws SQLException{
        if(sourceDao == null){
            sourceDao = new CountryDAO(getConnectionSource(), CountryModel.class);
        }
        return sourceDao;
    }
    //синглтон для RoleDAO
    public CityDAO getCityDAO() throws SQLException{
        if(postDao == null){
            postDao = new CityDAO(getConnectionSource(), CityModel.class);
        }
        return postDao;
    }

    //выполняется при закрытии приложения
    @Override
    public void close(){
        super.close();
        sourceDao = null;
        postDao = null;
    }
}
