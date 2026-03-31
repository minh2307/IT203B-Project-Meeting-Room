package org.example.presentation;

import org.example.model.Booking;
import org.example.model.Equipment;
import org.example.model.Room;
import org.example.model.Service;
import org.example.model.User;
import org.example.service.impl.Authservice;
import org.example.service.impl.Bookingservice;
import org.example.service.impl.Equipmentservice;
import org.example.service.impl.Serviceservice;

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
    private final Serviceservice serviceservice = Serviceservice.getInstance();
    private final User currentUser;
    private final Authservice authservice = Authservice.getInstance();

    public EmployeeMenu(User currentUser) {
        this.currentUser = currentUser;
    }

    public void showEmployeeMenu() {
        while (true) {
            System.out.println("\n===== menu employee =====");
            System.out.println("1. xem phong trong theo thoi gian");
            System.out.println("2. dat phong va yeu cau dich vu");
            System.out.println("3. xem danh sach booking cua toi");
            System.out.println("4. huy booking pending");
            System.out.println("5. xem ho so ca nhan");
            System.out.println("6. cap nhat ho so ca nhan");
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
                case "4":
                    cancelPendingBooking();
                    break;
                case "5":
                    viewMyProfile();
                    break;
                case "6":
                    updateMyProfile();
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
            printRoomTable(rooms);
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
            printRoomTable(rooms);

            System.out.print("chon id phong muon dat: ");
            int roomId = Integer.parseInt(scanner.nextLine());

            System.out.print("nhap ghi chu booking: ");
            String note = scanner.nextLine();

            Map<Integer, Integer> equipmentRequests = inputEquipmentRequests();
            Map<Integer, Integer> serviceRequests = inputServiceRequests();

            boolean result = bookingservice.createBooking(
                    currentUser.getUserId(),
                    roomId,
                    meetingTitle,
                    meetingDescription,
                    participantCount,
                    startTime,
                    endTime,
                    note,
                    equipmentRequests,
                    serviceRequests
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

    private Map<Integer, Integer> inputEquipmentRequests() {
        Map<Integer, Integer> equipmentRequests = new LinkedHashMap<>();

        System.out.print("ban co muon muon them thiet bi khong? (y/n): ");
        String chooseEquipment = scanner.nextLine();

        if (!"y".equalsIgnoreCase(chooseEquipment)) {
            return equipmentRequests;
        }

        List<Equipment> equipments = equipmentservice.getAllEquipments();
        if (equipments == null || equipments.isEmpty()) {
            System.out.println("khong co thiet bi nao trong he thong");
            return equipmentRequests;
        }

        System.out.println("\n===== danh sach thiet bi =====");
        printEquipmentTable(equipments);

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

        return equipmentRequests;
    }

    private Map<Integer, Integer> inputServiceRequests() {
        Map<Integer, Integer> serviceRequests = new LinkedHashMap<>();

        System.out.print("ban co muon them dich vu di kem khong? (y/n): ");
        String chooseService = scanner.nextLine();

        if (!"y".equalsIgnoreCase(chooseService)) {
            return serviceRequests;
        }

        List<Service> services = serviceservice.getAllServices();
        if (services == null || services.isEmpty()) {
            System.out.println("khong co dich vu nao trong he thong");
            return serviceRequests;
        }

        System.out.println("\n===== danh sach dich vu =====");
        printServiceTable(services);

        while (true) {
            System.out.print("nhap id dich vu (0 de dung): ");
            int serviceId = Integer.parseInt(scanner.nextLine());
            if (serviceId == 0) {
                break;
            }

            System.out.print("nhap so luong dich vu: ");
            int quantity = Integer.parseInt(scanner.nextLine());

            if (quantity <= 0) {
                System.out.println("so luong phai lon hon 0");
                continue;
            }

            serviceRequests.put(
                    serviceId,
                    serviceRequests.getOrDefault(serviceId, 0) + quantity
            );
        }

        return serviceRequests;
    }

    private void viewMyBookings() {
        List<Booking> bookings = bookingservice.getBookingsByUser(currentUser.getUserId());

        if (bookings.isEmpty()) {
            System.out.println("ban chua co booking nao");
            return;
        }

        System.out.println("\n===== booking cua toi =====");
        printMyBookingTable(bookings);
    }

    private void cancelPendingBooking() {
        try {
            viewMyBookings();

            System.out.print("nhap id booking muon huy: ");
            int bookingId = Integer.parseInt(scanner.nextLine());

            System.out.print("ban co chac chan muon huy booking nay khong? (y/n): ");
            String confirm = scanner.nextLine().trim();

            if (!"y".equalsIgnoreCase(confirm)) {
                System.out.println("da huy thao tac");
                return;
            }

            boolean result = bookingservice.cancelPendingBooking(currentUser.getUserId(), bookingId);
            System.out.println(result ? "huy booking thanh cong" : "huy booking that bai");
        } catch (Exception e) {
            System.out.println("du lieu khong hop le");
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

    private void viewMyProfile() {
        User profile = authservice.getUserProfile(currentUser.getUserId());

        if (profile == null) {
            System.out.println("khong lay duoc ho so ca nhan");
            return;
        }

        System.out.println("\n===== ho so ca nhan =====");
        System.out.println("username: " + profile.getUsername());
        System.out.println("ho ten: " + profile.getFullName());
        System.out.println("email: " + (profile.getEmail() == null ? "" : profile.getEmail()));
        System.out.println("so dien thoai: " + (profile.getPhone() == null ? "" : profile.getPhone()));
        System.out.println("trang thai: " + profile.getStatus());
    }

    private void updateMyProfile() {
        User profile = authservice.getUserProfile(currentUser.getUserId());

        if (profile == null) {
            System.out.println("khong tim thay ho so de cap nhat");
            return;
        }

        System.out.println("\n===== cap nhat ho so ca nhan =====");
        System.out.println("de trong neu muon giu nguyen gia tri cu");

        System.out.print("ho ten moi (hien tai: " + safe(profile.getFullName()) + "): ");
        String fullName = scanner.nextLine();

        System.out.print("email moi (hien tai: " + safe(profile.getEmail()) + "): ");
        String email = scanner.nextLine();

        System.out.print("so dien thoai moi (hien tai: " + safe(profile.getPhone()) + "): ");
        String phone = scanner.nextLine();

        if (fullName.trim().isEmpty()) {
            fullName = profile.getFullName();
        }
        if (email.trim().isEmpty()) {
            email = profile.getEmail();
        }
        if (phone.trim().isEmpty()) {
            phone = profile.getPhone();
        }

        boolean result = authservice.updateUserProfile(currentUser.getUserId(), fullName, email, phone);

        if (result) {
            currentUser.setFullName(fullName);
            currentUser.setEmail(email);
            currentUser.setPhone(phone);
            System.out.println("cap nhat ho so thanh cong");
        } else {
            System.out.println("cap nhat ho so that bai");
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private void printRoomTable(List<Room> rooms) {
        String line = "+----+------------------------------+----------+----------------------+------------------------------+------------------+";
        System.out.println(line);
        System.out.printf("| %-2s | %-28s | %-8s | %-20s | %-28s | %-16s |%n",
                "id", "ten phong", "suc chua", "vi tri", "mo ta", "trang thai");
        System.out.println(line);

        for (Room room : rooms) {
            System.out.printf("| %-2d | %-28s | %-8d | %-20s | %-28s | %-16s |%n",
                    room.getRoomId(),
                    safeText(room.getRoomName(), 28),
                    room.getCapacity(),
                    safeText(room.getLocation(), 20),
                    safeText(room.getDescription(), 28),
                    safeText(room.getStatus(), 16));
        }

        System.out.println(line);
    }

    private void printEquipmentTable(List<Equipment> equipments) {
        String line = "+----+------------------------------+--------------+----------+------------------+";
        System.out.println(line);
        System.out.printf("| %-2s | %-28s | %-12s | %-8s | %-16s |%n",
                "id", "ten thiet bi", "loai", "so luong", "trang thai");
        System.out.println(line);

        for (Equipment equipment : equipments) {
            System.out.printf("| %-2d | %-28s | %-12s | %-8d | %-16s |%n",
                    equipment.getEquipmentId(),
                    safeText(equipment.getEquipmentName(), 28),
                    safeText(equipment.getEquipmentType(), 12),
                    equipment.getQuantity(),
                    safeText(equipment.getStatus(), 16));
        }

        System.out.println(line);
    }

    private void printServiceTable(List<Service> services) {
        String line = "+----+------------------------------+--------------+------------+------------------+";
        System.out.println(line);
        System.out.printf("| %-2s | %-28s | %-12s | %-10s | %-16s |%n",
                "id", "ten dich vu", "don gia", "don vi", "trang thai");
        System.out.println(line);

        for (Service service : services) {
            System.out.printf("| %-2d | %-28s | %-12s | %-10s | %-16s |%n",
                    service.getServiceId(),
                    safeText(service.getServiceName(), 28),
                    service.getUnitPrice() == null ? "0" : service.getUnitPrice().toPlainString(),
                    safeText(service.getUnit(), 10),
                    safeText(service.getStatus(), 16));
        }

        System.out.println(line);
    }

    private void printMyBookingTable(List<Booking> bookings) {
        String line = "+----+---------+-------------------------+------------------+------------------+----------+------------+----------------------+";
        System.out.println(line);
        System.out.printf("| %-2s | %-7s | %-23s | %-16s | %-16s | %-8s | %-10s | %-20s |%n",
                "id", "room_id", "tieu de", "bat dau", "ket thuc", "so nguoi", "duyet", "ghi chu");
        System.out.println(line);

        for (Booking booking : bookings) {
            System.out.printf("| %-2d | %-7d | %-23s | %-16s | %-16s | %-8d | %-10s | %-20s |%n",
                    booking.getBookingId(),
                    booking.getRoomId(),
                    safeText(booking.getMeetingTitle(), 23),
                    formatTimestampVN(booking.getStartTime()),
                    formatTimestampVN(booking.getEndTime()),
                    booking.getParticipantCount(),
                    safeText(safe(booking.getBookingStatus()), 10),
                    safeText(safe(booking.getNote()), 20));
        }

        System.out.println(line);
    }

    private String safeText(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        text = text.trim();
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
}