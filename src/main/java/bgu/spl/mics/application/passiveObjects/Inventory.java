package bgu.spl.mics.application.passiveObjects;


import bgu.spl.mics.application.messages.BookOrderEvent;

import java.awt.print.Book;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory  implements Serializable {

	//--------Fields--------//

	private static Inventory inventory = null;
	private ConcurrentHashMap<String,BookInventoryInfo> bookInventory;

	//--------Methods--------//

	public ConcurrentHashMap<String, BookInventoryInfo> getBookInventory() {
		return bookInventory;
	}

	public void setBookInventory(ConcurrentHashMap<String, BookInventoryInfo> bookInventory) {
		this.bookInventory = bookInventory;
	}

	/**
     * Retrieves the single instance of this class.
     */
	public static Inventory getInstance() {
		if(inventory == null){
			inventory = new Inventory();
		}
		return inventory;
	}
	
	/**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */
	public void load (BookInventoryInfo[ ] inventory ) {
		bookInventory = new ConcurrentHashMap<>();
		for(BookInventoryInfo book: inventory){
			bookInventory.put(book.getBookTitle(),book);
		}
	}
	
	/**
     * Attempts to take one book from the store.
     * <p>
     * @param book 		Name of the book to take from the store
     * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
     * 			The first should not change the state of the inventory while the 
     * 			second should reduce by one the number of books of the desired type.
     */
	public OrderResult take (String book) {

		BookInventoryInfo value = bookInventory.get(book);
		if(value == null)
			return OrderResult.NOT_IN_STOCK;
		synchronized (value){
			if (value.getAmountInInventory()==0){
				return OrderResult.NOT_IN_STOCK;
			}

			value.setAmount(value.getAmountInInventory() - 1);
			bookInventory.put(value.getBookTitle(),value);
			return OrderResult.SUCCESSFULLY_TAKEN;
		}
	}
	
	
	
	/**
     * Checks if a certain book is available in the inventory.
     * <p>
     * @param book 		Name of the book.
     * @return the price of the book if it is available, -1 otherwise.
     */
	public int checkAvailabiltyAndGetPrice(String book) {
		BookInventoryInfo value = bookInventory.get(book);
		if(value == null)
			return -1;
		return value.getPrice();
	}

	/**
     * 
     * <p>
     * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
     * should be the titles of the books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory. 
     * This method is called by the main method in order to generate the output.
     */
	public void printInventoryToFile(String filename){
		try
		{
			HashMap<String,Integer> bookHashMap = new HashMap();
			for (String set : bookInventory.keySet()){
				bookHashMap.put(set,bookInventory.get(set).getAmountInInventory());
			}

			FileOutputStream fos = new FileOutputStream(filename);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(bookHashMap);
			oos.close();
			fos.close();
		}catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
}
