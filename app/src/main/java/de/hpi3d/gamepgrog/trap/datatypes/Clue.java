package de.hpi3d.gamepgrog.trap.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import androidx.annotation.NonNull;

import java.util.Objects;

/*

Sent to app by server, displayed in a list.
Please remember to change the Parcelable implementation when adding/removing variables.
 */
@Entity
public class Clue implements Parcelable {

    @Id(autoincrement = true)
    private Long id;


    private boolean personalized;
    private String text;
    private String name;

    public Clue(String text) {
        this.text = text;
    }


    @Generated(hash = 1213786884)
    public Clue(Long id, boolean personalized, String text, String name) {
        this.id = id;
        this.personalized = personalized;
        this.text = text;
        this.name = name;
    }


    @Generated(hash = 1330280195)
    public Clue() {
    }


    protected Clue(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        personalized = in.readByte() != 0;
        text = in.readString();
        name = in.readString();
    }

    public static final Creator<Clue> CREATOR = new Creator<Clue>() {
        @Override
        public Clue createFromParcel(Parcel in) {
            return new Clue(in);
        }

        @Override
        public Clue[] newArray(int size) {
            return new Clue[size];
        }
    };

    public String getText() {
        return text;
    }

    @NonNull
    @Override
    public String toString() {
        return text;
    }


    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public void setText(String text) {
        this.text = text;
    }


    public boolean getPersonalized() {
        return this.personalized;
    }


    public void setPersonalized(boolean personalized) {
        this.personalized = personalized;
    }


    public String getName() {
        return this.name;
    }


    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeByte((byte) (personalized ? 1 : 0));
        dest.writeString(text);
        dest.writeString(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Clue clue = (Clue) o;
        return personalized == clue.personalized &&
                Objects.equals(id, clue.id) &&
                Objects.equals(text, clue.text) &&
                Objects.equals(name, clue.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, personalized, text, name);
    }
}
