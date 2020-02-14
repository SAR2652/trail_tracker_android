package com.example.poachernotify;

public class Camera
{
    private int camera_id;
    private int longitude;
    private int latitude;
    private String zone;

    public Camera(int camera_id, int longitude, int latitude, String zone)
    {
        this.camera_id = camera_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.zone = zone;
    }

    public int ret_camera_id()
    {
        return camera_id;
    }

    public int retLong()
    {
        return longitude;
    }

    public int retLat()
    {
        return latitude;
    }

    public String retZone()
    {
        return zone;
    }
}
