package de.hpi3d.gamepgrog.trap;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

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
    public void testAddData() throws IOException {
        api.addData(10, new Contact("Bla")).execute();
    }
}
