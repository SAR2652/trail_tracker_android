package com.example.poachernotify;

public class Camera
{
    private int camera_id, longitude, latitude;
    private String zone, camera_ip, status;

    public Camera(int camera_id, int longitude, int latitude, String zone, String camera_ip, String status)
    {
        this.camera_id = camera_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.zone = zone;
        this.camera_ip = camera_ip;
        this.status = status;
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

    public String retCameraIP() {return camera_ip;}

    public String retStatus() {return status;}
}
