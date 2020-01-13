package de.hpi3d.gamepgrog.trap;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.hpi3d.gamepgrog.trap.future.ArrayExt;

public class ArrayExtTest {

    @Test
    public void testToIntList() {
        int[] a1 = new int[]{1, 2, 3};
        List<Integer> expected = Arrays.asList(1, 2, 3);
        Assert.assertEquals(expected, ArrayExt.toIntList(a1));

        a1 = new int[]{};
        expected = Collections.emptyList();
        Assert.assertEquals(expected, ArrayExt.toIntList(a1));

        a1 = new int[]{42};
        expected = Collections.singletonList(42);
        Assert.assertEquals(expected, ArrayExt.toIntList(a1));
    }
}
