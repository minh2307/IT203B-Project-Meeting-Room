package org.example.presentation;

import org.example.model.Equipment;
import org.example.model.Room;
import org.example.service.impl.Adminservice;
import org.example.service.impl.Equipmentservice;
import org.example.service.impl.Roomservice;
import org.example.service.interfaces.IAdminservice;
import org.example.service.interfaces.IEquipmentservice;
import org.example.service.interfaces.IRoomservice;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final Scanner scanner = new Scanner(System.in);
    private final IRoomservice roomservice = Roomservice.getInstance();
    private final IEquipmentservice equipmentservice = Equipmentservice.getInstance();
    private final IAdminservice adminservice = Adminservice.getInstance();

    public void showAdminMenu() {
        while (true) {
            System.out.println("\n===== menu admin =====");
            System.out.println("1. them phong");
            System.out.println("2. sua phong");
            System.out.println("3. xoa phong");
            System.out.println("4. xem danh sach phong");
            System.out.println("5. xem danh sach thiet bi");
            System.out.println("6. cap nhat so luong thiet bi");
            System.out.println("7. tao tai khoan support");
            System.out.println("0. dang xuat");
            System.out.print("chon: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    addRoom();
                    break;
                case "2":
                    updateRoom();
                    break;
                case "3":
                    deleteRoom();
                    break;
                case "4":
                    viewRooms();
                    break;
                case "5":
                    viewEquipments();
                    break;
                case "6":
                    updateEquipmentQuantity();
                    break;
                case "7":
                    createSupportStaff();
                    break;
                case "0":
                    System.out.println("dang xuat admin");
                    return;
                default:
                    System.out.println("lua chon khong hop le");
            }
        }
    }

    private void addRoom() {
        try {
            System.out.print("nhap ten phong: ");
            String roomName = scanner.nextLine();

            System.out.print("nhap suc chua: ");
            int capacity = Integer.parseInt(scanner.nextLine());

            System.out.print("nhap vi tri: ");
            String location = scanner.nextLine();

            System.out.print("nhap mo ta: ");
            String description = scanner.nextLine();

            System.out.print("nhap trang thai (available/maintenance/unavailable): ");
            String status = scanner.nextLine();

            boolean result = roomservice.addRoom(roomName, capacity, location, description, status);
            System.out.println(result ? "them phong thanh cong" : "them phong that bai");
        } catch (Exception e) {
            System.out.println("du lieu khong hop le");
        }
    }

    private void updateRoom() {
        try {
            System.out.print("nhap id phong can sua: ");
            int roomId = Integer.parseInt(scanner.nextLine());

            System.out.print("nhap ten phong moi: ");
            String roomName = scanner.nextLine();

            System.out.print("nhap suc chua moi: ");
            int capacity = Integer.parseInt(scanner.nextLine());

            System.out.print("nhap vi tri moi: ");
            String location = scanner.nextLine();

            System.out.print("nhap mo ta moi: ");
            String description = scanner.nextLine();

            System.out.print("nhap trang thai moi (available/maintenance/unavailable): ");
            String status = scanner.nextLine();

            boolean result = roomservice.updateRoom(roomId, roomName, capacity, location, description, status);
            System.out.println(result ? "sua phong thanh cong" : "sua phong that bai");
        } catch (Exception e) {
            System.out.println("du lieu khong hop le");
        }
    }

    private void deleteRoom() {
        try {
            System.out.print("nhap id phong can xoa: ");
            int roomId = Integer.parseInt(scanner.nextLine());

            boolean result = roomservice.deleteRoom(roomId);
            System.out.println(result ? "xoa phong thanh cong" : "xoa phong that bai");
        } catch (Exception e) {
            System.out.println("du lieu khong hop le");
        }
    }

    private void viewRooms() {
        List<Room> rooms = roomservice.getAllRooms();

        if (rooms == null || rooms.isEmpty()) {
            System.out.println("khong co phong nao");
            return;
        }

        System.out.println("\n===== danh sach phong =====");
        for (Room room : rooms) {
            System.out.println(
                    "id: " + room.getRoomId()
                            + " | ten phong: " + room.getRoomName()
                            + " | suc chua: " + room.getCapacity()
                            + " | vi tri: " + room.getLocation()
                            + " | mo ta: " + room.getDescription()
                            + " | trang thai: " + room.getStatus()
            );
        }
    }

    private void viewEquipments() {
        List<Equipment> equipments = equipmentservice.getAllEquipments();

        if (equipments == null || equipments.isEmpty()) {
            System.out.println("khong co thiet bi nao");
            return;
        }

        System.out.println("\n===== danh sach thiet bi =====");
        for (Equipment equipment : equipments) {
            System.out.println(
                    "id: " + equipment.getEquipmentId()
                            + " | ten thiet bi: " + equipment.getEquipmentName()
                            + " | loai: " + equipment.getEquipmentType()
                            + " | so luong: " + equipment.getQuantity()
                            + " | mo ta: " + equipment.getDescription()
                            + " | trang thai: " + equipment.getStatus()
            );
        }
    }

    private void updateEquipmentQuantity() {
        try {
            System.out.print("nhap id thiet bi: ");
            int equipmentId = Integer.parseInt(scanner.nextLine());

            System.out.print("nhap so luong moi: ");
            int quantity = Integer.parseInt(scanner.nextLine());

            boolean result = equipmentservice.updateQuantity(equipmentId, quantity);
            System.out.println(result ? "cap nhat thanh cong" : "cap nhat that bai");
        } catch (Exception e) {
            System.out.println("du lieu khong hop le");
        }
    }

    private void createSupportStaff() {
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

        boolean result = adminservice.createSupportStaff(username, fullName, email, phone, password);
        System.out.println(result ? "tao tai khoan support thanh cong" : "tao tai khoan that bai");
    }
}