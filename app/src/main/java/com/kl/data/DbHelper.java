package com.kl.data;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    private static DbHelper instance;

    private DbHelper(Context context) {
        super(context, DatabaseDefinition.DATABASE_NAME, null, DatabaseDefinition.DATABASE_VERSION);
    }

    static public synchronized DbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DbHelper(context);
        }
        return instance;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseDefinition.RadioStation.createTableSql());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseDefinition.RadioStation.dropTableSql());
        onCreate(db);
    }

    public SQLiteDatabase getReadableDatabase() {
        return getReadableDatabase(dbpw());
    }

    public SQLiteDatabase getWritableDatabase() {
        return getWritableDatabase(dbpw());
    }

    private String dbpw() {
        return "@Qwerty_pwd-N0n~h@ck@b|";
    }
}
