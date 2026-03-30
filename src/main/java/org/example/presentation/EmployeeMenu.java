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
        for (Service service : services) {
            System.out.println(
                    "id: " + service.getServiceId()
                            + " | ten dich vu: " + service.getServiceName()
                            + " | don gia: " + (service.getUnitPrice() == null ? "0" : service.getUnitPrice().toPlainString())
                            + " | don vi: " + service.getUnit()
                            + " | trang thai: " + service.getStatus()
            );
        }

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
        for (Booking booking : bookings) {
            System.out.println(
                    "id: " + booking.getBookingId()
                            + " | room_id: " + booking.getRoomId()
                            + " | tieu de: " + booking.getMeetingTitle()
                            + " | start: " + formatTimestampVN(booking.getStartTime())
                            + " | end: " + formatTimestampVN(booking.getEndTime())
                            + " | so nguoi: " + booking.getParticipantCount()
                            + " | trang thai duyet: " + booking.getBookingStatus()
                            + " | ghi chu: " + safe(booking.getNote())
            );
        }
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
}