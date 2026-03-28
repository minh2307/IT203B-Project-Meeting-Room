package org.example.presentation;

import org.example.model.User;
import org.example.service.impl.Authservice;
import org.example.service.interfaces.IAuthservice;

import java.util.Scanner;
import java.io.IOException;
import java.io.Console;

public class AuthMenu {
    private final Scanner scanner = new Scanner(System.in);
    private final IAuthservice authservice = Authservice.getInstance();

    public void showMainMenu() {
        while (true) {
            System.out.println("\n===== he thong quan ly phong hop =====");
            System.out.println("1. dang ky employee");
            System.out.println("2. dang nhap");
            System.out.println("0. thoat");
            System.out.print("chon: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    registerEmployee();
                    break;
                case "2":
                    login();
                    break;
                case "0":
                    System.out.println("thoat chuong trinh");
                    return;
                default:
                    System.out.println("lua chon khong hop le");
            }
        }
    }

    private void registerEmployee() {
        System.out.println("\n====Dang ky emloyee ====");
        System.out.print("nhap username: ");
        String username = scanner.nextLine();

        System.out.print("nhap ho ten: ");
        String fullName = scanner.nextLine();

        System.out.print("nhap email: ");
        String email = scanner.nextLine();

        System.out.print("nhap so dien thoai: ");
        String phone = scanner.nextLine();

        String password = readPasswordWithMask("nhap mat khau: ");


        boolean result = authservice.registerEmployee(username, fullName, email, phone, password);

        if (result) {
            System.out.println("dang ky thanh cong");
        } else {
            System.out.println("dang ky that bai");
        }
    }

    private void login() {
        System.out.println("\n===== dang nhap =====");
        System.out.print("nhap username: ");
        String username = scanner.nextLine();

        String password = readPasswordWithMask("nhap mat khau: ");

        User user = authservice.login(username, password);

        if (user == null) {
            System.out.println("dang nhap that bai");
            return;
        }

        System.out.println("dang nhap thanh cong");
        System.out.println("xin chao: " + user.getFullName());
        System.out.println("vai tro: " + user.getRole());

        if ("admin".equalsIgnoreCase(user.getRole())) {
            AdminMenu adminMenu = new AdminMenu();
            adminMenu.showAdminMenu();
        } else if ("employee".equalsIgnoreCase(user.getRole())) {
            EmployeeMenu employeeMenu = new EmployeeMenu(user);
            employeeMenu.showEmployeeMenu();
        } else if ("support".equalsIgnoreCase(user.getRole())) {
            System.out.println("menu support se lam o ngay sau");
        } else {
            System.out.println("vai tro khong hop le");
        }
    }

    private String readPasswordWithMask(String message) {
        System.out.print(message);
        StringBuilder password = new StringBuilder();

        try {
            while (true) {
                int ch = System.in.read();

                if (ch == -1 || ch == '\n') {
                    break;
                }

                if (ch == '\r') {
                    int next = System.in.read();
                    if (next != '\n' && next != -1) {
                        // bo qua
                    }
                    break;
                }

                if (ch == 8 || ch == 127) {
                    if (password.length() > 0) {
                        password.deleteCharAt(password.length() - 1);
                        System.out.print("\b \b");
                    }
                } else {
                    password.append((char) ch);
                    System.out.print("*");
                }
            }
        } catch (IOException e) {
            return scanner.nextLine();
        }

        System.out.println();
        return password.toString();
    }

    private String readPasswordHidden(String message) {
        Console console = System.console();

        if (console == null) {
            System.out.println("khong tim thay system console");
            System.out.println("hay chay bang Terminal, cmd hoac PowerShell");
            return "";
        }

        char[] passwordChars = console.readPassword(message);
        return passwordChars == null ? "" : new String(passwordChars);
    }
}