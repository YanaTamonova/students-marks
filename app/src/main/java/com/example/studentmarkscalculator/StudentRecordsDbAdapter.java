package com.example.studentmarkscalculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Адаптер базы данных для взаимодействия с базой данных SQLite
 */
public class StudentRecordsDbAdapter {
    /**
     * Столбец с ид студента
     */
    public static final String STUDENT_ID = "_id";

    /**
     * Столбец с именем студента
     */
    public static final String STUDENT_FIRSTNAME = "firstname";

    /**
     * Столбец с фамилией студента
     */
    public static final String STUDENT_LASTNAME = "lastname";


    /**
     * Идентификатор студента
     */
    public static final String MARK_STUDENT_ID = "_id";

    /**
     * Столбец оценки за лабораторные работы
     */
    public static final String MARK_LAB = "labmark";

    /**
     * Столбец с оценкой за контрольные
     */
    public static final String MARK_MIDTERM = "midtermmark";

    /**
     * Столбец с оценкой за зачет или экзамен
     */
    public static final String MARK_FINAL_EXAM = "finalexammark";

    /**
     * Суммарная посещаемость.
     */
    public static final String MARK_FINAL_ATTENDANCE = "finalattendancemark";

    /**
     * Столбец со средней оценкой за лабораторные
     */
    public static final String AVG_MARK_LAB = "avg("+MARK_LAB+")";

    /**
     * Столбец со средней оценкой за контрольные
     */
    public static final String AVG_MARK_MIDTERM = "avg("+MARK_MIDTERM+")";

    /**
     * Столбец со средней оценкой за зачет/экзамен
     */
    public static final String AVG_MARK_FINAL_EXAM = "avg("+MARK_FINAL_EXAM+")";

    /**
     * Столбец со средней посещаемостью
     */
    public static final String AVG_MARK_FINAL_ATTENDANCE= "avg("+MARK_FINAL_ATTENDANCE+")";


    /**
     * Для отладки
     */
    private static final String TAG = "StudentRecordsDbAdapter";

    /**
     * Database helper для создания и обновления базы данных
     */
    private DatabaseHelper mDbHelper;

    /**
     * Доступная для записи база данных
     */
    private SQLiteDatabase mDb;

    /**
     * Имя БД
     */
    private static final String DATABASE_NAME = "studentrecords";

    /**
     * Таблица со студентами
     */
    private static final String SQLITE_STUDENT_TABLE = "student";

    /**
     * Таблица с оценками
     */
    private static final String SQLITE_MARKS_TABLE = "marks";

    /**
     * Версия БД
     */
    private static final int DATABASE_VERSION = 7;

    /**
     * Актвити, которое использует этот адаптер
     */
    private final Context mCtx;

    /**
     * Запрос SQLite для создания таблицы учеников
     */
    private static final String CREATE_STUDENT_TABLE =
            "CREATE TABLE if not exists " + SQLITE_STUDENT_TABLE + " ("
                    + STUDENT_ID + " integer PRIMARY KEY NOT NULL,"
                    + STUDENT_FIRSTNAME + " NOT NULL,"
                    + STUDENT_LASTNAME + " NOT NULL);"
            ;

    /**
     * Запрос SQLite, создающий таблицу оценок
     */
    private static final String CREATE_MARKS_TABLE =
            "CREATE TABLE if not exists " + SQLITE_MARKS_TABLE + " ("
                    + STUDENT_ID + " integer PRIMARY KEY NOT NULL,"
                    + MARK_LAB + " real,"
                    + MARK_MIDTERM + " real,"
                    + MARK_FINAL_EXAM + " real,"
                    + MARK_FINAL_ATTENDANCE + " real,"
                    + "CONSTRAINT fk_student FOREIGN KEY (" + MARK_STUDENT_ID + ")"
                    + " REFERENCES " + SQLITE_STUDENT_TABLE + "(" + STUDENT_ID + ")"
                    + ");"
            ;


    /** Database helper для создания и обновления БД
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        /**
         * Constructor.
         * @param context
         */
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /**
         * Создает таблицы БД
         * @param db
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_STUDENT_TABLE);
            db.execSQL(CREATE_MARKS_TABLE);
        }

        /**
         * Обновляет БД
         * @param db
         * @param oldVersion
         * @param newVersion
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + SQLITE_STUDENT_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + SQLITE_MARKS_TABLE);
            onCreate(db);
        }
    }

    /**
     * Constructor
     * @param ctx
     */
    public StudentRecordsDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Инициализирует database helper и БД
     * @return
     * @throws SQLException
     */
    public StudentRecordsDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    /**
     * Закрывает database helper
     */
    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    /**
     * Добавляет студентов в БД студентов
     * @param studentNumber
     * @param firstname
     * @param lastname
     * @return
     */
    public long insertStudent(int studentNumber, String firstname, String lastname) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(STUDENT_ID, studentNumber);
        initialValues.put(STUDENT_FIRSTNAME, firstname);
        initialValues.put(STUDENT_LASTNAME, lastname);

