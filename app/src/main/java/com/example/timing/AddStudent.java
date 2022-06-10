package com.example.timing;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/*
Программный класс для добавления ученика в Таблицу: "Students"
 */
public class AddStudent extends MainActivity implements View.OnClickListener {
    //Поля для ввода данных
    EditText addIdStudent, addNameStudent, addPhonesStudent, addRank;
    //Кнопка сохранения ученика в Таблицу: "Students"
    Button btnSave;
    //Объект для работы с очередью сообщений
    final Handler handler = new Handler();
    //Количество строк в Таблице: "Students"
    int countStudent = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addstudent);
        addIdStudent = (EditText) findViewById(R.id.addIdStudent);
        addNameStudent = (EditText) findViewById(R.id.addNameStudent);
        addPhonesStudent = (EditText) findViewById(R.id.addPhonesStudent);
        addRank = (EditText) findViewById(R.id.addRank);
        btnSave = (Button) findViewById(R.id.savestudent);
        btnSave.setOnClickListener(this);
        //Подключение к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //Выполняем запрос на выборку данных из Таблицы: "Students"
        //Получаем интерфейс для чтения и записи значений результата запроса в БД
        Cursor cursor = db.rawQuery("SELECT * FROM Students", null);
        cursor.moveToLast();
        //Получаем количество строк из Таблицы: "Students"
        countStudent= cursor.getInt(0);
        //Устанавливаем в поле для ввода id последнего ученика + 1
        addIdStudent.setText(String.valueOf(countStudent + 1));
    }

    @Override
    public void onClick(View v) {
        //Используется для добавления новых строк в Таблицу: "Students"
        ContentValues cv = new ContentValues();
        //Сообщения об ошибках
        Toast messageInt = Toast.makeText(getApplicationContext(), "Данные id ученика, класс", Toast.LENGTH_LONG);
        Toast messageIdNull = Toast.makeText(getApplicationContext(), "Пустое поле - id ученика", Toast.LENGTH_LONG);
        Toast messageNameNull = Toast.makeText(getApplicationContext(), "Пустое поле - ФИО ", Toast.LENGTH_LONG);
        Toast messagePhonesNull = Toast.makeText(getApplicationContext(), "Пустое поле - Телефон ", Toast.LENGTH_LONG);
        Toast messageRankNull = Toast.makeText(getApplicationContext(), "Пустое поле - Класс", Toast.LENGTH_LONG);
        Toast messageSQL = Toast.makeText(getApplicationContext(), "Не верный запрос к базе данных", Toast.LENGTH_LONG);
        //Получаем данные из полей ввода
        String id = addIdStudent.getText().toString();
        String name = addNameStudent.getText().toString();
        String phones = addPhonesStudent.getText().toString();
        String rank = addRank.getText().toString();
        //Подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (v.getId()) {
            case R.id.savestudent:
                //Обработка ошибок возникающих в случае ввода текстовых данных в числовые поля
                try{
                    int id1 = Integer.parseInt(addIdStudent.getText().toString());
                    int individualDiscount1 = Integer.parseInt(addRank.getText().toString());
                }catch (Exception e){
                    messageInt.show();
                    messageInt.setGravity(Gravity.CENTER, 0, 0);
                    ((TextView)((LinearLayout)messageInt.getView()).getChildAt(0))
                            .setGravity(Gravity.CENTER_HORIZONTAL);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            messageInt.cancel();
                        }
                    }, 2000);
                    break;
                }
                //Добавляем данные в контекст
                cv.put("id", id);
                cv.put("name", name);
                //Формат номера телефона
                if((!phones.equals("")) && (phones != null))
                phones = phones.substring(0,1) + "("+phones.substring(1,4) + ")"+phones.substring(4,phones.length());
                cv.put("phones", phones);
                cv.put("rank", rank);
                //Обработка пустого ввода id ученика
                if (id.equals("")) {
                    messageIdNull.show();
                    messageIdNull.setGravity(Gravity.CENTER, 0, 0);
                    ((TextView)((LinearLayout)messageIdNull.getView()).getChildAt(0))
                            .setGravity(Gravity.CENTER_HORIZONTAL);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            messageIdNull.cancel();
                        }
                    }, 2000);
                    break;
                }
                //Обработка пустого ввода ФИО ученика
                if (name.equals("")) {
                    messageNameNull.show();
                    messageNameNull.setGravity(Gravity.CENTER, 0, 0);
                    ((TextView)((LinearLayout)messageNameNull.getView()).getChildAt(0))
                            .setGravity(Gravity.CENTER_HORIZONTAL);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            messageNameNull.cancel();
                        }
                    }, 2000);
                    break;
                }
                //Обработка пустого ввода Телефона ученика
                if (phones.equals("")) {
                    messagePhonesNull.show();
                    messagePhonesNull.setGravity(Gravity.CENTER, 0, 0);
                    ((TextView)((LinearLayout)messagePhonesNull.getView()).getChildAt(0))
                            .setGravity(Gravity.CENTER_HORIZONTAL);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            messagePhonesNull.cancel();
                        }
                    }, 2000);
                    break;
                }
                //Обработка пустого ввода класса ученика
                if (rank.equals("")) {
                    messageRankNull.show();
                    messageRankNull.setGravity(Gravity.CENTER, 0, 0);
                    ((TextView)((LinearLayout)messageRankNull.getView()).getChildAt(0))
                            .setGravity(Gravity.CENTER_HORIZONTAL);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            messageRankNull.cancel();
                        }
                    }, 2000);
                    break;
                }
                try{
                    //Вставляем в Таблицу: "Students" нового ученика
                     int addCount = (int) db.insert("Students", null, cv);
                    //В случае удачного добавления ученика возвращаемся на родительскую активность
                    if (addCount == (countStudent + 1)) {onBackPressed();}
                }
                //В случае если произошла ошибка при добавлении ученика в SQL запросе
                catch(SQLiteConstraintException e){
                    messageSQL.show();
                    messageSQL.setGravity(Gravity.CENTER, 0, 0);
                        ((TextView)((LinearLayout)messageSQL.getView()).getChildAt(0))
                            .setGravity(Gravity.CENTER_HORIZONTAL);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            messageSQL.cancel();
                        }
                    }, 2000);
                }
        }
        //Закрываем подключение к БД
        dbHelper.close();
    }
}
