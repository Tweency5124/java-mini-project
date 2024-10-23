package ac.adit.pwj.miniproject.library;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

class User {
    protected String id;
    protected String name;

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }
}

class Librarian extends User {

    public Librarian(String id, String name) {
        super(id, name);
    }
}

class Member extends User {
    private String membershipType;
    private String contact;

    public Member(String id, String name, String membershipType, String contact) {
        super(id, name);
        this.membershipType = membershipType;
        this.contact = contact;
    }

    public String getMembershipType() {
        return membershipType;
    }

    public String getContact() {
        return contact;
    }
}

class BookInventory {
    private Map<String, Integer> books = new HashMap<>();

    public void addBook(String bookTitle, int quantity) {
        books.put(bookTitle, quantity);
    }

    public void issueBook(String bookTitle, int quantity) {
        if (books.containsKey(bookTitle)) {
            int currentStock = books.get(bookTitle);
            if (currentStock >= quantity) {
                books.put(bookTitle, currentStock - quantity);
                System.out.println(quantity + " copy/copies of " + bookTitle + " issued. Remaining stock: " + (currentStock - quantity));
            } else {
                System.out.println("Not enough stock of " + bookTitle);
            }
        } else {
            System.out.println("Book " + bookTitle + " not available.");
        }
    }

    public void showInventory() {
        System.out.println("Book Inventory:");
        for (Map.Entry<String, Integer> entry : books.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " copies available");
        }
    }
}

class BookIssue {
    private Librarian librarian;
    private Member member;
    private String bookTitle;
    private Date issueDate;
    private Date returnDate;

    public BookIssue(Librarian librarian, Member member, String bookTitle, Date issueDate, Date returnDate) {
        this.librarian = librarian;
        this.member = member;
        this.bookTitle = bookTitle;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
    }

    public void saveIssue() throws IOException {
        try (FileWriter writer = new FileWriter("book_issues.txt", true)) {
            writer.write("Book Issue: Librarian " + librarian.name + 
                         ", Member " + member.name + " (Membership: " + member.getMembershipType() + 
                         ", Contact: " + member.getContact() + 
                         "), Book: " + bookTitle + 
                         ", Issue Date: " + new SimpleDateFormat("dd/MM/yyyy").format(issueDate) + 
                         ", Return Date: " + new SimpleDateFormat("dd/MM/yyyy").format(returnDate) + "\n");
        }
    }

    public void sendReturnReminder() {
        System.out.println("Reminder: Book " + bookTitle + " issued to " + member.name + " should be returned by " 
                + new SimpleDateFormat("dd/MM/yyyy").format(returnDate));
    }
}

public class LibraryManagementSystem {
    private static BookInventory bookInventory = new BookInventory();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Add some books to the inventory
        bookInventory.addBook("To Kill a Mockingbird", 5);
        bookInventory.addBook("1984", 10);

        // Get librarian details
        System.out.println("Enter Librarian ID: ");
        String librarianId = scanner.nextLine();
        System.out.println("Enter Librarian Name: ");
        String librarianName = scanner.nextLine();

        Librarian librarian = new Librarian(librarianId, librarianName);

        // Get member details
        System.out.println("Enter Member ID: ");
        String memberId = scanner.nextLine();
        System.out.println("Enter Member Name: ");
        String memberName = scanner.nextLine();
        System.out.println("Enter Membership Type: ");
        String membershipType = scanner.nextLine();
        System.out.println("Enter Contact: ");
        String contact = scanner.nextLine();

        Member member = new Member(memberId, memberName, membershipType, contact);

        // Get book issue details
        System.out.println("Enter Book Title: ");
        String bookTitle = scanner.nextLine();
        System.out.println("Enter Issue Date (dd/MM/yyyy): ");
        String issueDateStr = scanner.nextLine();
        Date issueDate = null;
        try {
            issueDate = new SimpleDateFormat("dd/MM/yyyy").parse(issueDateStr);
        } catch (Exception e) {
            System.out.println("Invalid date format. Please try again.");
            return;
        }

        // Get return date
        System.out.println("Enter Return Date (dd/MM/yyyy): ");
        String returnDateStr = scanner.nextLine();
        Date returnDate = null;
        try {
            returnDate = new SimpleDateFormat("dd/MM/yyyy").parse(returnDateStr);
        } catch (Exception e) {
            System.out.println("Invalid date format. Please try again.");
            return;
        }

        // Issue book
        BookIssue bookIssue = new BookIssue(librarian, member, bookTitle, issueDate, returnDate);
        try {
            bookIssue.saveIssue();
            System.out.println("Book issue successfully recorded.");
        } catch (IOException e) {
            System.out.println("Error saving book issue: " + e.getMessage());
        }

        // Send return reminder
        bookIssue.sendReturnReminder();

        // Issue books
        System.out.println("\nDo you want to issue more books? (yes/no)");
        String issueChoice = scanner.nextLine();
        if (issueChoice.equalsIgnoreCase("yes")) {
            System.out.println("Enter Book Title: ");
            String title = scanner.nextLine();
            System.out.println("Enter Quantity to Issue: ");
            int quantity = scanner.nextInt();
            bookInventory.issueBook(title, quantity);
        }

        // Show book inventory
        bookInventory.showInventory();

        scanner.close();
    }
}
