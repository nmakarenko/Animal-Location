package com.evostest.animallocation.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Bird extends Animal {
    public boolean canFly;

    public Bird(int id, String name, boolean canFly) {
        super(id, name, AnimalType.BIRD);
        this.canFly = canFly;
    }

    public static final Parcelable.Creator<Bird> CREATOR = new Parcelable.Creator<Bird>() {
        public Bird createFromParcel(Parcel in) {
            return new Bird(in);
        }

        public Bird[] newArray(int size) {
            return new Bird[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeByte((byte) (canFly ? 1 : 0));
    }

    private Bird(Parcel in) {
        super(in);
        canFly = in.readByte() != 0;
    }
}
