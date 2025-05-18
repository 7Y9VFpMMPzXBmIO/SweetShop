package com.example.myproekt;

import android.app.AlertDialog;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class OrdersManagementFragment extends Fragment {
    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private OrderManagementAdapter adapter;
    private EditText searchInput;
    private Spinner searchTypeSpinner;
    private Spinner statusSpinner;
    private List<Order> allOrders = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders_management, container, false);

        dbHelper = new DatabaseHelper(getContext());
        recyclerView = view.findViewById(R.id.orders_recycler);
        searchInput = view.findViewById(R.id.search_input);
        searchTypeSpinner = view.findViewById(R.id.search_type_spinner);
        statusSpinner = view.findViewById(R.id.status_spinner);

        // Настройка адаптера
        adapter = new OrderManagementAdapter(dbHelper, this::showOrderDetails, this::updateOrderStatus);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Настройка Spinner для типа поиска
        ArrayAdapter<CharSequence> searchTypeAdapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.order_search_types,
                android.R.layout.simple_spinner_item);
        searchTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchTypeSpinner.setAdapter(searchTypeAdapter);

        // Настройка Spinner для статуса
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.order_status_options,
                android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);

        // Загрузка всех заказов
        loadOrders();

        // Обработчики изменений
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterOrders();
            }
        });

        searchTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                filterOrders();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                filterOrders();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        return view;
    }

    private void loadOrders() {
        allOrders = dbHelper.getAllOrders();
        filterOrders();
    }

    private void filterOrders() {
        String searchQuery = searchInput.getText().toString().toLowerCase();
        int searchType = searchTypeSpinner.getSelectedItemPosition();
        int statusPosition = statusSpinner.getSelectedItemPosition();

        List<Order> filteredOrders = new ArrayList<>();
        for (Order order : allOrders) {
            // Фильтрация по статусу
            boolean matchesStatus = statusPosition == 0 || // Все
                    (statusPosition == 1 && order.getStatus().equals("processing")) ||
                    (statusPosition == 2 && order.getStatus().equals("completed")) ||
                    (statusPosition == 3 && order.getStatus().equals("cancelled"));

            // Фильтрация по поисковому запросу
            boolean matchesSearch = searchQuery.isEmpty();
            if (!matchesSearch) {
                User user = dbHelper.getUserById(order.getUserId()); // Выносим получение пользователя здесь

                switch (searchType) {
                    case 0: // Номер заказа
                        matchesSearch = String.valueOf(order.getId()).contains(searchQuery);
                        break;
                    case 1: // Телефон
                        if (user != null) {
                            matchesSearch = user.getPhone().toLowerCase().contains(searchQuery);
                        }
                        break;
                    case 2: // Имя
                        if (user != null) {
                            matchesSearch = user.getName().toLowerCase().contains(searchQuery);
                        }
                        break;
                    case 3: // Логин
                        if (user != null) {
                            matchesSearch = user.getLogin().toLowerCase().contains(searchQuery);
                        }
                        break;
                }
            }

            if (matchesStatus && matchesSearch) {
                filteredOrders.add(order);
            }
        }
        adapter.setOrders(filteredOrders);
    }

    private void showOrderDetails(Order order) {
        List<OrderItem> items = dbHelper.getOrderItems(order.getId());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Состав заказа #" + order.getId());

        StringBuilder details = new StringBuilder();
        for (OrderItem item : items) {
            details.append(String.format("%s x%d = %.2f руб.\n",
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getProduct().getPrice() * item.getQuantity()));
        }
        details.append("\nИтого: ").append(order.getTotalPrice()).append(" руб.");

        builder.setMessage(details.toString());
        builder.setPositiveButton("Закрыть", null);
        builder.show();
    }

    private void updateOrderStatus(Order order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Изменить статус заказа #" + order.getId());

        String[] statuses = {"В обработке", "Завершен", "Отменен"};
        int currentStatus = 0;
        switch (order.getStatus()) {
            case "completed": currentStatus = 1; break;
            case "cancelled": currentStatus = 2; break;
        }

        builder.setSingleChoiceItems(statuses, currentStatus, null);
        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
            String newStatus = "processing";
            switch (selectedPosition) {
                case 1: newStatus = "completed"; break;
                case 2: newStatus = "cancelled"; break;
            }

            if (dbHelper.updateOrderStatus(order.getId(), newStatus)) {
                Toast.makeText(getContext(), "Статус обновлен", Toast.LENGTH_SHORT).show();
                loadOrders();
            } else {
                Toast.makeText(getContext(), "Ошибка обновления", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }
}