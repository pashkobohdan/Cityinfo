package com.pashkobohdan.cityinfo.data.ormLite.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.pashkobohdan.cityinfo.data.ormLite.HelperFactory;

import java.sql.SQLException;
import java.util.Collection;


/**
 * Created by bohdan on 25.03.17.
 */
@DatabaseTable(tableName = "countries")
public class CountryModel {

    @DatabaseField(generatedId = true)
    private int Id;


    @DatabaseField()
    private String name;


    @ForeignCollectionField(eager = true)
    private Collection<CityModel> postList;

    public void addPost(CityModel value) throws SQLException{
        value.setSource(this);
        //HelperFactory.getHelper().getPostDAO().create(value);
        postList.add(value);
    }

    public void removePost(CityModel value) throws SQLException{
        postList.remove(value);
        HelperFactory.getHelper().getPostDAO().delete(value);
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<CityModel> getPostList() {
        return postList;
    }

    public void setPostList(Collection<CityModel> postList) {
        this.postList = postList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CountryModel that = (CountryModel) o;

        if (Id != that.Id) return false;
        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        int result = Id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}