package com.pashkobohdan.cityinfo.data.ormLite.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.pashkobohdan.cityinfo.data.model.CityModel;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by bohdan on 01.04.17.
 */

public class PostDao extends BaseDaoImpl<CityModel, Integer> {

    public PostDao(ConnectionSource connectionSource, Class<CityModel> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<CityModel> getAllPosts() throws SQLException {
        return this.queryForAll();
    }

    public void bulkInsertDataByCallBatchTasks(final Collection<CityModel> users) throws SQLException {
        callBatchTasks(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for (CityModel user : users) {
                    create(user);
                }
                return null;
            }
        });
    }

}