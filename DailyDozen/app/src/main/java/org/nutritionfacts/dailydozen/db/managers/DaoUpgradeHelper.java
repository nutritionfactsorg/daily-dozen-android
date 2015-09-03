package org.nutritionfacts.dailydozen.db.managers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.nutritionfacts.dailydozen.db.DaoMaster;

/**
 * Created by chankruse on 15-05-27.
 */
public class DaoUpgradeHelper extends DaoMaster.OpenHelper {

    private static final String TAG = DaoUpgradeHelper.class.getSimpleName();

    public DaoUpgradeHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Migrating database from version " + oldVersion + " to version " + newVersion);
        switch (newVersion) {
            default:
                return;
        }
    }
}
