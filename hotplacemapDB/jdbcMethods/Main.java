package adventuredesign11;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Login login = new Login();

        while (true) {
            System.out.println("Select an option: ");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Delete Account"); // 수정된 부분: 회원탈퇴 옵션 추가
            System.out.println("4. Exit");
            System.out.print("Your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter name: "); // 수정된 부분: name 입력 추가
                    String name = scanner.nextLine();
                    System.out.print("Enter username: ");
                    String regUsername = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String regPassword = scanner.nextLine();

                    boolean isRegistered = login.register(name, regUsername, regPassword); // 수정된 부분: registerUser -> register로 변경
                    if (isRegistered) {
                        System.out.println("User registered successfully!");
                    } else {
                        System.out.println("Registration failed.");
                    }
                    break;
                case 2:
                    System.out.print("Enter username: ");
                    String logUsername = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String logPassword = scanner.nextLine();

                    boolean isLoggedIn = login.login(logUsername, logPassword); // 수정된 부분: loginUser -> login으로 변경
                    if (isLoggedIn) {
                        System.out.println("Login successful!");
                    } else {
                        System.out.println("Invalid username or password.");
                    }
                    break;
                case 3:
                    System.out.print("Enter username: ");
                    String delUsername = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String delPassword = scanner.nextLine();

                    boolean isDeleted = login.deleteUser(delUsername, delPassword); // 수정된 부분: 회원탈퇴 기능 추가
                    if (isDeleted) {
                        System.out.println("Account deleted successfully.");
                    } else {
                        System.out.println("Account deletion failed.");
                    }
                    break;
                case 4:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}

