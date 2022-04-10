package com.example.studentmarkscalculator;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.example.studentmarkscalculator.NavigationActionBarActivity;
import com.example.studentmarkscalculator.integration.R;

/**
 * Основное действие для вспомогательного приложения калькулятора оценок учащихся
 */
public class StudentMarksCalculatorActivity extends NavigationActionBarActivity {

    /**
     * Адаптер для взаимодействия с базой данных SQL
     */
    private StudentRecordsDbAdapter dbHelper;

    /**
     *Вызывается при создании этой активности
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smc_student_marks_calculator);

        dbHelper = new StudentRecordsDbAdapter(this);
        dbHelper.open();

        if (findViewById(R.id.fragment_container) != null) {

            if (savedInstanceState != null) {
                return;
            }

            StudentsListFragment firstFragment = new StudentsListFragment();
            firstFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }
    }

    /**
     * Вызывается, когда выбирается идентификатор студента; открывает StudentDetailsFragment
     * @param id
     * @param firstName
     * @param lastName
     */
    public void onStudentSelected(long id, String firstName, String lastName) {

        StudentDetailsFragment detailsFrag = (StudentDetailsFragment)
                getSupportFragmentManager().findFragmentById(R.id.details_fragment);

        if (detailsFrag != null) {

            detailsFrag.updateDetailsView(id, firstName, lastName);

        } else {

            StudentDetailsFragment newFragment = new StudentDetailsFragment();
            Bundle args = new Bundle();
            args.putLong(StudentDetailsFragment.ARG_STUDENT_ID, id);
            args.putString(StudentDetailsFragment.ARG_STUDENT_FIRSTNAME, firstName);
            args.putString(StudentDetailsFragment.ARG_STUDENT_LASTNAME, lastName);
            newFragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);


            transaction.commit();
        }
    }

    /**
     * Открывает StudentsSummaryFragment
     */
    public void openSummary() {

        StudentDetailsFragment detailsFrag = (StudentDetailsFragment)
                getSupportFragmentManager().findFragmentById(R.id.details_fragment);

        if (detailsFrag != null) {

        } else {

            StudentsSummaryFragment newFragment = new StudentsSummaryFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    /**
     * Возвращает адаптер базы данных
     * @return
     */
    public StudentRecordsDbAdapter getDbHelper() {return dbHelper;}
}
