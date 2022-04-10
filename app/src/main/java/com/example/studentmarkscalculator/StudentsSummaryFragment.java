package com.example.studentmarkscalculator;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.studentmarkscalculator.integration.R;

import java.text.DecimalFormat;

/**
 * Фрагмент, отображающий средние оценки всех учащихся
 */
public class StudentsSummaryFragment extends Fragment {

    /**
     * Вызывается, чтобы этот фрагмент создал свой пользовательский интерфейс
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.smc_students_summary, container, false);
    }

    /**
     * Вызывается, когда этот фрагмент становится видимым для пользователя; отображает сводную информацию из базы данных.
     */
    @Override
    public void onStart() {
        super.onStart();

        Cursor marks = ((StudentMarksCalculatorActivity) getActivity()).getDbHelper().fetchAverageMarks();

        TextView labMarkField = (TextView) getActivity().findViewById(R.id.averageLabMark);
        TextView midtermMarkField = (TextView) getActivity().findViewById(R.id.averageMidtermMark);
        TextView finalExamMarkField = (TextView) getActivity().findViewById(R.id.averageFinalExamMark);
        TextView finalAttendanceMarkField = (TextView) getActivity().findViewById(R.id.averageAttendanceMark);
        TextView overallMarkField = (TextView) getActivity().findViewById(R.id.averageOverallMark);

        double labMark = marks.getDouble(marks.getColumnIndex(StudentRecordsDbAdapter.AVG_MARK_LAB));
        double midtermMark = marks.getDouble(marks.getColumnIndex(StudentRecordsDbAdapter.AVG_MARK_MIDTERM));
        double finalExamMark = marks.getDouble(marks.getColumnIndex(StudentRecordsDbAdapter.AVG_MARK_FINAL_EXAM));
        double finalAttendanceMark = marks.getDouble(marks.getColumnIndex(StudentRecordsDbAdapter.AVG_MARK_FINAL_ATTENDANCE));
        double overallMark = labMark + midtermMark + finalExamMark + finalAttendanceMark;

        DecimalFormat numberFormat = new DecimalFormat("#.00");

        labMarkField.setText(numberFormat.format(labMark));
        midtermMarkField.setText(numberFormat.format(midtermMark));
        finalExamMarkField.setText(numberFormat.format(finalExamMark));
        finalAttendanceMarkField.setText(numberFormat.format(finalAttendanceMark));
        overallMarkField.setText(numberFormat.format(overallMark));

    }

}
