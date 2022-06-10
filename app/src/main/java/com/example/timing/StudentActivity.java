package com.example.timing;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Программный класс для работы с Таблицей "Students"
 */
public class StudentActivity extends MainActivity implements View.OnClickListener {
    //Кнопки для работы с Таблицей "Students"
    Button btnAdd, btnDel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        //Установка заголовка окна
        setTitle("Ученики");
        //Вкладки
        TabHost tabHost = (TabHost) findViewById(R.id.tabHostStudent);
        tabHost.setup();
        //Формирование вкладки с таблицей данных
        TabHost.TabSpec tabSpec1 = tabHost.newTabSpec("tag1");
        tabSpec1.setContent(R.id.linearLayout1);
        tabSpec1.setIndicator("Таблица");
        tabHost.addTab(tabSpec1);
        //Формирование вкладки с настройками
        TabHost.TabSpec tabSpec2 = tabHost.newTabSpec("tag2");
        tabSpec2.setContent(R.id.linearLayout2);
        tabSpec2.setIndicator("Настройки");
        tabHost.addTab(tabSpec2);
        //Установка текущей вкладки
        tabHost.setCurrentTab(0);
        //Обработка выбора вкладки таблицы
        tabHost.setOnTabChangedListener(tabId -> {
            //Если выбрана вкладка "Таблица"
            if("tag1".equals(tabId)) {
                //Чтение данных из Таблицы: "Students"
                initTableStudent(dbHelper);
            }
        });
        //Обработка кнопок
        btnAdd = (Button) findViewById(R.id.addstudent);
        btnAdd.setOnClickListener(this);
        btnDel = (Button) findViewById(R.id.delstudent);
        btnDel.setOnClickListener(this);
        //Чтение данных из Таблицы: "Students"
        initTableStudent(dbHelper);
    }

    /**
     * Функция чтения данных из Таблицы: "Students"
     * @param dbHelper
     */
    public void initTableStudent(DBHelper dbHelper){
        //Подключение к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //Список учеников
        ArrayList<HashMap<String, Object>> students = new ArrayList<HashMap<String, Object>>();
        //Список параметров каждого ученика
        HashMap<String, Object> student;
        //Отправляем запрос в БД для Таблицы: "Students"
        Cursor cursor = db.rawQuery("SELECT * FROM Students", null);
        cursor.moveToFirst();
        //Цикл по все ученикам
        while (!cursor.isAfterLast()) {
            student = new HashMap<String, Object>();
            //Заполняем ученика для отображения
            student.put("id", "id ученика: " + cursor.getString(0));
            student.put("name", "ФИО: " + cursor.getString(1));
            student.put("phones", "телефон: " + cursor.getString(2));
            student.put("rank", "класс: " + cursor.getString(3) );
            //Добавляем ученика в список
            students.add(student);
            //Переходим к следующему
            cursor.moveToNext();
        }
        cursor.close();
        //Параметры ученика, которые будем отображать в соответствующих
        //элементах из разметки adapter_student.xml
        String[] from = {"id", "name", "phones", "rank"};
        int[] to = {R.id.textId, R.id.textName, R.id.textPhones, R.id.textrank};
        //Создаем адаптер для работы с ListView
        SimpleAdapter adapter = new SimpleAdapter(this, students, R.layout.adapter_student, from, to);
        ListView listView = (ListView) findViewById(R.id.listViewStudent);
        listView.setAdapter(adapter);
        dbHelper.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //Добавление ученика
            case R.id.addstudent:
                Intent addStudent = new Intent(this, AddStudent.class);
                //Запуск activity AddStudent
                startActivity(addStudent);
                break;
            //Удаление ученика
            case R.id.delstudent:
                Intent delStudent = new Intent(this, DelStudent.class);
                //Запуск activity DelStudent
                startActivity(delStudent);
                break;
        }
    }
}