package de.hpi3d.gamepgrog.trap;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.stubbing.Answer;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.hpi3d.gamepgrog.trap.datatypes.Contact;
import de.hpi3d.gamepgrog.trap.datatypes.UserData;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyByte;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ParcelableTest {

    @Test
    public void testStuff() {
        Assert.assertEquals(new Contact("Paul").getDisplayNamePrimary(),
                process(new Contact("Paul")).getDisplayNamePrimary());
        Gson g = new GsonBuilder().create();

        Contact c = new Contact("Paul");
        Parcelable p1 = Parcels.wrap(c);
        UserData d = Parcels.unwrap(p1);
        System.out.println(g.toJson(d));

        ArrayList<Contact> contacts = new ArrayList<>();
        contacts.add(c);
        contacts.add(new Contact("Antonio"));
        Parcelable p = Parcels.wrap(contacts);
        List<UserData> data = Parcels.unwrap(p);
        System.out.println(g.toJson(data));
    }

    private <T> T process(T value) {
        Parcelable p = Parcels.wrap(value);
        return Parcels.unwrap(p);
    }

    public static class MockParcel {

        @NonNull
        public static Parcel obtain() {
            return new MockParcel().mParcel;
        }

        private int mPosition = 0;
        private List<Object> mStore = new LinkedList<>();
        private Parcel mParcel = mock(Parcel.class);

        private MockParcel() {
            setupWrites();
            setupReads();
            setupOthers();
        }

        // uncomment when needed for the first time
        private void setupWrites() {
            final Answer<Object> answer = i -> {
                final Object arg = i.getArgument(0);
                mStore.add(arg);
                return arg;
            };
            doAnswer(answer).when(mParcel).writeByte(anyByte());
            doAnswer(answer).when(mParcel).writeInt(anyInt());
            doAnswer(answer).when(mParcel).writeString(anyString());
            doAnswer(answer).when(mParcel).writeParcelable(any(Parcelable.class), anyInt());
            doAnswer(answer).when(mParcel).writeLong(anyLong());
            doAnswer(answer).when(mParcel).writeFloat(anyFloat());
            doAnswer(answer).when(mParcel).writeDouble(anyDouble());
        }

        // uncomment when needed for the first time
        private void setupReads() {
            final Answer<Object> answer = i -> mStore.get(mPosition++);
            when(mParcel.readByte()).thenAnswer(answer);
            when(mParcel.readInt()).thenAnswer(answer);
            when(mParcel.readString()).thenAnswer(answer);
            when(mParcel.readParcelable(any(ClassLoader.class))).then(answer);
            when(mParcel.readLong()).thenAnswer(answer);
            when(mParcel.readFloat()).thenAnswer(answer);
            when(mParcel.readDouble()).thenAnswer(answer);
        }

        private void setupOthers() {
            doAnswer(i -> {
                mPosition = i.getArgument(0);
                return null;
            }).when(mParcel).setDataPosition(anyInt());
        }

    }
}
