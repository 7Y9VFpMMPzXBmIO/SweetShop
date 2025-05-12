package com.example.myproekt;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProfileFragment extends Fragment {
    private static final int PICK_IMAGE = 1;
    private DatabaseHelper dbHelper;
    private int userId;
    private ImageView avatarImage;
    private EditText nameInput, phoneInput;
    private RecyclerView ordersRecycler;
    private OrderAdapter orderAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Инициализация элементов
        avatarImage = view.findViewById(R.id.avatar_image);
        nameInput = view.findViewById(R.id.name_input);
        phoneInput = view.findViewById(R.id.phone_input);
        ordersRecycler = view.findViewById(R.id.orders_recycler);
        Button saveBtn = view.findViewById(R.id.save_btn);

        // Настройка RecyclerView
        ordersRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        orderAdapter = new OrderAdapter();
        ordersRecycler.setAdapter(orderAdapter);

        // Получаем userId из SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("current_user_id", -1);
        dbHelper = new DatabaseHelper(getContext());

        // Загружаем данные пользователя
        loadUserData();

        // Загружаем историю заказов
        loadOrders();

        // Обработчики событий
        avatarImage.setOnClickListener(v -> openGallery());
        saveBtn.setOnClickListener(v -> saveProfile());
        orderAdapter.setOnOrderClickListener(order -> {
            Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
            intent.putExtra("order_id", order.getId());
            startActivity(intent);
        });

        return view;
    }

    private void loadUserData() {
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT name, phone, avatar_path FROM users WHERE id = ?",
                new String[]{String.valueOf(userId)}
        );

        if (cursor.moveToFirst()) {
            nameInput.setText(cursor.getString(0));
            phoneInput.setText(cursor.getString(1));

            // Загрузка аватарки
            String avatarPath = cursor.getString(2);
            if (avatarPath != null) {
                avatarImage.setImageURI(Uri.parse(avatarPath));
            }
        }
        cursor.close();
    }

    private void loadOrders() {
        List<Order> orders = dbHelper.getUserOrders(userId);
        orderAdapter.setOrders(orders);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            avatarImage.setImageURI(imageUri);

            // Сохраняем путь к изображению
            dbHelper.updateUserAvatar(userId, imageUri.toString());
        }
    }

    private void saveProfile() {
        String name = nameInput.getText().toString();
        String phone = phoneInput.getText().toString();

        if (dbHelper.updateUserProfile(userId, name, phone)) {
            Toast.makeText(getContext(), "Профиль сохранен", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Ошибка сохранения", Toast.LENGTH_SHORT).show();
        }
    }
}