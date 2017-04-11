package com.pashkobohdan.cityinfo.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.pashkobohdan.cityinfo.data.ormLite.HelperFactory;

import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;


/**
 * Created by bohdan on 25.03.17.
 */
@DatabaseTable(tableName = "country")
public class CountryModel {

    @DatabaseField(generatedId = true)
    private int Id;

    @DatabaseField()
    private String name;


    @ForeignCollectionField(eager = true)
    private Collection<CityModel> citiesList;

    public CountryModel() {
        citiesList = new LinkedList<>();
    }

    public void addPost(CityModel value) throws SQLException {
        value.setCountry(this);
        citiesList.add(value);
    }

    public void removePost(CityModel value) throws SQLException {
        citiesList.remove(value);
        HelperFactory.getHelper().getPostDAO().delete(value);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Collection<CityModel> getCitiesList() {
        return citiesList;
    }

    public void setCitiesList(Collection<CityModel> citiesList) {
        this.citiesList = citiesList;
    }

    @Override
    public String toString() {
        return name;
    }
}