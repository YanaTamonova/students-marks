package com.example.studentmarkscalculator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.studentmarkscalculator.integration.R;

import java.text.DecimalFormat;

/**
 * Фрагмент, который отображает оценки учащегося и позволяет пользователю редактировать их
 */
public class StudentDetailsFragment extends Fragment implements View.OnClickListener{

    /**
     * Присваивание идентификатора студенту
     */
    public final static String ARG_STUDENT_ID = "studentId";

    /**
     * Присваивание имени студенту
     */
    public final static String ARG_STUDENT_FIRSTNAME = "studentFirstName";

    /**
     * Присваивание фамилии студенту
     */
    public final static String ARG_STUDENT_LASTNAME = "studentLastName";


    /**
     * Идентификатор отображаемого студента
     */
    private long studentId = -1;

    /**
     * Имя студента
     */
    private String studentFirstName;

    /**
     * Фамилия студента
     */
    private String studentLastName;

    /**
     * Заголовок в верхней части этого фрагмента, отображающий идентификатор и имя учащегося
     */
    TextView detailsHeader;


    /**
     * EditText для оценок за лабораторные
     */
    EditText labMarkField;

    /**
     * EditText для оценок за контрольные
     */
    EditText midtermMarkField;

    /**
     * EditText для оценки за зачет или экзамен
     */
    EditText finalExamMarkField;

    /**
     * EditText за посещаемость
     */
    EditText finalAttendanceMarkField;

    /**
     * TextView для итоговой оценки
     */
    TextView overallMarkField;

    /**
     * Сохраняет битовое поле изменения параметров конфигурации при уничтожении; используется для обнаружения поворота экрана
     */
    private static int oldConfigInt;

    /**
     * Вызывается, когда этот фрагмент создает свой пользовательский интерфейс; устанавливает ссылки на поля, OnClickListeners
     * и MarkTextWatchers
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.smc_student_details, container, false);

        if (savedInstanceState != null) {
            studentId = savedInstanceState.getInt(ARG_STUDENT_ID);
            studentFirstName = savedInstanceState.getString(ARG_STUDENT_FIRSTNAME);
            studentLastName = savedInstanceState.getString(ARG_STUDENT_LASTNAME);
            //updateDetailsView(studentId, studentFirstName, studentLastName);
        }

        Button deleteStudentRecordButton = (Button) v.findViewById(R.id.deleteStudentRecordButton);
        deleteStudentRecordButton.setOnClickListener(this);

        Button saveMarksButton = (Button) v.findViewById(R.id.saveMarksButton);
        saveMarksButton.setOnClickListener(this);

        labMarkField = (EditText) v.findViewById(R.id.labMark);
        midtermMarkField = (EditText) v.findViewById(R.id.midtermMark);
        finalExamMarkField = (EditText) v.findViewById(R.id.finalExamMark);
        finalAttendanceMarkField = (EditText) v.findViewById(R.id.finalAttendanceMark);
        overallMarkField = (TextView) v.findViewById(R.id.overallMark);

        MarkTextWatcher markTextWatcher = new MarkTextWatcher();
        labMarkField.addTextChangedListener(markTextWatcher);
        midtermMarkField.addTextChangedListener(markTextWatcher);
        finalExamMarkField.addTextChangedListener(markTextWatcher);
        finalAttendanceMarkField.addTextChangedListener(markTextWatcher);

        return v;
    }

    /**
     * Вызывается, когда этот фрагмент становится видимым для пользователя; обновляет детали просмотра при необходимости
     */
    @Override
    public void onStart() {
        super.onStart();

        Bundle args = getArguments();
        if (args != null) {
            updateDetailsView(args.getLong(ARG_STUDENT_ID), args.getString(ARG_STUDENT_FIRSTNAME), args.getString(ARG_STUDENT_LASTNAME));
        } else if (studentId != -1) {
            updateDetailsView(studentId, studentFirstName, studentLastName);
        }
    }

    /**
     * Вызывается при закрытии этого фрагмента; хранит статическое битовое поле (int)
     * параметры конфигурации для обнаружения поворота экрана
     */
    public void onDestroy() {
        super.onDestroy();
        oldConfigInt = getActivity().getChangingConfigurations();
    }

