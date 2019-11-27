package de.hpi3d.gamepgrog.trap;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import de.hpi3d.gamepgrog.trap.datatypes.DaoMaster;
import de.hpi3d.gamepgrog.trap.datatypes.DaoSession;


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
