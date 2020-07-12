package example1;

import utils.PropertiesUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class JdbcExample {

    private PropertiesUtils properties = new PropertiesUtils();
    private String url = properties.get("datasource.url");
    private String username = properties.get("datasource.username");
    private String password = properties.get("datasource.password");

    public static void main(String[] args) {
        JdbcExample db = new JdbcExample();
        db.selectFromDb();
        db.preparedStatement();
    }

    private void selectFromDb() {
        // org.postgresql.Driver вызывается с помощью Maven и регистрируется
        // в классе DriverManager (static method registerDriver(Driver driver)).
        // он реализует интерфейс java.sql.Driver
        try {
            // DriverManager с помощью метода интерфейса java.sql.Driver и его метода
            // boolean acceptsURL(String url) определяет одходящий драйвер для URL из
            // списка зарегистрированных в нем драйверов. (по первой часте URL схеме,
            // в нашем случае равной jdbc:postgresql.
            // Далее вызывает реализацию для postgresql интерфейса java.sql.Connection
            // и инициализирует его с помощью второй части URL localhost:5432/user-role-db,
            // а также проходит аутентификацию
            Connection connection = DriverManager.getConnection(url, username, password);
            // через соединение создаем запрос, который реализует интерфейс java.sql.Statement
            Statement statement = connection.createStatement();
            // через запрос выполняем код запроса и получаем результат, реализацию интерфейса java.sql.ResultSet
            // ResultSet представляет собой набор строк таблицы, которую возвращает выполненный запрос
            ResultSet resultSet = statement.executeQuery("SELECT * FROM user_info");
            while (resultSet.next()) {
                String loginLabel = "login";
                //значение ячейки можно получить по названию колонки (Label) иди по порядковому индексу колонке
                String login = String.join(" : ", loginLabel, resultSet.getString(loginLabel));
                String nameLabel = "first_name";
                // индексирование начинается с 1, а не 0 в отличе от массивов
                int nameIndex = 3;
                String name = String.join(" : ", nameLabel, resultSet.getString(nameIndex));
                System.out.println(String.join("\n", login, name));
            }
            // закрытие объектов JDBC особождает ресурсы, это действие критически важно
            // для поизводительности.
            // Однако в JDBC 4 все эти объект реализуют интерфейс AutoCloseable, который автоматически
            // вызывает close() для объект в пределах блока try
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(" Select statement has not been executed");
        }
    }

    private void preparedStatement() {
        // Переменные для примера
        String login = "Tratra";
        String firstName = "Roman";
        String lastName = "Shch";
        String password2 = "sfd212";
        String email = "rrtt@mail.ru";
        String phone = "+375287111233";
        // Запрос с указанием мест для параметров в виде знака "?"
        String sql = "INSERT INTO public.user_info("
                     + "id, login, first_name, last_name, password, email, phone, address, status, password_changed_on,"
                     + "is_enabled, created_on, updated_on) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement stmt = connection.prepareStatement(sql);

                // Установка параметров
            stmt.setInt(1, 3);
            stmt.setString(2, login);
            stmt.setString(3, firstName);
            stmt.setString(4, lastName);
            stmt.setString(5, password2);
            stmt.setString(6, email);
            stmt.setString(7, phone);
            stmt.setString(8, "");
            stmt.setString(9, "");
            stmt.setTimestamp(10, new Timestamp(System.currentTimeMillis()));
            stmt.setBoolean(11, false);
            stmt.setTimestamp(12, new Timestamp(System.currentTimeMillis()));
            stmt.setTimestamp(13, new Timestamp(System.currentTimeMillis()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
