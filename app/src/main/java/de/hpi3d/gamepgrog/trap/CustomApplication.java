package de.hpi3d.gamepgrog.trap;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import de.hpi3d.gamepgrog.trap.tasks.DaoMaster;
import de.hpi3d.gamepgrog.trap.tasks.DaoSession;


/**
 * Custom application is necessary so we have a single globally accessible spot where we
 * can access our DaoSession. AFAIK this is GreenDAO best practice.
 */
public class CustomApplication extends Application {

    private DaoSession daoSession;
    private DaoMaster daoMaster;

    @Override
    public void onCreate() {
        super.onCreate();
        // do this once, for example in your Application class

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
