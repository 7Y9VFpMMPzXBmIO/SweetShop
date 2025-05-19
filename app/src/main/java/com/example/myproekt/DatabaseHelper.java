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
    private static final int DATABASE_VERSION = 6; // Увеличена версия!

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Создаем таблицу ролей
        db.execSQL("CREATE TABLE roles (" +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT NOT NULL)");

        // Таблица пользователей
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "phone TEXT, " +
                "login TEXT UNIQUE, " +
                "password TEXT, " +
                "avatar_path TEXT, " +
                "role_id INTEGER, " +
                "FOREIGN KEY (role_id) REFERENCES roles (id))");

        // Таблица категорий
        db.execSQL("CREATE TABLE categories (" +
                "_id INTEGER PRIMARY KEY, " +
                "name TEXT NOT NULL)");

        // Обновленная таблица товаров с полем details
        db.execSQL("CREATE TABLE products (" +
                "_id INTEGER PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "description TEXT, " +
                "price REAL NOT NULL, " +
                "category_id INTEGER, " +
                "details TEXT, " + // Новое поле для описания и КБЖУ
                "FOREIGN KEY (category_id) REFERENCES categories (_id))");

        // Таблица корзины
        db.execSQL("CREATE TABLE cart (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "product_id INTEGER NOT NULL, " +
                "quantity INTEGER DEFAULT 1, " +
                "FOREIGN KEY (user_id) REFERENCES users(id), " +
                "FOREIGN KEY (product_id) REFERENCES products(_id))");

        // Таблица заказов
        db.execSQL("CREATE TABLE orders (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "total_price REAL, " +
                "status TEXT DEFAULT 'processing', " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES users (id))");

        // Таблица элементов заказа
        db.execSQL("CREATE TABLE order_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "order_id INTEGER, " +
                "product_id INTEGER, " +
                "quantity INTEGER, " +
                "price_per_item REAL, " +
                "FOREIGN KEY (order_id) REFERENCES orders (id), " +
                "FOREIGN KEY (product_id) REFERENCES products (_id))");

        // Инициализация данных
        initializeSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
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
        // Роли
        db.execSQL("INSERT INTO roles (id, name) VALUES (1, 'manager')");
        db.execSQL("INSERT INTO roles (id, name) VALUES (2, 'client')");
        db.execSQL("INSERT INTO roles (id, name) VALUES (3, 'banned')");

        // Пользователи
        db.execSQL("INSERT INTO users (name, phone, login, password, role_id) " +
                "VALUES ('Админ', '79991112233', 'admin', 'admin', 1)");
        db.execSQL("INSERT INTO users (name, phone, login, password, role_id) " +
                "VALUES ('Тестер', '79871234567', 'r', 'r', 2)");

        // Категории
        db.execSQL("INSERT INTO categories (_id, name) VALUES (1, 'Торты')");
        db.execSQL("INSERT INTO categories (_id, name) VALUES (2, 'Печенье')");
        db.execSQL("INSERT INTO categories (_id, name) VALUES (3, 'Конфеты')");
        db.execSQL("INSERT INTO categories (_id, name) VALUES (4, 'Десерты')");
        db.execSQL("INSERT INTO categories (_id, name) VALUES (5, 'Напитки')");

        // Товары с полными описаниями
        db.execSQL("INSERT INTO products (_id, name, description, price, category_id, details) VALUES " +
                "(1, 'Красный бархат', 'kb', 250.0, 1, 'Нежный бисквит с крем-чизом\\nКалории: 320 ккал\\nБелки: 5г\\nЖиры: 15г\\nУглеводы: 45г')");

        db.execSQL("INSERT INTO products (_id, name, description, price, category_id, details) VALUES " +
                "(2, 'Шоколадный торт', 'sht', 180.0, 1, 'Шоколадный бисквит с шоколадным кремом\\nКалории: 380 ккал\\nБелки: 6г\\nЖиры: 18г\\nУглеводы: 50г')");

        db.execSQL("INSERT INTO products (_id, name, description, price, category_id, details) VALUES " +
                "(3, 'Овсяное печенье', 'op', 150.0, 2, 'Хрустящее печенье с овсяными хлопьями\\nКалории: 420 ккал\\nБелки: 7г\\nЖиры: 16г\\nУглеводы: 65г')");

        db.execSQL("INSERT INTO products (_id, name, description, price, category_id, details) VALUES " +
                "(4, 'Темный шоколад', 'dh', 150.0, 2, 'Горький шоколад 70% какао\\nКалории: 550 ккал\\nБелки: 8г\\nЖиры: 35г\\nУглеводы: 45г')");

        db.execSQL("INSERT INTO products (_id, name, description, price, category_id, details) VALUES " +
                "(5, 'Молочный шоколад', 'mh', 140.0, 2, 'Нежный молочный шоколад\\nКалории: 520 ккал\\nБелки: 7г\\nЖиры: 30г\\nУглеводы: 55г')");

        db.execSQL("INSERT INTO products (_id, name, description, price, category_id, details) VALUES " +
                "(6, 'Карамельные конфеты', 'cc', 90.0, 3, 'Мягкие карамельные конфеты\\nКалории: 380 ккал\\nБелки: 2г\\nЖиры: 8г\\nУглеводы: 75г')");

        db.execSQL("INSERT INTO products (_id, name, description, price, category_id, details) VALUES " +
                "(7, 'Мармеладные конфеты', 'mc', 120.0, 3, 'Фруктовый мармелад\\nКалории: 320 ккал\\nБелки: 1г\\nЖиры: 0г\\nУглеводы: 80г')");

        db.execSQL("INSERT INTO products (_id, name, description, price, category_id, details) VALUES " +
                "(8, 'Чизкейк', 'chease', 170.0, 4, 'Классический Нью-Йоркский чизкейк\\nКалории: 350 ккал\\nБелки: 6г\\nЖиры: 22г\\nУглеводы: 30г')");

        db.execSQL("INSERT INTO products (_id, name, description, price, category_id, details) VALUES " +
                "(9, 'Апельсиновый сок', 'juice', 90.0, 5, 'Свежевыжатый апельсиновый сок\\nКалории: 45 ккал\\nБелки: 1г\\nЖиры: 0г\\nУглеводы: 10г')");

        db.execSQL("INSERT INTO products (_id, name, description, price, category_id, details) VALUES " +
                "(10, 'Яблочный сок', 'apple', 80.0, 5, 'Натуральный яблочный сок\\nКалории: 40 ккал\\nБелки: 0г\\nЖиры: 0г\\nУглеводы: 10г')");

        db.execSQL("INSERT INTO products (_id, name, description, price, category_id, details) VALUES " +
                "(11, 'Виноградный сок', 'berry', 100.0, 5, 'Натуральный виноградный сок\\nКалории: 60 ккал\\nБелки: 0г\\nЖиры: 0г\\nУглеводы: 15г')");

        db.execSQL("INSERT INTO products (_id, name, description, price, category_id, details) VALUES " +
                "(12, 'Шоколадное печенье', 'sp', 150.0, 2, 'Песочное печенье с шоколадом\\nКалории: 480 ккал\\nБелки: 6г\\nЖиры: 25г\\nУглеводы: 60г')");

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

    public int getUserIdByLogin(String login) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id FROM users WHERE login = ?",
                new String[]{login}
        );
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            cursor.close();
            return id;
        }
        cursor.close();
        return -1;
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

    public Product getProductById(int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT _id, name, description, price, category_id, details FROM products WHERE _id = ?",
                new String[]{String.valueOf(productId)}
        );

        if (cursor.moveToFirst()) {
            Product product = new Product(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getDouble(3),
                    cursor.getInt(4),
                    cursor.getString(5)
            );
            cursor.close();
            return product;
        }
        cursor.close();
        return null;
    }

    public List<Product> getProductsByCategory(int categoryId) {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT _id, name, description, price, category_id, details FROM products WHERE category_id = ?",
                new String[]{String.valueOf(categoryId)});


        if (cursor.moveToFirst()) {
            do {
                products.add(new Product(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getDouble(3),
                        cursor.getInt(4),
                        cursor.getString(5)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return products;
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT _id, name, description, price, category_id, details FROM products", null);

        if (cursor.moveToFirst()) {
            do {
                products.add(new Product(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getDouble(3),
                        cursor.getInt(4),
                        cursor.getString(5)
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
                "products.description, products.details, cart.quantity " +
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
                        -1,
                        cursor.getString(5)
                );

                cartItems.add(new CartItem(
                        cursor.getInt(0),
                        product,
                        cursor.getInt(6)
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

    // ==================== МЕТОДЫ ДЛЯ ПРОФИЛЯ И ЗАКАЗОВ ====================
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
                "SELECT oi.id, p._id, p.name, oi.quantity, oi.price_per_item, p.details " +
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
                        -1,
                        cursor.getString(5)
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

        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }

        ContentValues orderValues = new ContentValues();
        orderValues.put("user_id", userId);
        orderValues.put("total_price", total);
        orderValues.put("status", "processing");
        long orderId = db.insert("orders", null, orderValues);

        if (orderId == -1) return -1;

        for (CartItem item : cartItems) {
            ContentValues itemValues = new ContentValues();
            itemValues.put("order_id", orderId);
            itemValues.put("product_id", item.getProduct().getId());
            itemValues.put("quantity", item.getQuantity());
            itemValues.put("price_per_item", item.getProduct().getPrice());
            db.insert("order_items", null, itemValues);
        }

        db.delete("cart", "user_id = ?", new String[]{String.valueOf(userId)});
        return orderId;
    }

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

    public boolean updateOrderStatus(long orderId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", newStatus);
        return db.update("orders", values, "id = ?",
                new String[]{String.valueOf(orderId)}) > 0;
    }

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

    // Новый метод для поиска товаров
    public List<Product> searchProducts(String query) {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT _id, name, description, price, category_id, details FROM products " +
                        "WHERE name LIKE ? OR description LIKE ?",
                new String[]{"%" + query + "%", "%" + query + "%"}
        );

        if (cursor.moveToFirst()) {
            do {
                products.add(new Product(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getDouble(3),
                        cursor.getInt(4),
                        cursor.getString(5)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return products;
    }
    // Получение всех пользователей
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id, name, login, phone, role_id FROM users",
                null);

        if (cursor.moveToFirst()) {
            do {
                int roleId = cursor.getInt(4);
                // Пропускаем админов (role_id = 1)
                if (roleId != 1) {
                    users.add(new User(
                            cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            roleId
                    ));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return users;
    }

    // Обновление роли пользователя
    public boolean updateUserRole(int userId, int newRoleId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("role_id", newRoleId);
        return db.update("users", values, "id = ?",
                new String[]{String.valueOf(userId)}) > 0;
    }
    public String[] checkUserWithStatus(String login, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT roles.name, users.status FROM users " +
                "JOIN roles ON users.role_id = roles.id " +
                "WHERE login = ? AND password = ?";
        Cursor cursor = db.rawQuery(query, new String[]{login, password});

        if (cursor.moveToFirst()) {
            String[] result = new String[2];
            result[0] = cursor.getString(0); // role name
            result[1] = cursor.getString(1); // status
            cursor.close();
            return result;
        }
        cursor.close();
        return null;
    }
    public boolean updateProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", product.getName());
        values.put("price", product.getPrice());
        values.put("details", product.getDetails());
        values.put("category_id", product.getCategoryId());

        return db.update("products", values, "_id = ?",
                new String[]{String.valueOf(product.getId())}) > 0;
    }

    public Category getCategoryById(int categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT _id, name FROM categories WHERE _id = ?",
                new String[]{String.valueOf(categoryId)});

        if (cursor.moveToFirst()) {
            Category category = new Category(
                    cursor.getInt(0),
                    cursor.getString(1));
            cursor.close();
            return category;
        }
        cursor.close();
        return null;
    }
    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id, name, login, phone, role_id FROM users WHERE id = ?",
                new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            User user = new User(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4));
            cursor.close();
            return user;
        }
        cursor.close();
        return null;
    }
    public boolean addProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        try {
            values.put("name", product.getName());
            values.put("description", product.getDescription());
            values.put("price", product.getPrice());
            values.put("category_id", product.getCategoryId());
            values.put("details", product.getDetails());

            long result = db.insert("products", null, values);
            return result != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }

    public boolean deleteProduct(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            // Сначала удаляем из корзины
            db.delete("cart", "product_id = ?", new String[]{String.valueOf(productId)});
            // Затем удаляем сам товар
            int result = db.delete("products", "_id = ?", new String[]{String.valueOf(productId)});
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }
}