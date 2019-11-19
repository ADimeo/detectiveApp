package de.hpi3d.gamepgrog.trap.datatypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for a UserDataPostRequest.
 * To build a PostRequest call the Constructor,
 * than call #addData for each data to add
 * and when finished call #build.
 * Calls can be concatenated
 */
public class UserDataPostRequestBuilder {

    private UserDataPostRequest postRequest;


    public UserDataPostRequestBuilder() {
        postRequest = new UserDataPostRequest();
    }

    public UserDataPostRequestBuilder addData(List<Contact> contacts) {
        postRequest.data.contacts.addAll(contacts);
        return this;
    }

    public UserDataPostRequest build() {
        return postRequest;
    }

    /**
     * Shortcut for building a List of contacts
     */
    public static UserDataPostRequest build(List<Contact> contacts) {
        return new UserDataPostRequestBuilder().addData(contacts).build();
    }

    public class UserDataPostRequest {
        private final String origin = "app";
        private UserDataDictionary data = new UserDataDictionary();
    }

    private class UserDataDictionary {
        private List<Contact> contacts = new ArrayList<>();
    }
}
