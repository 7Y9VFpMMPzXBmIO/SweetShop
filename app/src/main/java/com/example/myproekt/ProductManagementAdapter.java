package com.example.myproekt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class ProductManagementAdapter extends RecyclerView.Adapter<ProductManagementAdapter.ViewHolder> {
    private List<Product> products;
    private DatabaseHelper dbHelper;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public void updateProducts(List<Product> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }

    public ProductManagementAdapter(List<Product> products, DatabaseHelper dbHelper, OnProductClickListener listener) {
        this.products = products;
        this.dbHelper = dbHelper;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_management, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        Context context = holder.itemView.getContext();

        // Установка текстовых значений
        holder.nameTextView.setText(product.getName());
        holder.priceTextView.setText(String.format("%.2f руб.", product.getPrice()));

        // Получаем название категории
        Category category = dbHelper.getCategoryById(product.getCategoryId());
        holder.categoryTextView.setText(category != null ? category.getName() : "Без категории");

        // Загрузка изображения
        if (product.getDescription() != null && !product.getDescription().isEmpty()) {
            try {
                File file = new File(context.getFilesDir(), product.getDescription());
                if (file.exists() && file.length() > 0) {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    holder.imageView.setImageBitmap(bitmap);
                } else {
                    // Если файл не найден, пробуем загрузить из ресурсов
                    int resId = context.getResources().getIdentifier(
                            product.getDescription().replace(".", ""), // Удаляем расширение для ресурсов
                            "drawable",
                            context.getPackageName());
                    if (resId != 0) {
                        holder.imageView.setImageResource(resId);
                    } else {
                        holder.imageView.setImageResource(R.drawable.placeholder);
                    }
                }
            } catch (Exception e) {
                holder.imageView.setImageResource(R.drawable.placeholder);
            }
        } else {
            holder.imageView.setImageResource(R.drawable.placeholder);
        }

        holder.itemView.setOnClickListener(v -> listener.onProductClick(product));
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    public List<Product> getProducts() {
        return products;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView;
        TextView priceTextView;
        TextView categoryTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.productImage);
            nameTextView = itemView.findViewById(R.id.productName);
            priceTextView = itemView.findViewById(R.id.productPrice);
            categoryTextView = itemView.findViewById(R.id.productCategory);
        }
    }
}