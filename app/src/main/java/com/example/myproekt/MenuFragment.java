package com.example.myproekt;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MenuFragment extends Fragment {
    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private TabLayout tabLayout;
    private SearchView searchView;
    private ImageButton sortButton;
    private int currentCategoryId = -1;
    private List<Category> categories = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        dbHelper = new DatabaseHelper(getContext());
        recyclerView = view.findViewById(R.id.recyclerView);
        tabLayout = view.findViewById(R.id.tabLayout);
        searchView = view.findViewById(R.id.searchView);
        sortButton = view.findViewById(R.id.sortButton);

        // Инициализация адаптера с пустым списком
        adapter = new ProductAdapter(new ArrayList<>(), getContext(), dbHelper);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadCategories();
        setupSearchView();
        setupSortButton();

        return view;
    }

    private void loadCategories() {
        categories = dbHelper.getAllCategories();
        tabLayout.addTab(tabLayout.newTab().setText("Все"));

        for (Category category : categories) {
            tabLayout.addTab(tabLayout.newTab().setText(category.getName()));
        }

        loadAllProducts();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    loadAllProducts();
                    currentCategoryId = -1;
                } else {
                    int categoryIndex = tab.getPosition() - 1;
                    if (categoryIndex < categories.size()) {
                        currentCategoryId = categories.get(categoryIndex).getId();
                        loadProductsForCategory(currentCategoryId);
                    }
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchProducts(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    if (currentCategoryId == -1) loadAllProducts();
                    else loadProductsForCategory(currentCategoryId);
                }
                return true;
            }
        });
    }

    private void setupSortButton() {
        sortButton.setOnClickListener(v -> showSortDialog());
    }

    private void loadAllProducts() {
        List<Product> allProducts = dbHelper.getAllProducts();
        updateAdapter(allProducts);
    }

    private void loadProductsForCategory(int categoryId) {
        List<Product> products = dbHelper.getProductsByCategory(categoryId);
        updateAdapter(products);
    }

    private void searchProducts(String query) {
        List<Product> productsToSearch = currentCategoryId == -1 ?
                dbHelper.getAllProducts() :
                dbHelper.getProductsByCategory(currentCategoryId);

        List<Product> filtered = new ArrayList<>();
        for (Product product : productsToSearch) {
            if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(product);
            }
        }

        updateAdapter(filtered);
    }

    private void updateAdapter(List<Product> products) {
        adapter = new ProductAdapter(products, getContext(), dbHelper);
        recyclerView.setAdapter(adapter);
    }

    private void showSortDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Сортировка")
                .setItems(new String[]{
                                "По умолчанию",
                                "По цене (↑)",
                                "По цене (↓)",
                                "По названию (А-Я)",
                                "По названию (Я-А)"},
                        (dialog, which) -> sortProducts(which))
                .show();
    }

    private void sortProducts(int sortType) {
        // Получаем продукты из базы данных
        List<Product> products;
        if (currentCategoryId == -1) {
            products = dbHelper.getAllProducts();
        } else {
            products = dbHelper.getProductsByCategory(currentCategoryId);
        }

        // Сортируем продукты
        switch (sortType) {
            case 0:
                Collections.sort(products, (p1, p2) -> Integer.compare(p1.getId(), p2.getId()));
                break;
            case 1:
                Collections.sort(products, (p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice()));
                break;
            case 2:
                Collections.sort(products, (p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()));
                break;
            case 3:
                Collections.sort(products, (p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
                break;
            case 4:
                Collections.sort(products, (p1, p2) -> p2.getName().compareToIgnoreCase(p1.getName()));
                break;
        }

        // Обновляем адаптер
        adapter = new ProductAdapter(products, getContext(), dbHelper);
        recyclerView.setAdapter(adapter);
    }
}