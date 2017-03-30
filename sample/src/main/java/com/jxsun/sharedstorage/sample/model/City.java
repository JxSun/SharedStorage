package com.jxsun.sharedstorage.sample.model;

import com.jxsun.sharedstorage.annotation.ModelField;
import com.jxsun.sharedstorage.annotation.SharedModel;

@SharedModel(
        authority = "test.app1",
        databaseName = "city.db",
        tableName = "city",
        version = 1)
public class City {
    @ModelField(name = "id", primary = true)
    long mId;
    @ModelField(name = "name")
    String mName;
    @ModelField(name = "population")
    int mPopulation;
}
