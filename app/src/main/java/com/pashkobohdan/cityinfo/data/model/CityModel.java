package com.pashkobohdan.cityinfo.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "city")
public class CityModel {


    @DatabaseField(generatedId = true)
    private int Id;

    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private CountryModel country;

    @DatabaseField()
    private String name;

    public String getName() {
        return name;
    }

    /**
     * @param name Site name
     */
    public void setName(String name) {
        this.name = name;
    }


    public CountryModel getCountry() {
        return country;
    }

    public void setCountry(CountryModel country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return getName();
    }
}