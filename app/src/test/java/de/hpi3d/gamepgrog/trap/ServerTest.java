package de.hpi3d.gamepgrog.trap;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import de.hpi3d.gamepgrog.trap.datatypes.Contact;

public class ServerTest {

    private APIBuilder.API api;

    @Before
    public void setUp() {
        api = APIBuilder.build();
    }

    @Test
    public void testRegister() {
        APIBuilder.User user = api.register().blockingLast();
        Assert.assertNotNull(user);
        Assert.assertTrue(user.id >= 0);
    }

    @Test
    public void testAsyncRegister() {
        api.register().subscribe(user -> {
            Assert.assertNotNull(user);
            Assert.assertTrue(user.id >= 0);
        });
    }

    @Test
    public void testGetPersonalizedClues() {
        List<Clue> clues = api.listPersonalizedClues(10).blockingLast();
        Assert.assertEquals(2, clues.size());
        Assert.assertEquals(clues.get(0).getKey(), "clue0");
        Assert.assertEquals(clues.get(0).getText(), "First Clue");
    }

    @Test
    public void testAddData() throws IOException {
        api.addData(10, new Contact("Bla")).execute();
    }
}
