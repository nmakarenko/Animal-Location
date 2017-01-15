package com.evostest.animallocation.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Mammal extends Animal {
    public boolean hasMilk;

    public Mammal(int id, String name, boolean hasMilk) {
        super(id, name, AnimalType.MAMMAL);
        this.hasMilk = hasMilk;
    }

    public static final Parcelable.Creator<Mammal> CREATOR = new Parcelable.Creator<Mammal>() {
        public Mammal createFromParcel(Parcel in) {
            return new Mammal(in);
        }

        public Mammal[] newArray(int size) {
            return new Mammal[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeByte((byte) (hasMilk ? 1 : 0));
    }

    private Mammal(Parcel in) {
        super(in);
        hasMilk = in.readByte() != 0;
    }
}
