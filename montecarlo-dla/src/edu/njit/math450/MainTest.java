package edu.njit.math450;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by nhale on 10/23/15.
 */
public class MainTest {

    @org.junit.Before
    public void setUp() throws Exception {

    }

    @org.junit.After
    public void tearDown() throws Exception {

    }

    @Test
    public void testReturnValue() throws Exception {
        int a = 5;
        Main main = new Main();
        int b = main.returnValue(a);
        assert(a == b);
    }
}