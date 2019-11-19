package de.hpi3d.gamepgrog.trap.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/*

Sent to app by server, displayed in a list.
Please remember to change the Parcelable implementation when adding/removing variables.
 */
public class Clue implements Parcelable {


    private String hintText;

    public Clue(String hintText) {
        this.hintText = hintText;
    }


    public String getHintText() {
        return hintText;
    }

    @NonNull
    @Override
    public String toString() {
        return hintText;
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
        this.hintText = in.readString();

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
        dest.writeString(hintText);

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
