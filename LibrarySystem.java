package Library;

import java.sql.*;
import java.util.Scanner;

public class LibrarySystem {
    public static void main(String[] args) {
        Connection connection = null;
        PreparedStatement bookStatement = null;
        PreparedStatement studentStatement = null;
        ResultSet bookResultSet = null;
        ResultSet studentResultSet = null;

        try {
            // Step 1: Register the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Step 2: Open a connection
            String url = "jdbc:mysql://localhost:3306/mydb";
            String username = "root";
            String password = "root";
            connection = DriverManager.getConnection(url, username, password);

            // Step 3: Prompt user for student ID
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter student ID: ");
            int studentId = scanner.nextInt();

            // Step 4: Retrieve student information
            String studentQuery = "SELECT * FROM students WHERE id = ?";
            studentStatement = connection.prepareStatement(studentQuery);
            studentStatement.setInt(1, studentId);
            studentResultSet = studentStatement.executeQuery();

            if (studentResultSet.next()) {
                String studentName = studentResultSet.getString("name");
                System.out.println("Student ID: " + studentId);
                System.out.println("Name: " + studentName);

                // Step 5: Prompt user for book title and action
                System.out.print("Enter book title: ");
                scanner.nextLine(); // Clear the input buffer
                String bookTitle = scanner.nextLine();

                System.out.print("Enter action (issue/return): ");
                String action = scanner.nextLine();

                // Step 6: Perform the corresponding action (issue or return)
                if (action.equalsIgnoreCase("issue")) {
                    // Issue book
                    String issueQuery = "SELECT * FROM books WHERE title = ? AND is_available = 1";
                    bookStatement = connection.prepareStatement(issueQuery);
                    bookStatement.setString(1, bookTitle);
                    bookResultSet = bookStatement.executeQuery();

                    if (bookResultSet.next()) {
                        int bookId = bookResultSet.getInt("id");
                        System.out.println("Book issued successfully!");
                        System.out.println("Book ID: " + bookId);
                        System.out.println("Title: " + bookTitle);

                        // Update book availability
                        String updateQuery = "UPDATE books SET is_available = 0 WHERE id = ?";
                        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                        updateStatement.setInt(1, bookId);
                        updateStatement.executeUpdate();
                    } else {
                        System.out.println("Book not found or not available for issuing.");
                    }
                } else if (action.equalsIgnoreCase("return")) {
                    // Return book
                    String returnQuery = "SELECT * FROM books WHERE title = ? AND is_available = 0";
                    bookStatement = connection.prepareStatement(returnQuery);
                    bookStatement.setString(1, bookTitle);
                    bookResultSet = bookStatement.executeQuery();

                    if (bookResultSet.next()) {
                        int bookId = bookResultSet.getInt("id");
                        System.out.println("Book returned successfully!");
                        System.out.println("Book ID: " + bookId);
                        System.out.println("Title: " + bookTitle);

                        // Update book availability
                        String updateQuery = "UPDATE books SET is_available = 1 WHERE id = ?";
                        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                        updateStatement.setInt(1, bookId);
                        updateStatement.executeUpdate();
                    } else {
                        System.out.println("Book not found or already returned.");
                    }
                } else {
                    System.out.println("Invalid action. Please enter 'issue' or 'return'.");
                }
            } else {
                System.out.println("Student not found.");
            }

        } catch (ClassNotFoundException e) {
            System.out.println("Error loading JDBC driver: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
        } finally {
            // Step 7: Close the resources
            try {
                if (bookResultSet != null) {
                    bookResultSet.close();
                }
                if (bookStatement != null) {
                    bookStatement.close();
                }
                if (studentResultSet != null) {
                    studentResultSet.close();
                }
                if (studentStatement != null) {
                    studentStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}
