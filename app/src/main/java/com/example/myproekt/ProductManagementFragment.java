package com.example.myproekt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ProductManagementFragment extends Fragment {
    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private ProductManagementAdapter adapter;
    private EditText searchView;
    private TabLayout tabLayout;
    private ImageButton addButton;
    private List<Category> categories = new ArrayList<>();
    private List<Product> allProducts = new ArrayList<>();
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;
    private String selectedImageName;
    private AlertDialog currentDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_management, container, false);

        dbHelper = new DatabaseHelper(getContext());
        recyclerView = view.findViewById(R.id.recyclerView);
        searchView = view.findViewById(R.id.searchView);
        tabLayout = view.findViewById(R.id.tabLayout);
        addButton = view.findViewById(R.id.addButton);

        adapter = new ProductManagementAdapter(new ArrayList<>(), dbHelper, this::showEditDialog);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadCategories();
        loadAllProducts();

        searchView.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts();
            }
        });

        addButton.setOnClickListener(v -> showAddDialog());

        return view;
    }

    private void showAddDialog() {
        selectedImageUri = null;
        selectedImageName = "";

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Добавить товар");
        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_edit_product, null);

        // Инициализация всех View
        EditText nameInput = dialogView.findViewById(R.id.nameInput);
        EditText priceInput = dialogView.findViewById(R.id.priceInput);
        EditText detailsInput = dialogView.findViewById(R.id.detailsInput);
        Spinner categorySpinner = dialogView.findViewById(R.id.categorySpinner);
        Button selectImageBtn = dialogView.findViewById(R.id.selectImageBtn);
        Button removeImageBtn = dialogView.findViewById(R.id.removeImageBtn);
        TextView imageNameText = dialogView.findViewById(R.id.imageNameText);
        ImageView imagePreview = dialogView.findViewById(R.id.imagePreview);

        removeImageBtn.setVisibility(View.GONE);

        // Настройка адаптера для Spinner с категориями
        ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<Category>(
                getContext(),
                android.R.layout.simple_spinner_item,
                categories) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setText(categories.get(position).getName());
                return view;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setText(categories.get(position).getName());
                return view;
            }
        };

        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        selectImageBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        builder.setView(dialogView);
        builder.setPositiveButton("Добавить", (dialog, which) -> {
            try {
                String name = nameInput.getText().toString().trim();
                double price = Double.parseDouble(priceInput.getText().toString());
                String details = detailsInput.getText().toString().trim();
                Category selectedCategory = (Category) categorySpinner.getSelectedItem();

                if (name.isEmpty()) {
                    Toast.makeText(getContext(), "Введите название товара", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedImageName.isEmpty()) {
                    Toast.makeText(getContext(), "Выберите изображение", Toast.LENGTH_SHORT).show();
                    return;
                }

                Product newProduct = new Product(0, name, selectedImageName, price,
                        selectedCategory.getId(), details);

                if (dbHelper.addProduct(newProduct)) {
                    saveImageToInternalStorage(selectedImageUri, selectedImageName);
                    Toast.makeText(getContext(), "Товар добавлен", Toast.LENGTH_SHORT).show();
                    loadAllProducts();
                } else {
                    Toast.makeText(getContext(), "Ошибка при добавлении товара", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Некорректная цена", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Отмена", null);
        currentDialog = builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            selectedImageName = "product_" + System.currentTimeMillis() + ".jpg";

            if (currentDialog != null) {
                TextView imageNameText = currentDialog.findViewById(R.id.imageNameText);
                ImageView imagePreview = currentDialog.findViewById(R.id.imagePreview);
                Button removeImageBtn = currentDialog.findViewById(R.id.removeImageBtn);

                if (imageNameText != null) {
                    imageNameText.setText(selectedImageName);
                    imageNameText.setVisibility(View.VISIBLE);
                }

                if (imagePreview != null && selectedImageUri != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                requireActivity().getContentResolver(),
                                selectedImageUri);
                        imagePreview.setImageBitmap(bitmap);
                        imagePreview.setVisibility(View.VISIBLE);
                        removeImageBtn.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private boolean saveImageToInternalStorage(Uri imageUri, String imageName) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
            FileOutputStream outputStream = requireContext().openFileOutput(imageName, Context.MODE_PRIVATE);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean deleteImageFromStorage(String imageName) {
        if (imageName != null && !imageName.isEmpty()) {
            try {
                File file = new File(requireContext().getFilesDir(), imageName);
                if (file.exists()) {
                    return file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void loadCategories() {
        categories = dbHelper.getAllCategories();
        tabLayout.removeAllTabs();
        tabLayout.addTab(tabLayout.newTab().setText("Все"));

        for (Category category : categories) {
            tabLayout.addTab(tabLayout.newTab().setText(category.getName()));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterProducts();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadAllProducts() {
        allProducts = dbHelper.getAllProducts();
        filterProducts();
    }

    private void filterProducts() {
        String searchQuery = searchView.getText().toString().toLowerCase();
        int selectedTabPos = tabLayout.getSelectedTabPosition();

        List<Product> filtered = new ArrayList<>();
        for (Product product : allProducts) {
            boolean matchesCategory = selectedTabPos == 0 ||
                    (selectedTabPos > 0 && selectedTabPos <= categories.size() &&
                            product.getCategoryId() == categories.get(selectedTabPos - 1).getId());

            boolean matchesSearch = searchQuery.isEmpty() ||
                    product.getName().toLowerCase().contains(searchQuery) ||
                    (product.getDetails() != null &&
                            product.getDetails().toLowerCase().contains(searchQuery));

            if (matchesCategory && matchesSearch) {
                filtered.add(product);
            }
        }
        adapter.updateProducts(filtered);
    }

    private void showEditDialog(Product product) {
        selectedImageUri = null;
        selectedImageName = "";

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Редактирование товара");

        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_edit_product, null);

        // Инициализация всех View элементов
        EditText nameInput = dialogView.findViewById(R.id.nameInput);
        EditText priceInput = dialogView.findViewById(R.id.priceInput);
        EditText detailsInput = dialogView.findViewById(R.id.detailsInput);
        Spinner categorySpinner = dialogView.findViewById(R.id.categorySpinner);
        Button selectImageBtn = dialogView.findViewById(R.id.selectImageBtn);
        Button removeImageBtn = dialogView.findViewById(R.id.removeImageBtn);
        TextView imageNameText = dialogView.findViewById(R.id.imageNameText);
        ImageView imagePreview = dialogView.findViewById(R.id.imagePreview);

        // Установка текущих значений товара
        nameInput.setText(product.getName());
        priceInput.setText(String.valueOf(product.getPrice()));
        detailsInput.setText(product.getDetails());

        // Настройка адаптера для Spinner с категориями
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(
                getContext(),
                android.R.layout.simple_spinner_item,
                getCategoryNames()) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ((TextView) view).setText(getItem(position));
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                ((TextView) view).setText(getItem(position));
                return view;
            }
        };

        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        // Установка выбранной категории
        int selectedPosition = getCategoryPositionById(product.getCategoryId());
        if (selectedPosition != -1) {
            categorySpinner.setSelection(selectedPosition);
        }

        // Отображение текущего изображения
        if (product.getDescription() != null && !product.getDescription().isEmpty()) {
            imageNameText.setText(product.getDescription());
            imageNameText.setVisibility(View.VISIBLE);
            removeImageBtn.setVisibility(View.VISIBLE);

            try {
                File file = new File(requireContext().getFilesDir(), product.getDescription());
                if (file.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    imagePreview.setImageBitmap(bitmap);
                    imagePreview.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            imageNameText.setVisibility(View.GONE);
            imagePreview.setVisibility(View.GONE);
            removeImageBtn.setVisibility(View.GONE);
        }

        // Обработчики кнопок
        removeImageBtn.setOnClickListener(v -> {
            deleteImageFromStorage(product.getDescription());
            product.setDescription("");
            imageNameText.setVisibility(View.GONE);
            imagePreview.setVisibility(View.GONE);
            removeImageBtn.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Изображение удалено", Toast.LENGTH_SHORT).show();
        });

        selectImageBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        builder.setView(dialogView);
        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            try {
                String oldImageName = product.getDescription();

                product.setName(nameInput.getText().toString());
                product.setPrice(Double.parseDouble(priceInput.getText().toString()));
                product.setDetails(detailsInput.getText().toString());

                if (selectedImageUri != null && !selectedImageName.isEmpty()) {
                    if (oldImageName != null && !oldImageName.isEmpty()) {
                        deleteImageFromStorage(oldImageName);
                    }
                    product.setDescription(selectedImageName);
                    saveImageToInternalStorage(selectedImageUri, selectedImageName);
                }

                int selectedCategoryPosition = categorySpinner.getSelectedItemPosition();
                if (selectedCategoryPosition >= 0 && selectedCategoryPosition < categories.size()) {
                    product.setCategoryId(categories.get(selectedCategoryPosition).getId());
                }

                if (dbHelper.updateProduct(product)) {
                    int position = adapter.getProducts().indexOf(product);
                    if (position != -1) {
                        adapter.notifyItemChanged(position);
                    }
                    Toast.makeText(getContext(), "Товар обновлен", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Ошибка обновления", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Некорректная цена", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNeutralButton("Удалить", (dialog, which) -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Подтверждение удаления")
                    .setMessage("Вы уверены, что хотите удалить этот товар?")
                    .setPositiveButton("Да", (d, w) -> {
                        deleteImageFromStorage(product.getDescription());
                        if (dbHelper.deleteProduct(product.getId())) {
                            Toast.makeText(getContext(), "Товар удален", Toast.LENGTH_SHORT).show();
                            loadAllProducts();
                        } else {
                            Toast.makeText(getContext(), "Ошибка при удалении", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Нет", null)
                    .show();
        });

        builder.setNegativeButton("Отмена", null);
        currentDialog = builder.show();
    }

    // Вспомогательные методы
    private List<String> getCategoryNames() {
        List<String> names = new ArrayList<>();
        for (Category category : categories) {
            names.add(category.getName());
        }
        return names;
    }

    private int getCategoryPositionById(int categoryId) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId() == categoryId) {
                return i;
            }
        }
        return -1;
    }
}