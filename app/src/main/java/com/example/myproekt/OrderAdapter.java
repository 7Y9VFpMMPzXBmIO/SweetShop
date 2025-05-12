package com.example.myproekt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private static List<Order> orders;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
    private static OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public void setOnOrderClickListener(OnOrderClickListener listener) {
        this.listener = listener;
    }


    public void setOrders(List<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.orderNumber.setText(String.format("Заказ #%d", order.getId()));
        holder.orderTotal.setText(String.format("Итого: %.2f руб.", order.getTotalPrice()));
        holder.orderStatus.setText(getStatusString(order.getStatus()));

        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(order.getCreatedAt());
            holder.orderDate.setText(dateFormat.format(date));
        } catch (Exception e) {
            holder.orderDate.setText(order.getCreatedAt());
        }
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

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderNumber, orderTotal, orderStatus, orderDate;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            orderNumber = itemView.findViewById(R.id.order_number);
            orderTotal = itemView.findViewById(R.id.order_total);
            orderStatus = itemView.findViewById(R.id.order_status);
            orderDate = itemView.findViewById(R.id.order_date);
            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onOrderClick(orders.get(getAdapterPosition()));
                }
            });
        }
    }
}