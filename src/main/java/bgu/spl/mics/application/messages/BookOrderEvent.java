package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

import java.awt.print.Book;
import java.util.concurrent.ConcurrentHashMap;


public class BookOrderEvent implements Event<OrderReceipt> {

    //--------Fields--------//

    private Customer customer;
    private BookInventoryInfo book;
    private int orderedTick;

    //--------Constructor--------//

    public BookOrderEvent(Customer customer,BookInventoryInfo book, int orderedTick){
        this.book = book;
        this.customer = customer;
        this.orderedTick = orderedTick;
    }

    //--------Methods--------//

    public BookInventoryInfo getBook() {
        return book;
    }

    public void setBook(BookInventoryInfo book) {
        this.book = book;
    }

    public Customer getCustomer() {
        return customer;
    }

    public int getOrderedTick() {
        return orderedTick;
    }
}
