package com.example.timing;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

//Программный класс главного окна приложения "Lessons"
public class MainActivity extends Activity implements OnClickListener {
    //Кнопки управления объектами
    Button btnStudents,  btnLessons, btnMes;
    //Объект для создания и управления таблицами в БД
    public DBHelper dbHelper;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //dbHelper = new DBHelper(this);
        //Установка заголовка.
        setTitle("Расписание");
        //Обработка кнопок
        btnStudents = (Button) findViewById(R.id.students);
        btnStudents.setOnClickListener(this);
        btnLessons = (Button) findViewById(R.id.lessons);
        btnLessons.setOnClickListener(this);
        btnMes = (Button) findViewById(R.id.mes);
        btnMes.setOnClickListener(this);
       new Thread(new Runnable() {
            @Override
            public void run() {
                dbHelper = new DBHelper(getApplicationContext());
            }
        }).start();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.students:
                //Работа с учениками
                Intent studentActivity = new Intent(this, StudentActivity.class);
                //Запуск StudentActivity
                startActivity(studentActivity);
                break;
            case R.id.lessons:
                //Работа с занятиями
                Intent lessonActivity = new Intent(this, LessonActivity.class);
                //Запуск LessonActivity
                startActivity(lessonActivity);
                break;
            case R.id.mes:
                //Уведомления о занятиях
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Подключение к БД
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        //Отправляем запрос в БД для Таблицы: "Lessons"
                        Cursor cursor = db.rawQuery("SELECT * FROM Lessons", null);
                        cursor.moveToFirst();
                        //Цикл по всем занятиям
                        while (!cursor.isAfterLast()) {
                            String currentDate = new SimpleDateFormat("dd/MM", Locale.getDefault()).format(new Date());
                            String theme = cursor.getString(1);
                            String date = cursor.getString(2);
                            String time = cursor.getString(3);
                            String sTime = time.substring(0,time.indexOf("."));
                            //Если даты совпадают
                            if(currentDate.equals(date)){
                                String text = "Сегодня запланировано занятие в " + sTime + " ч. по теме: " + theme ;
                                Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                ((TextView)((LinearLayout)toast.getView()).getChildAt(0))
                                        .setGravity(Gravity.CENTER_HORIZONTAL);
                                toast.show();
                            }
                            //Переходим к следующему
                            cursor.moveToNext();
                        }
                        cursor.close();
                    }
                }, 2000);
                break;
        }
        //Закрываем подключение к БД
        dbHelper.close();
    }
}