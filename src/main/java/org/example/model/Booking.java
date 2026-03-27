package org.example.model;

import java.sql.Timestamp;

public class Booking {
    private int bookingId;
    private int userId;
    private int roomId;
    private String meetingTitle;
    private String meetingDescription;
    private int participantCount;
    private Timestamp startTime;
    private Timestamp endTime;
    private String bookingStatus;
    private String note;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Booking() {
    }

    public Booking(int bookingId, int userId, int roomId, String meetingTitle,
                   String meetingDescription, int participantCount,
                   Timestamp startTime, Timestamp endTime, String bookingStatus,
                   String note, Timestamp createdAt, Timestamp updatedAt) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.roomId = roomId;
        this.meetingTitle = meetingTitle;
        this.meetingDescription = meetingDescription;
        this.participantCount = participantCount;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bookingStatus = bookingStatus;
        this.note = note;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Booking(int userId, int roomId, String meetingTitle,
                   String meetingDescription, int participantCount,
                   Timestamp startTime, Timestamp endTime,
                   String bookingStatus, String note) {
        this.userId = userId;
        this.roomId = roomId;
        this.meetingTitle = meetingTitle;
        this.meetingDescription = meetingDescription;
        this.participantCount = participantCount;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bookingStatus = bookingStatus;
        this.note = note;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getMeetingTitle() {
        return meetingTitle;
    }

    public void setMeetingTitle(String meetingTitle) {
        this.meetingTitle = meetingTitle;
    }

    public String getMeetingDescription() {
        return meetingDescription;
    }

    public void setMeetingDescription(String meetingDescription) {
        this.meetingDescription = meetingDescription;
    }

    public int getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(int participantCount) {
        this.participantCount = participantCount;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}