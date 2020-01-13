package de.hpi3d.gamepgrog.trap.tasks;


import android.app.Activity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import de.hpi3d.gamepgrog.trap.future.Promise;

@Entity
@Parcel(Parcel.Serialization.BEAN)
public class Task {

    @Id(autoincrement = true)
    private long id;
    private String name, description, dataType;

    @ParcelConstructor
    @Keep
    public Task(long id, String name, String description, String dataType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dataType = dataType;
    }

    @Generated(hash = 733837707)
    public Task() {
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

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Promise<TaskResolver.ExecutionResult> execute(Activity app) {
        return TaskResolverManager.getResolverFor(this).execute(app, this);
    }
}
