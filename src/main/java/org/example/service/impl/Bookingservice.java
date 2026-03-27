package org.example.service.impl;

import org.example.dao.impl.Bookingdao;
import org.example.dao.impl.Bookingdetaildao;
import org.example.model.Booking;
import org.example.model.Bookingdetail;
import org.example.model.Room;
import org.example.service.interfaces.IBookingservice;
import org.example.util.JDBCConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Bookingservice implements IBookingservice {
    private static Bookingservice instance;

    private final Bookingdao bookingdao;
    private final Bookingdetaildao bookingdetaildao;

    private Bookingservice() {
        this.bookingdao = Bookingdao.getInstance();
        this.bookingdetaildao = Bookingdetaildao.getInstance();
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
                                 Map<Integer, Integer> equipmentRequests) {

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

        Connection conn = null;
        try {
            conn = JDBCConnection.getInstance().getConnection();
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
                    System.out.println("tao booking detail that bai");
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
}