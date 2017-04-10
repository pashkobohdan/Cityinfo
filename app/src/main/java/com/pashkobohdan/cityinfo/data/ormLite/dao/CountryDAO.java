package com.pashkobohdan.cityinfo.data.ormLite.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.pashkobohdan.cityinfo.data.ormLite.model.CountryModel;

import java.sql.SQLException;
import java.util.Collection;

/**
 * Created by bohdan on 01.04.17.
 */

public class CountryDAO extends BaseDaoImpl<CountryModel, Integer> {

    public CountryDAO(ConnectionSource connectionSource, Class<CountryModel> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public Collection<CountryModel> getAllSources() throws SQLException {
        return this.queryForAll();
    }

}