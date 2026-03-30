package org.example.presentation;

import org.example.model.Booking;
import org.example.model.Equipment;
import org.example.model.Room;
import org.example.model.Service;
import org.example.model.User;
import org.example.service.impl.Adminservice;
import org.example.service.impl.Bookingservice;
import org.example.service.impl.Equipmentservice;
import org.example.service.impl.Roomservice;
import org.example.service.impl.Serviceservice;
import org.example.service.interfaces.IAdminservice;
import org.example.service.interfaces.IEquipmentservice;
import org.example.service.interfaces.IRoomservice;
import org.example.service.interfaces.IServiceservice;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final Scanner scanner = new Scanner(System.in);
    private final IRoomservice roomservice = Roomservice.getInstance();
    private final IEquipmentservice equipmentservice = Equipmentservice.getInstance();
    private final IServiceservice serviceservice = Serviceservice.getInstance();
    private final IAdminservice adminservice = Adminservice.getInstance();
    private final Bookingservice bookingservice = Bookingservice.getInstance();

    public void showAdminMenu() {
        while (true) {
            System.out.println("\n===== menu admin =====");
            System.out.println("1. quan ly phong hop");
            System.out.println("2. quan ly thiet bi di dong");
            System.out.println("3. quan ly dich vu di kem");
            System.out.println("4. tao tai khoan support");
            System.out.println("5. duyet booking va phan cong support");
            System.out.println("0. dang xuat");
            System.out.print("chon: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    showRoomManagementMenu();
                    break;
                case "2":
                    showEquipmentManagementMenu();
                    break;
                case "3":
                    showServiceManagementMenu();
                    break;
                case "4":
                    createSupportStaff();
                    break;
                case "5":
                    showBookingApprovalMenu();
                    break;
                case "0":
                    System.out.println("dang xuat admin");
                    return;
                default:
                    System.out.println("lua chon khong hop le");
            }
        }
    }

    private void showRoomManagementMenu() {
        while (true) {
            System.out.println("\n===== quan ly phong hop =====");
            System.out.println("1. hien thi danh sach phong");
            System.out.println("2. them phong");
            System.out.println("3. sua phong");
            System.out.println("4. xoa phong");
            System.out.println("5. tim kiem phong theo ten");
            System.out.println("0. quay lai");
            System.out.print("chon: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    viewRooms();
                    break;
                case "2":
                    addRoom();
                    break;
                case "3":
                    updateRoom();
                    break;
                case "4":
                    deleteRoom();
                    break;
                case "5":
                    searchRoomsByName();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("lua chon khong hop le");
            }
        }
    }

    private void showEquipmentManagementMenu() {
        while (true) {
            System.out.println("\n===== quan ly thiet bi di dong =====");
            System.out.println("1. hien thi danh sach thiet bi");
            System.out.println("2. them thiet bi");
            System.out.println("3. sua thiet bi");
            System.out.println("4. xoa thiet bi");
            System.out.println("0. quay lai");
            System.out.print("chon: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    viewEquipments();
                    break;
                case "2":
                    addEquipment();
                    break;
                case "3":
                    updateEquipment();
                    break;
                case "4":
                    deleteEquipment();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("lua chon khong hop le");
            }
        }
    }

    private void showServiceManagementMenu() {
        while (true) {
            System.out.println("\n===== quan ly dich vu di kem =====");
            System.out.println("1. hien thi danh sach dich vu");
            System.out.println("2. them dich vu");
            System.out.println("3. sua dich vu");
            System.out.println("4. xoa dich vu");
            System.out.println("0. quay lai");
            System.out.print("chon: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    viewServices();
                    break;
                case "2":
                    addService();
                    break;
                case "3":
                    updateService();
                    break;
                case "4":
                    deleteService();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("lua chon khong hop le");
            }
        }
    }

    private void showBookingApprovalMenu() {
        while (true) {
            System.out.println("\n===== duyet booking va phan cong =====");
            System.out.println("1. xem tat ca booking");
            System.out.println("2. xem danh sach booking pending");
            System.out.println("3. duyet booking");
            System.out.println("4. tu choi booking");
            System.out.println("5. xem danh sach support staff");
            System.out.println("6. phan cong support staff");
            System.out.println("0. quay lai");
            System.out.print("chon: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    viewAllBookings();
                    break;
                case "2":
                    viewPendingBookings();
                    break;
                case "3":
                    approveBooking();
                    break;
                case "4":
                    rejectBooking();
                    break;
                case "5":
                    viewSupportStaffs();
                    break;
                case "6":
                    assignSupportStaff();
                    break;
                case "0":
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

            Room oldRoom = roomservice.findById(roomId);
            if (oldRoom == null) {
                System.out.println("khong tim thay phong");
                return;
            }

            System.out.println("\n===== thong tin phong cu =====");
            printRoomTable(List.of(oldRoom));

            System.out.print("nhap ten phong moi (enter de giu nguyen): ");
            String roomName = scanner.nextLine().trim();
            if (roomName.isEmpty()) {
                roomName = oldRoom.getRoomName();
            }

            System.out.print("nhap suc chua moi (enter de giu nguyen): ");
            String capacityInput = scanner.nextLine().trim();
            int capacity = capacityInput.isEmpty() ? oldRoom.getCapacity() : Integer.parseInt(capacityInput);

            System.out.print("nhap vi tri moi (enter de giu nguyen): ");
            String location = scanner.nextLine().trim();
            if (location.isEmpty()) {
                location = oldRoom.getLocation();
            }

            System.out.print("nhap mo ta moi (enter de giu nguyen): ");
            String description = scanner.nextLine().trim();
            if (description.isEmpty()) {
                description = oldRoom.getDescription();
            }

            System.out.print("nhap trang thai moi (available/maintenance/unavailable, enter de giu nguyen): ");
            String status = scanner.nextLine().trim();
            if (status.isEmpty()) {
                status = oldRoom.getStatus();
            }

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

            Room room = roomservice.findById(roomId);
            if (room == null) {
                System.out.println("khong tim thay phong");
                return;
            }

            System.out.println("\n===== phong sap xoa =====");
            printRoomTable(List.of(room));

            System.out.print("ban co chac chan muon xoa phong nay khong? (y/n): ");
            String confirm = scanner.nextLine().trim();

            if (!"y".equalsIgnoreCase(confirm)) {
                System.out.println("da huy thao tac xoa");
                return;
            }

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
        printRoomTable(rooms);
    }

    private void searchRoomsByName() {
        System.out.print("nhap tu khoa ten phong can tim: ");
        String keyword = scanner.nextLine();

        List<Room> rooms = roomservice.searchRoomsByName(keyword);

        if (rooms == null || rooms.isEmpty()) {
            System.out.println("khong tim thay phong phu hop");
            return;
        }

        System.out.println("\n===== ket qua tim kiem =====");
        printRoomTable(rooms);
    }

    private void addEquipment() {
        try {
            System.out.print("nhap ten thiet bi: ");
            String equipmentName = scanner.nextLine();

            System.out.print("nhap loai thiet bi (mobile/fixed): ");
            String equipmentType = scanner.nextLine();

            System.out.print("nhap so luong: ");
            int quantity = Integer.parseInt(scanner.nextLine());

            System.out.print("nhap mo ta: ");
            String description = scanner.nextLine();

            System.out.print("nhap trang thai (active/maintenance/inactive): ");
            String status = scanner.nextLine();

            boolean result = equipmentservice.addEquipment(
                    equipmentName,
                    equipmentType,
                    quantity,
                    description,
                    status
            );

            System.out.println(result ? "them thiet bi thanh cong" : "them thiet bi that bai");
        } catch (Exception e) {
            System.out.println("du lieu khong hop le");
        }
    }

    private void updateEquipment() {
        try {
            System.out.print("nhap id thiet bi can sua: ");
            int equipmentId = Integer.parseInt(scanner.nextLine());

            Equipment oldEquipment = equipmentservice.findById(equipmentId);
            if (oldEquipment == null) {
                System.out.println("khong tim thay thiet bi");
                return;
            }

            System.out.println("\n===== thong tin thiet bi cu =====");
            printEquipmentTable(List.of(oldEquipment));

            System.out.print("nhap ten thiet bi moi (enter de giu nguyen): ");
            String equipmentName = scanner.nextLine().trim();
            if (equipmentName.isEmpty()) {
                equipmentName = oldEquipment.getEquipmentName();
            }

            System.out.print("nhap loai moi (enter de giu nguyen): ");
            String equipmentType = scanner.nextLine().trim();
            if (equipmentType.isEmpty()) {
                equipmentType = oldEquipment.getEquipmentType();
            }

            System.out.print("nhap so luong moi (enter de giu nguyen): ");
            String quantityInput = scanner.nextLine().trim();
            int quantity = quantityInput.isEmpty() ? oldEquipment.getQuantity() : Integer.parseInt(quantityInput);

            System.out.print("nhap mo ta moi (enter de giu nguyen): ");
            String description = scanner.nextLine().trim();
            if (description.isEmpty()) {
                description = oldEquipment.getDescription();
            }

            System.out.print("nhap trang thai moi (active/maintenance/inactive, enter de giu nguyen): ");
            String status = scanner.nextLine().trim();
            if (status.isEmpty()) {
                status = oldEquipment.getStatus();
            }

            boolean result = equipmentservice.updateEquipment(
                    equipmentId,
                    equipmentName,
                    equipmentType,
                    quantity,
                    description,
                    status
            );

            System.out.println(result ? "sua thiet bi thanh cong" : "sua thiet bi that bai");
        } catch (Exception e) {
            System.out.println("du lieu khong hop le");
        }
    }

    private void deleteEquipment() {
        try {
            System.out.print("nhap id thiet bi can xoa: ");
            int equipmentId = Integer.parseInt(scanner.nextLine());

            Equipment equipment = equipmentservice.findById(equipmentId);
            if (equipment == null) {
                System.out.println("khong tim thay thiet bi");
                return;
            }

            System.out.println("\n===== thiet bi sap xoa =====");
            printEquipmentTable(List.of(equipment));

            System.out.print("ban co chac chan muon xoa thiet bi nay khong? (y/n): ");
            String confirm = scanner.nextLine().trim();

            if (!"y".equalsIgnoreCase(confirm)) {
                System.out.println("da huy thao tac xoa");
                return;
            }

            boolean result = equipmentservice.deleteEquipment(equipmentId);
            System.out.println(result ? "xoa thiet bi thanh cong" : "xoa thiet bi that bai");
        } catch (Exception e) {
            System.out.println("du lieu khong hop le");
        }
    }

    private void viewEquipments() {
        List<Equipment> equipments = equipmentservice.getAllEquipments();

        if (equipments == null || equipments.isEmpty()) {
            System.out.println("khong co thiet bi nao");
            return;
        }

        System.out.println("\n===== danh sach thiet bi =====");
        printEquipmentTable(equipments);
    }

    private void addService() {
        try {
            System.out.print("nhap ten dich vu: ");
            String serviceName = scanner.nextLine();

            System.out.print("nhap don gia: ");
            BigDecimal unitPrice = new BigDecimal(scanner.nextLine().trim());

            System.out.print("nhap don vi tinh: ");
            String unit = scanner.nextLine();

            System.out.print("nhap mo ta: ");
            String description = scanner.nextLine();

            System.out.print("nhap trang thai (active/inactive): ");
            String status = scanner.nextLine();

            boolean result = serviceservice.addService(serviceName, unitPrice, unit, description, status);
            System.out.println(result ? "them dich vu thanh cong" : "them dich vu that bai");
        } catch (Exception e) {
            System.out.println("du lieu khong hop le");
        }
    }

    private void updateService() {
        try {
            System.out.print("nhap id dich vu can sua: ");
            int serviceId = Integer.parseInt(scanner.nextLine());

            Service oldService = serviceservice.findById(serviceId);
            if (oldService == null) {
                System.out.println("khong tim thay dich vu");
                return;
            }

            System.out.println("\n===== thong tin dich vu cu =====");
            printServiceTable(List.of(oldService));

            System.out.print("nhap ten dich vu moi (enter de giu nguyen): ");
            String serviceName = scanner.nextLine().trim();
            if (serviceName.isEmpty()) {
                serviceName = oldService.getServiceName();
            }

            System.out.print("nhap don gia moi (enter de giu nguyen): ");
            String priceInput = scanner.nextLine().trim();
            BigDecimal unitPrice = priceInput.isEmpty() ? oldService.getUnitPrice() : new BigDecimal(priceInput);

            System.out.print("nhap don vi tinh moi (enter de giu nguyen): ");
            String unit = scanner.nextLine().trim();
            if (unit.isEmpty()) {
                unit = oldService.getUnit();
            }

            System.out.print("nhap mo ta moi (enter de giu nguyen): ");
            String description = scanner.nextLine().trim();
            if (description.isEmpty()) {
                description = oldService.getDescription();
            }

            System.out.print("nhap trang thai moi (active/inactive, enter de giu nguyen): ");
            String status = scanner.nextLine().trim();
            if (status.isEmpty()) {
                status = oldService.getStatus();
            }

            boolean result = serviceservice.updateService(serviceId, serviceName, unitPrice, unit, description, status);
            System.out.println(result ? "sua dich vu thanh cong" : "sua dich vu that bai");
        } catch (Exception e) {
            System.out.println("du lieu khong hop le");
        }
    }

    private void deleteService() {
        try {
            System.out.print("nhap id dich vu can xoa: ");
            int serviceId = Integer.parseInt(scanner.nextLine());

            Service service = serviceservice.findById(serviceId);
            if (service == null) {
                System.out.println("khong tim thay dich vu");
                return;
            }

            System.out.println("\n===== dich vu sap xoa =====");
            printServiceTable(List.of(service));

            System.out.print("ban co chac chan muon xoa dich vu nay khong? (y/n): ");
            String confirm = scanner.nextLine().trim();

            if (!"y".equalsIgnoreCase(confirm)) {
                System.out.println("da huy thao tac xoa");
                return;
            }

            boolean result = serviceservice.deleteService(serviceId);
            System.out.println(result ? "xoa dich vu thanh cong" : "xoa dich vu that bai");
        } catch (Exception e) {
            System.out.println("du lieu khong hop le");
        }
    }

    private void viewServices() {
        List<Service> services = serviceservice.getAllServices();

        if (services == null || services.isEmpty()) {
            System.out.println("khong co dich vu nao");
            return;
        }

        System.out.println("\n===== danh sach dich vu =====");
        printServiceTable(services);
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

    private void viewPendingBookings() {
        List<Booking> bookings = bookingservice.getPendingBookings();
        if (bookings == null || bookings.isEmpty()) {
            System.out.println("khong co booking pending nao");
            return;
        }

        System.out.println("\n===== danh sach booking pending =====");
        for (Booking booking : bookings) {
            System.out.println(
                    "id: " + booking.getBookingId()
                            + " | room_id: " + booking.getRoomId()
                            + " | user_id: " + booking.getUserId()
                            + " | tieu de: " + booking.getMeetingTitle()
                            + " | bat dau: " + formatDateTime(booking.getStartTime())
                            + " | ket thuc: " + formatDateTime(booking.getEndTime())
                            + " | so nguoi: " + booking.getParticipantCount()
                            + " | trang thai: " + safe(booking.getBookingStatus())
                            + " | ghi chu: " + safe(booking.getNote())
            );
        }
    }

    private void approveBooking() {
        try {
            viewPendingBookings();
            System.out.print("nhap booking id muon duyet: ");
            int bookingId = Integer.parseInt(scanner.nextLine());

            boolean result = bookingservice.approveBooking(bookingId);
            if (result) {
                System.out.println("duyet booking thanh cong");
            } else {
                System.out.println("duyet booking that bai hoac booking da bi tu choi do xung dot lich");
            }
        } catch (Exception e) {
            System.out.println("du lieu khong hop le");
        }
    }

    private void rejectBooking() {
        try {
            viewPendingBookings();
            System.out.print("nhap booking id muon tu choi: ");
            int bookingId = Integer.parseInt(scanner.nextLine());

            System.out.print("nhap ly do tu choi: ");
            String rejectReason = scanner.nextLine();

            boolean result = bookingservice.rejectBooking(bookingId, rejectReason);
            System.out.println(result ? "tu choi booking thanh cong" : "tu choi booking that bai");
        } catch (Exception e) {
            System.out.println("du lieu khong hop le");
        }
    }

    private void viewSupportStaffs() {
        List<User> supports = adminservice.getSupportStaffs();
        if (supports == null || supports.isEmpty()) {
            System.out.println("khong co support staff nao");
            return;
        }

        System.out.println("\n===== danh sach support staff =====");
        for (User user : supports) {
            System.out.println(
                    "id: " + user.getUserId()
                            + " | username: " + user.getUsername()
                            + " | ho ten: " + user.getFullName()
                            + " | email: " + safe(user.getEmail())
                            + " | sdt: " + safe(user.getPhone())
                            + " | trang thai: " + safe(user.getStatus())
            );
        }
    }

    private void assignSupportStaff() {
        try {
            System.out.print("nhap booking id da duyet can phan cong: ");
            int bookingId = Integer.parseInt(scanner.nextLine());

            viewSupportStaffs();
            System.out.print("nhap support staff id: ");
            int supportStaffId = Integer.parseInt(scanner.nextLine());

            boolean result = bookingservice.assignSupportStaff(bookingId, supportStaffId);
            System.out.println(result ? "phan cong support staff thanh cong" : "phan cong support staff that bai");
        } catch (Exception e) {
            System.out.println("du lieu khong hop le");
        }
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
        String line = "+----+------------------------------+--------------+----------+----------+------------------+";
        System.out.println(line);
        System.out.printf("| %-2s | %-28s | %-12s | %-8s | %-8s | %-16s |%n",
                "id", "ten thiet bi", "loai", "so luong", "kha dung", "trang thai");
        System.out.println(line);

        for (Equipment equipment : equipments) {
            System.out.printf("| %-2d | %-28s | %-12s | %-8d | %-8s | %-16s |%n",
                    equipment.getEquipmentId(),
                    safeText(equipment.getEquipmentName(), 28),
                    safeText(equipment.getEquipmentType(), 12),
                    equipment.getQuantity(),
                    getAvailableText(equipment),
                    safeText(equipment.getStatus(), 16));
        }

        System.out.println(line);
    }

    private void printServiceTable(List<Service> services) {
        String line = "+----+------------------------------+--------------+--------------+------------------------------+------------------+";
        System.out.println(line);
        System.out.printf("| %-2s | %-28s | %-12s | %-12s | %-28s | %-16s |%n",
                "id", "ten dich vu", "don gia", "don vi", "mo ta", "trang thai");
        System.out.println(line);

        for (Service service : services) {
            System.out.printf("| %-2d | %-28s | %-12s | %-12s | %-28s | %-16s |%n",
                    service.getServiceId(),
                    safeText(service.getServiceName(), 28),
                    service.getUnitPrice() == null ? "0" : service.getUnitPrice().toPlainString(),
                    safeText(service.getUnit(), 12),
                    safeText(service.getDescription(), 28),
                    safeText(service.getStatus(), 16));
        }

        System.out.println(line);
    }

    private String getAvailableText(Equipment equipment) {
        boolean available = equipment.getQuantity() > 0
                && ("active".equalsIgnoreCase(equipment.getStatus())
                || "available".equalsIgnoreCase(equipment.getStatus()));
        return available ? "co" : "khong";
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

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String formatDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }
        return timestamp.toInstant()
                .atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    private void viewAllBookings() {
        List<Booking> bookings = bookingservice.getAllBookings();
        if (bookings == null || bookings.isEmpty()) {
            System.out.println("khong co booking nao");
            return;
        }

        System.out.println("\n===== danh sach tat ca booking =====");
        for (Booking booking : bookings) {
            System.out.println(
                    "id: " + booking.getBookingId()
                            + " | user_id: " + booking.getUserId()
                            + " | room_id: " + booking.getRoomId()
                            + " | tieu de: " + booking.getMeetingTitle()
                            + " | bat dau: " + formatDateTime(booking.getStartTime())
                            + " | ket thuc: " + formatDateTime(booking.getEndTime())
                            + " | duyet: " + safe(booking.getBookingStatus())
                            + " | support_id: " + (booking.getAssignedSupportId() == null ? "" : booking.getAssignedSupportId())
                            + " | chuan bi: " + safe(booking.getPreparationStatus())
                            + " | ghi chu: " + safe(booking.getNote())
            );
        }
    }
}