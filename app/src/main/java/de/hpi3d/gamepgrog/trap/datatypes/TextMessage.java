package de.hpi3d.gamepgrog.trap.datatypes;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;


@Parcel(Parcel.Serialization.BEAN)
public class TextMessage implements UserData, Comparable {

    private long id;
    private long timeInUtcSeconds;
    private String body;
    private String address; // Doesn't need to be a number
    private boolean inbound;


    @ParcelConstructor
    public TextMessage(long id, long timeInUtcSeconds, String body, String address, boolean inbound) {
        this.id = id;
        this.timeInUtcSeconds = timeInUtcSeconds;
        this.body = body;
        this.address = address;
        this.inbound = inbound;
    }

    public static HashMap<String, ArrayList<TextMessage>> orderByAddress(ArrayList<TextMessage> messages) {

        HashMap<String, ArrayList<TextMessage>> messagesByAddress = new HashMap<>();
        for (TextMessage message : messages) {
            String address = message.address;

            if (!messagesByAddress.containsKey(address)) {
                messagesByAddress.put(address, new ArrayList<>());
            }
            messagesByAddress.get(address).add(message);
        }

        return messagesByAddress;
    }


    public String getBody() {
        return body;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public long getTimeInUtcSeconds() {
        return timeInUtcSeconds;
    }

    public void setTimeInUtcSeconds(long timeInUtcSeconds) {
        this.timeInUtcSeconds = timeInUtcSeconds;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isInbound() {
        return inbound;
    }

    public void setInbound(boolean inbound) {
        this.inbound = inbound;
    }

    @NonNull
    @Override
    public String toString() {
        return id + " ||| " + timeInUtcSeconds + " ||| " + body + " ||| " + address + " ||| " + inbound;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextMessage textMessage = (TextMessage) o;
        return id == textMessage.id &&
                Objects.equals(timeInUtcSeconds, textMessage.timeInUtcSeconds) &&
                Objects.equals(body, textMessage.body) &&
                Objects.equals(address, textMessage.address) &&
                Objects.equals(inbound, textMessage.inbound);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timeInUtcSeconds, body, address, inbound);
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof TextMessage) {
            return Long.compare(getTimeInUtcSeconds(), ((TextMessage) o).getTimeInUtcSeconds());
        }
        return 0;
    }
}
