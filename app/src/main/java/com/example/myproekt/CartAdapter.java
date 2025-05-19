package com.example.myproekt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<CartItem> cartItems;
    private Context context;
    private DatabaseHelper dbHelper;
    private int userId;
    private TotalUpdateListener totalUpdateListener;

    public interface TotalUpdateListener {
        void onTotalUpdated();
    }

    public CartAdapter(List<CartItem> cartItems, Context context, DatabaseHelper dbHelper,
                       int userId, TotalUpdateListener listener) {
        this.cartItems = cartItems;
        this.context = context;
        this.dbHelper = dbHelper;
        this.userId = userId;
        this.totalUpdateListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        Product product = cartItem.getProduct();

        // Установка изображения
        int imageResId = context.getResources().getIdentifier(
                product.getDescription(),
                "drawable",
                context.getPackageName());
        if (imageResId != 0) {
            holder.imageView.setImageResource(imageResId);
        }

        holder.nameTextView.setText(product.getName());
        holder.priceTextView.setText(String.format("%.2f руб.", product.getPrice()));
        holder.quantityTextView.setText(String.valueOf(cartItem.getQuantity()));
        holder.totalTextView.setText(String.format("%.2f руб.", cartItem.getTotalPrice()));

        // Обработчики кнопок
        holder.increaseButton.setOnClickListener(v -> {
            int newQuantity = cartItem.getQuantity() + 1;
            cartItem.setQuantity(newQuantity);
            dbHelper.updateCartItemQuantity(cartItem.getId(), newQuantity);
            holder.quantityTextView.setText(String.valueOf(newQuantity));
            holder.totalTextView.setText(String.format("%.2f руб.", product.getPrice() * newQuantity));
            if (totalUpdateListener != null) {
                totalUpdateListener.onTotalUpdated();
            }
        });

        holder.decreaseButton.setOnClickListener(v -> {
            int newQuantity = cartItem.getQuantity() - 1;
            if (newQuantity > 0) {
                cartItem.setQuantity(newQuantity);
                dbHelper.updateCartItemQuantity(cartItem.getId(), newQuantity);
                holder.quantityTextView.setText(String.valueOf(newQuantity));
                holder.totalTextView.setText(String.format("%.2f руб.", product.getPrice() * newQuantity));
            } else {
                dbHelper.removeFromCart(cartItem.getId());
                cartItems.remove(position);
                notifyItemRemoved(position);
            }
            if (totalUpdateListener != null) {
                totalUpdateListener.onTotalUpdated();
            }
        });

        holder.removeButton.setOnClickListener(v -> {
            dbHelper.removeFromCart(cartItem.getId());
            cartItems.remove(position);
            notifyItemRemoved(position);
            if (totalUpdateListener != null) {
                totalUpdateListener.onTotalUpdated();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void updateCartItems(List<CartItem> newItems) {
        this.cartItems = newItems;
        notifyDataSetChanged();
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView;
        TextView priceTextView;
        TextView quantityTextView;
        TextView totalTextView;
        ImageButton increaseButton;
        ImageButton decreaseButton;
        ImageButton removeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.cart_item_image);
            nameTextView = itemView.findViewById(R.id.cart_item_name);
            priceTextView = itemView.findViewById(R.id.cart_item_price);
            quantityTextView = itemView.findViewById(R.id.cart_item_quantity);
            totalTextView = itemView.findViewById(R.id.cart_item_total);
            increaseButton = itemView.findViewById(R.id.cart_item_increase);
            decreaseButton = itemView.findViewById(R.id.cart_item_decrease);
            removeButton = itemView.findViewById(R.id.cart_item_remove);
        }
    }
}