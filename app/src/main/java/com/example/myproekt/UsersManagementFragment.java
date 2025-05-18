package com.example.myproekt;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class UsersManagementFragment extends Fragment {
    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private EditText searchInput;
    private Spinner filterSpinner;
    private List<User> allUsers = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users_management, container, false);

        dbHelper = new DatabaseHelper(getContext());
        recyclerView = view.findViewById(R.id.users_recycler);
        searchInput = view.findViewById(R.id.search_input);
        filterSpinner = view.findViewById(R.id.filter_spinner);
        TextView filterLabel = view.findViewById(R.id.filter_label);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Настройка фильтра
        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.user_filter_options,
                android.R.layout.simple_spinner_item);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(filterAdapter);

        // Загрузка всех пользователей
        loadUsers();

        // Поиск при изменении текста
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Фильтрация при изменении выбора
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterUsers();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return view;
    }

    private void loadUsers() {
        allUsers = dbHelper.getAllUsers();
        filterUsers();
    }

    private void filterUsers() {
        String searchQuery = searchInput.getText().toString().toLowerCase();
        int filterType = filterSpinner.getSelectedItemPosition();

        List<User> filteredUsers = new ArrayList<>();
        for (User user : allUsers) {
            // Фильтрация по выбранному типу
            boolean matchesFilter = (filterType == 0) || // Все
                    (filterType == 1 && user.getRoleId() == 2) || // Только клиенты
                    (filterType == 2 && user.getRoleId() == 3); // Только заблокированные

            // Поиск по всем полям
            boolean matchesSearch = searchQuery.isEmpty() ||
                    user.getName().toLowerCase().contains(searchQuery) ||
                    user.getLogin().toLowerCase().contains(searchQuery) ||
                    user.getPhone().toLowerCase().contains(searchQuery);

            if (matchesFilter && matchesSearch) {
                filteredUsers.add(user);
            }
        }

        adapter = new UserAdapter(filteredUsers, new UserAdapter.OnRoleChangeListener() {
            @Override
            public void onRoleChanged(int userId, int newRoleId) {
                if (newRoleId == 1) { // Запрет на менеджера
                    Toast.makeText(getContext(),
                            "Нельзя назначить роль менеджера",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean success = dbHelper.updateUserRole(userId, newRoleId);
                if (success) {
                    loadUsers(); // Обновляем список
                    Toast.makeText(getContext(),
                            "Роль обновлена",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }
}