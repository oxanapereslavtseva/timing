package com.example.timing;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;

/**
 * Класс для работы с БД "ProcatDB"
 */
public class DBHelper extends SQLiteOpenHelper {
    //Контекст данных
    private final Context fContext;
    //Название базы данных
    private static final String DATABASE_NAME = "TimingDB";
    //Название таблицы с учениками
    public static final String TABLE_STUDENTS = "Students";
    //Название таблицы с занятиями
    public static final String TABLE_LESSONS = "Lessons";

    /**
     * Конструктор класса
     * @param context
     */
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        fContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Создание таблицы с учениками
        db.execSQL("CREATE TABLE " + TABLE_STUDENTS +"("
                + "id integer primary key,"
                + "name text,"
                + "phones text,"
                + "rank integer" + ");");
        //Создание таблицы с занятиями
        db.execSQL("CREATE TABLE " + TABLE_LESSONS +"("
                + "id integer primary key,"
                + "theme text,"
                + "date text,"
                + "time text,"
                + "status integer,"
                + "idStudent integer"
                + ");");
        //Добавляем записи в таблицу учеников
        ContentValues values_student = new ContentValues();
        //Получаем файл из ресурсов
        Resources res_student = fContext.getResources();
        //Открываем xml-файл
        XmlResourceParser students_records_xml = res_student.getXml(R.xml.students_records);
        try {
            //Ищем конец документа
            int eventType = students_records_xml.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                //Ищем теги record
                if ((eventType == XmlPullParser.START_TAG)
                        && (students_records_xml.getName().equals("record"))) {
                    //Получаем атрибуты Тега Record и вставляем в таблицу
                    String id = students_records_xml.getAttributeValue(0);
                    String name = students_records_xml.getAttributeValue(1);
                    String phones = students_records_xml.getAttributeValue(2);
                    String rank = students_records_xml.getAttributeValue(3);
                    values_student.put("id", id);
                    values_student.put("name", name);
                    values_student.put("phones", phones);
                    values_student.put("rank", Integer.valueOf(rank));
                    db.insert(TABLE_STUDENTS, null, values_student);
                }
                eventType = students_records_xml.next();
            }
        }
        //Обработка ошибок работы с файлом xml
        catch (XmlPullParserException e) {
            Log.e("Test", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("Test", e.getMessage(), e);
        } finally {
            //Закрываем xml файл
            students_records_xml.close();
        }
        //Добавляем записи в таблицу занятий
        ContentValues values_lessons = new ContentValues();
        //Получаем файл из ресурсов
        Resources res_lessons = fContext.getResources();
        //Открываем xml-файл
        XmlResourceParser lessons_records_xml = res_lessons.getXml(R.xml.lessons_records);
        try {
            //Ищем конец документа
            int eventType = lessons_records_xml.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                //Ищем теги record
                if ((eventType == XmlPullParser.START_TAG)
                        && (lessons_records_xml.getName().equals("record"))) {
                    //Получаем атрибуты Тега Record и вставляем в таблицу
                    String id = lessons_records_xml.getAttributeValue(1);
                    String theme = lessons_records_xml.getAttributeValue(4);
                    String date = lessons_records_xml.getAttributeValue(0);
                    String time = lessons_records_xml.getAttributeValue(5);
                    String status = lessons_records_xml.getAttributeValue(3);
                    String idStudent = lessons_records_xml.getAttributeValue(2);
                    values_lessons.put("id", id);
                    values_lessons.put("theme", theme);
                    values_lessons.put("date", date);
                    values_lessons.put("time", time);
                    values_lessons.put("status", Integer.valueOf(status));
                    values_lessons.put("idStudent", Integer.valueOf(idStudent));
                    db.insert(TABLE_LESSONS, null, values_lessons);
                }
                eventType = lessons_records_xml.next();
            }
        }
        //Обработка ошибок работы с файлом xml
        catch (XmlPullParserException e) {
            Log.e("Test", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("Test", e.getMessage(), e);
        } finally {
            //Закрываем xml файл
            lessons_records_xml.close();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS  " + TABLE_STUDENTS);
        db.execSQL("DROP TABLE IF EXISTS  " + TABLE_LESSONS);
        onCreate(db);
    }
}
