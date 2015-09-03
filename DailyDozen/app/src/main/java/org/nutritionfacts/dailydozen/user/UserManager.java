package org.nutritionfacts.dailydozen.user;

import android.content.Context;
import android.util.Log;

import org.nutritionfacts.dailydozen.db.DBUser;
import org.nutritionfacts.dailydozen.db.DBUserDao;
import org.nutritionfacts.dailydozen.db.DaoSession;
import org.nutritionfacts.dailydozen.db.managers.DatabaseManager;
import org.nutritionfacts.dailydozen.util.SharedPreferenceHelper;

import java.io.File;
import java.util.List;

/**
 * Created by chankruse on 15-05-26.
 */
public class UserManager {

    private DBUser currentUser;
    private String activeUserId;

    private Context context; //Use application context

    private static UserManager mInstance;

    public static UserManager getInstance() {

        if (mInstance == null) {
            mInstance = new UserManager();
        }

        return mInstance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    //constructor hidden because it's a singleton
    private UserManager() {
    }

    public String getUserId() {
        if (DatabaseManager.getInstance().getDaoSession() == null && activeUserId != null) {
            DatabaseManager.getInstance().loadDatabaseForUser(context, activeUserId);
        }

        return activeUserId;
    }

    public void setActiveUser(String userId) {
        currentUser = null;
        activeUserId = userId;
    }

    public DBUser getDbUser() {
        if (DatabaseManager.getInstance().getDaoSession() == null && activeUserId != null) {
            DatabaseManager.getInstance().loadDatabaseForUser(context, activeUserId);
        }

        if (currentUser == null && DatabaseManager.getInstance().getDaoSession() != null) {

            DBUserDao dbUserDao = DatabaseManager.getInstance().getDaoSession().getDBUserDao();
            List<DBUser> dbUsers = dbUserDao.loadAll();
            if (dbUsers == null || dbUsers.isEmpty()) {
                currentUser = new DBUser();
                currentUser.setUsageCount(1L);

                dbUserDao.insert(currentUser);
            } else {
                currentUser = dbUsers.get(0);
            }
        }

        return currentUser;
    }

    public boolean hasUserRegistered() {
        return SharedPreferenceHelper.getInstance().getActiveUserId() != null;
        //return false;
    }

    public boolean createUser() {

        UserManager.getInstance().createActiveUserAndDb("0");

        DaoSession session = DatabaseManager.getInstance().getDaoSession();

        DBUser dbUser = UserManager.getInstance().getDbUser();

        UserManager.getInstance().saveActiveUserIdToSharedPrefs();

        return true;
    }

    // this method creates a new user but doesn't save the user id to sharedPreferences
    // (called during registration)
    private void createActiveUserAndDb(String activeUserId) {
        //let's delete any previous db if it exists
        File dbFile = context.getDatabasePath(DatabaseManager.getInstance().getDatabaseNameForUserId(activeUserId));

        if (dbFile.exists()) {
            Log.d("API", "delete prev db");
            dbFile.delete();
        }

        setActiveUser(activeUserId);
        getDbUser(); // this will create and load the DB
    }

    public void saveActiveUserIdToSharedPrefs() {
        SharedPreferenceHelper.getInstance().setActiveUserId(activeUserId);
    }

    public void initializeUserDatabase() {
        String userId = SharedPreferenceHelper.getInstance().getActiveUserId();

        if (userId != null) {
            setActiveUser(userId);

            //this will load the DB and get the active user
            getDbUser();
        }
    }
}
