package ru.issergeev.parking;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ParkingDataBase";
    private static final String TABLE_CARS = "Cars";
    private static final String KEY_ID = "Car_ID";
    private static final String KEY_NAME = "Car_Name";
    private static final String KEY_LICENCE_PLATE = "Licence_Plate";
    private static final String KEY_COUNTRY = "Car_Country";

    DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    static String getTableCars() {
        return TABLE_CARS;
    }

    public static String getKeyId() {
        return KEY_ID;
    }

    public static String getKeyName() {
        return KEY_NAME;
    }

    public static String getKeyLicencePlate() {
        return KEY_LICENCE_PLATE;
    }

    public static String getKeyCountry() {
        return KEY_COUNTRY;
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_CARS +
                    "(" + KEY_LICENCE_PLATE + " text(15) primary key,"
                    + KEY_NAME + " text(20),"
                    + KEY_COUNTRY + " text(19)"+ ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_CARS);

        onCreate(db);
    }
}

