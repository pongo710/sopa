package de.sopa.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.sopa.model.LevelInfo;
import java.util.ArrayList;
import java.util.List;

import static de.sopa.database.LevelInfoTable.COLUMN_FEWEST_MOVES;
import static de.sopa.database.LevelInfoTable.COLUMN_ID;
import static de.sopa.database.LevelInfoTable.COLUMN_LOCKED;
import static de.sopa.database.LevelInfoTable.TABLE_LEVEL_INFO;

/**
 * David Schilling - davejs92@gmail.com
 */
public class LevelInfoDataSource {

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {COLUMN_ID, COLUMN_LOCKED, COLUMN_FEWEST_MOVES};

    public LevelInfoDataSource (Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public LevelInfo createLevelInfo(LevelInfo levelInfo) {
        ContentValues values = new ContentValues();
        if(levelInfo.getLevelId() != null){
            values.put(COLUMN_ID, levelInfo.getLevelId());
        }
        values.put(COLUMN_LOCKED, levelInfo.isLocked());
        values.put(COLUMN_FEWEST_MOVES, levelInfo.getFewestMoves());
        long insertId = database.insert(TABLE_LEVEL_INFO, null,
                values);
        Cursor cursor = database.query(TABLE_LEVEL_INFO,
                allColumns, COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        LevelInfo newLevelInfo = cursorToLevelInfo(cursor);
        cursor.close();
        return newLevelInfo;
    }
    public LevelInfo updateLevelInfo(LevelInfo levelInfo) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, levelInfo.getLevelId());
        values.put(COLUMN_LOCKED, levelInfo.isLocked());
        values.put(COLUMN_FEWEST_MOVES, levelInfo.getFewestMoves());
        database.replace(TABLE_LEVEL_INFO, null,
                values);
        return getLevelInfoById(levelInfo.getLevelId());
    }

    public List<LevelInfo> getAllLevelInfos() {
        List<LevelInfo> levelInfos = new ArrayList<>();
        Cursor cursor = database.query(TABLE_LEVEL_INFO,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            LevelInfo levelInfo= cursorToLevelInfo(cursor);
            levelInfos.add(levelInfo);
            cursor.moveToNext();
        }
        cursor.close();
        return levelInfos;
    }

    public LevelInfo getLevelInfoById(Integer id) {
        Cursor cursor = database.query(TABLE_LEVEL_INFO, allColumns, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        cursor.moveToFirst();
        LevelInfo levelInfo = cursorToLevelInfo(cursor);
        cursor.close();
        return levelInfo;
    }

    public int getLevelCount() {
        Cursor cursor = database.rawQuery("Select count(*) from " + TABLE_LEVEL_INFO, null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    private LevelInfo cursorToLevelInfo(Cursor cursor) {
        int anInt = cursor.getInt(0);
        boolean locked = cursor.getInt(1) != 0;
        int anInt1 = cursor.getInt(2);
        LevelInfo levelInfo = new LevelInfo(anInt, locked, anInt1);
        return levelInfo;
    }
}
