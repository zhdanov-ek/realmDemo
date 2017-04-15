package com.example.alex.realmdemo.data;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Переопределяем класс, который отвечает за все приложение и инициализируется первым.
 * Указываем его в манифесте, что бы использовался этот, а не дефолтный
 */

public class App extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        // Инициализируем риалм и создаем конфигурацию, которую подаем
        Realm.init(this);
        RealmConfiguration configuration =
                new RealmConfiguration.Builder()
                .name("realm_demo.realm")
                .schemaVersion(2).migration(new MyMigration())
                .build();
        Realm.setDefaultConfiguration(configuration);
    }
}
