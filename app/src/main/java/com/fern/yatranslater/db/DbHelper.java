package com.fern.yatranslater.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.fern.yatranslater.entities.TranslateItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrey Saprykin on 16.04.2017.
 */

public class DbHelper extends SQLiteOpenHelper {
    private Context context;
    public static final int dbVersion = 1;
    public static final String databaseName = "ya_translator.db";
    private static DbHelper instance;

    private static SQLiteDatabase db;

    private YaTable yaTable;

    private DbHelper(Context context) {
        super(context, databaseName, null, dbVersion);
        this.context = context;
        yaTable = new YaTable();
    }

    public static DbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DbHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(yaTable.sqlCreateTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(sqlDeleteTable());
        onCreate(db);
    }

    private String sqlDeleteTable() {
        return "drop table if exists " + YaTable.table_name;
    }

    /**Вставить в таблицу результаты перевода*/
    public boolean insertTranslate(TranslateItem translateItem) {
        ContentValues cv = new ContentValues();
        boolean isFavorite = false;
        cv.put(yaTable.s_text, translateItem.getSource());
        cv.put(yaTable.t_text, translateItem.getTranslate());
        cv.put(yaTable.f_item, String.valueOf(isFavorite));

        db = getWritableDatabase();
        boolean isInserted = (-1 != db.insert(YaTable.table_name, null, cv));
        db.close();
        return isInserted;
    }

    /**Изменение принадлежности записи к избранному"*/
    public boolean updateFavorite(TranslateItem translateItem, boolean isFavorite) {
        boolean isUpdate = false;
        if (translateItem.getItem_id() != -1) {
            ContentValues cv = new ContentValues();
            cv.put(yaTable.f_item, String.valueOf(isFavorite));

            String where_conditions = yaTable.id + " = ?";
            String[] update_args = new String[]{ String.valueOf(translateItem.getItem_id()) };

            db = getWritableDatabase();
            isUpdate = (db.update(YaTable.table_name, cv, where_conditions, update_args) >= 1);
            db.close();
        }
        return isUpdate;
    }

    /**Получить последний идентификатор*/
    public int getLastId() {
        int result = -1;
        String[] columns = { yaTable.id };
        db = getReadableDatabase();
        Cursor cursor = db.query(YaTable.table_name, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToLast();
            result = cursor.getInt(cursor.getColumnIndex(yaTable.id));
        }
        cursor.close();
        db.close();
        return result;
    }

    /**Получить последнюю запись из таблицы*/
    @Nullable
    public TranslateItem getLastItem(){
        TranslateItem item = null;
        String[] columns = { yaTable.id,
                yaTable.s_text,
                yaTable.t_text,
                yaTable.f_item };
        String sortOrder = yaTable.id + " desc";
        String limit = "1";
        db = getReadableDatabase();
        Cursor cursor = db.query(YaTable.table_name, columns, null, null, null, null, sortOrder, limit);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                     item = new TranslateItem(
                             cursor.getInt(cursor.getColumnIndex(yaTable.id)),
                             cursor.getString(cursor.getColumnIndex(yaTable.s_text)),
                             cursor.getString(cursor.getColumnIndex(yaTable.t_text)),
                             Boolean.valueOf(cursor.getString(cursor.getColumnIndex(yaTable.f_item))));
            }
        }
        return item;
    }

    /**Удалить запись из таблицы*/
    public boolean deleteTranslateItem(TranslateItem translateItem) {
        boolean isDelete = false;
        if (translateItem.getItem_id() != -1) {
            String where_conditions = yaTable.id + " = ?";
            String[] delete_args = new String[]{ String.valueOf(translateItem.getItem_id()) };
            db = getWritableDatabase();
            isDelete = (db.delete(YaTable.table_name, where_conditions, delete_args) >= 1);
            db.close();
        }
        return isDelete;
    }

    /**Удалить все записи истории или избранного*/
    public void deleteAllHistoryOrFavorites(boolean isFavorites) {
        String where_conditions = yaTable.f_item + "=?";
        String[] delete_args = new String[]{ String.valueOf(isFavorites) };
        db = getWritableDatabase();
        db.delete(YaTable.table_name, where_conditions, delete_args);
        db.close();
    }

    /**Получиить записи только "Избранное"*/
    private Cursor getFavoritesItems() {
        String[] columns = { yaTable.id,
                yaTable.s_text,
                yaTable.t_text,
                yaTable.f_item };
        String where_conditions = yaTable.f_item + "=?";
        String[] selection_args = new String[]{ String.valueOf("true") };
        String sortOrder = yaTable.id + " desc";

        Cursor resultCursor = db.query(YaTable.table_name, columns, where_conditions, selection_args, null, null, sortOrder);
        return resultCursor;
    }

    /**Получить записи "Истории" (включая избранное)*/
    private Cursor getHistoryItems() {
        String[] columns = { yaTable.id,
                yaTable.s_text,
                yaTable.t_text,
                yaTable.f_item };
        String sortOrder = yaTable.id + " DESC";

        Cursor resultCursor = db.query(YaTable.table_name, columns, null, null, null, null, sortOrder);
        return resultCursor;
    }

    /**Получить нужные записи (История или только Избранное)*/
    public List<TranslateItem> getItems(boolean isFavorite) {
        db = getReadableDatabase();
        Cursor cursor = isFavorite ? getFavoritesItems() : getHistoryItems();
        List<TranslateItem> result = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    result.add(new TranslateItem(
                            cursor.getInt(cursor.getColumnIndex(yaTable.id)),
                            cursor.getString(cursor.getColumnIndex(yaTable.s_text)),
                            cursor.getString(cursor.getColumnIndex(yaTable.t_text)),
                            Boolean.valueOf(cursor.getString(cursor.getColumnIndex(yaTable.f_item)))));
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();

        return result;
    }
}
