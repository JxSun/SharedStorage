package com.jxsun.sharedstorage.processor.model;

import com.jxsun.sharedstorage.annotation.SharedModel;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;

public class Model {
    private final String mPackage;
    private final String mName;
    private final String mAuthority;
    private final String mDatabaseName;
    private final String mTableName;
    private final int mVersion;
    private final List<Field> mFields;

    public Model(Elements elementUtil, Element element, List<Field> fields) {
        mPackage = elementUtil.getPackageOf(element).getQualifiedName().toString();
        mName = element.getSimpleName().toString();

        SharedModel annotation = element.getAnnotation(SharedModel.class);
        mAuthority = annotation.authority();
        mDatabaseName = annotation.databaseName();
        mTableName = annotation.tableName();
        mVersion = annotation.version();

        mFields = fields;
    }

    public String getPackage() {
        return mPackage;
    }

    public String getName() {
        return mName;
    }

    public String getAuthority() {
        return mAuthority;
    }

    public String getDatabaseName() {
        return mDatabaseName;
    }

    public String getTableName() {
        return mTableName;
    }

    public int getVersion() {
        return mVersion;
    }

    public List<Field> getFields() {
        return mFields;
    }
}
