import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Date;

class Book {
    private String isbn;
    private String title;
    private String author;
    private double price;
    private int stock;
    private int sold;
    private int returns;
    private Date purchaseDate;

    // Constructor with all necessary fields
    public Book(String isbn, String title, String author, double price, int stock) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.price = price;
        this.stock = stock;
        this.sold = 0;
        this.returns = 0;
        this.purchaseDate = null;
    }

    // Getter methods
    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public int getSold() {
        return sold;
    }

    public int getReturns() {
        return returns;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    // Methods to manipulate book information
    public void addStock(int quantity) {
        stock += quantity;
    }

    public void sellBook(int quantity) {
        if (quantity <= stock) {
            stock -= quantity;
            sold += quantity;
            purchaseDate = new Date(); // Update purchase date when book is sold
        } else {
            JOptionPane.showMessageDialog(null, "Not enough stock available.");
        }
    }

    public void returnBook(int quantity) {
        if (sold >= quantity) {
            sold -= quantity;
            stock += quantity;
            returns += quantity;
        } else {
            JOptionPane.showMessageDialog(null, "Cannot return more books than sold.");
        }
    }

    public double getTotalPrice(int quantity) {
        return price * quantity;
    }

    public boolean isLowStock() {
        return stock <= 5;  // Low stock threshold (set to 5)
    }

    @Override
    public String toString() {
        return "ISBN: " + isbn + ", Title: " + title + ", Author: " + author + ", Price: $" + price + ", Stock: " + stock + ", Sold: " + sold + ", Returns: " + returns;
    }
}

interface BookStoreOperations {
    void addBook(Book book);
    void listBooks();
    void searchBookByTitle(String title);
    void sellBook(String isbn, int quantity);
    void viewStock(String isbn);
    void returnBook(String isbn, int quantity);
    void reorderBook(String isbn, int quantity);
    void generateSalesReport();
    void generateInventoryReport();
    void showBookWithLowestStock();  // Show book with lowest stock
}

public class BookStore extends JFrame implements BookStoreOperations {
    private HashMap<String, Book> books;
    private JTextArea displayArea;

