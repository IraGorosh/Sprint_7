package ru.yandex.praktikum.model;

public class OrderTrack {
    private int trackId;

    public OrderTrack(int trackId) {
        this.trackId = trackId;
    }

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    @Override
    public String toString() {
        return "OrderTrack{" +
                "trackId='" + trackId +
                '}';
    }
}
