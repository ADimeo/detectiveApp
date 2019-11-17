package de.hpi3d.gamepgrog.trap.datatypes;


/*
Represents data from a single contact
 */

import androidx.annotation.NonNull;

public class Contact extends UserData {

    private String displayNamePrimary;

    public Contact(String primaryName){
        displayNamePrimary = primaryName;
    }

    @NonNull
    @Override
    public String toString() {
        return displayNamePrimary;
    }

}
