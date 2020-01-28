package de.hpi3d.gamepgrog.trap.datatypes;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.Locale;

@Parcel(Parcel.Serialization.BEAN)
public class Language implements UserData {

    private String languageCode;


    public static Language getCurrentLanguage() {
        String languageString = Locale.getDefault().getLanguage();
        return new Language(languageString);
    }

    @ParcelConstructor
    public Language(String languageCode) {
        this.languageCode = languageCode;
    }

    @Override
    public String[] requiredPermission() {
        // No permission required.
        return new String[0];
    }
}
