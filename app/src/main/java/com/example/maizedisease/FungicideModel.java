package com.example.maizedisease;

import android.os.Parcel;
import android.os.Parcelable;

public class FungicideModel implements Parcelable {
    private int imageResource;
    private String name;
    private String description;

    public FungicideModel(int imageResource, String name, String description) {
        this.imageResource = imageResource;
        this.name = name;
        this.description = description;
    }

    protected FungicideModel(Parcel in) {
        imageResource = in.readInt();
        name = in.readString();
        description = in.readString();
    }

    public static final Creator<FungicideModel> CREATOR = new Creator<FungicideModel>() {
        @Override
        public FungicideModel createFromParcel(Parcel in) {
            return new FungicideModel(in);
        }

        @Override
        public FungicideModel[] newArray(int size) {
            return new FungicideModel[size];
        }
    };

    public int getImageResource() {
        return imageResource;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(imageResource);
        dest.writeString(name);
        dest.writeString(description);
    }
}