package xjasz.core.storage.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SqlDatabase {
    private static final String TAG = SqlDatabase.class.getSimpleName();

    // DATABASE
    public static final String DATABASE_NAME = "DATABASE_MAIN";
    public static final int DATABASE_VERSION = 1;

    // Main Table
    public static final String MAIN_TABLE = "MAIN_TABLE";
    public static final String MainID = "MainID";
    public static final String Main_Pref = "Main_Pref";
    public static final String Main_Title = "Main_Title";

    private static SQLiteDatabase database;
    private static DatabaseHelper databaseHelper;
    private static boolean open = false;

    public SqlDatabase(Context c) {
        super();
        databaseHelper = new DatabaseHelper(c);
    }

    public SqlDatabase openReadDB(String what) throws SQLException {
        Log.d(TAG, "----------------> DATABASE EVENT <---------------- ReadDB " + what);
        if (!open) {
            open = true;
            database = databaseHelper.getReadableDatabase();
            return this;
        }
        return null;
    }

    public SqlDatabase openWriteDB(String what) throws SQLException {
        Log.d(TAG, "----------------> DATABASE EVENT <---------------- WriteDB " + what);
        if (!open) {
            open = true;
            database = databaseHelper.getWritableDatabase();
            return this;
        }
        return null;
    }

    public void close(String what) {
        Log.d(TAG, "----------------> DATABASE EVENT <---------------- CloseDB " + what);
        databaseHelper.close();
    }

    protected static class DatabaseHelper extends SQLiteOpenHelper {

        protected DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(TAG, "DATABASE CREATION ");
            db.execSQL("CREATE TABLE " + MAIN_TABLE + " (" +
                    MainID + " INTEGER PRIMARY KEY, " +
                    Main_Pref + " VARCHAR(55), " +
                    Main_Title + " VARCHAR(255);");
        }

        @Override
        public synchronized void close() {
            super.close();
            open = false;
        }

        @Override
        public SQLiteDatabase getWritableDatabase() {
            return super.getWritableDatabase();
        }

        @Override
        public SQLiteDatabase getReadableDatabase() {
            open = true;
            return super.getReadableDatabase();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onCreate(db);
        }
    }
}
