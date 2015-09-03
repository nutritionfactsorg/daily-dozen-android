package org.nutritionfacts.dailydozen.db.managers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.nutritionfacts.dailydozen.db.DaoMaster;
import org.nutritionfacts.dailydozen.db.DaoSession;

/**
 * Created by chankruse on 15-05-27.
 */
public class DatabaseManager {

    private static DatabaseManager instance;

    private SQLiteDatabase database;
    private DaoSession daoSession;

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private DatabaseManager() {}

    public String getDatabaseNameForUserId(String userId) {
        return "user" + userId + "-db";
    }

    public DaoSession loadDatabaseForUser(Context context, String userId) {
        return loadDatabase(context, getDatabaseNameForUserId(userId));
    }

    private DaoSession loadDatabase(Context context, String dbName) {
        if (database != null) {
            unloadDatabase();
        }
        DaoUpgradeHelper helper = new DaoUpgradeHelper(context, dbName, null);
        database = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(database);
        daoSession = daoMaster.newSession();
        return daoSession;
    }

    public void unloadDatabase() {
        if (database != null) {
            database.close();
            database = null;
            daoSession = null;
        }
        return;
    }

    public boolean hasOpenedSession() {
        return database != null && database.isOpen();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
