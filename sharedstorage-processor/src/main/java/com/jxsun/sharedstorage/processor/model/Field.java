package com.jxsun.sharedstorage.processor.model;

import com.jxsun.sharedstorage.annotation.ModelField;

import javax.lang.model.element.Element;

public class Field {
    private String mName;
    private boolean mIsPrimary;

    public Field(ModelField annotation, Element element) {
        mName = annotation.name();
        mIsPrimary = annotation.primary();
    }

    public String getName() {
        return mName;
    }

    public boolean isPrimary() {
        return mIsPrimary;
    }
}
