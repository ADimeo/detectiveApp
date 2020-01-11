package de.hpi3d.gamepgrog.trap.datatypes;


import android.Manifest;
import android.app.Activity;
import android.content.Context;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import de.hpi3d.gamepgrog.trap.PermissionHelper;

@Parcel(Parcel.Serialization.BEAN)
public class Task {

    private long id;
    private String name, description, dataType;

    @ParcelConstructor
    public Task(long id, String name, String description, String dataType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dataType = dataType;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDataType() {
        return dataType;
    }

    public void execute(Activity app) {
//        PermissionHelper.setPermission(app, userDataPermission);
    }
}
