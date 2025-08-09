import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

class Product {
    int id;
    String name;
    double price;

    Product(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    void display() {
        System.out.printf("Product ID: %d | Name: %s | Price: Rs.%.2f\n", id, name, price);
    }
}

class CartItem {
    Product product;
    int quantity;

    CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    double getTotalPrice() {
        return product.price * quantity;
    }
}

class Order {
    int orderId;
    List<CartItem> items;
    String customerName;
    String status;
    LocalDate orderDate;
    LocalDate deliveryDate;

    double totalAmount;    
    double discountPercent; 
    double finalAmount;     

    Order(int orderId, List<CartItem> items, String customerName) {
        this.orderId = orderId;
        this.items = items;
        this.customerName = customerName;
        this.status = "Placed";
        this.orderDate = LocalDate.now();
        this.deliveryDate = orderDate.plusDays(5);
        calculateTotals();
    }

    void calculateTotals() {
        totalAmount = 0;
        for (CartItem item : items) {
            totalAmount += item.getTotalPrice();
        }
        if(totalAmount >=30000){
            discountPercent =0.50;
        }
        else if(totalAmount >=10000 && totalAmount <=20000){
            discountPercent =0.30;
        }
        else if (totalAmount >= 5000 && totalAmount <=9000) {
            discountPercent = 0.20;
        } else if (totalAmount >= 4000) {
            discountPercent = 0.15;
        } else if (totalAmount >= 2000) {
            discountPercent = 0.10;
        } else {
            discountPercent = 0;
        }

        finalAmount = totalAmount - (totalAmount * discountPercent);
    }

    void updateStatusAutomatically() {
        long daysPassed = ChronoUnit.DAYS.between(orderDate, LocalDate.now());
        if (daysPassed <= 1) status = "Placed";
        else if (daysPassed <= 3) status = "Shipped";
        else if (daysPassed <= 5) status = "Out for Delivery";
        else status = "Delivered";
    }

    void displayStatus() {
        updateStatusAutomatically();
        System.out.printf("Order ID: %d | Customer: %s | Items: %d | Status: %s | Total: Rs.%.2f | Discount: %.0f%% | Payable: Rs.%.2f | ETA: %s\n",
                orderId, customerName, items.size(), status,
                totalAmount, discountPercent * 100, finalAmount,
                deliveryDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
    }

    void printInvoice() {
        updateStatusAutomatically();
        System.out.println("\n----Invoice-----");
        System.out.println("-------------------");
        System.out.println("Order ID     : " + orderId);
        System.out.println("Customer     : " + customerName);
        for (CartItem item : items) {
            System.out.printf("%s (x%d) - Rs.%.2f\n", item.product.name, item.quantity, item.getTotalPrice());
        }
        System.out.printf("Total Amount : Rs.%.2f\n", totalAmount);
        System.out.printf("Discount     : %.0f%%\n", discountPercent * 100);
        System.out.printf("Amount Payable: Rs.%.2f\n", finalAmount);
        System.out.println("Order Date   : " + orderDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        System.out.println("Delivery ETA : " + deliveryDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        System.out.println("Status       : " + status);
        System.out.println("----------------------");
    }
}

public class CraftCartApp{
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Map<Integer, Product> catalog = new HashMap<>();
        Map<Integer, Order> orders = new HashMap<>();
        List<CartItem> cart = new ArrayList<>(); 
        int orderCounter = 5000;

        catalog.put(101, new Product(101, "wooden craft", 350));
        catalog.put(102, new Product(102, "handmade craft", 220));
        catalog.put(103, new Product(103, "handloom", 499));
        catalog.put(104, new Product(104, "paintings", 299));

        System.out.println(" Welcome to CraftCart Pro");
        System.out.print("Enter username: ");
        String username = sc.nextLine();
        System.out.print("Enter password: ");
        String password = sc.nextLine();
        System.out.println("Login successful. Hello, " + username + "!");

        while (true) {
            System.out.println("\n-----CraftCart Menu-----");
            System.out.println("1. View Product Catalog");
            System.out.println("2. Add Products to Cart");
            System.out.println("3. View & Edit Cart");
            System.out.println("4. Place Order");
            System.out.println("5. Track Order");
            System.out.println("6. Print Invoice");
            System.out.println("7. Exit");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> {
                    System.out.println("\nProduct Catalog:");
                    for (Product p : catalog.values()) {
                        p.display();
                    }
                }
                case 2 -> {
                    boolean adding = true;
                    while (adding) {
                        System.out.print("Enter Product ID to add to cart: ");
                        int pid = sc.nextInt();
                        if (catalog.containsKey(pid)) {
                            System.out.print("Enter quantity: ");
                            int qty = sc.nextInt();

                            
                            boolean found = false;
                            for (CartItem item : cart) {
                                if (item.product.id == pid) {
                                    item.quantity += qty;
                                    System.out.println("Updated " + item.product.name + " to quantity " + item.quantity);
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                cart.add(new CartItem(catalog.get(pid), qty));
                                System.out.println("Added: " + catalog.get(pid).name);
                            }

                            System.out.print("Add another product? (yes/no): ");
                            sc.nextLine();
                            adding = sc.nextLine().equalsIgnoreCase("yes");
                        } else {
                            System.out.println("Product not found.");
                            sc.nextLine();
                        }
                    }
                }
                case 3 -> {
                    if (cart.isEmpty()) {
                        System.out.println("Cart is empty.");
                        break;
                    }

                    System.out.println("\nCurrent Cart Items:");
                    int index = 1;
                    for (CartItem item : cart) {
                        System.out.printf("%d. %s (x%d) - Rs.%.2f\n", index++, item.product.name, item.quantity, item.getTotalPrice());
                    }

                    System.out.print("Do you want to remove any item? (yes/no): ");
                    String removeChoice = sc.nextLine();
                    if (removeChoice.equalsIgnoreCase("yes")) {
                        System.out.print("Enter product ID to remove: ");
                        int removeId = sc.nextInt();
                        sc.nextLine();
                        boolean removed = false;
                        Iterator<CartItem> it = cart.iterator();
                        while (it.hasNext()) {
                            if (it.next().product.id == removeId) {
                                it.remove();
                                System.out.println("Item removed from cart.");
                                removed = true;
                                break;
                            }
                        }
                        if (!removed) {
                            System.out.println("Item not found in cart.");
                        }
                    }
                }
                case 4 -> {
                    if (cart.isEmpty()) {
                        System.out.println("Your cart is empty. Add products first.");
                    } else {
                        Order newOrder = new Order(orderCounter, new ArrayList<>(cart), username);
                        orders.put(orderCounter, newOrder);
                        System.out.println("Order placed! Your Order ID is: " + orderCounter);
                        orderCounter++;
                        cart.clear(); 
                    }
                }
                case 5 -> {
                    System.out.print("Enter your Order ID: ");
                    int oid = sc.nextInt();
                    if (orders.containsKey(oid)) {
                        orders.get(oid).displayStatus();
                    } else {
                        System.out.println("Order not found.");
                    }
                }
                case 6 -> {
                    System.out.print("Enter Order ID for invoice: ");
                    int invId = sc.nextInt();
                    if (orders.containsKey(invId)) {
                        orders.get(invId).printInvoice();
                    } else {
                        System.out.println("No such order.");
                    }
                }
                case 7 -> {
                    System.out.println("Thanks for shopping with CraftCart, " + username + "!");
                    sc.close();
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }
}

