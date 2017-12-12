package com.soumit.firebaseauthentication.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by SOUMIT on 10/20/2017.
 */

public class Doctor implements Parcelable{

    public String userid;
    public String name;
    public String email;
    public String address;
    public String qualifications;
    public String visitingHours;
    public String verificationCode;
    public String imageUrl;
    public String time;
    public String tag;
    public ArrayList<String> registeredPatients = new ArrayList<>();


    public Doctor() {
    }


    public Doctor(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Doctor(String name, String email, String address, String qualifications) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.qualifications = qualifications;
    }

    public Doctor(String name, String email, String address,
                  String qualifications, String verificationCode) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.qualifications = qualifications;
        this.verificationCode = verificationCode;
    }

    protected Doctor(Parcel in) {
        name = in.readString();
        email = in.readString();
        address = in.readString();
        qualifications = in.readString();
        verificationCode = in.readString();
        imageUrl = in.readString();
        time = in.readString();
    }

    public static final Creator<Doctor> CREATOR = new Creator<Doctor>() {
        @Override
        public Doctor createFromParcel(Parcel in) {
            return new Doctor(in);
        }

        @Override
        public Doctor[] newArray(int size) {
            return new Doctor[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(email);
        parcel.writeString(address);
        parcel.writeString(qualifications);
        parcel.writeString(verificationCode);
        parcel.writeString(imageUrl);
        parcel.writeString(time);
    }
}
