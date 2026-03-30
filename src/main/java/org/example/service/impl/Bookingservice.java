package org.example.service.impl;

import org.example.dao.impl.Bookingdao;
import org.example.dao.impl.Bookingdetaildao;
import org.example.dao.impl.Userdao;
import org.example.model.Booking;
import org.example.model.Bookingdetail;
import org.example.model.Room;
import org.example.model.User;
import org.example.service.interfaces.IBookingservice;
import org.example.util.JDBCConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Bookingservice implements IBookingservice {
    private static Bookingservice instance;

    private final Bookingdao bookingdao;
    private final Bookingdetaildao bookingdetaildao;
    private final Userdao userdao;

    private Bookingservice() {
        this.bookingdao = Bookingdao.getInstance();
        this.bookingdetaildao = Bookingdetaildao.getInstance();
        this.userdao = Userdao.getInstance();
    }

    public static Bookingservice getInstance() {
        if (instance == null) {
            instance = new Bookingservice();
        }
        return instance;
    }

    public Timestamp getCurrentVietnamTime() {
        return Timestamp.from(
                java.time.ZonedDateTime.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh")).toInstant()
        );
    }

    public List<Room> getAvailableRooms(Timestamp startTime, Timestamp endTime, int participantCount) {
        if (startTime == null || endTime == null) {
            System.out.println("thoi gian khong duoc de trong");
            return Collections.emptyList();
        }

        Timestamp nowVN = getCurrentVietnamTime();

        if (startTime.before(nowVN)) {
            System.out.println("thoi gian bat dau khong duoc nho hon thoi gian hien tai cua viet nam");
            return Collections.emptyList();
        }

        if (!startTime.before(endTime)) {
            System.out.println("thoi gian bat dau phai nho hon thoi gian ket thuc");
            return Collections.emptyList();
        }

        if (participantCount <= 0) {
            System.out.println("so nguoi tham gia phai lon hon 0");
            return Collections.emptyList();
        }

        return bookingdao.getAvailableRooms(startTime, endTime, participantCount);
    }

    public boolean createBooking(int userId,
                                 int roomId,
                                 String meetingTitle,
                                 String meetingDescription,
                                 int participantCount,
                                 Timestamp startTime,
                                 Timestamp endTime,
                                 String note,
                                 Map<Integer, Integer> equipmentRequests,
                                 Map<Integer, Integer> serviceRequests) {

        if (userId <= 0) {
            System.out.println("user id khong hop le");
            return false;
        }

        if (roomId <= 0) {
            System.out.println("room id khong hop le");
            return false;
        }

        if (meetingTitle == null || meetingTitle.trim().isEmpty()) {
            System.out.println("ten cuoc hop khong duoc de trong");
            return false;
        }

        if (participantCount <= 0) {
            System.out.println("so nguoi tham gia phai lon hon 0");
            return false;
        }

        if (startTime == null || endTime == null) {
            System.out.println("thoi gian khong hop le");
            return false;
        }

        Timestamp nowVN = getCurrentVietnamTime();

        if (startTime.before(nowVN)) {
            System.out.println("khong duoc dat phong voi thoi gian bat dau nho hon thoi gian hien tai cua viet nam");
            return false;
        }

        if (!startTime.before(endTime)) {
            System.out.println("thoi gian bat dau phai nho hon thoi gian ket thuc");
            return false;
        }

        if (equipmentRequests == null) {
            equipmentRequests = Collections.emptyMap();
        }

        if (serviceRequests == null) {
            serviceRequests = Collections.emptyMap();
        }

        Connection conn = null;
        try {
            conn = JDBCConnection.getConnection();
            conn.setAutoCommit(false);

            Room room = bookingdao.findRoomById(conn, roomId);
            if (room == null) {
                System.out.println("khong tim thay phong");
                conn.rollback();
                return false;
            }

            if (!"available".equalsIgnoreCase(room.getStatus())) {
                System.out.println("phong hien tai khong kha dung");
                conn.rollback();
                return false;
            }

            if (room.getCapacity() < participantCount) {
                System.out.println("so nguoi vuot qua suc chua phong");
                conn.rollback();
                return false;
            }

            boolean conflict = bookingdao.hasTimeConflict(conn, roomId, startTime, endTime);
            if (conflict) {
                System.out.println("phong da bi trung lich");
                conn.rollback();
                return false;
            }

            for (Map.Entry<Integer, Integer> entry : equipmentRequests.entrySet()) {
                int equipmentId = entry.getKey();
                int quantity = entry.getValue();

                if (equipmentId <= 0 || quantity <= 0) {
                    System.out.println("du lieu thiet bi khong hop le");
                    conn.rollback();
                    return false;
                }

                boolean ok = bookingdetaildao.isEquipmentAvailable(conn, equipmentId, quantity);
                if (!ok) {
                    System.out.println("thiet bi id " + equipmentId + " khong kha dung hoac khong du so luong");
                    conn.rollback();
                    return false;
                }
            }

            for (Map.Entry<Integer, Integer> entry : serviceRequests.entrySet()) {
                int serviceId = entry.getKey();
                int quantity = entry.getValue();

                if (serviceId <= 0 || quantity <= 0) {
                    System.out.println("du lieu dich vu khong hop le");
                    conn.rollback();
                    return false;
                }

                BigDecimal unitPrice = bookingdetaildao.getServiceUnitPrice(conn, serviceId);
                if (unitPrice == null) {
                    System.out.println("dich vu id " + serviceId + " khong ton tai hoac khong hoat dong");
                    conn.rollback();
                    return false;
                }
            }

            Booking booking = new Booking(
                    userId,
                    roomId,
                    meetingTitle.trim(),
                    meetingDescription == null ? "" : meetingDescription.trim(),
                    participantCount,
                    startTime,
                    endTime,
                    "pending",
                    note == null ? "" : note.trim()
            );
            booking.setPreparationStatus("preparing");

            int bookingId = bookingdao.addBooking(conn, booking);
            if (bookingId <= 0) {
                System.out.println("tao booking that bai");
                conn.rollback();
                return false;
            }

            for (Map.Entry<Integer, Integer> entry : equipmentRequests.entrySet()) {
                Bookingdetail detail = new Bookingdetail(
                        bookingId,
                        entry.getKey(),
                        null,
                        entry.getValue(),
                        BigDecimal.ZERO,
                        "equipment",
                        "muon them thiet bi"
                );

                boolean added = bookingdetaildao.addBookingDetail(conn, detail);
                if (!added) {
                    System.out.println("tao booking detail thiet bi that bai");
                    conn.rollback();
                    return false;
                }
            }

            for (Map.Entry<Integer, Integer> entry : serviceRequests.entrySet()) {
                BigDecimal unitPrice = bookingdetaildao.getServiceUnitPrice(conn, entry.getKey());

                Bookingdetail detail = new Bookingdetail(
                        bookingId,
                        null,
                        entry.getKey(),
                        entry.getValue(),
                        unitPrice,
                        "service",
                        "yeu cau dich vu"
                );

                boolean added = bookingdetaildao.addBookingDetail(conn, detail);
                if (!added) {
                    System.out.println("tao booking detail dich vu that bai");
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (Exception ex) {
                System.out.println("rollback that bai: " + ex.getMessage());
            }

            System.out.println("loi createBooking: " + e.getMessage());
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (Exception e) {
                System.out.println("dong connection that bai: " + e.getMessage());
            }
        }
    }

    public List<Booking> getBookingsByUser(int userId) {
        if (userId <= 0) {
            System.out.println("user id khong hop le");
            return Collections.emptyList();
        }
        return bookingdao.getBookingsByUser(userId);
    }

    public boolean cancelPendingBooking(int userId, int bookingId) {
        if (userId <= 0) {
            System.out.println("user id khong hop le");
            return false;
        }

        if (bookingId <= 0) {
            System.out.println("booking id khong hop le");
            return false;
        }

        boolean result = bookingdao.cancelPendingBooking(bookingId, userId);
        if (!result) {
            System.out.println("chi duoc huy booking cua chinh ban khi dang o trang thai pending");
        }
        return result;
    }

    public List<Booking> getPendingBookings() {
        return bookingdao.getPendingBookings();
    }

    public boolean approveBooking(int bookingId) {
        if (bookingId <= 0) {
            System.out.println("booking id khong hop le");
            return false;
        }

        Connection conn = null;
        try {
            Booking booking = bookingdao.findBookingById(bookingId);
            if (booking == null) {
                System.out.println("khong tim thay booking");
                return false;
            }

            if (!"pending".equalsIgnoreCase(booking.getBookingStatus())) {
                System.out.println("chi duyet duoc booking dang pending");
                return false;
            }

            conn = JDBCConnection.getConnection();
            conn.setAutoCommit(false);

            boolean conflict = bookingdao.hasApprovedConflict(
                    conn,
                    booking.getBookingId(),
                    booking.getRoomId(),
                    booking.getStartTime(),
                    booking.getEndTime()
            );

            if (conflict) {
                boolean rejected = bookingdao.updateBookingStatus(
                        conn,
                        bookingId,
                        "rejected",
                        "tu dong tu choi do xung dot lich khi duyet"
                );
                if (!rejected) {
                    conn.rollback();
                    return false;
                }
                conn.commit();
                System.out.println("booking da bi tu choi vi xung dot lich");
                return false;
            }

            boolean approved = bookingdao.updateBookingStatus(conn, bookingId, "approved", "booking da duoc admin duyet");
            if (!approved) {
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (Exception ex) {
                System.out.println("rollback that bai: " + ex.getMessage());
            }
            System.out.println("loi approveBooking: " + e.getMessage());
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (Exception e) {
                System.out.println("dong connection that bai: " + e.getMessage());
            }
        }
    }

    public boolean rejectBooking(int bookingId, String rejectReason) {
        if (bookingId <= 0) {
            System.out.println("booking id khong hop le");
            return false;
        }

        Booking booking = bookingdao.findBookingById(bookingId);
        if (booking == null) {
            System.out.println("khong tim thay booking");
            return false;
        }

        if (!"pending".equalsIgnoreCase(booking.getBookingStatus())) {
            System.out.println("chi tu choi duoc booking dang pending");
            return false;
        }

        String note = (rejectReason == null || rejectReason.trim().isEmpty())
                ? "booking bi tu choi"
                : "booking bi tu choi: " + rejectReason.trim();

        Connection conn = null;
        try {
            conn = JDBCConnection.getConnection();
            conn.setAutoCommit(false);

            boolean rejected = bookingdao.updateBookingStatus(conn, bookingId, "rejected", note);
            if (!rejected) {
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (Exception ex) {
                System.out.println("rollback that bai: " + ex.getMessage());
            }
            System.out.println("loi rejectBooking: " + e.getMessage());
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (Exception e) {
                System.out.println("dong connection that bai: " + e.getMessage());
            }
        }
    }

    public boolean assignSupportStaff(int bookingId, int supportStaffId) {
        if (bookingId <= 0) {
            System.out.println("booking id khong hop le");
            return false;
        }

        if (supportStaffId <= 0) {
            System.out.println("support staff id khong hop le");
            return false;
        }

        Booking booking = bookingdao.findBookingById(bookingId);
        if (booking == null) {
            System.out.println("khong tim thay booking");
            return false;
        }

        if (!"approved".equalsIgnoreCase(booking.getBookingStatus())) {
            System.out.println("chi phan cong support cho booking da duyet");
            return false;
        }

        User supportUser = userdao.findById(supportStaffId);
        if (supportUser == null) {
            System.out.println("khong tim thay support staff");
            return false;
        }

        if (!"support".equalsIgnoreCase(supportUser.getRole())) {
            System.out.println("user duoc chon khong phai support staff");
            return false;
        }

        if (!"active".equalsIgnoreCase(supportUser.getStatus())) {
            System.out.println("support staff dang khong hoat dong");
            return false;
        }

        return bookingdao.assignSupportStaff(bookingId, supportStaffId);
    }

    public List<Booking> getAssignedBookingsBySupport(int supportStaffId, LocalDate workDate) {
        if (supportStaffId <= 0) {
            System.out.println("support staff id khong hop le");
            return Collections.emptyList();
        }

        if (workDate == null) {
            System.out.println("ngay lam viec khong hop le");
            return Collections.emptyList();
        }

        return bookingdao.getAssignedBookingsBySupport(supportStaffId, Date.valueOf(workDate));
    }

    public boolean updatePreparationStatus(int bookingId, int supportStaffId, String preparationStatus) {
        if (bookingId <= 0 || supportStaffId <= 0) {
            System.out.println("du lieu cap nhat khong hop le");
            return false;
        }

        String status = normalizePreparationStatus(preparationStatus);
        if (status == null) {
            System.out.println("trang thai chuan bi khong hop le");
            return false;
        }

        boolean result = bookingdao.updatePreparationStatus(bookingId, supportStaffId, status);
        if (!result) {
            System.out.println("chi cap nhat duoc booking da duoc phan cong cho chinh ban");
        }
        return result;
    }

    private String normalizePreparationStatus(String preparationStatus) {
        if (preparationStatus == null) {
            return null;
        }

        String value = preparationStatus.trim().toLowerCase();
        switch (value) {
            case "preparing":
                return "preparing";
            case "ready":
                return "ready";
            case "thieu thiet bi":
            case "thieu_thiet_bi":
            case "missing_equipment":
                return "missing_equipment";
            default:
                return null;
        }
    }

    public List<Booking> getAllBookings() {
        return bookingdao.getAllBookings();
    }
}