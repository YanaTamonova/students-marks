package com.example.studentmarkscalculator;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.studentmarkscalculator.integration.R;

/**
 * Действия с панелью действий вверху, которая позволяет пользователю переключаться между подприложениями.
 */
public class NavigationActionBarActivity extends ActionBarActivity {

    /**
     * Вызывается при создании меню опций; добавляет элементы на панель действий, если она присутствует.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Вызывается при нажатии элемента панели действий; переключается между подприложениями.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Переключение на другое подприложение.
     * @param c
     */
    private void go(Class c){
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }
}
