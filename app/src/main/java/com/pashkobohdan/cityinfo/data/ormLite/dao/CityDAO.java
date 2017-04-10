package com.pashkobohdan.cityinfo.data.ormLite.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.pashkobohdan.cityinfo.data.ormLite.model.CityModel;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by bohdan on 01.04.17.
 */

public class CityDAO extends BaseDaoImpl<CityModel, Integer> {

    public CityDAO(ConnectionSource connectionSource, Class<CityModel> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<CityModel> getAllPosts() throws SQLException{
        return this.queryForAll();
    }

}