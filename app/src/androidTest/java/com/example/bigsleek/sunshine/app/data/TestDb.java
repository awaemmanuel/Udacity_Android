/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.bigsleek.sunshine.app.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(com.example.bigsleek.sunshine.app.data.WeatherDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    /*
        Students: Uncomment this test once you've written the code to create the Location
        table.  Note that you will have to have chosen the same column names that I did in
        my solution for this test to compile, so if you haven't yet done that, this is
        a good time to change your column names to match mine.

        Note that this only tests that the Location table has the correct columns, since we
        give you the code for the weather table.  This test does not look at the
     */
    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(com.example.bigsleek.sunshine.app.data.WeatherContract.LocationEntry.TABLE_NAME);
        tableNameHashSet.add(com.example.bigsleek.sunshine.app.data.WeatherContract.WeatherEntry.TABLE_NAME);

        mContext.deleteDatabase(com.example.bigsleek.sunshine.app.data.WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new com.example.bigsleek.sunshine.app.data.WeatherDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        /* now, do our tables contain the correct columns? */
        c = db.rawQuery("PRAGMA table_info(" + com.example.bigsleek.sunshine.app.data.WeatherContract.LocationEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(com.example.bigsleek.sunshine.app.data.WeatherContract.LocationEntry._ID);
        locationColumnHashSet.add(com.example.bigsleek.sunshine.app.data.WeatherContract.LocationEntry.COLUMN_CITY_NAME);
        locationColumnHashSet.add(com.example.bigsleek.sunshine.app.data.WeatherContract.LocationEntry.COLUMN_COORD_LAT);
        locationColumnHashSet.add(com.example.bigsleek.sunshine.app. data.WeatherContract.LocationEntry.COLUMN_COORD_LONG);
        locationColumnHashSet.add(com.example.bigsleek.sunshine.app.data.WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                locationColumnHashSet.isEmpty());
        db.close();
    }

    /*
        Students:  Here is where you will build code to test that we can insert and query the
        location database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can uncomment out the "createNorthPoleLocationValues" function.  You can
        also make use of the ValidateCurrentRecord function from within TestUtilities.
    */
    public void testLocationTable() {
        insertLocation();
    }

    /*
        Students:  Here is where you will build code to test that we can insert and query the
        database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can use the "createWeatherValues" function.  You can
        also make use of the validateCurrentRecord function from within TestUtilities.
     */
    public void testWeatherTable() {
        // First insert the location, and then use the locationRowId to insert
        // the weather. Make sure to cover as many failure cases as you can.
        long locationRowID = insertLocation();

        assertTrue("Error: Inserting into location table failed!", locationRowID != -1L );

        // Instead of rewriting all of the code we've already written in testLocationTable
        // we can move this code to insertLocation and then call insertLocation from both
        // tests. Why move it? We need the code to return the ID of the inserted location
        // and our testLocationTable can only return void because it's a test.

        // First step: Get reference to writable database
        WeatherDbHelper dbhelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbhelper.getWritableDatabase();

        // Second Step: Create ContentValues of what you want to insert
        // (you can use the createWeatherValues TestUtilities function if you wish)
        ContentValues testValues = TestUtilities.createWeatherValues(locationRowID);


        // Third Step: Insert ContentValues into database and get a row ID back
        long weatherRowID = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back
        assertTrue("Error: No row was gotten back", weatherRowID != -1);

        // Fourth Step: Query the database and receive a Cursor back
        Cursor cursor = db.query(
                WeatherContract.WeatherEntry.TABLE_NAME, // Table name
                null,
                null,
                null,
                null,
                null,
                null
        );

        // Fifth Step: Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from location query", cursor.moveToFirst() );

        // Sixth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed", cursor, testValues);

        // Seventh Step: Move the cursor to demonstrate that there is only one record in teh database
        assertFalse("Error: More than one record returned from location query", cursor.moveToNext());


        // Finally, close the cursor and database
        cursor.close();
        db.close();
    }


    /*
        Students: This is a helper method for the testWeatherTable quiz. You can move your
        code from testLocationTable to here so that you can call this code from both
        testWeatherTable and testLocationTable.
     */
    public long insertLocation() {
        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database
        WeatherDbHelper dbhelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbhelper.getWritableDatabase();


        // Second step: Create ContentValues of what you want to insert
        // (you can use the createNorthPoleLocationValues if you wish)
        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();


        // Third Step: Insert ContentValues into database and get a row ID back
        long locationRowId;
        locationRowId = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back
        assertTrue(locationRowId != -1);

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results
        Cursor cursor = db.query(
                WeatherContract.LocationEntry.TABLE_NAME, // Table to Query
                null,   // all columns
                null,   // Columns for the where clause
                null,   // Values of the where clause
                null,   // Columns to group by
                null,   // Columns to filter by row groups
                null    // sort order
        );


        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from location query", cursor.moveToFirst() );


        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed", cursor, testValues);

        // Sixth Step: Move the cursor to demonstrate that there is only one record in teh database
        assertFalse("Error: More than one record returned from location query", cursor.moveToNext());

        // Seventh Step: Finally, close the cursor and database
        cursor.close();
        db.close();
        return locationRowId;
    }
}
