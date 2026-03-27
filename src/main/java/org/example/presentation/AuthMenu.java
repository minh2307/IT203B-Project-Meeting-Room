package org.example.presentation;

import org.example.model.User;
import org.example.service.impl.Authservice;
import org.example.service.interfaces.IAuthservice;

import java.util.Scanner;

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
        System.out.print("nhap username: ");
        String username = scanner.nextLine();

        System.out.print("nhap ho ten: ");
        String fullName = scanner.nextLine();

        System.out.print("nhap email: ");
        String email = scanner.nextLine();

        System.out.print("nhap so dien thoai: ");
        String phone = scanner.nextLine();

        System.out.print("nhap mat khau: ");
        String password = scanner.nextLine();

        boolean result = authservice.registerEmployee(username, fullName, email, phone, password);

        if (result) {
            System.out.println("dang ky thanh cong");
        } else {
            System.out.println("dang ky that bai");
        }
    }

    private void login() {
        System.out.print("nhap username: ");
        String username = scanner.nextLine();

        System.out.print("nhap mat khau: ");
        String password = scanner.nextLine();

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
}