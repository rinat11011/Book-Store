package bgu.spl.mics.application.passiveObjects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class InventoryTest {
    Inventory inventory;
    BookInventoryInfo book = new BookInventoryInfo("Harry Potter",1,3000);
    BookInventoryInfo book2 = new BookInventoryInfo("Lord of the rings",2,10);
    BookInventoryInfo[] newBooks = new BookInventoryInfo[]{book,book2};

    @Before
    public void setUp() throws Exception {
         inventory = new Inventory();
    }

    @After
    public void tearDown() throws Exception {
        inventory = null;
        assertNull(inventory);
    }

    @Test
    public void getInstance() {
        inventory.getInstance();
        Assert.assertNotNull(inventory);
    }

    @Test
    public void load() {
        inventory.load(newBooks);
        Assert.assertEquals(inventory.checkAvailabiltyAndGetPrice(book.getBookTitle()),book.getPrice());
        Assert.assertEquals(inventory.checkAvailabiltyAndGetPrice(book2.getBookTitle()),book2.getPrice());
    }

    @Test
    public void take() {
        inventory.load(newBooks);
        Assert.assertEquals(1,inventory.take(book.getBookTitle()));
        Assert.assertEquals(0,inventory.take("Atkins Diet"));
    }

    @Test
    public void checkAvailabiltyAndGetPrice() {
        inventory.load(newBooks);
        Assert.assertEquals(book.getPrice(),inventory.checkAvailabiltyAndGetPrice(book.getBookTitle()));
        Assert.assertEquals(-1,inventory.checkAvailabiltyAndGetPrice("Atkins Diet"));
    }

    @Test
    public void printInventoryToFile() {
    }
}