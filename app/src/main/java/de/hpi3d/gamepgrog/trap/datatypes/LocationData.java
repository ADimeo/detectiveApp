package de.hpi3d.gamepgrog.trap.datatypes;

import android.location.Location;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

/**
 * A single location at a specific point in time.
 * @see <a href="https://github.com/EatingBacon/gameprog-detective-server/wiki/Spied-User-Data#location>LocationData Specification</a>
 */
@Entity
@Parcel(Parcel.Serialization.BEAN)
public class LocationData implements UserData {

    @Id(autoincrement = true)
    private Long id;

    private double longitude, latitude;
    private long timeInUtcSeconds;

    public LocationData(Location location) {
        this.longitude = location.getLongitude();
        this.latitude = location.getLatitude();
        this.timeInUtcSeconds = location.getTime() / 1000;
    }

    @Keep
    @ParcelConstructor
    @Generated(hash = 1475985557)
    public LocationData(Long id, double longitude, double latitude,
                        long timeInUtcSeconds) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.timeInUtcSeconds = timeInUtcSeconds;
    }

    @Generated(hash = 1606831457)
    public LocationData() {
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public long getTimeInUtcSeconds() {
        return timeInUtcSeconds;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setTimeInUtcSeconds(long timeInUtcSeconds) {
        this.timeInUtcSeconds = timeInUtcSeconds;
    }
}
