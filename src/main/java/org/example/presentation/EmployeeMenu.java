package org.example.presentation;

import org.example.model.Booking;
import org.example.model.Equipment;
import org.example.model.Room;
import org.example.model.User;
import org.example.service.impl.Bookingservice;
import org.example.service.impl.Equipmentservice;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class EmployeeMenu {
    private final Scanner scanner = new Scanner(System.in);
    private final Bookingservice bookingservice = Bookingservice.getInstance();
    private final Equipmentservice equipmentservice = Equipmentservice.getInstance();
    private final User currentUser;

    public EmployeeMenu(User currentUser) {
        this.currentUser = currentUser;
    }

    public void showEmployeeMenu() {
        while (true) {
            System.out.println("\n===== menu employee =====");
            System.out.println("1. xem phong trong theo thoi gian");
            System.out.println("2. dat phong hop");
            System.out.println("3. xem danh sach booking cua toi");
            System.out.println("0. dang xuat");
            System.out.print("chon: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    viewAvailableRooms();
                    break;
                case "2":
                    createBooking();
                    break;
                case "3":
                    viewMyBookings();
                    break;
                case "0":
                    System.out.println("dang xuat employee");
                    return;
                default:
                    System.out.println("lua chon khong hop le");
            }
        }
    }

    private void viewAvailableRooms() {
        try {
            Timestamp startTime = inputTimestamp("nhap thoi gian bat dau");
            Timestamp endTime = inputTimestamp("nhap thoi gian ket thuc");

            System.out.print("nhap so nguoi tham gia: ");
            int participantCount = Integer.parseInt(scanner.nextLine());

            List<Room> rooms = bookingservice.getAvailableRooms(startTime, endTime, participantCount);

            if (rooms.isEmpty()) {
                System.out.println("khong co phong trong trong khoang thoi gian nay");
                return;
            }

            System.out.println("\n===== danh sach phong trong =====");
            System.out.println("thoi gian tim kiem: " + formatTimestampVN(startTime) + " -> " + formatTimestampVN(endTime));

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
        } catch (Exception e) {
            System.out.println("du lieu thoi gian hoac so nguoi khong hop le");
        }
    }

    private void createBooking() {
        try {
            System.out.print("nhap ten cuoc hop: ");
            String meetingTitle = scanner.nextLine();

            System.out.print("nhap mo ta cuoc hop: ");
            String meetingDescription = scanner.nextLine();

            System.out.print("nhap so nguoi tham gia: ");
            int participantCount = Integer.parseInt(scanner.nextLine());

            Timestamp startTime = inputTimestamp("nhap thoi gian bat dau");
            Timestamp endTime = inputTimestamp("nhap thoi gian ket thuc");

            List<Room> rooms = bookingservice.getAvailableRooms(startTime, endTime, participantCount);
            if (rooms.isEmpty()) {
                System.out.println("khong co phong trong de dat");
                return;
            }

            System.out.println("\n===== phong trong =====");
            System.out.println("khung gio dat: " + formatTimestampVN(startTime) + " -> " + formatTimestampVN(endTime));

            for (Room room : rooms) {
                System.out.println(
                        "id: " + room.getRoomId()
                                + " | ten phong: " + room.getRoomName()
                                + " | suc chua: " + room.getCapacity()
                                + " | vi tri: " + room.getLocation()
                );
            }

            System.out.print("chon id phong muon dat: ");
            int roomId = Integer.parseInt(scanner.nextLine());

            System.out.print("nhap ghi chu booking: ");
            String note = scanner.nextLine();

            Map<Integer, Integer> equipmentRequests = new LinkedHashMap<>();

            System.out.print("ban co muon muon them thiet bi khong? (y/n): ");
            String chooseEquipment = scanner.nextLine();

            if ("y".equalsIgnoreCase(chooseEquipment)) {
                List<Equipment> equipments = equipmentservice.getAllEquipments();

                if (equipments != null && !equipments.isEmpty()) {
                    System.out.println("\n===== danh sach thiet bi =====");
                    for (Equipment equipment : equipments) {
                        System.out.println(
                                "id: " + equipment.getEquipmentId()
                                        + " | ten thiet bi: " + equipment.getEquipmentName()
                                        + " | loai: " + equipment.getEquipmentType()
                                        + " | so luong: " + equipment.getQuantity()
                                        + " | trang thai: " + equipment.getStatus()
                        );
                    }

                    while (true) {
                        System.out.print("nhap id thiet bi (0 de dung): ");
                        int equipmentId = Integer.parseInt(scanner.nextLine());
                        if (equipmentId == 0) {
                            break;
                        }

                        System.out.print("nhap so luong muon muon: ");
                        int quantity = Integer.parseInt(scanner.nextLine());

                        if (quantity <= 0) {
                            System.out.println("so luong phai lon hon 0");
                            continue;
                        }

                        equipmentRequests.put(
                                equipmentId,
                                equipmentRequests.getOrDefault(equipmentId, 0) + quantity
                        );
                    }
                } else {
                    System.out.println("khong co thiet bi nao trong he thong");
                }
            }

            boolean result = bookingservice.createBooking(
                    currentUser.getUserId(),
                    roomId,
                    meetingTitle,
                    meetingDescription,
                    participantCount,
                    startTime,
                    endTime,
                    note,
                    equipmentRequests
            );

            if (result) {
                System.out.println("dat phong thanh cong, trang thai = pending");
            } else {
                System.out.println("dat phong that bai");
            }
        } catch (Exception e) {
            System.out.println("du lieu khong hop le");
        }
    }

    private void viewMyBookings() {
        List<Booking> bookings = bookingservice.getBookingsByUser(currentUser.getUserId());

        if (bookings.isEmpty()) {
            System.out.println("ban chua co booking nao");
            return;
        }

        System.out.println("\n===== booking cua toi =====");
        for (Booking booking : bookings) {
            System.out.println(
                    "id: " + booking.getBookingId()
                            + " | room_id: " + booking.getRoomId()
                            + " | tieu de: " + booking.getMeetingTitle()
                            + " | start: " + formatTimestampVN(booking.getStartTime())
                            + " | end: " + formatTimestampVN(booking.getEndTime())
                            + " | status: " + booking.getBookingStatus()
            );
        }
    }

    private Timestamp inputTimestamp(String message) {
        System.out.print(message + " (dd/MM/yyyy HH:mm - gio viet nam): ");
        String input = scanner.nextLine().trim();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(input, formatter);

        return Timestamp.from(
                localDateTime.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant()
        );
    }

    private String formatTimestampVN(Timestamp timestamp) {
        return timestamp.toInstant()
                .atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}