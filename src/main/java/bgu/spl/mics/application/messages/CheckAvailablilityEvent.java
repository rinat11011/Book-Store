package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;

public class CheckAvailablilityEvent implements Event<Integer>{

    //--------Fields--------//

    private BookInventoryInfo book;

    //--------Methods--------//

    public CheckAvailablilityEvent(BookInventoryInfo book){ //******************like this?*********************//
        this.book = book;
    }

    public BookInventoryInfo getBook() {
        return book;
    }

    public void setBook(BookInventoryInfo book) {
        this.book = book;
    }
}
