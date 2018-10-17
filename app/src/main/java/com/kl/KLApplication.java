package com.kl;

import android.app.Application;
import android.content.Context;

import com.kl.utils.Logger;

import net.sqlcipher.database.SQLiteDatabase;

public class KLApplication extends Application {

    private static KLApplication instance;

    public static KLApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;

        super.onCreate();

        Logger.getLogger().e("[INF] in application onCreate");

        SQLiteDatabase.loadLibs(this);
    }

    public Context getContext() {
        return getApplicationContext();
    }

}
