package com.pashkobohdan.cityinfo.data.ormLite.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "cities")
public class CityModel {

    @DatabaseField(generatedId = true)
    private int Id;

    @DatabaseField(foreign = true)
    private CountryModel source;

    @DatabaseField()
    private String name;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public CountryModel getSource() {
        return source;
    }

    public void setSource(CountryModel source) {
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CityModel cityModel = (CityModel) o;

        if (Id != cityModel.Id) return false;
        return name != null ? name.equals(cityModel.name) : cityModel.name == null;

    }

    @Override
    public int hashCode() {
        int result = Id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}