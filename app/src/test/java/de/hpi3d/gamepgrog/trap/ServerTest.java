package de.hpi3d.gamepgrog.trap;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hpi3d.gamepgrog.trap.datatypes.Clue;
import de.hpi3d.gamepgrog.trap.datatypes.Contact;
import de.hpi3d.gamepgrog.trap.datatypes.UserDataPostRequestFactory;

public class ServerTest {

    private APIBuilder.API api;

    @Before
    public void setUp() {
        api = APIBuilder.build();
    }

    @Test
    public void testGetClues() {
        List<Clue> clues = api.getClues(0).blockingLast();
        Assert.assertEquals(1, clues.size());
        Assert.assertEquals(true, clues.get(0).getPersonalized());
        Assert.assertEquals("clue_test", clues.get(0).getName());
        Assert.assertEquals("Hello World", clues.get(0).getText());
    }

    @Test
    public void testAddData() throws IOException {
        List<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact("paul"));
        api.addData(0, UserDataPostRequestFactory.buildWithContacts(contacts)).execute();
    }

}
