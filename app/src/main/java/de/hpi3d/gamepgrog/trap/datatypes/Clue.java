package de.hpi3d.gamepgrog.trap.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import androidx.annotation.NonNull;

/*

Sent to app by server, displayed in a list.
Please remember to change the Parcelable implementation when adding/removing variables.
 */
@Entity
public class Clue implements Parcelable {

    @Id(autoincrement = true)
    private Long id;

    private String clueText;

    public Clue(String clueText) {
        this.clueText = clueText;
    }


    public String getClueText() {
        return clueText;
    }

    @NonNull
    @Override
    public String toString() {
        return clueText;
    }


    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        return 0;
    }


    Clue(Parcel in) {
        this.clueText = in.readString();

    }


    @Generated(hash = 178786675)
    public Clue(Long id, String clueText) {
        this.id = id;
        this.clueText = clueText;
    }


    @Generated(hash = 1330280195)
    public Clue() {
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(clueText);

    }


    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public void setClueText(String clueText) {
        this.clueText = clueText;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Clue createFromParcel(Parcel in){
            return new Clue(in);
        }

        public Clue[] newArray(int size){
            return new Clue[size];
        }
    };
}
