package com.example.alex.realmdemo.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Описание модели, которое по сути является аналогом создания таблицы в БД
 * Обязательно формируется из полей и гетеров с сетерами
 */

public class User extends RealmObject {

    @PrimaryKey
    private int id;
    private String name;
    private String searchName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName.toLowerCase();
    }
}
