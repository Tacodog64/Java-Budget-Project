package budget;
import java.util.*;
import java.io.*;

public class Main {
    static double balance = 0;

    public static void main(String[] args) {
        // write your code here
        Scanner scanner = new Scanner(System.in);
        

        boolean exit = false;
        
        Map<String, Purchases> hashedPurchases = new HashMap<>();
        hashedPurchases.put("Food", new Purchases());
        hashedPurchases.put("Clothes", new Purchases());
        hashedPurchases.put("Entertainment", new Purchases());
        hashedPurchases.put("Other", new Purchases());
        
        while(!exit) {
            System.out.println("Choose your action:");
            System.out.println("1) Add income");
            System.out.println("2) Add purchase");
            System.out.println("3) Show list of purchases");
            System.out.println("4) Balance");
            System.out.println("5) Save");
            System.out.println("6) Load");
            System.out.println("7) Analyze (Sort)");
            System.out.println("0) Exit");
            
            String choice = scanner.nextLine();
            
            System.out.println();
               
            switch(choice) {
                case "1":
                    System.out.println("Enter income:");
                    balance += Double.parseDouble(scanner.nextLine());
                    System.out.println();
                    System.out.println("Income was added!");
                    break;
                case "2":
                    while (true) {
                        String type = ChooseType(false, scanner);
                        if (type.equals("Back")) {
                            break;
                        }
                        balance = AddPurchase(hashedPurchases, type, scanner, balance);
                    }
                    break;
                case "3": //need to change for types
                    while (true) {
                        String type = ChooseType(true, scanner);
                        if (type.equals("Back")) {
                            break;
                        }
                        PrintPurchases(hashedPurchases, type);
                    }
                    break;
                case "4":
                    System.out.println("Balance: $" + balance);
                    System.out.println();
                    break;
                case "5":
                    Save(hashedPurchases, balance);//save
                    break;
                case "6":
                    Load(hashedPurchases);//load
                    break;
                case "7":
                    while (true) {
                        String answer = ChooseSort(scanner);
                        if (Objects.equals(answer, "4")) {
                            break;
                        }
                        PrintSort(hashedPurchases, answer, scanner);
                    }
                    break;
                case "0":
                    exit = true;
                    break;
                default:
            }  
        }
        
        System.out.println("Bye!");
    }

    private static String ChooseSort(Scanner scanner) {
        System.out.println("How do you want to sort?");
        System.out.println("1) Sort all purchases");
        System.out.println("2) Sort by type");
        System.out.println("3) Sort certain type");
        System.out.println("4) Back");

        String answer = scanner.nextLine();
        System.out.println();

        return answer;
    }

    private static void PrintSort(Map<String, Purchases> hashedPurchases, String answer, Scanner scanner) {
        switch (answer) {
            case "1"://sort all
                SortAll(hashedPurchases);
                break;
            case "2"://sort types
                List<Purchase> sortType = new ArrayList<>();
                sortType.add(new Purchase("Food -", GetTypeTotal(hashedPurchases, "Food")));
                sortType.add(new Purchase("Clothes -", GetTypeTotal(hashedPurchases, "Clothes")));
                sortType.add(new Purchase("Entertainment -", GetTypeTotal(hashedPurchases, "Entertainment")));
                sortType.add(new Purchase("Other -", GetTypeTotal(hashedPurchases, "Other")));
                sortType.sort(Collections.reverseOrder());

                double total = 0;

                for (var item : sortType) {
                    System.out.println(item.ItemAndPrice());
                    total += item.price;
                }

                String output = String.format("Total: $%.2f",total);
                System.out.println(output);
                System.out.println();
                break;
            case "3"://sort certain type
                System.out.println("Choose the type of purchase");
                System.out.println("1) Food");
                System.out.println("2) Clothes");
                System.out.println("3) Entertainment");
                System.out.println("4) Other");

                String type = scanner.nextLine();
                System.out.println();

                switch (type) {
                    case "1":
                        if (ListIsEmpty("Food", hashedPurchases)) {
                            break;
                        }
                        hashedPurchases.get("Food").SortList();
                        PrintPurchases(hashedPurchases, "Food");
                        break;
                    case "2":
                        if (ListIsEmpty("Clothes", hashedPurchases)) {
                            break;
                        }
                        hashedPurchases.get("Clothes").SortList();
                        PrintPurchases(hashedPurchases, "Clothes");
                        break;
                    case "3":
                        if (ListIsEmpty("Entertainment", hashedPurchases)) {
                            break;
                        }
                        hashedPurchases.get("Entertainment").SortList();
                        PrintPurchases(hashedPurchases, "Entertainment");
                        break;
                    case "4":
                        if (ListIsEmpty("Other", hashedPurchases)) {
                            break;
                        }
                        hashedPurchases.get("Other").SortList();
                        PrintPurchases(hashedPurchases, "Other");
                        break;
                    default:
                }

                break;
            default:
        }
    }

    private static boolean ListIsEmpty(String type, Map<String, Purchases> hashedPurchases) {
        if ((long) hashedPurchases.get(type).items.size() == 0) {
            System.out.println("The purchase list is empty!");
            System.out.println();
            return true;
        }
        return false;
    }

    private static double GetTypeTotal(Map<String, Purchases> hashedPurchases, String type) {
        double total = 0;
        for (var item : hashedPurchases.get(type).items) {
            total += item.price;
        }
        return total;
    }

    private static void SortAll(Map<String, Purchases> hashedPurchases) {
        List<Purchase> purchases = new ArrayList<>();

        for (var item : hashedPurchases.values()) {
            purchases.addAll(item.items);
        }
        purchases.sort(Collections.reverseOrder());
        if ((long) purchases.size() == 0) {
            System.out.println("The purchase list is empty!");
            System.out.println();
            return;
        }

        double total = 0;
        System.out.println("All:");
        for (var item : purchases) {
            System.out.println(item.ItemAndPrice());
            total += item.price;
        }
        String output = String.format("Total: $%.2f",total);
        System.out.println(output);

        System.out.println();
    }

