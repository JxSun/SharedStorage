package com.jxsun.sharedstorage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqliteHelper extends SharedStorageHelper {
    private static final String TAG = "SqliteHelper";

    private Map<Class, Table> table = new HashMap<>();

    @Override
    public <I> Accessor<I> where(Class<I> item) {
        return new AccessorImpl<>(table.get(item.getClass()));
    }

    public static class AccessorImpl<I> implements SharedStorageHelper.Accessor<I> {

        private final Table<I> mTable;

        public AccessorImpl(Table<I> table) {
            mTable = table;
        }

        @Override
        public boolean createItem(Context context, I item) {
            Log.d(TAG, "createItem");
            boolean result = false;
            Uri uri = context.getContentResolver()
                    .insert(mTable.getContentUri(),
                            mTable.getContentValues(item, false));
            if (uri != null) {
                result = true;
                Log.d(TAG, "Add new item success");
            }
            return result;
        }

        @Override
        public boolean deleteItem(Context context, long id) {
            Log.d(TAG, "deleteItem");
            return handleResult(context.getContentResolver()
                    .delete(mTable.getContentUri(),
                            mTable.getPrimaryFieldName() + "=?",
                            new String[]{String.valueOf(id)}));
        }

        @Override
        public I getItem(Context context, long id) {
            Log.d(TAG, "getItem");
            Cursor cursor = context.getContentResolver()
                    .query(mTable.getContentUri(),
                            null,
                            mTable.getPrimaryFieldName() + "=?",
                            new String[]{String.valueOf(id)}, null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
            return mTable.getRow(cursor, true);
        }

        @Override
        public List getItems(Context context, String label) {
            Log.d(TAG, "getItems");
            Cursor cursor = context.getContentResolver()
                    .query(mTable.getContentUri(),
                            null,
                            mTable.getPrimaryFieldName() + "=?",
                            new String[]{label}, null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
            return mTable.getRows(cursor, true);
        }

        @Override
        public boolean updateItem(Context context, I item, long id) {
            Log.d(TAG, "updateItem");
            return handleResult(context.getContentResolver().
                    update(mTable.getContentUri(),
                            mTable.getContentValues(item, true),
                            mTable.getPrimaryFieldName() + "=?",
                            new String[]{String.valueOf(id)}));
        }

        @Override
        public boolean deleteAll(Context context) {
            Log.d(TAG, "deleteAll");
            return handleResult(context.getContentResolver().delete(mTable.getContentUri(), null, null));
        }

        private boolean handleResult(int rowsNumber) {
            return rowsNumber > 0;
        }
    }

    public interface Table<M> {
        String getPrimaryFieldName();

        Uri getContentUri();

        ContentValues getContentValues(M item, boolean flag);

        M getRow(Cursor cursor, boolean closeCursor);

        List<M> getRows(Cursor cursor, boolean closeCursor);
    }
}