        return mDb.insertOrThrow(SQLITE_STUDENT_TABLE, null, initialValues);
    }

    /**
     * Добавляет оценки студентов в БД оценок студентов
     * @param studentId
     * @param labMark
     * @param midtermMark
     * @param finalExamMark
     * @param finalAttendanceMark
     * @return
     */
    public long insertMarks(int studentId, Double labMark, Double midtermMark, Double finalExamMark, Double finalAttendanceMark) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(STUDENT_ID, studentId);
        initialValues.put(MARK_LAB, labMark);
        initialValues.put(MARK_MIDTERM, midtermMark);
        initialValues.put(MARK_FINAL_EXAM, finalExamMark);
        initialValues.put(MARK_FINAL_ATTENDANCE, finalAttendanceMark);

        return mDb.insertOrThrow(SQLITE_MARKS_TABLE, null, initialValues);
    }

    /**
     * Выбирает всех студентов из БД
     * @return
     */
    public Cursor fetchAllStudents() {
        Cursor mCursor = mDb.query(SQLITE_STUDENT_TABLE, new String[] {STUDENT_ID, STUDENT_FIRSTNAME, STUDENT_LASTNAME},
                null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * Получает студента из БД по его идентификатору
     * @param id
     * @return
     */
    public Cursor fetchStudentById(Long id) {
        Log.w(TAG, id.toString());
        Cursor mCursor = null;
        if (id == null  ||  id.toString().length () == 0)  {
            mCursor = mDb.query(SQLITE_STUDENT_TABLE, new String[] {STUDENT_ID,
                            STUDENT_FIRSTNAME, STUDENT_LASTNAME},
                    null, null, null, null, null);

        }
        else {
            mCursor = mDb.query(true, SQLITE_STUDENT_TABLE, new String[] {STUDENT_ID,
                            STUDENT_FIRSTNAME, STUDENT_LASTNAME},
                    STUDENT_ID + " = " + id, null,
                    null, null, null, null);
        }
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * Получает оценки студента по идентификатору студента
     * @param studentId
     * @return
     */
    public Cursor fetchMarksByStudentId(long studentId) {
        Cursor mCursor = mDb.query(SQLITE_MARKS_TABLE,
                new String[] {MARK_LAB, MARK_MIDTERM, MARK_FINAL_EXAM, MARK_FINAL_ATTENDANCE},
                STUDENT_ID + " = ?",
                new String[]{Long.toString(studentId)},
                null, null, null); {
            if (mCursor != null) {
                mCursor.moveToFirst();
            }
            return mCursor;
        }
    }

    /**
     * Получает средние оценки из БД
     * @return
     */
    public Cursor fetchAverageMarks() {
        Cursor mCursor = mDb.query(SQLITE_MARKS_TABLE,
                new String[] {AVG_MARK_LAB, AVG_MARK_MIDTERM, AVG_MARK_FINAL_EXAM, AVG_MARK_FINAL_ATTENDANCE},
                null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * Обновляет оценки студентов
     * @param studentId
     * @param labMark
     * @param midtermMark
     * @param finalExamMark
     * @param finalAttendanceMark
     * @return
     */
    public int updateMarks(long studentId, Double labMark, Double midtermMark, Double finalExamMark, Double finalAttendanceMark) {
        ContentValues newValues = new ContentValues();
        newValues.put(MARK_LAB, labMark);
        newValues.put(MARK_MIDTERM, midtermMark);
        newValues.put(MARK_FINAL_EXAM, finalExamMark);
        newValues.put(MARK_FINAL_ATTENDANCE, finalAttendanceMark);
        return mDb.update(
                SQLITE_MARKS_TABLE,
                newValues,
                STUDENT_ID + " = ?",
                new String[] {Long.toString(studentId)}
        );
    }

    /**
     * Удаляет студентов и их оценки из БД
     * @param id
     * @return
     */
    public boolean deleteStudentAndMarksById(long id) {
        int doneDelete = 0;
        doneDelete += mDb.delete(
                SQLITE_STUDENT_TABLE,
                STUDENT_ID + " = ?",
                new String[] {Long.toString(id)}
        );
        doneDelete += mDb.delete(
                SQLITE_MARKS_TABLE,
                STUDENT_ID + " = ?",
                new String[] {Long.toString(id)}
        );
        return doneDelete == 2;
    }

    /**
     * Удаляет всех студентов из БД
     * @return
     */
    public boolean deleteAllStudents() {
        int doneDelete = 0;
        doneDelete = mDb.delete(SQLITE_STUDENT_TABLE, null , null);
        Log.w(TAG, Integer.toString(doneDelete));
        return doneDelete > 0;
    }

    /**
     * Удаляет все оценки из БД
     * @return
     */
    public boolean deleteAllMarks() {
        int doneDelete = 0;
        doneDelete = mDb.delete(SQLITE_MARKS_TABLE, null , null);
        Log.w(TAG, Integer.toString(doneDelete));
        return doneDelete > 0;
    }
}
