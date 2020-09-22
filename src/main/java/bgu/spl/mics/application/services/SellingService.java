package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.CountDownLatch;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService {
    private MoneyRegister moneyRegister = MoneyRegister.getInstance();
    private Customer customer;
    private BookInventoryInfo book;
    private OrderReceipt orderReceipt;
    private int currentTick;
    CountDownLatch countDownLatch;

    public SellingService(String name, CountDownLatch count) {
        super(name);
        countDownLatch = count;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public BookInventoryInfo getBook() {
        return book;
    }

    public void setBook(BookInventoryInfo book) {
        this.book = book;
    }

    public int getCurrentTick() {
        return currentTick;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, call -> {
            currentTick = call.getCurrentTick();
            if (call.getCurrentTick() == call.getDurationTick()) {
                terminate();
            }
        });

        subscribeEvent(BookOrderEvent.class, call -> {
            customer = call.getCustomer();
            book = call.getBook();

            Future<Integer> futureCheckBook = sendEvent(new CheckAvailablilityEvent(book));

            synchronized (book) {
                synchronized (customer) {
                    int price = book.getAmountInInventory() == 0 ? -1 : book.getPrice();
                    if (price != -1) {
                        if (customer.getAvailableCreditAmount() >= book.getPrice()) {
                            moneyRegister.chargeCreditCard(customer, book.getPrice());
                            Future<OrderResult> bookTaken = sendEvent(new TakeBookEvent(book));
                            orderReceipt = new OrderReceipt(customer.getCustomerReceiptList().size(), getName(), customer.getId(), book.getBookTitle(), price, currentTick, call.getOrderedTick(), currentTick);
                            customer.getCustomerReceiptList().add(orderReceipt);
                            moneyRegister.file(orderReceipt);
                            sendEvent(new DeliveryEvent(customer.getAddress(), customer.getDistance()));
                            complete(call, orderReceipt);
                            return;
                        }
                    }
                }
            }
            complete(call, null);
        });
        countDownLatch.countDown();
    }

}
