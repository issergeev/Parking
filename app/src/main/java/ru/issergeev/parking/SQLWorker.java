package ru.issergeev.parking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SQLWorker {

    //Where clause constants
    private final String WHERE_CLAUSE = DB.getKeyId() + " = ?";

    //Classes to work with Database
    private DB db;
    private SQLiteDatabase database;

    private Context context;

    //Constructor
    public SQLWorker(Context c) {
        context = c;
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public SQLWorker open() throws SQLException {
        db = new DB(context);
        database = db.getWritableDatabase();
        return this;
    }

    //Function to INSERT new car into Database
    public long insertCar(String car_name, String licence_plate, String country) {
        ContentValues values = new ContentValues();

        values.put(DB.getKeyName(), car_name);
        values.put(DB.getKeyLicencePlate(), licence_plate);
        values.put(DB.getKeyCountry(), country);

        return database.insert(DB.getTableCars(), null, values);
    }

    //Function to UPDATE a car in Database
    public void updateCar(String car_id, String new_car_name, String new_licence_plate, String new_country) {
        ContentValues values = new ContentValues();

        values.put(DB.getKeyName(), new_car_name);
        values.put(DB.getKeyLicencePlate(), new_licence_plate);
        values.put(DB.getKeyCountry(), new_country);

        database.update(DB.getTableCars(), values, WHERE_CLAUSE, new String[]{car_id});
    }

    //Function to DELETE a car in Database
    public void deleteCar(String car_id) {
        database.delete(DB.getTableCars(), WHERE_CLAUSE,
                new String[]{car_id});
    }

    //Reading rows in a Table in Database
    public Cursor readEntry() {
        String[] allColumns = new String[] { DB.getKeyName(), DB.getKeyLicencePlate(),
                DB.getKeyCountry() };

        Cursor cursor = database.query(DB.getTableCars(), allColumns, null, null, null,
                null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;
    }

    public void close() {
        db.close();
    }
}