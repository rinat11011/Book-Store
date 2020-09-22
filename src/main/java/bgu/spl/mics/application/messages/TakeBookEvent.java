package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.OrderResult;

public class TakeBookEvent implements Event<OrderResult> {

    //--------Fields--------//

    BookInventoryInfo book;

    //--------Methods--------//

    public TakeBookEvent(BookInventoryInfo book){
        this.book = book;
    }

    public BookInventoryInfo getBook() {
        return book;
    }

    public void setBook(BookInventoryInfo book) {
        this.book = book;
    }

}
