package org.nutritionfacts.dailydozen.data;

import android.content.Context;

import org.nutritionfacts.dailydozen.db.DBConsumption;
import org.nutritionfacts.dailydozen.db.DBDailyReport;
import org.nutritionfacts.dailydozen.db.DBUser;
import org.nutritionfacts.dailydozen.db.DaoSession;
import org.nutritionfacts.dailydozen.db.managers.DatabaseManager;
import org.nutritionfacts.dailydozen.user.UserManager;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by chankruse on 15-09-02.
 */
public class DataManager {
    private Context context; //Use application context
    private DaoSession daoSession;

    private static DataManager ourInstance = new DataManager();

    public static DataManager getInstance() {
        return ourInstance;
    }

    private DataManager() {
    }

    public void setContext(Context context) {
        this.context = context;
    }

    // MAKE SURE WE NEVER CALL daoSession directly, since it may be null if we had a low mem condition
    public DaoSession getDaoSession() {

        if (daoSession == null) {

            if (DatabaseManager.getInstance().getDaoSession() == null) {
                UserManager.getInstance().getDbUser(); //just make sure the db is loaded
            }

            daoSession = DatabaseManager.getInstance().getDaoSession();
        }

        return daoSession;
    }

    public void incrementUsageCount() {
        DBUser user = UserManager.getInstance().getDbUser();
        long usageCount = user.getUsageCount();
        user.setUsageCount(usageCount + 1);
        user.update();
    }

    public Date getTodaysDate() {
        Calendar calendarDate = Calendar.getInstance();
        calendarDate.set(Calendar.HOUR_OF_DAY, 0);
        calendarDate.set(Calendar.MINUTE, 0);
        calendarDate.set(Calendar.SECOND, 0);
        calendarDate.set(Calendar.MILLISECOND, 0);

        return calendarDate.getTime();
    }

    public DBDailyReport getReportForToday() {

        Date date = getTodaysDate();

        return DBDailyReport.getDailyReportForDate(getDaoSession(), date);
    }

    public void setServingCount(DBConsumption consumption, double servingCount) {
        consumption.setConsumedServingCount(servingCount);
        getDaoSession().update(consumption);
    }

    public DBConsumption getConsumption(long id) {
        return DBConsumption.getConsumption(getDaoSession(), id);
    }
}
