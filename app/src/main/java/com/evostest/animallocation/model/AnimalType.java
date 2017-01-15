package com.evostest.animallocation.model;

import android.os.Parcel;
import android.os.Parcelable;

public enum AnimalType implements Parcelable {
    MAMMAL, BIRD;

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public AnimalType createFromParcel(Parcel in) {
            return AnimalType.values()[in.readInt()];
        }

        public AnimalType[] newArray(int size) {
            return new AnimalType[size];
        }

    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(ordinal());
    }
}

