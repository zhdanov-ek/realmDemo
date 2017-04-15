package com.example.alex.realmdemo;


//// TODO: 15.12.16 https://realm.io/news/android-search-text-view/

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.EditText;

import com.example.alex.realmdemo.data.User;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRv;
    private Realm mRealm;
    private final static String TAG = "GEK";
    private EditText etName;

    // Листенер, который срабатывает при изменениях данных и вызывает обновление ЮИ
    private final RealmChangeListener<RealmResults<User>> changeListener =
            new RealmChangeListener<RealmResults<User>>() {
        @Override
        public void onChange(RealmResults<User> elements) {
            updateUI(elements);
        }
    };

    // Если у списка нет адаптера то создаем его и подаем списку
    // Если же адаптер уже есть то получаем его и вызываем метод для обновления инфы
    private void updateUI(RealmResults<User> elements) {
        if (mRv.getAdapter() == null) {
            mRv.setAdapter(new UsersAdapter(elements));
        } else {
            UsersAdapter adapter = (UsersAdapter) mRv.getAdapter();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRv = (RecyclerView) findViewById(R.id.rvUsers);

        findViewById(R.id.btnAdd).setOnClickListener(view -> addUser());
        findViewById(R.id.btnGenerate).setOnClickListener(view -> generateUsers(getDummyUsers()));
        findViewById(R.id.btnRemoveAll).setOnClickListener(view -> removeUsers());
        findViewById(R.id.btnRemoveOne).setOnClickListener(view -> removeUser(etName.getText().toString()));
        findViewById(R.id.btnSearch).setOnClickListener(view -> searchUser(etName.getText().toString()));

        etName = (EditText) findViewById(R.id.etName);

        // Получаем доступ к нашему реалму. Этот риалм нужно закрыать после выполнения
        // необходимых операций и не открывать лишних
        mRealm = Realm.getDefaultInstance();

        // Получаем список всех объектов User. По сути это ссылки на объекты в БД
        final RealmResults<User> users = mRealm.where(User.class).findAllAsync();

        // На этот список устанавливаем листенер, который будет срабатывать при каждом изменении данных
        users.addChangeListener(changeListener);
    }

    /**Add user */
    private void addUser(){
        int id = 0;
        RealmResults<User> rawUsers = mRealm.where(User.class).findAllSorted("id", Sort.DESCENDING);
        if (rawUsers.size() > 0) {
            id = rawUsers.first().getId();
            id++;
        }
        String name = etName.getText().toString();
        final User newUser = new User();
        newUser.setId(id);
        newUser.setName(name);
        newUser.setSearchName(name.toLowerCase());
        mRealm.executeTransactionAsync(
                realm -> realm.insertOrUpdate(newUser),
                () -> Log.d(TAG, "onSuccessAddUser: " + newUser.getName() + " added"),
                error -> Log.d(TAG, "onError: "));
    }

    /** Remove all users */
    private void removeUsers(){
        mRealm.executeTransactionAsync(
                realm -> realm.deleteAll(),
                () -> Log.d(TAG, "onSuccessRemoveAllUsers: removed all users"),
                error -> Log.d(TAG, "removeUsers: Error " + error.toString()));
    }

    /** Remove one user with name  (no use Lambda) */
    private void removeUser(String name){
        final User result = mRealm
                .where(User.class)
                .equalTo("name", name)
                .findFirst();
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (result.isValid()) {
                    result.deleteFromRealm();
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccessRemoveUser: removed users " + result.getName());
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.d(TAG, "onError: ");
            }
        });
    }


    /** Search users */
    private List<User> searchUser(String name){
        RealmResults<User> searchUsers = mRealm
                .where(User.class)
                .contains("searchName", name.toLowerCase())
                .findAllSorted("searchName", Sort.ASCENDING);

        return searchUsers;
    }

    /** Generate users */
    private void generateUsers(final List<User> users) {
//        mRealm.beginTransaction();
//        //mRealm.insertOrUpdate(users);
//        mRealm.commitTransaction();
//        mRealm.close();

        mRealm.executeTransactionAsync(
                realm -> realm.insertOrUpdate(users),
                () -> {},
                error -> {});
    }

    @NonNull
    private List<User> getDummyUsers() {
        final List<User> users = new ArrayList<>();
        int min = 0;
        RealmResults<User> rawUsers = mRealm.where(User.class).findAllSorted("id", Sort.DESCENDING);
        if (rawUsers.size() > 0) {
            min = rawUsers.first().getId();
        }
        for (int i = min + 1; i < min + 3; i++) {
            User user = new User();
            user.setId(i);
            user.setName("User #" + i);
            user.setSearchName(user.getName().toLowerCase());
            users.add(user);
        }
        return users;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
