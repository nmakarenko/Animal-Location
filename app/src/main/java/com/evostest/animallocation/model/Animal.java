package com.evostest.animallocation.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.maps.model.LatLng;

public abstract class Animal implements Parcelable {

    private int id;
    private String name;
    private double latitude, longitude;
    private AnimalType type;

    Animal(int id, String name, AnimalType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public void setLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setLocation(LatLng location) {
        this.latitude = location.latitude;
        this.longitude = location.longitude;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public AnimalType getType() {
        return type;
    }

    public LatLng getLocation() {
        return new LatLng(latitude, longitude);
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(name);
        out.writeDouble(latitude);
        out.writeDouble(longitude);
        out.writeParcelable(type, flags);
    }

    protected Animal(Parcel in) {
        id = in.readInt();
        name = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        try {
            type = in.readParcelable(AnimalType.class.getClassLoader());
        } catch (Exception e) {
            type = AnimalType.BIRD;
        }
    }
}
