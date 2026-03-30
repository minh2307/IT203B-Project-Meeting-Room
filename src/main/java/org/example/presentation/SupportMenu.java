package org.example.presentation;

import org.example.model.Booking;
import org.example.model.User;
import org.example.service.impl.Bookingservice;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class SupportMenu {
    private final Scanner scanner = new Scanner(System.in);
    private final Bookingservice bookingservice = Bookingservice.getInstance();
    private final User currentUser;

    public SupportMenu(User currentUser) {
        this.currentUser = currentUser;
    }

    public void showSupportMenu() {
        while (true) {
            System.out.println("\n===== menu support staff =====");
            System.out.println("1. xem booking duoc phan cong theo ngay");
            System.out.println("2. cap nhat trang thai chuan bi");
            System.out.println("0. dang xuat");
            System.out.print("chon: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    viewAssignedBookingsByDate();
                    break;
                case "2":
                    updatePreparationStatus();
                    break;
                case "0":
                    System.out.println("dang xuat support staff");
                    return;
                default:
                    System.out.println("lua chon khong hop le");
            }
        }
    }

    private void viewAssignedBookingsByDate() {
        try {
            System.out.print("nhap ngay can xem (dd/MM/yyyy): ");
            String input = scanner.nextLine().trim();
            LocalDate workDate = LocalDate.parse(input, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            List<Booking> bookings = bookingservice.getAssignedBookingsBySupport(currentUser.getUserId(), workDate);
            if (bookings == null || bookings.isEmpty()) {
                System.out.println("khong co booking nao duoc phan cong trong ngay nay");
                return;
            }

            System.out.println("\n===== booking duoc phan cong =====");
            for (Booking booking : bookings) {
                System.out.println(
                        "id: " + booking.getBookingId()
                                + " | room_id: " + booking.getRoomId()
                                + " | tieu de: " + booking.getMeetingTitle()
                                + " | bat dau: " + formatDateTime(booking.getStartTime())
                                + " | ket thuc: " + formatDateTime(booking.getEndTime())
                                + " | duyet: " + booking.getBookingStatus()
                                + " | chuan bi: " + safe(booking.getPreparationStatus())
                                + " | ghi chu: " + safe(booking.getNote())
                );
            }
        } catch (Exception e) {
            System.out.println("ngay nhap vao khong hop le");
        }
    }

    private void updatePreparationStatus() {
        try {
            System.out.print("nhap booking id can cap nhat: ");
            int bookingId = Integer.parseInt(scanner.nextLine());

            System.out.print("nhap trang thai moi (preparing/ready/thieu thiet bi): ");
            String preparationStatus = scanner.nextLine();

            boolean result = bookingservice.updatePreparationStatus(
                    bookingId,
                    currentUser.getUserId(),
                    preparationStatus
            );

            if (result) {
                System.out.println("cap nhat trang thai chuan bi thanh cong");
            } else {
                System.out.println("cap nhat trang thai chuan bi that bai");
            }
        } catch (Exception e) {
            System.out.println("du lieu khong hop le");
        }
    }

    private String formatDateTime(java.sql.Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }
        return timestamp.toInstant()
                .atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}