    /**
     * Вызывается, когда пользователь нажимает на представление; обрабатывает нажатия кнопок
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.deleteStudentRecordButton:
                ((StudentMarksCalculatorActivity) getActivity()).getDbHelper().deleteStudentAndMarksById(studentId);
                getFragmentManager().popBackStackImmediate();
                break;
            case R.id.saveMarksButton:
                new saveMarksTask().execute();
                break;
        }
    }

    /**
     * Обновляет представление сведений данными об учениках из базы данных,
     * если этот фрагмент не был воссоздан после поворота экрана, и в этом случае
     * он сохраняет данные из предыдущего экземпляра
     * @param id
     * @param firstName
     * @param lastName
     */
    public void updateDetailsView(long id, String firstName, String lastName) {

        detailsHeader = (TextView) getActivity().findViewById(R.id.header);
        detailsHeader.setText(Long.toString(id)+" "+firstName+" "+lastName);

        if ((oldConfigInt & ActivityInfo.CONFIG_ORIENTATION) != ActivityInfo.CONFIG_ORIENTATION) {
            Cursor marks = ((StudentMarksCalculatorActivity) getActivity()).getDbHelper().fetchMarksByStudentId(id);

            String labMark = marks.getString(marks.getColumnIndex(StudentRecordsDbAdapter.MARK_LAB));
            String midtermMark = marks.getString(marks.getColumnIndex(StudentRecordsDbAdapter.MARK_MIDTERM));
            String finalExamMark = marks.getString(marks.getColumnIndex(StudentRecordsDbAdapter.MARK_FINAL_EXAM));
            String finalAttendanceMark = marks.getString(marks.getColumnIndex(StudentRecordsDbAdapter.MARK_FINAL_ATTENDANCE));

            labMarkField.setText(labMark);
            midtermMarkField.setText(midtermMark);
            finalExamMarkField.setText(finalExamMark);
            finalAttendanceMarkField.setText(finalAttendanceMark);
            updateOverallMark();
        }

        studentId = id;
    }

    /**
     * Сохраняет идентификатор студента, когда этот фрагмент закрывается
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(ARG_STUDENT_ID, studentId);
    }

    /**
     * Updates the overall mark field.
     */
    private void updateOverallMark() {
        double overallMark = 0;
        for (EditText field : new EditText[] {labMarkField, midtermMarkField, finalExamMarkField, finalAttendanceMarkField}) {
            String content = field.getText().toString();
            try {
                overallMark += Double.parseDouble(content);
            }
            catch (NumberFormatException e) {}
        }

        DecimalFormat numberFormat = new DecimalFormat("#.00");
        overallMarkField.setText(numberFormat.format(overallMark));
    }

    /**
     * Сохраняет оценки в оценки EditTexts в базе данных.
     * @return
     */
    private int saveMarks() throws NumberFormatException {
        Double[] marks = new Double[4];
        TextView[] fields = new TextView[] {labMarkField, midtermMarkField, finalExamMarkField, finalAttendanceMarkField};
        for (int i = 0; i < fields.length; i++) {
            String fieldText = fields[i].getText().toString();
            if (fieldText==null || fieldText.length()==0) {
                marks[i] = null;
            }
            else {
                try {
                    marks[i] = Double.valueOf(fields[i].getText().toString());
                } catch (NumberFormatException e) {
                    return -1;
                }
            }
        }
        return ((StudentMarksCalculatorActivity) getActivity()).getDbHelper().updateMarks(studentId, marks[0], marks[1], marks[2], marks[3]);
    }

    /**
     * Класс обработчика события, когда пользователь редактирует поле метки
     */
    private class MarkTextWatcher implements TextWatcher {

        /**
         * Вызывается перед изменением текста; ничего не делает, но требуется
         * @param s
         * @param start
         * @param count
         * @param after
         */
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        /**
         * Вызывается перед изменением текста; ничего не делает, но требуется
         * @param s
         * @param start
         * @param before
         * @param count
         */
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        /**
         * Вызывается после изменения текста; обновляет поле общей оценки
         * @param s
         */
        @Override
        public void afterTextChanged(Editable s) {
            updateOverallMark();
        }
    }

    /**
     * Выполняет запрос на сохранение оценок в базе данных в фоновом режиме
     */
    private class saveMarksTask extends AsyncTask<Void, Void, Integer> {

        /**
         * Выполняет запрос на обновление сохраненных оценок в базе данных в фоновом режиме
         * @param params
         * @return
         */
        @Override
        protected Integer doInBackground(Void... params) {
            return saveMarks();
        }

        /**
         * Отображает сообщение, было обновление успешным или нет
         * @param result
         */
        @Override
        protected void onPostExecute(Integer result) {
            if (result == 1) new AlertDialog.Builder(getActivity())
                    .setTitle("Оценки сохранены.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .show();
            else if (result == -1) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Оценки не сохранены.")
                        .setMessage("Впишите число")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        }
    }
}