    public BookStore() {
        books = new HashMap<>();
        
        // Setting up the GUI components
        setTitle("Bookstore Management System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        JPanel panel = new JPanel();
        add(panel, BorderLayout.SOUTH);
        
        // Buttons
        JButton addButton = new JButton("Add Book");
        JButton listButton = new JButton("List Books");
        JButton searchButton = new JButton("Search Book by Title");
        JButton sellButton = new JButton("Sell Book");
        JButton viewStockButton = new JButton("View Stock");
        JButton returnButton = new JButton("Return Book");
        JButton reorderButton = new JButton("Reorder Book");
        JButton generateSalesReportButton = new JButton("Generate Sales Report");
        JButton generateInventoryReportButton = new JButton("Generate Inventory Report");
        JButton showLowStockButton = new JButton("Show Book with Lowest Stock");
        
        panel.add(addButton);
        panel.add(listButton);
        panel.add(searchButton);
        panel.add(sellButton);
        panel.add(viewStockButton);
        panel.add(returnButton);
        panel.add(reorderButton);
        panel.add(generateSalesReportButton);
        panel.add(generateInventoryReportButton);
        panel.add(showLowStockButton);  // Add the "Show Book with Lowest Stock" button
        
        // Button actions
        addButton.addActionListener(e -> addBookGUI());
        listButton.addActionListener(e -> listBooks());
        searchButton.addActionListener(e -> searchBookGUI());
        sellButton.addActionListener(e -> sellBookGUI());
        viewStockButton.addActionListener(e -> viewStockGUI());
        returnButton.addActionListener(e -> returnBookGUI());
        reorderButton.addActionListener(e -> reorderBookGUI());
        generateSalesReportButton.addActionListener(e -> generateSalesReport());
        generateInventoryReportButton.addActionListener(e -> generateInventoryReport());
        showLowStockButton.addActionListener(e -> showBookWithLowestStock());  // Action for showing book with lowest stock
    }

    // Implementing the methods of BookStoreOperations interface
    @Override
    public void addBook(Book book) {
        books.put(book.getIsbn(), book);
    }

    @Override
    public void listBooks() {
        displayArea.setText("");
        if (books.isEmpty()) {
            displayArea.append("No books available.\n");
        } else {
            for (Book book : books.values()) {
                displayArea.append(book.toString() + "\n");
                if (book.isLowStock()) {
                    displayArea.append("Warning: Low stock for this book! Consider reordering.\n");
                }
            }
        }
    }

    @Override
    public void searchBookByTitle(String title) {
        displayArea.setText("");
        boolean found = false;
        for (Book book : books.values()) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                displayArea.append("Book found: " + book + "\n");
                found = true;
                break;
            }
        }
        if (!found) {
            displayArea.append("Book not found.\n");
        }
    }

    @Override
    public void sellBook(String isbn, int quantity) {
        if (books.containsKey(isbn)) {
            Book book = books.get(isbn);
            double totalPrice = book.getTotalPrice(quantity);
            double payment = processPayment(totalPrice);
            if (payment >= totalPrice) {
                book.sellBook(quantity);
                displayArea.append("Sold " + quantity + " copies of Book ISBN: " + isbn + ". Remaining stock: " + book.getStock() + "\n");
                displayArea.append("Total Price: $" + totalPrice + ". Payment received: $" + payment + ". Change: $" + (payment - totalPrice) + "\n");
            } else {
                JOptionPane.showMessageDialog(this, "Insufficient payment.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Book ISBN not found.");
        }
    }

    private double processPayment(double totalPrice) {
        String paymentStr = JOptionPane.showInputDialog("Enter payment amount: ");
        try {
            return Double.parseDouble(paymentStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid amount.");
            return 0;
        }
    }

    @Override
    public void viewStock(String isbn) {
        displayArea.setText("");
        if (books.containsKey(isbn)) {
            displayArea.append("Remaining stock for Book ISBN " + isbn + ": " + books.get(isbn).getStock() + "\n");
        } else {
            displayArea.append("Book ISBN not found.\n");
        }
    }

    @Override
    public void returnBook(String isbn, int quantity) {
        if (books.containsKey(isbn)) {
            Book book = books.get(isbn);
            Date currentDate = new Date();
            if (book.getPurchaseDate() != null) {
                long diffInMillies = currentDate.getTime() - book.getPurchaseDate().getTime();
                long diffInDays = diffInMillies / (24 * 60 * 60 * 1000);  // Convert millis to days
                if (diffInDays <= 5) {
                    book.returnBook(quantity);
                    displayArea.append("Returned " + quantity + " copies of Book ISBN: " + isbn + ". Remaining stock: " + book.getStock() + "\n");
                } else {
                    displayArea.append("Sorry, we can't replace the book. The return period is 5 days.\n");
                }
            } else {
                displayArea.append("The book wasn't purchased yet.\n");
            }
        } else {
            displayArea.append("Book ISBN not found.\n");
        }
    }

    @Override
    public void reorderBook(String isbn, int quantity) {
        if (books.containsKey(isbn)) {
            Book book = books.get(isbn);
            book.addStock(quantity);
            displayArea.append("Reordered " + quantity + " copies of Book ISBN: " + isbn + ". New stock: " + book.getStock() + "\n");
        } else {
            JOptionPane.showMessageDialog(this, "Book ISBN not found.");
        }
    }

    @Override
    public void generateSalesReport() {
        displayArea.setText("");
        int totalSold = 0;
        double totalRevenue = 0;
        for (Book book : books.values()) {
            totalSold += book.getSold();
            totalRevenue += book.getSold() * book.getPrice();
        }
        displayArea.append("Total Books Sold: " + totalSold + "\n");
        displayArea.append("Total Revenue: $" + totalRevenue + "\n");
    }

    @Override
    public void generateInventoryReport() {
        displayArea.setText("");
        int totalBooks = 0;
        for (Book book : books.values()) {
            totalBooks += book.getStock();
        }
        displayArea.append("Total Inventory: " + totalBooks + " books in stock.\n");
    }

    @Override
    public void showBookWithLowestStock() {
        displayArea.setText("");  // Clear the display area

        // Find the book with the lowest stock
        Book lowestStockBook = null;
        for (Book book : books.values()) {
            if (lowestStockBook == null || book.getStock() < lowestStockBook.getStock()) {
                lowestStockBook = book;
            }
        }

        // Display the book with the lowest stock or a message if no books exist
        if (lowestStockBook != null) {
            displayArea.append("Book with Lowest Stock:\n");
            displayArea.append("ISBN: " + lowestStockBook.getIsbn() + "\n");
            displayArea.append("Title: " + lowestStockBook.getTitle() + "\n");
            displayArea.append("Stock: " + lowestStockBook.getStock() + "\n");
        } else {
            displayArea.append("No books available.\n");
        }
    }

    // Methods to display dialogs for each action
    private void addBookGUI() {
        String isbn = JOptionPane.showInputDialog("Enter ISBN: ");
        String title = JOptionPane.showInputDialog("Enter Book Title: ");
        String author = JOptionPane.showInputDialog("Enter Author: ");
        String priceStr = JOptionPane.showInputDialog("Enter Price: ");
        String stockStr = JOptionPane.showInputDialog("Enter Stock Quantity: ");
        
        try {
            double price = Double.parseDouble(priceStr);
            int stock = Integer.parseInt(stockStr);
            Book newBook = new Book(isbn, title, author, price, stock);
            addBook(newBook);
            JOptionPane.showMessageDialog(this, "Book added successfully!");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter valid numbers for price and stock.");
        }
    }

    private void searchBookGUI() {
        String title = JOptionPane.showInputDialog("Enter Book Title to Search: ");
        searchBookByTitle(title);
    }

    private void sellBookGUI() {
        String isbn = JOptionPane.showInputDialog("Enter ISBN of Book to Sell: ");
        String quantityStr = JOptionPane.showInputDialog("Enter Quantity to Sell: ");
        try {
            int quantity = Integer.parseInt(quantityStr);
            sellBook(isbn, quantity);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity. Please enter a valid number.");
        }
    }

    private void viewStockGUI() {
        String isbn = JOptionPane.showInputDialog("Enter ISBN to View Stock: ");
        viewStock(isbn);
    }

    private void returnBookGUI() {
        String isbn = JOptionPane.showInputDialog("Enter ISBN to Return: ");
        String quantityStr = JOptionPane.showInputDialog("Enter Quantity to Return: ");
        try {
            int quantity = Integer.parseInt(quantityStr);
            returnBook(isbn, quantity);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity. Please enter a valid number.");
        }
    }

    private void reorderBookGUI() {
        String isbn = JOptionPane.showInputDialog("Enter ISBN to Reorder: ");
        String quantityStr = JOptionPane.showInputDialog("Enter Quantity to Reorder: ");
        try {
            int quantity = Integer.parseInt(quantityStr);
            reorderBook(isbn, quantity);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity. Please enter a valid number.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BookStore store = new BookStore();
            store.setVisible(true);
        });
    }
}
