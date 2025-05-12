package com.example.myproekt;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<Product> products;
    private Context context;
    private DatabaseHelper dbHelper;
    private int selectedPosition = -1;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable resetSelectionRunnable;
    private final int defaultBackgroundColor;
    private final int selectedBackgroundColor;

    public ProductAdapter(List<Product> products, Context context, DatabaseHelper dbHelper) {
        this.products = products;
        this.context = context;
        this.dbHelper = dbHelper;
        this.defaultBackgroundColor = Color.TRANSPARENT;
        this.selectedBackgroundColor = ContextCompat.getColor(context, R.color.selected_item_color);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);

        // Установка выделения
        holder.itemView.setBackgroundColor(
                position == selectedPosition ? selectedBackgroundColor : defaultBackgroundColor
        );

        // Установка данных товара
        int imageResId = context.getResources().getIdentifier(
                product.getDescription(),
                "drawable",
                context.getPackageName());
        if (imageResId != 0) {
            holder.imageView.setImageResource(imageResId);
        }

        holder.nameTextView.setText(product.getName());
        holder.priceTextView.setText(String.format("%.2f руб.", product.getPrice()));
        holder.weightTextView.setText(product.getWeight());

        String[] categories = {"", "Торт", "Печенье", "Конфеты", "Десерт", "Напиток"};
        holder.categoryTextView.setText(categories[product.getCategoryId()]);

        // Обработчик нажатия
        holder.itemView.setOnClickListener(v -> {
            // Отменяем предыдущий сброс выделения
            if (resetSelectionRunnable != null) {
                handler.removeCallbacks(resetSelectionRunnable);
            }

            // Обновляем выделение
            int prevSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            if (prevSelected != -1) {
                notifyItemChanged(prevSelected);
            }
            notifyItemChanged(selectedPosition);

            SharedPreferences prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            int userId = prefs.getInt("current_user_id", -1);

            if (userId != -1 && dbHelper != null) {
                dbHelper.addToCart(userId, product.getId());

                // Устанавливаем новый сброс выделения
                resetSelectionRunnable = () -> {
                    if (selectedPosition == holder.getAdapterPosition()) {
                        selectedPosition = -1;
                        notifyItemChanged(holder.getAdapterPosition());
                    }
                };
                handler.postDelayed(resetSelectionRunnable, 1000); // 1 секунда
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public List<Product> getProducts() {
        return products;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView;
        TextView priceTextView;
        TextView weightTextView;
        TextView categoryTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.productImage);
            nameTextView = itemView.findViewById(R.id.productName);
            priceTextView = itemView.findViewById(R.id.productPrice);
            weightTextView = itemView.findViewById(R.id.productWeight);
            categoryTextView = itemView.findViewById(R.id.productCategory);
        }
    }
}