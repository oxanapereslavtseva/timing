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
Программный класс для изменения параметров занятия в Таблице: "Lessons"
 */
public class EditLesson extends MainActivity implements View.OnClickListener {
    //Метки полей для ввода данных
    TextView themeLes, dataLes, timeLes, statusLes, idStudent;
    //Поля для ввода данных
    EditText etId, etTheme, etDate, etTime, etStatus, etIdStudent;
    //Кнопка сохранения измененного инструмента в Таблицу: "Lessons"
    Button btnSave;
    //Кнопка отображения параметров выбранного занятия
    Button btnShow;
    //Объект для работы с очередью сообщений
    final Handler handler = new Handler();
    //Количество строк в Таблице: "Lessons"
    int countLessons = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editlesson);
        etId = (EditText) findViewById(R.id.etId);
        etTheme = (EditText) findViewById(R.id.etTheme);
        etDate = (EditText) findViewById(R.id.etDate);
        etTime = (EditText) findViewById(R.id.etTime);
        etStatus = (EditText) findViewById(R.id.etStatus);
        etIdStudent = (EditText) findViewById(R.id.etIdStudent);
        btnShow = (Button) findViewById(R.id.showlesson);
        btnShow.setOnClickListener(this);
        btnSave = (Button) findViewById(R.id.savelesson);
        btnSave.setOnClickListener(this);
        //Подключение к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //Выполняем запрос на выборку данных из Таблицы: "Lessons"
        //Получаем интерфейс для чтения и записи значений результата запроса в БД
        Cursor cursor = db.rawQuery("SELECT * FROM Lessons", null);
        cursor.moveToLast();
        //Получаем количество строк из Таблицы: "Instruments"
        countLessons = cursor.getInt(0);
        //Устанавливаем в поле для ввода id - номер последнего инструмента
        etId.setText(String.valueOf(countLessons));
    }

    @Override
    public void onClick(View v) {
        //Используется для обновления параметров инструмента
        ContentValues cv = new ContentValues();
        Toast messageInt = Toast.makeText(getApplicationContext(), "Данные id занятия, статус занятия и idученика должны быть числовыми", Toast.LENGTH_LONG);
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
            case R.id.showlesson:
                //Отправляем запрос в БД для Таблицы: "Lessons"
                Cursor cursor = db.rawQuery("SELECT * FROM Lessons WHERE "+"id = " + id, null);
                cursor.moveToFirst();
                //Цикл по результату запроса в БД
                while (!cursor.isAfterLast()) {
                    etTheme.setText(cursor.getString(1));
                    etDate.setText(cursor.getString(2));
                    etTime.setText(cursor.getString(3));
                    etStatus.setText(cursor.getString(4));
                    etIdStudent.setText(cursor.getString(5));
                    cursor.moveToNext();
                }
                cursor.close();
                break;
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
                    }, 2000);
                    btnShow.setClickable(false);
                    break;
                }
                //Добавляем данные в контекст
                cv.put("id", etId.getText().toString());
                cv.put("theme", etTheme.getText().toString());
                cv.put("date", etDate.getText().toString());
                cv.put("time", etTime.getText().toString());
                cv.put("status", etStatus.getText().toString());
                cv.put("idStudent", etIdStudent.getText().toString());
                //Обработка пустого ввода
                if ((id.equals("") && theme.equals("")
                        && date.equals("") && time.equals("") && status.equals("") && idStudent.equals(""))) {
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
                    //Если статус занятия не равен 0, 1, 2
                    if (Integer.valueOf(status) != 0 && Integer.valueOf(status) != 1&& Integer.valueOf(status) != 2) {
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
                    //Изменяем параметры занятия в Таблице: "Lessons"
                    int updateCount = (int) db.update("Lessons", cv, "id = " + id, null);
                    System.out.println(updateCount);
                    //В случае удачного обновления параметров инструмента возвращаемся на родительскую активность
                    if (updateCount == 1) {onBackPressed();}
                }
                //В случае если произошла ошибка в запросе на обновление
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
