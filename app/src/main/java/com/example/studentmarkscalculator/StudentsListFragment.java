package com.example.studentmarkscalculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.studentmarkscalculator.integration.R;


/**
 * Фрагмент, отображающий список записей о студентах
 */
public class StudentsListFragment extends ListFragment implements View.OnClickListener{
    /**
     * Ссылка на родительскую активность
     */
    private StudentMarksCalculatorActivity parentActivity;

    /**
     * Адаптер для взаимодействия с базой данных SQL
     */
    private StudentRecordsDbAdapter dbHelper;

    /**
     * Идентификатор макета для каждого элемента списка
     */
    private int listItemLayout;

    /**
     * Вызывается при создании этого фрагмента
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // We need to use a different list item layout for devices older than Honeycomb
        listItemLayout = R.layout.smc_student_records_list_item;
    }

    /**
     * Вызывается, когда этот фрагмент создает свой пользовательский интерфейс; определяет OnClickListeners
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.smc_student_records, container, false);

        Button addStudentRecordButton = (Button) v.findViewById(R.id.addStudentRecordButton);
        addStudentRecordButton.setOnClickListener(this);

        Button studentsSummaryButton = (Button) v.findViewById(R.id.summaryButton);
        studentsSummaryButton.setOnClickListener(this);

        Button infoButton = (Button) v.findViewById(R.id.infoButton);
        infoButton.setOnClickListener(this);

        return v;
    }

    /**
     * Вызывается, когда этот фрагмент становится видимым для пользователя; получает ссылку на адаптер базы данных и отображает представление списка
     */
    @Override
    public void onStart() {
        super.onStart();

        dbHelper = ((StudentMarksCalculatorActivity) getActivity()).getDbHelper();
        displayListView();

        // When in two-pane layout, set the listview to highlight the selected list item
        // (We do this during onStart because at the point the listview is available.)
        if (getFragmentManager().findFragmentById(R.id.details_fragment) != null) {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
    }

    /**
     * Вызывается, когда этот фрагмент присоединяется к активности; устанавливает ссылку на родительскую активность
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        parentActivity = ((StudentMarksCalculatorActivity)activity);
    }

    /**
     * Вызывается, когда пользователь нажимает на элемент списка; уведомляет родительскую активность
     * @param l
     * @param v
     * @param position
     * @param id
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        ViewGroup listItem = ((ViewGroup)v);
        String firstName = ((TextView)listItem.findViewById(R.id.firstName)).getText().toString();
        String lastName = ((TextView)listItem.findViewById(R.id.lastName)).getText().toString();

        parentActivity.onStudentSelected(id, firstName, lastName);

        getListView().setItemChecked(position, true);
    }

    /**
     * Вызывается, когда пользователь нажимает на представление; обрабатывает нажатия кнопок
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.addStudentRecordButton:
                openAddStudentDialog();
                break;
            case R.id.summaryButton:
                ((StudentMarksCalculatorActivity) getActivity()).openSummary();
                break;
            case R.id.infoButton:
                openInfoDialog();
        }
    }

    /**
     * Открывает диалоговое окно, позволяющее пользователю добавить учащегося в базу данных
     */
    private void openAddStudentDialog() {
        LayoutInflater factory = LayoutInflater.from(getActivity());

        final View studentEntryView = factory.inflate(R.layout.smc_student_entry, null);

        final EditText studentNumberField = (EditText) studentEntryView.findViewById(R.id.studentNumber);
        final EditText studentFirstNameField = (EditText) studentEntryView.findViewById(R.id.firstName);
        final EditText studentLastNameField = (EditText) studentEntryView.findViewById(R.id.lastName);

        new AlertDialog.Builder(getActivity())
                .setTitle("Введите информацию о студенте:")
                .setView(studentEntryView).setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) throws NumberFormatException, SQLiteConstraintException {
                String studentNumber = studentNumberField.getText().toString();
                String studentFirstName = studentFirstNameField.getText().toString();
                String studentLastName = studentLastNameField.getText().toString();

                if (studentNumber.length() > 0
                        && studentFirstName.length() > 0
                        && studentLastName.length() > 0
                        ) { //если нет пустых полей

                    try {
                        dbHelper.insertStudent(
                                Integer.parseInt(studentNumberField.getText().toString()),
                                studentFirstNameField.getText().toString(),
                                studentLastNameField.getText().toString()
                        );

                        dbHelper.insertMarks(
                                Integer.valueOf(studentNumberField.getText().toString()),
                                null,
                                null,
                                null,
                                null
                        );
                    } catch (NumberFormatException e) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Неверное значение: студент не добавлен")
                                .setMessage(e.getMessage())
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                                .show();
                    } catch (SQLiteConstraintException e) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Неверное значение: студент не добавлен")
                                .setMessage("Номер студента уже используется!")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                                .show();
                    }
                    displayListView();
                } else { //пустые поля
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Неверное значение: студент не добавлен")
                            .setMessage("Номер студента, имя или фамилия не могут быть пустыми!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                }
            }
        })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }).show();

    }

    /**
     * Открывает диалоговое окно с информацией о приложении
     */
    private void openInfoDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("О приложении")
                .setMessage("Добро пожаловать в журнал успеваемости студентов! " +
                        "Здесь вы можете увидеть успехи студентов в обучении." +
                        "\n\n-Нажмите на поле студента для просмотра и исправления их баллов или для удаления записи. " +
                        "\n\n-Нажмите 'Добавить студента' для добавления записи о студенте." +
                        "\n\n-Нажмите 'Общая успеваемость' для просмотра средних баллов для всех студентов.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }


    /**
     * Настраивает список студентов со студентами из базы данных
     */
    private void displayListView() {
        Cursor cursor = dbHelper.fetchAllStudents();

        String[] columns = new String[] {
                StudentRecordsDbAdapter.STUDENT_ID,
                StudentRecordsDbAdapter.STUDENT_FIRSTNAME,
                StudentRecordsDbAdapter.STUDENT_LASTNAME
        };

        int[] to = new int[] {
                R.id.studentNumber,
                R.id.firstName,
                R.id.lastName
        };

        SimpleCursorAdapter dataAdapter = new SimpleCursorAdapter(
                getActivity(), listItemLayout,
                cursor,
                columns,
                to,
                0);

        setListAdapter(dataAdapter);

        dataAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence constraint) {
                return dbHelper.fetchStudentById(Long.valueOf(constraint.toString()));
            }
        });
    }
}
