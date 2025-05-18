package com.example.myproekt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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

        // Установка изображения
        if (product.getDescription() != null) {
            int imageResId = context.getResources().getIdentifier(
                    product.getDescription(),
                    "drawable",
                    context.getPackageName());
            if (imageResId != 0) {
                holder.imageView.setImageResource(imageResId);
            }
        }

        holder.nameTextView.setText(product.getName());
        holder.priceTextView.setText(String.format("%.2f руб.", product.getPrice()));

        // Получаем название категории
        Category category = dbHelper.getCategoryById(product.getCategoryId());
        if (category != null) {
            holder.categoryTextView.setText(category.getName());
        }

        holder.itemView.setOnClickListener(v -> listener.onProductClick(product));
    }


    @Override
    public int getItemCount() {
        return products.size();
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