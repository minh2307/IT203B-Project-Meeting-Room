package org.example.presentation;

import org.example.model.Booking;
import org.example.model.User;
import org.example.service.impl.Bookingservice;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
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
            System.out.println("2. xem tat ca booking duoc phan cong");
            System.out.println("3. cap nhat trang thai chuan bi");
            System.out.println("0. dang xuat");
            System.out.print("chon: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    viewAssignedBookingsByDate();
                    break;
                case "2":
                    viewAllAssignedBookings();
                    break;
                case "3":
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
            System.out.print("nhap ngay booking can cap nhat (dd/MM/yyyy, enter = hom nay): ");
            String inputDate = scanner.nextLine().trim();

            LocalDate workDate;
            if (inputDate.isEmpty()) {
                workDate = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));
            } else {
                workDate = LocalDate.parse(inputDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            }

            List<Booking> bookings = bookingservice.getAssignedBookingsBySupport(currentUser.getUserId(), workDate);
            if (bookings == null || bookings.isEmpty()) {
                System.out.println("khong co booking nao duoc phan cong trong ngay nay");
                return;
            }

            System.out.println("\n===== booking duoc phan cong =====");
            printAssignedBookingTable(bookings);
        } catch (Exception e) {
            System.out.println("ngay nhap vao khong hop le");
        }
    }

    private void updatePreparationStatus() {
        try {
            System.out.print("nhap ngay booking can cap nhat (dd/MM/yyyy, enter = hom nay): ");
            String inputDate = scanner.nextLine().trim();

            LocalDate workDate;
            if (inputDate.isEmpty()) {
                workDate = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));
            } else {
                workDate = LocalDate.parse(inputDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            }

            List<Booking> bookings = bookingservice.getAssignedBookingsBySupport(currentUser.getUserId(), workDate);
            if (bookings == null || bookings.isEmpty()) {
                System.out.println("khong co booking nao duoc phan cong cho ban trong ngay nay");
                return;
            }

            System.out.println("\n===== danh sach booking duoc phan cong cho ban =====");
            printAssignedBookingTable(bookings);

            System.out.print("nhap booking id can cap nhat: ");
            int bookingId = Integer.parseInt(scanner.nextLine().trim());

            boolean found = false;
            for (Booking booking : bookings) {
                if (booking.getBookingId() == bookingId) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                System.out.println("booking id nay khong nam trong danh sach duoc phan cong cho ban");
                return;
            }

            System.out.print("nhap trang thai moi (preparing/ready/thieu thiet bi): ");
            String preparationStatus = scanner.nextLine().trim();

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

        } catch (NumberFormatException e) {
            System.out.println("booking id phai la so");
        } catch (Exception e) {
            System.out.println("du lieu khong hop le");
        }
    }
    private void printAssignedBookingTable(List<Booking> bookings) {
        String line = "+----+---------+-------------------------+------------------+------------------+------------+------------------+";
        System.out.println(line);
        System.out.printf("| %-2s | %-7s | %-23s | %-16s | %-16s | %-10s | %-16s |%n",
                "id", "room_id", "tieu de", "bat dau", "ket thuc", "duyet", "chuan bi");
        System.out.println(line);

        for (Booking booking : bookings) {
            System.out.printf("| %-2d | %-7d | %-23s | %-16s | %-16s | %-10s | %-16s |%n",
                    booking.getBookingId(),
                    booking.getRoomId(),
                    safeText(booking.getMeetingTitle(), 23),
                    formatDateTime(booking.getStartTime()),
                    formatDateTime(booking.getEndTime()),
                    safeText(safe(booking.getBookingStatus()), 10),
                    safeText(formatPreparationStatus(booking.getPreparationStatus()), 16));
        }

        System.out.println(line);
    }

    private String formatPreparationStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return "";
        }

        switch (status.trim().toLowerCase()) {
            case "preparing":
                return "dang chuan bi";
            case "ready":
                return "san sang";
            case "missing_equipment":
                return "thieu thiet bi";
            default:
                return status;
        }
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

    private void viewAllAssignedBookings() {
        List<Booking> bookings = bookingservice.getAllAssignedBookingsBySupport(currentUser.getUserId());

        if (bookings == null || bookings.isEmpty()) {
            System.out.println("khong co booking nao duoc phan cong");
            return;
        }

        System.out.println("\n===== danh sach tat ca booking duoc phan cong cho ban =====");
        printBookingTable(bookings);
    }

    private void printBookingTable(List<Booking> bookings) {
        String line = "+----+---------+-------------------------+------------------+------------------+------------+------------------+";
        System.out.println(line);
        System.out.printf("| %-2s | %-7s | %-23s | %-16s | %-16s | %-10s | %-16s |%n",
                "id", "room_id", "tieu de", "bat dau", "ket thuc", "duyet", "chuan bi");
        System.out.println(line);

        for (Booking booking : bookings) {
            System.out.printf("| %-2d | %-7d | %-23s | %-16s | %-16s | %-10s | %-16s |%n",
                    booking.getBookingId(),
                    booking.getRoomId(),
                    safeText(booking.getMeetingTitle(), 23),
                    formatDateTime(booking.getStartTime()),
                    formatDateTime(booking.getEndTime()),
                    safeText(safe(booking.getBookingStatus()), 10),
                    safeText(formatPreparationStatus(booking.getPreparationStatus()), 16));
        }

        System.out.println(line);
    }

}