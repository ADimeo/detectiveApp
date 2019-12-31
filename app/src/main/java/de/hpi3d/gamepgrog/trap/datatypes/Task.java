package de.hpi3d.gamepgrog.trap.datatypes;

import android.os.Parcel;

public class Task extends ApiDataType {

    private String name, description, dataType;

    public Task(String name, String description, String dataType) {
        this.name = name;
        this.description = description;
        this.dataType = dataType;
    }

    @Override
    public Parcel toParcel() {
        Parcel p = Parcel.obtain();
        p.writeString(name);
        p.writeString(description);
        p.writeString(dataType);
        return p;
    }

    @Override
    protected void fromParcel(Parcel p) {
        name = p.readString();
        description = p.readString();
        dataType = p.readString();
    }

    @Override
    public String getTypeName() {
        return "task";
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
}
