package virg29.sr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{
    private SQLiteDatabase srReadableDatabase = getReadableDatabase();

    private SQLiteDatabase srWritableDatabase = getWritableDatabase();

    private int longOfAnswerMassive = 128;
    public int massiveCountElements;



    public DBHelper(Context context){
        super(context, "database", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db){

        db.execSQL("create table sr ("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "recognition text,"
                + "path text,"
                + "time text"
                + ");");

        srWritableDatabase = db;

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int v1, int v2){
        db.execSQL("DROP TABLE IF EXISTS `sr`");
        onCreate(db);
    }


    public String[][] getListRecords(){
        String[][] answer = new String[longOfAnswerMassive][3];
        String str = "SELECT id,name,time FROM `sr`";
        Cursor readebleDB = srReadableDatabase.rawQuery(str,null);
        int idIndex = readebleDB.getColumnIndex("id");
        int nameIndex = readebleDB.getColumnIndex("name");
        int timeIndex = readebleDB.getColumnIndex("time");
        int j=0;
        while(readebleDB.moveToNext()) {
            answer[j][0] = String.valueOf(readebleDB.getInt(idIndex));
            answer[j][1] = readebleDB.getString(nameIndex);
            answer[j][2] = readebleDB.getString(timeIndex);
            j++;
        }
        massiveCountElements=j;
        readebleDB.close();
        return(answer);
    }
    public String[] getDataById(int id){
        String[] answer = new String[4];
        Cursor tables = srReadableDatabase.rawQuery("SELECT name,time,recognition,path FROM `sr` WHERE `sr`.id="+id, null);
        int nameIndex = tables.getColumnIndex("name");
        int timeIndex = tables.getColumnIndex("time");
        int pathIndex = tables.getColumnIndex("path");
        int recognitionIndex = tables.getColumnIndex("recognition");
        while(tables.moveToNext()) {
            answer[0] = tables.getString(nameIndex);
            answer[1] = tables.getString(timeIndex);
            answer[2] = tables.getString(recognitionIndex);
            answer[3] = tables.getString(pathIndex);
        }
        tables.close();
        return answer;
    }

    public void updateRecordName(int id,String name){
        srWritableDatabase.execSQL("UPDATE `sr` SET name='"+name
                +"' WHERE `sr`.id="+id);

    }
    public void updateRecordRecognition(int id,String recognition){
        srWritableDatabase.execSQL("UPDATE `sr` SET recognition='"+recognition
                +"' WHERE `sr`.id="+id);

    }
    public void deleteRecord(int id){
        srWritableDatabase.execSQL("DELETE FROM `sr` WHERE `sr`.id="+id);
    }
    public void appendRecord(String name,String time,String path){
        ContentValues cv = new ContentValues();
        cv.put("name",name);
        cv.put("time", time);
        cv.put("path",path);
        srWritableDatabase.insert("sr", null, cv);
        cv.clear();
    }



}
