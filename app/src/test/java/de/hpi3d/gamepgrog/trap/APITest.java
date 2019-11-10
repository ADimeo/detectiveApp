package de.hpi3d.gamepgrog.trap;



import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class APITest {

    private APIBuilder.API api;

    @Before
    public void setUp() {
        api = APIBuilder.build();
    }

    @Test
    public void testRegisterNoException() throws IOException {
        APIBuilder.User user = api.register().execute().body();
        Assert.assertNotNull(user);
        Assert.assertTrue(user.id >= 0);
    }
}
