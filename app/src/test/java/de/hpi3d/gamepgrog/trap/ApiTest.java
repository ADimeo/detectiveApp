package de.hpi3d.gamepgrog.trap;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import de.hpi3d.gamepgrog.trap.api.ApiBuilder;
import de.hpi3d.gamepgrog.trap.api.UserDataPostRequestFactory;
import de.hpi3d.gamepgrog.trap.datatypes.Contact;
import retrofit2.Response;

public class ApiTest {

    @Test
    public void testDataPR() throws IOException {
        UserDataPostRequestFactory.UserDataPostRequest pr =
                UserDataPostRequestFactory.buildWithContacts(Arrays.asList(
                new Contact("Hey"), new Contact("Bla")
        ));
        Response res = ApiBuilder.build().addData(4, pr).execute();
        Assert.assertNotNull(res);
        Assert.assertEquals(200, res.code());
    }
}
