package de.hpi3d.gamepgrog.trap.datatypes;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import androidx.annotation.NonNull;

@Entity
@Parcel(Parcel.Serialization.BEAN)
public class Clue implements Displayable {

    @Id(autoincrement = true)
    private Long id;

    private boolean personalized;
    private String text;
    private String name;

    public Clue(String text) {
        this.text = text;
    }

    @ParcelConstructor
    @Keep
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

    public String getText() {
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
    public String getDisplayString() {
        return getText();
    }

    @NonNull
    @Override
    public String toString() {
        return text;
    }
}
