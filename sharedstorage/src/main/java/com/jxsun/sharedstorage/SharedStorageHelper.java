package com.jxsun.sharedstorage;

import android.content.Context;
import android.net.Uri;

import java.util.List;

public abstract class SharedStorageHelper {

    /**
     * The factory class for the SharedStorageHelper
     */
    public static class Factory {
        public static SharedStorageHelper create() {
            return new SqliteHelper();
        }
    }

    public abstract <I> Accessor<I> where(Class<I> item);

    public interface Accessor<I> {
        boolean createItem(Context context, I item);

        boolean deleteItem(Context context, long id);

        I getItem(Context context, long id);

        List getItems(Context context, String label);

        boolean updateItem(Context context, I item, long id);

        boolean deleteAll(Context context);
    }
}
