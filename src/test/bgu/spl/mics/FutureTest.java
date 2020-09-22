package bgu.spl.mics;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {
    Future future;

    @Before
    public void setUp(){
        future = new Future();
    }

    @After
    public void tearDown(){
        future = null;
        Assert.assertNull(future);
    }


    /**
     * Initialize Future with a String/int val and checking whether future.get() work properly
     */
    @org.junit.Test
    public void get() {
        future.resolve("Get");
        Assert.assertEquals("Get",future.get());
    }

    /**
     * Initialize Future with a String/int val and checking whether future.resolve() work properly
     */
    @org.junit.Test
    public void resolve() {
        future.resolve("Resolved");
        Assert.assertEquals("Resolved",future.get());
    }

    /**
     * checking if future1 is resolved
     */
    @org.junit.Test
    public void isDone() {
        Future future1 = new Future();
        Assert.assertEquals(false,future1.isDone());
        future1.resolve("Resolved");
        Assert.assertEquals(true,future1.isDone());
    }

    @org.junit.Test
    public void get1(){
        Future future2 = new Future();
        future.resolve("Get");
        Assert.assertEquals("Get",future2.get(1500,TimeUnit.MILLISECONDS));
    }
}