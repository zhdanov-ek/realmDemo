package com.example.alex.realmdemo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.alex.realmdemo.data.User;

import java.util.List;

/**
 * Created by Alex on 12.12.2016.
 */

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private List<User> users;
    private LayoutInflater inflater;

    public UsersAdapter(List<User> users) {
        this.users = users;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return users.get(position).getId();
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //ОБъект инфлейтер создаем только один, что бы не делать их для каждого айтема
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        return UserViewHolder.create(inflater, parent);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        holder.bind(users.get(position));

    }

    @Override
    public int getItemCount() {
        return users == null ? 0 : users.size();
    }


    /** Вью холдер */
    static class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvId;

        public static UserViewHolder create(LayoutInflater inflater, ViewGroup parent) {
            return new UserViewHolder(inflater.inflate(R.layout.user_item_layout, parent, false));
        }

        private UserViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvId = (TextView) itemView.findViewById(R.id.tvUserId);
        }

        public void bind(User user) {
            tvName.setText(user.getName() + " (" + user.getSearchName() + ")");
            tvId.setText(String.valueOf(user.getId()));
        }
    }
}
