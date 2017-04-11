package com.pashkobohdan.cityinfo.data.ormLite.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.pashkobohdan.cityinfo.data.model.CountryModel;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * Created by bohdan on 01.04.17.
 */

public class SourceDao extends BaseDaoImpl<CountryModel, Integer> {

    public SourceDao(ConnectionSource connectionSource, Class<CountryModel> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<CountryModel> getAllSources() throws SQLException {
        return this.queryForAll();
    }

}