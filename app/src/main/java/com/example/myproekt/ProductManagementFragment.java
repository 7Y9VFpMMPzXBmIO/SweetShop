package com.example.myproekt;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class ProductManagementFragment extends Fragment {
    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private ProductManagementAdapter adapter;
    private EditText searchView;
    private TabLayout tabLayout;
    private ImageButton sortButton;
    private List<Category> categories = new ArrayList<>();
    private List<Product> allProducts = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_management, container, false);

        dbHelper = new DatabaseHelper(getContext());
        recyclerView = view.findViewById(R.id.recyclerView);
        searchView = view.findViewById(R.id.searchView);
        tabLayout = view.findViewById(R.id.tabLayout);
        sortButton = view.findViewById(R.id.sortButton);

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

        sortButton.setOnClickListener(v -> showSortDialog());

        return view;
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
                    product.getCategoryId() == categories.get(selectedTabPos - 1).getId();

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

    private void showSortDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Сортировка")
                .setItems(new String[]{
                                "По умолчанию",
                                "По цене (возрастание)",
                                "По цене (убывание)",
                                "По названию (А-Я)",
                                "По названию (Я-А)"},
                        (dialog, which) -> sortProducts(which))
                .show();
    }

    private void sortProducts(int sortType) {
        List<Product> products = new ArrayList<>(adapter.getProducts());

        switch (sortType) {
            case 0: // По умолчанию
                Collections.sort(products, (p1, p2) -> Integer.compare(p1.getId(), p2.getId()));
                break;
            case 1: // По цене (возрастание)
                Collections.sort(products, (p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice()));
                break;
            case 2: // По цене (убывание)
                Collections.sort(products, (p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()));
                break;
            case 3: // По названию (А-Я)
                Collections.sort(products, (p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
                break;
            case 4: // По названию (Я-А)
                Collections.sort(products, (p1, p2) -> p2.getName().compareToIgnoreCase(p1.getName()));
                break;
        }

        adapter.updateProducts(products);
    }

    private void showEditDialog(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Редактирование товара");

        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_edit_product, null);

        EditText nameInput = dialogView.findViewById(R.id.nameInput);
        EditText priceInput = dialogView.findViewById(R.id.priceInput);
        EditText detailsInput = dialogView.findViewById(R.id.detailsInput);
        Spinner categorySpinner = dialogView.findViewById(R.id.categorySpinner);

        nameInput.setText(product.getName());
        priceInput.setText(String.valueOf(product.getPrice()));
        detailsInput.setText(product.getDetails());

        ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId() == product.getCategoryId()) {
                categorySpinner.setSelection(i);
                break;
            }
        }

        builder.setView(dialogView);
        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            try {
                product.setName(nameInput.getText().toString());
                product.setPrice(Double.parseDouble(priceInput.getText().toString()));
                product.setDetails(detailsInput.getText().toString());

                Category selectedCategory = (Category) categorySpinner.getSelectedItem();
                product.setCategoryId(selectedCategory.getId());

                if (dbHelper.updateProduct(product)) {
                    Toast.makeText(getContext(), "Товар обновлен", Toast.LENGTH_SHORT).show();
                    loadAllProducts();
                } else {
                    Toast.makeText(getContext(), "Ошибка обновления", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Некорректная цена", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }
}