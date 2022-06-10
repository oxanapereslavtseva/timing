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
 * Программный класс для работы с Таблицей "Instruments"
 */
public class LessonActivity extends MainActivity implements View.OnClickListener {
    //Кнопки для работы с Таблицей "Lessons"
    Button btnAdd, btnEdit, btnDel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);
        //Установка заголовка
        setTitle("Занятия");
        //Вкладки
        TabHost tabHost = (TabHost) findViewById(R.id.tabHostLesson);
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
                //Чтение данных из Таблицы: "Lessons"
                initTableLessons(dbHelper);
            }
        });
        //Обработка кнопок
        btnAdd = (Button) findViewById(R.id.addlesson);
        btnAdd.setOnClickListener(this);
        btnEdit = (Button) findViewById(R.id.editlesson);
        btnEdit.setOnClickListener(this);
        btnDel = (Button) findViewById(R.id.dellesson);
        btnDel.setOnClickListener(this);
        //Чтение данных из Таблицы: "Lessons"
        initTableLessons(dbHelper);
    }

    /**
     * Функция чтения данных из Таблицы: "Lessons"
     * @param dbHelper
     */
    public void initTableLessons(DBHelper dbHelper){
        //Подключение к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //Список занятий
        ArrayList<HashMap<String, Object>> lessons = new ArrayList<HashMap<String, Object>>();
        //Список параметров каждого занятия
        HashMap<String, Object> lesson;
        //Отправляем запрос в БД для Таблицы: "Lessons"
        Cursor cursor = db.rawQuery("SELECT * FROM Lessons", null);
        cursor.moveToFirst();
        //Цикл по всем занятиям
        while (!cursor.isAfterLast()) {
            lesson = new HashMap<String, Object>();
            //Заполняем занятие для отображения
            lesson.put("id", "id занятия: " + cursor.getString(0));
            lesson.put("theme", "тема: " + cursor.getString(1));
            lesson.put("date", "дата: " + cursor.getString(2));
            lesson.put("time", "время: " + cursor.getString(3));
            String stat = " ";
            if(cursor.getString(4).equals("0")) stat = " (запланировано)";
            if(cursor.getString(4).equals("1")) stat = " (проведено)";
            if(cursor.getString(4).equals("2")) stat = " (отменено)";
            lesson.put("status", "статус: " + cursor.getString(4)+stat);
            lesson.put("idStudent", "id ученика: " + cursor.getString(5));
            //Добавляем занятие в список
            lessons.add(lesson);
            //Переходим к следующему
            cursor.moveToNext();
        }
        cursor.close();
        //Параметры занятия, которые будем отображать в соответствующих
        //элементах из разметки adapter_lessons.xml
        String[] from = {"id", "theme",  "date","time", "status", "idStudent"};
        int[] to = {R.id.textId, R.id.textTheme, R.id.textDate, R.id.textTime, R.id.textStatus, R.id.textIdStudent};
        //Создаем адаптер для работы с ListView
        SimpleAdapter adapter = new SimpleAdapter(this, lessons, R.layout.adapter_lessons, from, to);
        ListView listView = (ListView) findViewById(R.id.listViewLesson);
        listView.setAdapter(adapter);
        dbHelper.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //Добавление занятия
            case R.id.addlesson:
                Intent addLesson = new Intent(this, AddLesson.class);
                //Запуск activity AddLesson
                startActivity(addLesson);
                break;
            //Изменение занятия
            case R.id.editlesson:
                Intent editLesson = new Intent(this, EditLesson.class);
                //Запуск activity EditLesson
                startActivity(editLesson);
                break;
            //Удаление занятия
            case R.id.dellesson:
                Intent delLesson = new Intent(this, DelLesson.class);
                //Запуск activity DelInstrument
                startActivity(delLesson);
                break;
        }
    }
}