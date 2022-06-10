package com.example.timing;

import android.content.ContentValues;
import android.database.Cursor;
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
Программный класс для добавления занятия в Таблицу: "Lessons" для БД
 */
public class AddLesson extends MainActivity implements View.OnClickListener {
    //Поля для ввода данных
    EditText etId, etTheme, etDate, etTime, etStatus, etIdStudent;
    //Кнопка сохранения занятия в Таблицу: "Занятия"
    Button btnSave;
    //Объект для работы с очередью сообщений
    final Handler handler = new Handler();
    //Количество строк в Таблице: "Lessons"
    int countLessons = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addlesson);
        etId = (EditText) findViewById(R.id.etId);
        etTheme = (EditText) findViewById(R.id.etTheme);
        etDate = (EditText) findViewById(R.id.etDate);
        etTime = (EditText) findViewById(R.id.etTime);
        etStatus = (EditText) findViewById(R.id.etStatus);
        etIdStudent = (EditText) findViewById(R.id.etIdStudent);
        btnSave = (Button) findViewById(R.id.savelesson);
        btnSave.setOnClickListener(this);
        //Подключение к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //Выполняем запрос на выборку данных из Таблицы: "Lessons"
        //Получаем интерфейс для чтения и записи значений результата запроса в БД
        Cursor cursor = db.rawQuery("SELECT * FROM Lessons", null);
        cursor.moveToLast();
        //Получаем количество строк из Таблицы: "Lessons"
        //countLessons = cursor.getInt(0);
        if(cursor.getCount() == 0){ countLessons = 0; }else { countLessons = cursor.getInt(0); }
        //Устанавливаем в поле для ввода id последнего инструмента + 1
        etId.setText(String.valueOf(countLessons + 1));
    }

    @Override
    public void onClick(View v) {
        //Используется для добавления новых строк в Таблицу: "Lessons"
        ContentValues cv = new ContentValues();
        //Сообщения о ошибках
        Toast messageInt = Toast.makeText(getApplicationContext(), "Данные id занятия, статус и id ученика должны быть числовыми", Toast.LENGTH_LONG);
        Toast messageNull = Toast.makeText(getApplicationContext(), "Пустые поля ввода данных", Toast.LENGTH_LONG);
        Toast message = Toast.makeText(getApplicationContext(), "Не верный статус занятия", Toast.LENGTH_LONG);
        Toast messageSQL = Toast.makeText(getApplicationContext(), "Не верный запрос к базе данных", Toast.LENGTH_LONG);
        //Получаем данные из полей ввода
        String id = etId.getText().toString();
        String theme = etTheme.getText().toString();
        String date = etDate.getText().toString();
        String time = etTime.getText().toString();
        String status = etStatus.getText().toString();
        String idStudent = etIdStudent.getText().toString();
        //Подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (v.getId()) {
            case R.id.savelesson:
                //Обработка ошибок возникающих в случае ввода текстовых данных в числовые поля
                try{
                    int id1 = Integer.parseInt(etId.getText().toString());
                    int status1 = Integer.parseInt(etStatus.getText().toString());
                    int idStudent1 = Integer.parseInt(etIdStudent.getText().toString());
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
                    }, 2000);// 5 sec
                    break;
                }
                //Добавляем данные в контекст
                cv.put("id", id);
                cv.put("theme", theme);
                cv.put("date", date);
                cv.put("time", time);
                cv.put("status", status);
                cv.put("idStudent", idStudent);
                //Обработка пустого ввода
                if ((id.equals("") && theme.equals("")
                        && date.equals("") && time.equals("") && status.equals("") && idStudent.equals(""))){
                    messageNull.show();
                    messageNull.setGravity(Gravity.CENTER, 0, 0);
                    ((TextView)((LinearLayout)messageNull.getView()).getChildAt(0))
                            .setGravity(Gravity.CENTER_HORIZONTAL);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            messageNull.cancel();
                        }
                    }, 2000);
                }else{
                    //Обработка ввода статуса занятия (0 - планируется, 1 - проведено, 2 - отменено)
                    if (Integer.valueOf(status) != 0) {
                        message.show();
                        message.setGravity(Gravity.CENTER, 0, 0);
                        ((TextView)((LinearLayout)message.getView()).getChildAt(0))
                                .setGravity(Gravity.CENTER_HORIZONTAL);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                message.cancel();
                            }
                        }, 2000);
                        break;
                    }
                try{
                    //Вставляем в Таблицу: "Lessons" новое занятие
                    int addCount = (int) db.insert("Lessons", null, cv);
                    //В случае удачного добавления занятия возвращаемся на родительскую активность
                    if (addCount == (countLessons + 1)) {onBackPressed();}
                }
                //В случае если произошла ошибка при добавлении занятия в SQL запросе
                catch(android.database.sqlite.SQLiteConstraintException e){
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
        }
        //Закрываем подключение к БД
        dbHelper.close();
    }
}
