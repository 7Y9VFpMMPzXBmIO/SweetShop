package com.example.myproekt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {
    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private TextView totalPriceTextView;
    private Button clearCartButton;
    private int userId;
    private Button checkoutButton;
    private View emptyCartView;
    private View nonEmptyCartView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        // Инициализация views
        emptyCartView = view.findViewById(R.id.empty_cart_view);
        nonEmptyCartView = view.findViewById(R.id.non_empty_cart_view);

        SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("current_user_id", -1);

        dbHelper = new DatabaseHelper(getContext());
        recyclerView = view.findViewById(R.id.cart_recycler);
        totalPriceTextView = view.findViewById(R.id.cart_total_price);
        clearCartButton = view.findViewById(R.id.cart_clear_button);
        checkoutButton = view.findViewById(R.id.checkout_button);
        checkoutButton.setOnClickListener(v -> checkout());

        setupRecyclerView();
        updateTotalPrice();

        clearCartButton.setOnClickListener(v -> {
            dbHelper.clearCart(userId);
            adapter = new CartAdapter(new ArrayList<>(), getContext(), dbHelper, userId, this::updateTotalPrice);
            recyclerView.setAdapter(adapter);
            updateTotalPrice();
            updateCartVisibility();
            Toast.makeText(getContext(), "Корзина очищена", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void checkout() {
        if (adapter == null || adapter.getItemCount() == 0) {
            Toast.makeText(getContext(), "Корзина пуста", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Оформление заказа")
                .setMessage("Проверьте номер перед оформлением заказа")
                .setPositiveButton("Подтвердить", (dialog, which) -> {
                    createOrder();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void createOrder() {
        long orderId = dbHelper.createOrder(userId, adapter.getCartItems());
        if (orderId != -1) {
            Toast.makeText(getContext(), "Заказ #" + orderId + " оформлен!",
                    Toast.LENGTH_SHORT).show();

            // Очищаем корзину
            adapter = new CartAdapter(new ArrayList<>(), getContext(),
                    dbHelper, userId, this::updateTotalPrice);
            recyclerView.setAdapter(adapter);
            updateTotalPrice();
            updateCartVisibility();
        } else {
            Toast.makeText(getContext(), "Ошибка оформления заказа",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecyclerView() {
        List<CartItem> cartItems = dbHelper.getCartItems(userId);
        adapter = new CartAdapter(cartItems, getContext(), dbHelper, userId, () -> {
            updateTotalPrice();
            updateCartVisibility();
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        updateCartVisibility();
    }

    private void updateCartVisibility() {
        boolean isEmpty = adapter == null || adapter.getItemCount() == 0;
        emptyCartView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        nonEmptyCartView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

        // Обновляем текст общей суммы даже для пустой корзины
        totalPriceTextView.setText(isEmpty ? "Итого: 0 руб." : totalPriceTextView.getText());
    }

    private void updateTotalPrice() {
        if (adapter == null || adapter.getItemCount() == 0) {
            totalPriceTextView.setText("Итого: 0 руб.");
            return;
        }

        double total = dbHelper.getCartTotal(userId);
        totalPriceTextView.setText(String.format("Итого: %.2f руб.", total));
    }
}