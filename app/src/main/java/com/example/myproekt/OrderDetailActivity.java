package com.example.myproekt;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderDetailActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private OrderItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        long orderId = getIntent().getLongExtra("order_id", -1);
        if (orderId == -1) {
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);
        Order order = dbHelper.getOrderById(orderId);

        if (order == null) {
            finish();
            return;
        }

        // Настройка заголовка
        TextView orderNumber = findViewById(R.id.order_number);
        TextView orderDate = findViewById(R.id.order_date);
        TextView orderTotal = findViewById(R.id.order_total);
        TextView orderStatus = findViewById(R.id.order_status);

        orderNumber.setText(String.format("Заказ #%d", order.getId()));
        orderTotal.setText(String.format("Итого: %.2f руб.", order.getTotalPrice()));
        orderStatus.setText(getStatusString(order.getStatus()));

        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(order.getCreatedAt());
            String formattedDate = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(date);
            orderDate.setText(formattedDate);
        } catch (Exception e) {
            orderDate.setText(order.getCreatedAt());
        }

        // Настройка списка товаров
        RecyclerView recyclerView = findViewById(R.id.order_items_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderItemAdapter();
        recyclerView.setAdapter(adapter);

        List<OrderItem> items = dbHelper.getOrderItems(orderId);
        adapter.setItems(items);
    }

    private String getStatusString(String status) {
        switch (status) {
            case "processing": return "В обработке";
            case "completed": return "Завершен";
            case "cancelled": return "Отменен";
            default: return status;
        }
    }

}