    private static void Save(Map<String, Purchases> purchases, double balance) {
        File textFile = new File("purchases.txt");
        try {
            //textFile.createNewFile();
            PrintWriter writer = new PrintWriter(textFile);

            //content of saving
            writer.println(balance);
            SaveType("Food", purchases, writer);
            SaveType("Clothes", purchases, writer);
            SaveType("Entertainment", purchases, writer);
            SaveType("Other", purchases, writer);

            writer.flush();
            writer.close();

            System.out.println("Purchases were saved!");
        }
        catch (IOException e) {
            System.out.println("beans");
        }
    }

    private static void SaveType(String type, Map<String, Purchases> purchases, PrintWriter writer) {
        writer.println("Type: " + type);

        //foreach item in type
        for (Purchase item : purchases.get(type).items) {
            writer.println(item.name);
            writer.println(item.price);
        }
        //writer disconnects
    }

    private static void Load(Map<String, Purchases> purchases) {
        File textFile = new File("purchases.txt");
        //content of loading
        try (Scanner scanner = new Scanner(textFile)) {//somewhere here
            purchases.clear();
            balance = scanner.nextDouble();
            scanner.nextLine();
            String type = scanner.nextLine().split("\\s+")[1];//Food
            type = LoadType(type, purchases, scanner);
            type = LoadType(type, purchases, scanner);
            type = LoadType(type, purchases, scanner);
            LoadType(type, purchases, scanner);
        }
        catch (IOException e) {
            System.out.println("You suck cant even load");
        }

        System.out.println("Purchases were loaded!");
        System.out.println();
    }

    private static String LoadType(String type, Map<String, Purchases> purchases, Scanner scanner) {
        Purchases items = new Purchases();
        purchases.put(type, items);

        while (scanner.hasNext()) {
            String name = scanner.nextLine();
            if (name.contains("Type: ")) {
                return name.split("\\s+")[1];
            }
            double price = scanner.nextDouble();
            scanner.nextLine();

            items.AddItem(new Purchase(name, price));
        }

        return "";
    }

    public static String ChooseType(boolean isPlural, Scanner scanner) {
        System.out.println("Choose the type of purchase" + (isPlural ? "s" : ""));
        System.out.println("1) Food");
        System.out.println("2) Clothes");
        System.out.println("3) Entertainment");
        System.out.println("4) Other");    
        if (isPlural) System.out.println("5) All");
        System.out.println((isPlural ? "6)" : "5)") + " Back");
        
        String choice = scanner.nextLine();
            
        System.out.println();
        
        switch (choice) {
            case "1":
                return "Food";
            case "2":
                return "Clothes";
            case "3":
                return "Entertainment";
            case "4":
                return "Other";
            case "5":
                return (isPlural ? "All" : "Back");
            default: //Back case
                return "Back";
        }
    }
    
    public static double AddPurchase(Map<String, Purchases> purchases, String type, Scanner scanner, double balance) {
        System.out.println("Enter purchase name:");
        String name = scanner.nextLine();
        System.out.println("Enter its price:");
        double price = Double.parseDouble(scanner.nextLine());
        System.out.println();
        
        Purchases items = purchases.get(type);
        items.AddItem(new Purchase(name, price));
        
        return balance - price;
    }
    
    public static void PrintPurchases(Map<String, Purchases> purchases, String type) {
        if(purchases.size() == 0) {
            System.out.println("The purchase list is empty");
        }
        else {
            if (Objects.equals(type, "All")) {
                PrintTotalAll(purchases);
            } else {
                PrintTotal(purchases, type);
            }
        }
        System.out.println();
    }
    
    public static void PrintTotalAll(Map<String, Purchases> purchases) {
        double total = 0;

        System.out.println("All:");

        for(var item : purchases.get("Food").items) {
            System.out.println(item.ItemAndPrice());
            total += item.GetPrice();
        }
        for(var item : purchases.get("Clothes").items) {
            System.out.println(item.ItemAndPrice());
            total += item.GetPrice();
        }
        for(var item : purchases.get("Entertainment").items) {
            System.out.println(item.ItemAndPrice());
            total += item.GetPrice();
        }
        for(var item : purchases.get("Other").items) {
            System.out.println(item.ItemAndPrice());
            total += item.GetPrice();
        }
        String output = String.format("Total: $%.2f",total);
        System.out.println(output);
    }
    
    public static void PrintTotal(Map<String, Purchases> purchases, String type) {
        double total = 0;

        System.out.println(type + ":");
        for(Purchase item : purchases.get(type).GetItems()) {
            System.out.println(item.ItemAndPrice());
            total += item.GetPrice();  
        }
        String output = String.format("Total: $%.2f",total);
        System.out.println(output);
    }
}

class Purchases {
    public List<Purchase> items = new ArrayList<>();
    
    public void AddItem(Purchase item) {
        items.add(item);
    } 
    
    public List<Purchase> GetItems() {
        return items;
    }

    public void SortList(){
        items.sort(Collections.reverseOrder());
    }
}

class Purchase implements Comparable<Purchase> {
    public String name;
    public double price;
    
    public Purchase(String newName, double newPrice) {
        name = newName;
        price = newPrice; 
    }
    
    public String ItemAndPrice() {
        return String.format(name + " $%.2f", price);
    }
    
    public double GetPrice() {
        return price;
    }

    @Override
    public int compareTo(Purchase o)
    {
        return Double.compare(price, o.price);
    }
}
