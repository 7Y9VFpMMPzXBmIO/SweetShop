package com.example.myproekt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderManagementAdapter extends RecyclerView.Adapter<OrderManagementAdapter.ViewHolder> {
    private List<Order> orders;
    private DatabaseHelper dbHelper;
    private OnOrderClickListener detailsListener;
    private OnOrderClickListener statusListener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public OrderManagementAdapter(DatabaseHelper dbHelper,
                                  OnOrderClickListener detailsListener,
                                  OnOrderClickListener statusListener) {
        this.dbHelper = dbHelper;
        this.detailsListener = detailsListener;
        this.statusListener = statusListener;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_management, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orders.get(position);
        User user = dbHelper.getUserById(order.getUserId());

        holder.orderNumber.setText("Заказ #" + order.getId());
        holder.orderTotal.setText(String.format("Сумма: %.2f руб.", order.getTotalPrice()));
        holder.orderStatus.setText(getStatusString(order.getStatus()));

        if (user != null) {
            holder.userInfo.setText(user.getName() + " (" + user.getPhone() + ")");
        }

        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(order.getCreatedAt());
            holder.orderDate.setText(dateFormat.format(date));
        } catch (Exception e) {
            holder.orderDate.setText(order.getCreatedAt());
        }

        holder.itemView.setOnClickListener(v -> detailsListener.onOrderClick(order));
        holder.statusButton.setOnClickListener(v -> statusListener.onOrderClick(order));
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }

    private String getStatusString(String status) {
        switch (status) {
            case "processing": return "В обработке";
            case "completed": return "Завершен";
            case "cancelled": return "Отменен";
            default: return status;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderNumber, orderTotal, orderStatus, orderDate, userInfo;
        Button statusButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderNumber = itemView.findViewById(R.id.order_number);
            orderTotal = itemView.findViewById(R.id.order_total);
            orderStatus = itemView.findViewById(R.id.order_status);
            orderDate = itemView.findViewById(R.id.order_date);
            userInfo = itemView.findViewById(R.id.user_info);
            statusButton = itemView.findViewById(R.id.status_button);
        }
    }
}