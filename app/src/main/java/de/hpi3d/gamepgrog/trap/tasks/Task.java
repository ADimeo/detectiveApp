package de.hpi3d.gamepgrog.trap.tasks;


import android.app.Activity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.Random;

import de.hpi3d.gamepgrog.trap.datatypes.Displayable;
import de.hpi3d.gamepgrog.trap.future.EmptyPromise;

/**
 * Task is send from the server to fulfill by the user.
 * Each Tasks has a required datatype which will be fetched by a Data stealer.
 * Tasks are executed by a {@link TaskResolver}.
 * Resolvers are registered in {@link TaskResolverManager}.
 * Some Tasks need initialization, which is done by the {@link TaskInitializerManager}
 *
 * @see <a href="https://github.com/EatingBacon/gameprog-detective-server/wiki/Task">Specification</a>
 */
@Entity
@Parcel(Parcel.Serialization.BEAN)
public class Task implements Displayable {

    @Id(autoincrement = true)
    private long id;

    private String name, description, datatype;
    private String permissionExplanation;
    private boolean finished = false;

    public Task(String description) {
        this.description = description;
        this.id = new Random().nextLong();
    }

    @Generated(hash = 733837707)
    public Task() {
    }

    @Keep
    @ParcelConstructor
    public Task(long id, String name, String description, String datatype,
                String permissionExplanation, boolean finished) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.datatype = datatype;
        this.permissionExplanation = permissionExplanation;
        this.finished = finished;
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

    public String getDatatype() {
        return datatype;
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

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public void setFinished() {
        finished = true;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public EmptyPromise execute(Activity app) {
        return TaskResolverManager.getResolverFor(this).executeAndShowResult(app, this);
    }

    @Override
    public String getDisplayString() {
        return getDescription();
    }

    public boolean getFinished() {
        return this.finished;
    }

    public String getPermissionExplanation() {
        return this.permissionExplanation;
    }

    public void setPermissionExplanation(String permissionExplanation) {
        this.permissionExplanation = permissionExplanation;
    }
}
