package org.example;
import org.flywaydb.core.Flyway;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.io.IOException;

public  class App {
    public static Properties properties = new Properties();

    //Если подключение к бд не выполнено, то выводим ошибку
    static {
        try {
            properties.load(App.class.getClassLoader().getResourceAsStream("application.properties"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //void main
    // }(String[] args) {
    public static void main(String[] arg) {
        try (Connection connection = createConnection()) {
            if (connection != null) {
                flywayMigrate(connection);
                performCrudOperations(connection);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Метод для создания подключения к БД
    private static Connection createConnection() throws Exception {
        Class.forName("org.postgresql.Driver");//??
        return DriverManager.getConnection(properties.getProperty("db.url"),
                properties.getProperty("db.user"),
                properties.getProperty("db.password"));
    }


    // Запуск миграций Flyway
    public static void flywayMigrate(Connection connection) {
        //Connection connection = null;
        //flywayMigrate(connection);
        Flyway.configure()

                .dataSource(
                        properties.getProperty("db.url"),
                        properties.getProperty("db.user"),
                        properties.getProperty("db.password"))
                .baselineOnMigrate(true)//запуск первичной миграции
                .load()
                .migrate();
    }
    //Метод для выполнения  CRUD операций
    public static void performCrudOperations(Connection connection) throws Exception {
        try {
            connection.setAutoCommit(false);
            insertNewProductandCustomer(connection);
            readLastThreeOrders(connection);
            updatePrice(connection);
            deleteRecord(connection);
            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            throw e;
        }
    }
    // Вставка данных в таблицы product и customer
    public static void insertNewProductandCustomer(Connection connection) throws Exception {
        try (
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO product(description, price, quantity, category) VALUES (?, ?, ?, ?)");)
            {
                ps.setString(1, "Конфеты");
                ps.setDouble(2, 99.99);
                ps.setInt(3, 10);
                ps.setString(4, "Sweets");
                int insertRows = ps.executeUpdate();
                System.out.println("Вставлена запись в product  " + insertRows + " строка");
            }
            try (
                PreparedStatement psC = connection.prepareStatement(
                        "INSERT INTO customer(first_name, last_name, phone, email) VALUES (?, ?, ?, ?)");)
                {
                    psC.setString(1, "Ольга");
                    psC.setString(2, "Есина");
                    psC.setString(3, "+79024096662");
                    psC.setString(4, "olga_esina@example.com");
                    //psC.executeUpdate();
                    int insertCustomerRows = psC.executeUpdate();
                    System.out.println("Вставлена запись в customer: " + insertCustomerRows + " строка(и)");
                    //}
                }
        try (// создание нового заказа
                PreparedStatement psO = connection.prepareStatement(
                        "INSERT INTO orders(product_id, customer_id, quantity, status_id)  VALUES (?, ?, ?, ?)");)
        {
            psO.setInt(1, 11);
            psO.setInt(2, 10);
            psO.setInt(3, 5);
            psO.setInt(4, 1);
            //psC.executeUpdate();
            int insertOrdersRows = psO.executeUpdate();
            System.out.println("Вставлена запись в orders: " + insertOrdersRows + " строка(и)");
            //}
        }
            }
    //Чтение и вывод последних 5 заказов
            public static void readLastThreeOrders (Connection connection) throws Exception {
                try (PreparedStatement ps = connection.prepareStatement(
                        "SELECT id, product_id, customer_id, quantity FROM orders ORDER BY date_order DESC LIMIT 5");
                     ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        System.out.println("Заказ №" + rs.getInt("id") +
                                ", Продукт ID: " + rs.getInt("product_id") +
                                ", Клиент ID: " + rs.getInt("customer_id") +
                                ", Количество: " + rs.getInt("quantity"));
                    }
                }
                //Вывод списка продуктов
                try (PreparedStatement ps = connection.prepareStatement(
                        "select id, description, price,quantity,category from product p order by 1 asc");
                     ResultSet rsP = ps.executeQuery()) {
                    while (rsP.next()) {
                        System.out.println("Продукт ID " + rsP.getInt("id") +
                                ". Описание: " + rsP.getString("description") +
                                ", Цена: " + rsP.getDouble("price") +
                                ", Количество: " + rsP.getInt("quantity"));
                    }
                }
            }
    //Обновление цены продукта
            public static void updatePrice (Connection connection) throws Exception {
                int idToUpdate = 9;
                double newPrice = 210.00;
        PreparedStatement ps = connection.prepareStatement(
                        "UPDATE product SET price = ? WHERE id = ?");
                {
                    ps.setDouble(1, newPrice);
                    ps.setInt(2, idToUpdate);
                    int rowsUpdate = ps.executeUpdate();
                    System.out.println("Цена " + newPrice + " обновлена для id " + idToUpdate);
                }
    }
            // public static void deleteRecord(Connection connection) throws Exception {
            //    PreparedStatement ps = connection.prepareStatement(
            //            "DELETE FROM product WHERE id = ?");
            //   {
            //       ps.setInt(1, 19);
            //       int deleteRows = ps.executeUpdate();
            //      System.out.println("Записи удалены " + deleteRows);
//        }
            //   }
    //Удаление записей без заказов
            public static void deleteRecord (Connection connection) throws Exception {
                String sql = "DELETE FROM product WHERE id = ? RETURNING id, description";
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setInt(1, 12);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            int deletedId = rs.getInt("id");
                            String description = rs.getString("description");
                            System.out.println("Удалена запись: id=" + deletedId + ", описание=" + description);
                        }
                    }
                }
            }
        }













