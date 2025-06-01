package ExpenseTracker;

import java.io.*;
import java.util.*;
import java.time.*;

class Transaction {
    String type, category;
    double amount;
    int month, year;

    Transaction(String type, String category, double amount, int month, int year) {
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.month = month;
        this.year = year;
    }

    String toCSV() {
        return String.join(",", type, category, String.valueOf(amount), String.valueOf(month), String.valueOf(year));
    }

    static Transaction fromCSV(String line) {
        String[] p = line.split(",");
        return new Transaction(p[0], p[1], Double.parseDouble(p[2]), Integer.parseInt(p[3]), Integer.parseInt(p[4]));
    }
}

public class ExpenseTracker {
    List<Transaction> transactions = new ArrayList<>();
    Scanner sc = new Scanner(System.in);

    final String[] incomeCategories = {"Salary", "Business"};
    final String[] expenseCategories = {"Food", "Rent", "Travel"};

    void addTransaction(String type) {
        String[] categories = type.equals("Income") ? incomeCategories : expenseCategories;

        System.out.println("Select " + type + " category:");
        for (int i = 0; i < categories.length; i++) {
            System.out.println((i + 1) + ". " + categories[i]);
        }
        System.out.print("Enter choice (1-" + categories.length + "): ");
        int catChoice = sc.nextInt();
        sc.nextLine();

        if (catChoice < 1 || catChoice > categories.length) {
            System.out.println("Invalid category choice!");
            return;
        }
        String category = categories[catChoice - 1];

        System.out.print("Enter Amount: ₹");
        double amount = sc.nextDouble();
        sc.nextLine();

        System.out.print("Enter Date (dd-MM-yyyy): ");
        String dateStr = sc.nextLine();
        LocalDate date;
        try {
            date = LocalDate.parse(dateStr, java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        } catch (Exception e) {
            System.out.println("Invalid date format!");
            return;
        }

        int month = date.getMonthValue();
        int year = date.getYear();

        transactions.add(new Transaction(type, category, amount, month, year));
        System.out.println(type + " added!\n");
    }

    void showSummary(int month, int year) {
        double income = 0, expense = 0;
        Map<String, Double> incomeMap = new HashMap<>(), expenseMap = new HashMap<>();

        for (Transaction t : transactions)
            if (t.month == month && t.year == year)
                if (t.type.equals("Income"))
                    incomeMap.merge(t.category, t.amount, Double::sum);
                else
                    expenseMap.merge(t.category, t.amount, Double::sum);

        income = incomeMap.values().stream().mapToDouble(Double::doubleValue).sum();
        expense = expenseMap.values().stream().mapToDouble(Double::doubleValue).sum();

        System.out.println("\n--- Monthly Summary for " + Month.of(month) + " " + year + " ---");
        System.out.println("Income:");
        if (incomeMap.isEmpty()) System.out.println("  No income recorded.");
        else incomeMap.forEach((k, v) -> System.out.println("  " + k + ": ₹" + v));
        System.out.println("Total Income: ₹" + income);

        System.out.println("\nExpenses:");
        if (expenseMap.isEmpty()) System.out.println("  No expenses recorded.");
        else expenseMap.forEach((k, v) -> System.out.println("  " + k + ": ₹" + v));
        System.out.println("Total Expenses: ₹" + expense);

        System.out.println("\nNet Savings: ₹" + (income - expense) + "\n");
    }

    void saveToCSV(String file) {
        try (PrintWriter w = new PrintWriter(file)) {
            w.println("Type,Category,Amount,Month,Year");
            transactions.forEach(t -> w.println(t.toCSV()));
            System.out.println("Data saved to " + file);
        } catch (IOException e) {
            System.out.println("Error saving: " + e);
        }
    }

    void loadFromCSV(String file) {
        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            transactions.clear();
            r.readLine();
            r.lines().map(Transaction::fromCSV).forEach(transactions::add);
            System.out.println("Data loaded from " + file);
        } catch (IOException e) {
            System.out.println("Error loading: " + e);
        }
    }

    public static void main(String[] args) {
        ExpenseTracker et = new ExpenseTracker();
        Scanner sc = et.sc;

        while (true) {
            System.out.println("\n--- Expense Tracker ---");
            System.out.println("1. Add Income");
            System.out.println("2. Add Expense");
            System.out.println("3. Show Monthly Summary");
            System.out.println("4. Save to CSV File");
            System.out.println("5. Load from CSV File");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");
            int ch = sc.nextInt();
            sc.nextLine();

            switch (ch) {
                case 1:
                    et.addTransaction("Income");
                    break;
                case 2:
                    et.addTransaction("Expense");
                    break;
                case 3:
                    System.out.print("Enter Month and Year (MM-yyyy): ");
                    String monthYear = sc.nextLine();
                    try {
                        java.time.YearMonth ym = java.time.YearMonth.parse(monthYear, java.time.format.DateTimeFormatter.ofPattern("MM-yyyy"));
                        et.showSummary(ym.getMonthValue(), ym.getYear());
                    } catch (Exception e) {
                        System.out.println("Invalid format!");
                    }
                    break;
                case 4:
                    System.out.print("Enter filename to save: ");
                    String fSave = sc.nextLine();
                    et.saveToCSV(fSave);
                    break;
                case 5:
                    System.out.print("Enter filename to load: ");
                    String fLoad = sc.nextLine();
                    et.loadFromCSV(fLoad);
                    break;
                case 6:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }
}
