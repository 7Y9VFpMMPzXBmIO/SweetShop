package com.example.myproekt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<Product> products;
    private Context context;
    private DatabaseHelper dbHelper;
    private int userId;

    public ProductAdapter(List<Product> products, Context context, DatabaseHelper dbHelper) {
        this.products = products;
        this.context = context;
        this.dbHelper = dbHelper;

        // Получаем ID текущего пользователя
        SharedPreferences prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        this.userId = prefs.getInt("current_user_id", -1);
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

        // Установка изображения
        int imageResId = context.getResources().getIdentifier(
                product.getDescription(),
                "drawable",
                context.getPackageName());
        if (imageResId != 0) {
            holder.productImage.setImageResource(imageResId);
        }

        holder.productName.setText(product.getName());
        holder.productCategory.setText(dbHelper.getCategoryName(product.getCategoryId()));
        holder.productWeight.setText(product.getWeight() + " г");
        holder.productPrice.setText(String.format("%.2f руб.", product.getPrice()));

        // Проверяем, есть ли товар в корзине
        CartItem cartItem = dbHelper.getCartItem(userId, product.getId());
        boolean isInCart = cartItem != null;

        // Настраиваем видимость элементов
        holder.addToCartButton.setVisibility(isInCart ? View.GONE : View.VISIBLE);
        holder.quantityControls.setVisibility(isInCart ? View.VISIBLE : View.GONE);

        if (isInCart) {
            holder.quantityTextView.setText(String.valueOf(cartItem.getQuantity()));
        }

        // Обработчик кнопки добавления в корзину
        holder.addToCartButton.setOnClickListener(v -> {
            dbHelper.addToCart(userId, product.getId(), 1);
            Toast.makeText(context, product.getName() + " добавлен в корзину", Toast.LENGTH_SHORT).show();

            // Обновляем отображение
            notifyItemChanged(position);
        });

        // Обработчики кнопок управления количеством
        holder.increaseButton.setOnClickListener(v -> {
            int newQuantity = cartItem.getQuantity() + 1;
            dbHelper.updateCartItemQuantity(cartItem.getId(), newQuantity);
            holder.quantityTextView.setText(String.valueOf(newQuantity));
        });

        holder.decreaseButton.setOnClickListener(v -> {
            int newQuantity = cartItem.getQuantity() - 1;
            if (newQuantity > 0) {
                dbHelper.updateCartItemQuantity(cartItem.getId(), newQuantity);
                holder.quantityTextView.setText(String.valueOf(newQuantity));
            } else {
                dbHelper.removeFromCart(cartItem.getId());
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateProducts(List<Product> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productCategory;
        TextView productWeight;
        TextView productPrice;
        Button addToCartButton;
        LinearLayout quantityControls;
        ImageButton decreaseButton;
        TextView quantityTextView;
        ImageButton increaseButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productCategory = itemView.findViewById(R.id.productCategory);
            productWeight = itemView.findViewById(R.id.productWeight);
            productPrice = itemView.findViewById(R.id.productPrice);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
            quantityControls = itemView.findViewById(R.id.quantityControls);
            decreaseButton = itemView.findViewById(R.id.decreaseButton);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            increaseButton = itemView.findViewById(R.id.increaseButton);
        }
    }
}