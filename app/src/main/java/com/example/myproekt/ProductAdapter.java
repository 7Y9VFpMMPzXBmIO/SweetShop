package com.example.myproekt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<Product> products;
    private Context context;
    private DatabaseHelper dbHelper;

    public ProductAdapter(List<Product> products, Context context, DatabaseHelper dbHelper) {
        this.products = products;
        this.context = context;
        this.dbHelper = dbHelper;
    }
    public List<Product> getProducts() {
        return products;
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
            holder.imageView.setImageResource(imageResId);
        }

        // Установка данных
        holder.nameTextView.setText(product.getName());
        holder.priceTextView.setText(String.format("%.2f руб.", product.getPrice()));
        holder.weightTextView.setText(product.getWeight());

        String[] categories = {"", "Торт", "Печенье", "Конфеты", "Десерт", "Напиток"};
        holder.categoryTextView.setText(categories[product.getCategoryId()]);

        // Обработчик кнопки "В корзину"
        holder.addToCartButton.setOnClickListener(v -> {
            SharedPreferences prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            int userId = prefs.getInt("current_user_id", -1);

            if (userId != -1) {
                dbHelper.addToCart(userId, product.getId());
                Toast.makeText(context, product.getName() + " добавлен в корзину",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Ошибка: пользователь не авторизован",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Обработчик клика по карточке (показ деталей)
        holder.itemView.setOnClickListener(v -> showProductDetails(product));
    }

    private void showProductDetails(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(product.getName());

        // Форматируем детали (заменяем \n на переносы строк)
        String formattedDetails = product.getDetails() != null ?
                product.getDetails().replace("\\n", "\n") :
                "Описание отсутствует";

        builder.setMessage(formattedDetails);
        builder.setPositiveButton("Закрыть", null);

        // Кнопка добавления в корзину прямо из диалога
        builder.setNeutralButton("Добавить в корзину", (dialog, which) -> {
            SharedPreferences prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            int userId = prefs.getInt("current_user_id", -1);

            if (userId != -1) {
                dbHelper.addToCart(userId, product.getId());
                Toast.makeText(context, product.getName() + " добавлен в корзину",
                        Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView;
        TextView priceTextView;
        TextView weightTextView;
        TextView categoryTextView;
        Button addToCartButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.productImage);
            nameTextView = itemView.findViewById(R.id.productName);
            priceTextView = itemView.findViewById(R.id.productPrice);
            weightTextView = itemView.findViewById(R.id.productWeight);
            categoryTextView = itemView.findViewById(R.id.productCategory);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
        }
    }
}