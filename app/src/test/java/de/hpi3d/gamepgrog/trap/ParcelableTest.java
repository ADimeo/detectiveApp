package de.hpi3d.gamepgrog.trap;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.hpi3d.gamepgrog.trap.api.UserDataPostRequestFactory;
import de.hpi3d.gamepgrog.trap.datatypes.CalendarEvent;
import de.hpi3d.gamepgrog.trap.datatypes.Clue;
import de.hpi3d.gamepgrog.trap.datatypes.Contact;
import de.hpi3d.gamepgrog.trap.datatypes.LocationData;
import de.hpi3d.gamepgrog.trap.datatypes.UserStatus;

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

    private Clue clue;
    private CalendarEvent calendarEvent;
    private Contact contact;
    private LocationData location;
    private UserStatus userStatus;
    private UserDataPostRequestFactory.UserDataPostRequest pr;

    @Before
    public void setup() {
        clue = new Clue(10L, true, "Hello World", "kevin");
        calendarEvent = new CalendarEvent(42, "Doom", "Earth",
                7120984768L, 98761236182L);
        contact = new Contact(192, "Mickey Mouse");
        location = new LocationData(9128370918L, 126721L, 281728L);
        userStatus = new UserStatus(1209L, "Start", "@Mickey",
                "start");
        pr = UserDataPostRequestFactory.buildWithContacts(Arrays.asList(
                new Contact(1, "a"),
                new Contact(2, "b"),
                new Contact(3, "c")
        ));
    }

    @Test
    public void testClues() {
        Clue parsed = copyWithParcel(clue, Clue.CREATOR);
        Assert.assertNotNull(parsed);
        Assert.assertEquals(clue, parsed);
    }

    @Test
    public void testCalendarEvent() {
        CalendarEvent parsed = copyWithParcel(calendarEvent, CalendarEvent.CREATOR);
        Assert.assertNotNull(parsed);
        Assert.assertEquals(calendarEvent, parsed);
    }

    @Test
    public void testContact() {
        Contact parsed = copyWithParcel(contact, Contact.CREATOR);
        Assert.assertNotNull(parsed);
        Assert.assertEquals(contact, parsed);
    }

    @Test
    public void testLocationData() {
        LocationData parsed = copyWithParcel(location, LocationData.CREATOR);
        Assert.assertNotNull(parsed);
        Assert.assertEquals(location, parsed);
    }

    @Test
    public void testUserStatus() {
        UserStatus parsed = copyWithParcel(userStatus, UserStatus.CREATOR);
        Assert.assertNotNull(parsed);
        Assert.assertEquals(userStatus, parsed);
    }

//    Cannot be tested yet, since MockParcel does not support nested Parcelables
//    @Test
//    public void testPostRequest() {
//        UserDataPostRequestFactory.UserDataPostRequest parsed = copyWithParcel(pr, UserDataPostRequestFactory.UserDataPostRequest.CREATOR);
//        Assert.assertNotNull(parsed);
//        Assert.assertEquals(pr, parsed);
//    }

    private <T extends Parcelable> T copyWithParcel(T original, Parcelable.Creator<T> creator) {
        Parcel parcel = MockParcel.obtain();
        original.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        return creator.createFromParcel(parcel);
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
