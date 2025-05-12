package com.example.myproekt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "AppDB";
    private static final int DATABASE_VERSION = 4; // Увеличиваем версию!

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Создаем таблицу ролей
        db.execSQL("CREATE TABLE roles (" +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT NOT NULL)");

        // Обновленная таблица пользователей (добавлено поле avatar_path)
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "phone TEXT, " +
                "login TEXT UNIQUE, " +
                "password TEXT, " +
                "avatar_path TEXT, " +
                "role_id INTEGER, " +
                "FOREIGN KEY (role_id) REFERENCES roles (id))");

        // Создаем таблицу категорий
        db.execSQL("CREATE TABLE categories (" +
                "_id INTEGER PRIMARY KEY, " +
                "name TEXT NOT NULL)");

        // Создаем таблицу товаров
        db.execSQL("CREATE TABLE products (" +
                "_id INTEGER PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "description TEXT, " +
                "price REAL NOT NULL, " +
                "category_id INTEGER, " +
                "FOREIGN KEY (category_id) REFERENCES categories (_id))");

        // Таблица корзины
        db.execSQL("CREATE TABLE cart (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "product_id INTEGER NOT NULL, " +
                "quantity INTEGER DEFAULT 1, " +
                "FOREIGN KEY (user_id) REFERENCES users(id), " +
                "FOREIGN KEY (product_id) REFERENCES products(_id))");

        // Новая таблица заказов (без адреса, только самовывоз)
        db.execSQL("CREATE TABLE orders (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "total_price REAL, " +
                "status TEXT DEFAULT 'processing', " + // processing/completed/cancelled
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES users (id))");

        // Новая таблица элементов заказа
        db.execSQL("CREATE TABLE order_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "order_id INTEGER, " +
                "product_id INTEGER, " +
                "quantity INTEGER, " +
                "price_per_item REAL, " +
                "FOREIGN KEY (order_id) REFERENCES orders (id), " +
                "FOREIGN KEY (product_id) REFERENCES products (_id))");

        // Добавляем стартовые данные
        initializeSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Полная пересоздаем все таблицы при обновлении
        db.execSQL("DROP TABLE IF EXISTS order_items");
        db.execSQL("DROP TABLE IF EXISTS orders");
        db.execSQL("DROP TABLE IF EXISTS cart");
        db.execSQL("DROP TABLE IF EXISTS products");
        db.execSQL("DROP TABLE IF EXISTS categories");
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS roles");
        onCreate(db);
    }

    private void initializeSampleData(SQLiteDatabase db) {
        // Добавляем роли
        db.execSQL("INSERT INTO roles (id, name) VALUES (1, 'manager')");
        db.execSQL("INSERT INTO roles (id, name) VALUES (2, 'client')");
        db.execSQL("INSERT INTO roles (id, name) VALUES (3, 'banned')");

        // Добавляем тестового менеджера
        db.execSQL("INSERT INTO users (name, phone, login, password, role_id) " +
                "VALUES ('Админ', '79991112233', 'admin', 'admin', 1)");
        db.execSQL("INSERT INTO users (name, phone, login, password, role_id) " +
                "VALUES ('Тестер', '79871234567', 'r', 'r', 2)");

        // Добавляем категории
        db.execSQL("INSERT INTO categories (_id, name) VALUES (1, 'Торты')");
        db.execSQL("INSERT INTO categories (_id, name) VALUES (2, 'Печенье')");
        db.execSQL("INSERT INTO categories (_id, name) VALUES (3, 'Конфеты')");
        db.execSQL("INSERT INTO categories (_id, name) VALUES (4, 'Десерты')");
        db.execSQL("INSERT INTO categories (_id, name) VALUES (5, 'Напитки')");

        // Добавляем товары
        db.execSQL("INSERT INTO products (_id, name, description, price, category_id) VALUES " +
                "(1, 'Красный бархат', 'kb', 250.0, 1)");
        db.execSQL("INSERT INTO products (_id, name, description, price, category_id) VALUES " +
                "(2, 'Шоколадный торт', 'sht', 180.0, 1)");
        db.execSQL("INSERT INTO products (_id, name, description, price, category_id) VALUES " +
                "(3, 'Овсяное печенье', 'op', 150.0, 2)");
        db.execSQL("INSERT INTO products (_id, name, description, price, category_id) VALUES " +
                "(4, 'Темный шоколад', 'dh', 150.0, 2)");
        db.execSQL("INSERT INTO products (_id, name, description, price, category_id) VALUES " +
                "(5, 'Молочный шоколад', 'mh', 140.0, 2)");
        db.execSQL("INSERT INTO products (_id, name, description, price, category_id) VALUES " +
                "(6, 'Карамельные конфеты', 'cc', 90.0, 3)");
        db.execSQL("INSERT INTO products (_id, name, description, price, category_id) VALUES " +
                "(7, 'Мармеладные конфеты', 'mc', 120.0, 3)");
        db.execSQL("INSERT INTO products (_id, name, description, price, category_id) VALUES " +
                "(8, 'Чизкейк', 'chease', 170.0, 4)");
        db.execSQL("INSERT INTO products (_id, name, description, price, category_id) VALUES " +
                "(9, 'Апельсиновый сок', 'juice', 90.0, 5)");
        db.execSQL("INSERT INTO products (_id, name, description, price, category_id) VALUES " +
                "(10, 'Яблочный сок', 'apple', 80.0, 5)");
        db.execSQL("INSERT INTO products (_id, name, description, price, category_id) VALUES " +
                "(11, 'Виноградный сок', 'berry', 100.0, 5)");
        db.execSQL("INSERT INTO products (_id, name, description, price, category_id) VALUES " +
                "(12, 'Шоколадное печенье', 'sp', 150.0, 2)");

        // Тестовый заказ
        db.execSQL("INSERT INTO orders (user_id, total_price, status) VALUES (2, 450.0, 'completed')");
        db.execSQL("INSERT INTO order_items (order_id, product_id, quantity, price_per_item) VALUES (1, 1, 1, 250.0)");
        db.execSQL("INSERT INTO order_items (order_id, product_id, quantity, price_per_item) VALUES (1, 9, 2, 90.0)");
    }

    // ==================== МЕТОДЫ ДЛЯ РАБОТЫ С ПОЛЬЗОВАТЕЛЯМИ ====================
    public String checkUser(String login, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT roles.name FROM users " +
                "JOIN roles ON users.role_id = roles.id " +
                "WHERE login = ? AND password = ?";
        Cursor cursor = db.rawQuery(query, new String[]{login, password});
        if (cursor.moveToFirst()) {
            String role = cursor.getString(0);
            cursor.close();
            return role;
        }
        cursor.close();
        return null;
    }

    // ==================== МЕТОДЫ ДЛЯ РАБОТЫ С КАТЕГОРИЯМИ И ТОВАРАМИ ====================
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _id, name FROM categories", null);

        if (cursor.moveToFirst()) {
            do {
                categories.add(new Category(
                        cursor.getInt(0),
                        cursor.getString(1)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return categories;
    }

    public List<Product> getProductsByCategory(int categoryId) {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT _id, name, description, price, category_id FROM products WHERE category_id = ?",
                new String[]{String.valueOf(categoryId)}
        );

        if (cursor.moveToFirst()) {
            do {
                products.add(new Product(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getDouble(3),
                        cursor.getInt(4)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return products;
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _id, name, description, price, category_id FROM products", null);

        if (cursor.moveToFirst()) {
            do {
                products.add(new Product(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getDouble(3),
                        cursor.getInt(4)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return products;
    }

    // ==================== МЕТОДЫ ДЛЯ РАБОТЫ С КОРЗИНОЙ ====================
    public void addToCart(int userId, int productId) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, quantity FROM cart WHERE user_id = ? AND product_id = ?",
                new String[]{String.valueOf(userId), String.valueOf(productId)}
        );

        ContentValues values = new ContentValues();
        if (cursor.moveToFirst()) {
            int newQuantity = cursor.getInt(1) + 1;
            values.put("quantity", newQuantity);
            db.update("cart", values, "id = ?",
                    new String[]{String.valueOf(cursor.getInt(0))});
        } else {
            values.put("user_id", userId);
            values.put("product_id", productId);
            db.insert("cart", null, values);
        }
        cursor.close();
    }

    public List<CartItem> getCartItems(int userId) {
        List<CartItem> cartItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT cart.id, products._id, products.name, products.price, " +
                "products.description, cart.quantity " +
                "FROM cart JOIN products ON cart.product_id = products._id " +
                "WHERE cart.user_id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                Product product = new Product(
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(4),
                        cursor.getDouble(3),
                        -1
                );

                cartItems.add(new CartItem(
                        cursor.getInt(0),
                        product,
                        cursor.getInt(5)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return cartItems;
    }

    public void updateCartItemQuantity(int cartItemId, int newQuantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quantity", newQuantity);
        db.update("cart", values, "id = ?", new String[]{String.valueOf(cartItemId)});
    }

    public void removeFromCart(int cartItemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("cart", "id = ?", new String[]{String.valueOf(cartItemId)});
    }

    public void clearCart(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("cart", "user_id = ?", new String[]{String.valueOf(userId)});
    }

    public double getCartTotal(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(products.price * cart.quantity) " +
                        "FROM cart JOIN products ON cart.product_id = products._id " +
                        "WHERE cart.user_id = ?",
                new String[]{String.valueOf(userId)}
        );

        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    // ==================== НОВЫЕ МЕТОДЫ ДЛЯ ПРОФИЛЯ И ЗАКАЗОВ ====================
    public boolean updateUserProfile(int userId, String name, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("phone", phone);
        return db.update("users", values, "id = ?",
                new String[]{String.valueOf(userId)}) > 0;
    }

    public void updateUserAvatar(int userId, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("avatar_path", imagePath);
        db.update("users", values, "id = ?",
                new String[]{String.valueOf(userId)});
    }

    public String getUserAvatarPath(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT avatar_path FROM users WHERE id = ?",
                new String[]{String.valueOf(userId)}
        );
        String path = null;
        if (cursor.moveToFirst()) path = cursor.getString(0);
        cursor.close();
        return path;
    }

    public List<Order> getUserOrders(int userId) {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id, total_price, status, created_at FROM orders " +
                        "WHERE user_id = ? ORDER BY created_at DESC",
                new String[]{String.valueOf(userId)}
        );

        if (cursor.moveToFirst()) {
            do {
                orders.add(new Order(
                        cursor.getInt(0),
                        userId,
                        cursor.getDouble(1),
                        cursor.getString(2),
                        cursor.getString(3)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return orders;
    }

    public List<OrderItem> getOrderItems(long orderId) {
        List<OrderItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT oi.id, p._id, p.name, oi.quantity, oi.price_per_item " +
                        "FROM order_items oi JOIN products p ON oi.product_id = p._id " +
                        "WHERE oi.order_id = ?",
                new String[]{String.valueOf(orderId)}
        );

        if (cursor.moveToFirst()) {
            do {
                Product product = new Product(
                        cursor.getInt(1),
                        cursor.getString(2),
                        null,
                        cursor.getDouble(4),
                        -1
                );
                items.add(new OrderItem(
                        cursor.getInt(0),
                        orderId,
                        product,
                        cursor.getInt(3)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return items;
    }

    public long createOrder(int userId, List<CartItem> cartItems) {
        SQLiteDatabase db = this.getWritableDatabase();
        double total = 0;

        // Считаем общую сумму
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }

        // Создаем заказ
        ContentValues orderValues = new ContentValues();
        orderValues.put("user_id", userId);
        orderValues.put("total_price", total);
        orderValues.put("status", "processing");
        long orderId = db.insert("orders", null, orderValues);

        if (orderId == -1) return -1;

        // Добавляем товары в заказ
        for (CartItem item : cartItems) {
            ContentValues itemValues = new ContentValues();
            itemValues.put("order_id", orderId);
            itemValues.put("product_id", item.getProduct().getId());
            itemValues.put("quantity", item.getQuantity());
            itemValues.put("price_per_item", item.getProduct().getPrice());
            db.insert("order_items", null, itemValues);
        }

        // Очищаем корзину
        db.delete("cart", "user_id = ?", new String[]{String.valueOf(userId)});

        return orderId;
    }

    // Для менеджера: получить все заказы
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id, user_id, total_price, status, created_at FROM orders ORDER BY created_at DESC",
                null
        );

        if (cursor.moveToFirst()) {
            do {
                orders.add(new Order(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getDouble(2),
                        cursor.getString(3),
                        cursor.getString(4)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return orders;
    }

    // Для менеджера: обновить статус заказа
    public boolean updateOrderStatus(long orderId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", newStatus);
        return db.update("orders", values, "id = ?",
                new String[]{String.valueOf(orderId)}) > 0;
    }
    // Получить заказ по ID
    public Order getOrderById(long orderId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id, user_id, total_price, status, created_at FROM orders WHERE id = ?",
                new String[]{String.valueOf(orderId)}
        );

        if (cursor.moveToFirst()) {
            Order order = new Order(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getDouble(2),
                    cursor.getString(3),
                    cursor.getString(4)
            );
            cursor.close();
            return order;
        }
        cursor.close();
        return null;
    }



}