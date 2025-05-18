package com.example.myproekt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> users;
    private OnRoleChangeListener roleChangeListener;

    public interface OnRoleChangeListener {
        void onRoleChanged(int userId, int newRoleId);
    }

    public UserAdapter(List<User> users, OnRoleChangeListener listener) {
        this.users = users;
        this.roleChangeListener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.nameTextView.setText(String.format("Имя: %s", user.getName()));
        holder.loginTextView.setText(String.format("Логин: %s", user.getLogin()));
        holder.phoneTextView.setText(String.format("Телефон: %s", user.getPhone()));

        // Настройка Spinner для ролей (только клиент и заблокированный)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                holder.itemView.getContext(),
                R.array.roles_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.roleSpinner.setAdapter(adapter);

        // Установка текущей роли (2-клиент, 3-заблокированный)
        holder.roleSpinner.setSelection(user.getRoleId() - 2);

        holder.roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                int newRoleId = pos + 2; // +2 т.к. у нас только клиент (2) и заблокированный (3)
                if (newRoleId != user.getRoleId()) {
                    roleChangeListener.onRoleChanged(user.getId(), newRoleId);
                    user.setRoleId(newRoleId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, loginTextView, phoneTextView;
        Spinner roleSpinner;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.user_name);
            loginTextView = itemView.findViewById(R.id.user_login);
            phoneTextView = itemView.findViewById(R.id.user_phone);
            roleSpinner = itemView.findViewById(R.id.user_role_spinner);
        }
    }
}