package de.hpi3d.gamepgrog.trap.datatypes;

import androidx.annotation.NonNull;

/*

Sent to app by server, displayed in a list.
 */
public class Hint {


    private String hintText;

    public Hint(String hintText){
        this.hintText = hintText;
    }


    public String getHintText(){
        return hintText;
    }

    @NonNull
    @Override
    public String toString() {
        return hintText;
    }
}
