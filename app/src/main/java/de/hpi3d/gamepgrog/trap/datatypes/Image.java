package de.hpi3d.gamepgrog.trap.datatypes;

import android.content.Context;
import android.graphics.Bitmap;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Parcel(Parcel.Serialization.BEAN)
public class Image implements UserData {

    private static int lastId = 0;
    private static final String filename = "stolen_image_%s.png";

    private Bitmap picture;

    @ParcelConstructor
    public Image(Bitmap picture) {
        this.picture = picture;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public File toFile(Context c) {
        File f = new File(c.getCacheDir(), String.format(filename, ++lastId));
        try {
            if (f.createNewFile()) {

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                picture.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] data = stream.toByteArray();

                FileOutputStream out = new FileOutputStream(f);
                out.write(data);
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }
}
