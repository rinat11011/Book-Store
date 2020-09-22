package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner implements Serializable {
    public static void main(String[] args) throws Exception {
        Inventory inventory = Inventory.getInstance();
        MoneyRegister moneyRegister = MoneyRegister.getInstance();
        HashMap<Integer, Customer> customersHashMap = new HashMap<>();
        try {
            JsonParser parser = new JsonParser();
            Object obj = parser.parse(new FileReader(args[0]));
            JsonObject jsonObject = (JsonObject) obj;


            //INITIALIZING THE INVENTORY:

            JsonArray initialInventory = jsonObject.getAsJsonArray("initialInventory");
            BookInventoryInfo[] inventoryBooks = new BookInventoryInfo[initialInventory.size()];
            for (int i = 0; i < initialInventory.size(); i++) {
                JsonObject book = (JsonObject) initialInventory.get(i);
                String bookTitle = book.get("bookTitle").getAsString();
                int amount = book.get("amount").getAsInt();
                int price = book.get("price").getAsInt();
                inventoryBooks[i] = new BookInventoryInfo(bookTitle, amount, price);
            }
            inventory.load(inventoryBooks);


            //INITIALIZING THE RESOURCES:

            JsonArray initialResources = jsonObject.get("initialResources").getAsJsonArray();

            //INITIALIZING THE DELIVERY VEHICLES:

            JsonObject theObject = (JsonObject) initialResources.get(0);
            JsonArray vehicles = theObject.get("vehicles").getAsJsonArray();
            DeliveryVehicle[] deliveryVehicles = new DeliveryVehicle[vehicles.size()];
            for (int i = 0; i < vehicles.size(); i++) {
                JsonObject vehicle = (JsonObject) vehicles.get(i);
                int speed = vehicle.get("speed").getAsInt();
                int license = vehicle.get("license").getAsInt();
                deliveryVehicles[i] = new DeliveryVehicle(license, speed);
            }
            ResourcesHolder loadVehicles = ResourcesHolder.getInstance();
            loadVehicles.load(deliveryVehicles);


            //INITIALING THE SERVICES:

            LinkedList<Thread> threadList = new LinkedList<>();

            JsonObject services = jsonObject.get("services").getAsJsonObject();
            int sum = services.get("selling").getAsInt() + services.get("inventoryService").getAsInt() + services.get("logistics").getAsInt() + services.get("resourcesService").getAsInt() + services.get("customers").getAsJsonArray().size();
            CountDownLatch countDownLatch = new CountDownLatch(sum);

            for (int i = 1; i <= services.get("selling").getAsInt(); i++) {
                SellingService sellingService = new SellingService("Selling Service" + i + "", countDownLatch);
                Thread sellingThread = new Thread(sellingService);
                threadList.add(sellingThread);
                sellingThread.start();
            }

            for (int i = 1; i <= services.get("inventoryService").getAsInt(); i++) {
                InventoryService inventoryService = new InventoryService("Inventory Service" + i + "", countDownLatch);
                Thread inventoryThread = new Thread(inventoryService);
                threadList.add(inventoryThread);
                inventoryThread.start();
            }

            for (int i = 1; i <= services.get("logistics").getAsInt(); i++) {
                LogisticsService logisticsService = new LogisticsService("Logistics Service" + i + "", countDownLatch);
                Thread logisticsThread = new Thread(logisticsService);
                threadList.add(logisticsThread);
                logisticsThread.start();
            }
            for (int i = 1; i <= services.get("resourcesService").getAsInt(); i++) {
                ResourceService resourceService = new ResourceService("Resource Service" + i + "", countDownLatch);
                Thread resourceThread = new Thread(resourceService);
                threadList.add(resourceThread);
                resourceThread.start();
            }


            //INITIALIZING THE CUSTOMERS:

            JsonArray customers = services.get("customers").getAsJsonArray();

            for (int i = 0; i < customers.size(); i++) {
                APIService apiService;
                apiService = new APIService("API Service " + (i + 1) + "", countDownLatch);

                JsonObject customer = customers.get(i).getAsJsonObject();
                int id = customer.get("id").getAsInt();

                String name = customer.get("name").getAsString();
                String address = customer.get("address").getAsString();
                int distance = customer.get("distance").getAsInt();

                JsonObject creditCard = customer.get("creditCard").getAsJsonObject();
                int creditCardNumber = creditCard.get("number").getAsInt();
                int creditCardAmount = creditCard.get("amount").getAsInt();
                JsonArray orderSchedule = customer.get("orderSchedule").getAsJsonArray();
                Customer newCustomer = new Customer(name, id, address, distance, creditCardAmount, creditCardNumber);
                apiService.setCurrCustomer(newCustomer);
                customersHashMap.put(id, newCustomer);
                for (int j = 0; j < orderSchedule.size(); j++) {
                    JsonObject order = orderSchedule.get(j).getAsJsonObject();
                    String bookTitle = order.get("bookTitle").getAsString();
                    int tick = order.get("tick").getAsInt();
                    if (apiService.getTickToBook().get(tick) == null)
                        apiService.getTickToBook().put(tick, new LinkedBlockingQueue<>());
                    apiService.getTickToBook().get(tick).add(inventory.getBookInventory().get(bookTitle));
                }
                Thread apiThread = new Thread(apiService);
                threadList.add(apiThread);
                apiThread.start();
            }

            JsonObject time = services.get("time").getAsJsonObject();

            countDownLatch.await();

            int speed = time.get("speed").getAsInt();
            int duration = time.get("duration").getAsInt();
            TimeService timeService = new TimeService("TimeService", duration, speed);
            Thread timer = new Thread(timeService);
            threadList.add(timer);
            timer.start();
            for (Thread t : threadList) {
                t.join();
            }

        }catch(InterruptedException ex){

        }


        try {
            //Printing into files
            //Inventory
            inventory.printInventoryToFile(args[2]);
            //Order Receipts
            moneyRegister.printOrderReceipts(args[3]);
            //Customers

            FileOutputStream fos =
                    new FileOutputStream(args[1]);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(customersHashMap);
            oos.close();
            fos.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        //MoneyRegister

        try {
            FileOutputStream fos =
                    new FileOutputStream(args[4]);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(MoneyRegister.getInstance());
            oos.close();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


}