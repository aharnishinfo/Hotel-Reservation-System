package Project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Code_Alpha_Task4 {
    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String username = "root";  

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Error loading MySQL Driver: " + e.getMessage());
        }

        try {
          
            System.out.println("Enter the password for MySQL: ");
            Scanner sc = new Scanner(System.in);
            String pass = sc.next();
            System.out.println();

           
            Connection conn = DriverManager.getConnection(url, username, pass);
            Statement statement = conn.createStatement();
            System.out.println("Connection established! Welcome to the Hotel Reservation System.");
            
            while (true) {
                System.out.println("\nHOTEL RESERVATION SYSTEM");
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservations");
                System.out.println("5. Delete Reservations");
                System.out.println("0. Exit");
                System.out.print("Choose an option: ");
                
                int choice = sc.nextInt();

                switch (choice) {
                    case 1:
                        reserveRoom(conn, statement, sc);
                        break;
                    case 2:
                        viewReservations(conn, statement);
                        break;
                    case 3:
                        getRoomNumber(conn, statement, sc);
                        break;
                    case 4:
                        updateReservation(conn, statement, sc);
                        break;
                    case 5:
                        deleteReservation(conn, statement, sc);
                        break;
                    case 0:
                        exit();
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error establishing connection: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void reserveRoom(Connection conn, Statement statement, Scanner sc) {
        System.out.print("Enter guest name: ");
        String guestName = sc.next();
        System.out.print("Enter room number: ");
        int roomNumber = sc.nextInt();
        System.out.print("Enter guest's contact number: ");
        String contactNumber = sc.next();

        String sql = "INSERT INTO reservations (guest_name, room_number, contact_number) " +
                "VALUES ('" + guestName + "'," + roomNumber + ",'" + contactNumber + "')";

        try {
            int affectedRows = statement.executeUpdate(sql);
            if (affectedRows > 0) {
                System.out.println("Reservation successful.");
            } else {
                System.out.println("Reservation failed.");
            }
        } catch (SQLException e) {
            System.out.println("Error making reservation: " + e.getMessage());
        }
    }

    public static void viewReservations(Connection conn, Statement statement) {
        String sql = "SELECT * FROM reservations";
        try {
            ResultSet result = statement.executeQuery(sql);
            System.out.println("Current Reservations:");
            System.out.println("-------------------------------------------------------------------------------------");
            System.out.println("Reservation ID | Guest Name | Room Number | Contact Number | Reservation Date");
            System.out.println("-------------------------------------------------------------------------------------");
            while (result.next()) {
                int reservationId = result.getInt("reservation_id");
                String guestName = result.getString("guest_name");
                int roomNumber = result.getInt("room_number");
                String contactNumber = result.getString("contact_number");
                String reservationDate = result.getString("reservation_date");

                System.out.println(reservationId + "              | " + guestName + "      | " + roomNumber + "       | " + contactNumber + "        | " + reservationDate);
            }
            System.out.println("-------------------------------------------------------------------------------------");
        } catch (SQLException e) {
            System.out.println("Error viewing reservations: " + e.getMessage());
        }
    }

    public static void getRoomNumber(Connection conn, Statement statement, Scanner sc) {
        System.out.print("Enter Reservation ID: ");
        int reservationId = sc.nextInt();
        String sql = "SELECT room_number, guest_name FROM reservations WHERE reservation_id = " + reservationId;
        try {
            ResultSet result = statement.executeQuery(sql);
            if (result.next()) {
                int roomNumber = result.getInt("room_number");
                String guestName = result.getString("guest_name");
                System.out.println("Room number for reservation ID " + reservationId + " (Guest: " + guestName + "): " + roomNumber);
            } else {
                System.out.println("Reservation not found for given ID.");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving room number: " + e.getMessage());
        }
    }

    public static void updateReservation(Connection conn, Statement statement, Scanner sc) {
        System.out.print("Enter Reservation ID to update: ");
        int reservationId = sc.nextInt();
        sc.nextLine();  // Consume newline left-over

        if (!reservationExists(conn, statement, reservationId)) {
            System.out.println("Reservation not found.");
            return;
        }

        System.out.print("Enter new guest name: ");
        String newGuestName = sc.nextLine();
        System.out.print("Enter new room number: ");
        int newRoomNumber = sc.nextInt();
        System.out.print("Enter new contact number: ");
        String newContactNumber = sc.next();

        String sql = "UPDATE reservations SET guest_name = '" + newGuestName + "', room_number = " + newRoomNumber +
                ", contact_number = '" + newContactNumber + "' WHERE reservation_id = " + reservationId;

        try {
            int affectedRows = statement.executeUpdate(sql);
            if (affectedRows > 0) {
                System.out.println("Reservation updated successfully.");
            } else {
                System.out.println("Reservation update failed.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating reservation: " + e.getMessage());
        }
    }

    public static void deleteReservation(Connection conn, Statement statement, Scanner sc) {
        System.out.print("Enter Reservation ID to delete: ");
        int reservationId = sc.nextInt();

        if (!reservationExists(conn, statement, reservationId)) {
            System.out.println("Reservation not found.");
            return;
        }

        String sql = "DELETE FROM reservations WHERE reservation_id = " + reservationId;
        try {
            int affectedRows = statement.executeUpdate(sql);
            if (affectedRows > 0) {
                System.out.println("Reservation deleted successfully.");
            } else {
                System.out.println("Reservation deletion failed.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting reservation: " + e.getMessage());
        }
    }

    private static boolean reservationExists(Connection conn, Statement statement, int reservationId) {
        String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = " + reservationId;
        try {
            ResultSet result = statement.executeQuery(sql);
            return result.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void exit() throws InterruptedException {
        System.out.print("Exiting system");
        for (int i = 5; i > 0; i--) {
            System.out.print(".");
            Thread.sleep(300);
        }
        System.out.println("\nThank you for using our system.");
    }
}
