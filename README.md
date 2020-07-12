# TMS.18.JDBC
# 18. JDBC (Java DataBase Connectivity)
На этом уроке я расскажу о работе с базами данных в Java. Это очень важный раздел в программировании на любом языке.
И большое количество программ приложений нужнается в системе, которая будет управлять данными. Если возникает вопрос 
**"А почему просто не хранить данные в файлах?"**, то наиболее очевидным ответом на него будет **"Потому что файловая 
система не поддерживает многопользовательское использование (многопоточное)"**. И это необходимо организовывать на 
програмном уровне, что является невыполнимой задачей. Поэтому разработчики используют [СУБД](https://ru.wikipedia.org/wiki/%D0%A1%D0%A3%D0%91%D0%94), 
которая в отличие от файловых систем решает слeдующие проблемы:

1. Общий доступ к данным в сети. Чтоб прочитать файл его нужно скачать. Большой объем файла даже с хорошей скоростью 
очевидно создаст проблему производительности приложения.
2. Возможность одновременного изменения файла несколькими программами.
3. Организация прав доступа.
4. Обработка конфликтных операций при одновременном доступе.

#### Драйверы, соединения, запросы:
СУБД как правило называется сервером баз данных, работает по схеме **клиенты > СУБД > Файловая система**. При работе 
с СУБД клиент выполняет довольно четкие задачи:

1. Соединение с СУБД с аутентификацией и авторизацией. (допустим по сетевой модели TCP/IP*)
2. Отправление команд (запросов) для получения/изменения данных в СУБД
3. Отправление команд (запросов) для работы с структурой СУБД

Запросы выполняются на языке СУБД в частности SQL один из наиболее распространненых. SQL мы будем рассматривать на 
этом уроке. Клиент (компонент, посылающий запросы серверу) в JAVA програмируется с помощью JDBC — Java Database 
Connectivity - архитектура, стандарт взаимодействия Java-приложений с различными СУБД, реализованный в виде 
пакета ```java.sql```. Простыми словами это набор интерфейсов (и классов), которые позволяют работать с базами 
данных. Главным принципом архитектуры является унифицированный (универсальный, стандартный) способ общения с разными 
базами данных. Т.е. с точки зрения приложения на Java общение с Oracle или PostgreSQL не должно отличаться. В 
реальности это не совсем так SQL-запросы могут отличаться за счет разного набора функций для дат, строк и других. 

Наше приложение не должно думать над тем, с какой базой оно работает — все базы должны выглядеть одинаково. Однако 
внутреннее устройство передачи данных для разных СУБД разное. Правила передачи байтов для Oracle отличается от правил 
передачи байтов для MySQL и PostgreSQL. В итоге имеем — с одной стороны все выглядят одинаково, но с другой реализации 
будут разные. Напоминает ли вам это что-нибудь? Еще раз — разные реализации, но одинаковый набор функциональности. 
Думаю, что вы уже догадались — типичный полиморфизм через интерфейсы. Именно на этом и строится архитектура JDBC. JDBC 
основан на концепции так называемых драйверов, позволяющих получать соединение с базой данных по специально 
описанному [URL](https://ru.wikipedia.org/wiki/URL).

**JAVA App -> JDBC -> JDBC-drivers[PostgreSQL,T-SQL] -> СУБД[PostgreSQL,T-SQL]**

Приложение работает с абстракцией JDBC в виде набора интерфейсов. А вот реализация для каждого типа СУБД используется 
своя. Эта реализация называется “JDBC-драйвер”. Для каждого типа СУБД используется свой JDBC-драйвер — для Oracle свой, 
для MySQL — свой. Как приложение выбирает, какой надо использовать, мы увидим чуть позже. Что важно понять сейчас — 
система JDBC позволяет загрузить JDBC-драйвер для конкретной СУБД и единообразно использовать компоненты этого драйвера 
за счет того, что мы к этим компонентам обращаемся не напрямую, а через интерфейсы. Хочу отметить несколько важных 
интерфейсов, мы рассмотрим их в той или иной мере:

1. ```java.sql.DriverManager```
2. ```java.sql.Driver```
3. ```java.sql.Connection```
4. ```java.sql.Statement```
5. ```java.sql.PreparedStatement```
6. ```java.sql.ResultSet```

##### Пример кода:
Инициализируем объекты для создания соединения
``` 
    String url = "jdbc:postgresql://localhost:5432/user-role-db";
    String username = "postgres";
    String password = "postgres";
``` 

 * ```org.postgresql.Driver``` вызывается с помощью Maven и регистрируется в классе DriverManager (static method 
 ```registerDriver(Driver driver)```). Он реализует интерфейс ```java.sql.Driver```
 * DriverManager с помощью метода интерфейса ```java.sql.Driver``` и его метода ```boolean acceptsURL(String url)``` 
 определяет одходящий драйвер для URL из списка зарегистрированных в нем драйверов. (по первой часте URL схеме, в нашем 
 случае равной **jdbc:postgresql**.
 * Далее вызывает реализацию для postgresql интерфейса ```java.sql.Connection``` и инициализирует его с помощью второй 
 части URL **localhost:5432/user-role-db**, а также проходит аутентификацию.

``` 
    try {
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
``` 
закрытие объектов JDBC особождает ресурсы, это действие критически важно для поизводительности. Однако в JDBC 4 все 
эти объект реализуют интерфейс ```AutoCloseable```, который автоматически вызывает ```close()``` для объект в пределах 
блока try.
``` 
        resultSet.close();
        statement.close();
        connection.close();
    } catch (SQLException e) {
        throw new RuntimeException(" Select statement has not been executed");
    }
}
```

Интерфейс ```PreparedStatement``` позволяет выполнять обрабатываь запросы с типа данных отличающимся в разных СУБД по 
формату например даты.

``` 
// Переменные для примера
        String login = "Tratra";
        String firstName = "Roman";
        String lastName = "Shch";
        String password2 = "sfd212";
        String email = "rrtt@mail.ru";
        String phone = "+375287111233";
 
// Запрос с указанием мест для параметров в виде знака "?"
String sql =  "INSERT INTO public.user_info(id, login, first_name, last_name, password, email, phone, address," +
                " status, password_changed_on, is_enabled, created_on, updated_on)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

// Создание запроса. Переменная con - это объект типа Connection
PreparedStatement stmt = con.prepareStatement(sql);
 
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
 
// Выполнение запроса
stmt.executeUpdate();
```

#### Транзакции:
Транзакция - это механизм, который при выполнении последовательности операций, отменяет результат выполненных операций 
в случае, если одна из них не выполнилась успешно. В случае SQL это последовательность запросов, и если один из 
запросов не пройдет, то предыдущие запросы также будут отменены. Например:

|   id   | username | password  |   roles  |
| ------ | -------- | --------  |   -----  |
|   1    |  Gigant  |  qwerty   | customer |
|   2    |  Logan   | holliwood | customer |

1. Правильный запрос -   ``` UPDATE public.users SET roles='customer,VIP' WHERE id=1;```
2. Правильный запрос -   ``` UPDATE public.users SET roles='customer,VIP' WHERE id=2;```
3. **Неправильный запрос** - ``` UPDATE public.users SET roless='customer,VIP' WHERE id=3;```

При при выполнении транзакции сначала первые два запроса выполнятся, а второй выбросит ошибку 
```ERROR:  column "roless" of relation "users" does not exist```, значит транзакция должна быть отменена т.к. одна из 
ее частей не выполнилась. И в нашем примере update первых двух запросов откатиться.

Очень хороший пример из жизни описывающий транзакцию - банковский перевод. С исходного счета деньги списались на 
приходной деньги не начислились - значит списание необходимо отменить.

#### SQL:
SQL — язык позволяющий осуществлять запросы в БД посредством СУБД. В конкретной СУБД, язык SQL может иметь специфичную 
реализацию (свой диалект). DDL и DML — подмножество языка SQL:

* Язык **DDL – Data Definition Language (язык описания данных)** служит для создания и модификации структуры БД, т.е. 
для создания/изменения/удаления таблиц и связей. Сейчас это довольно богатый язык, которые получил ответвление в 
специальностях разработки (BI developer). Пример:
```
CREATE TABLE public.users
   (
       id integer NOT NULL DEFAULT nextval('users_id_seq'::regclass),
       username character varying(63) COLLATE pg_catalog."default" NOT NULL,
       password character varying(255) COLLATE pg_catalog."default" NOT NULL,
       roles character varying(255) COLLATE pg_catalog."default" NOT NULL,
       CONSTRAINT users_pkey PRIMARY KEY (id)
   )
   
DROP TABLE public.users;
```

* Язык **DML - Data Manipulation Language (язык манипулирования данными)** позволяет осуществлять манипуляции с данными 
таблиц, т.е. с ее строками. Он позволяет делать выборку данных из таблиц, добавлять новые данные в таблицы, а так же 
обновлять и удалять существующие данные. Мы будем рассматривать работу с DML SQL.

DML  можно еще условно разделить на две группы относительно работы с JDBC:

* Получение данных — к ним относится оператор ```SELECT```
* Изменение данных — к ним относятся операторы ```INSERT```, ```UPDATE``` и ```DELETE```
* Для первой группы используется уже знакомый нам метод интерфейса Statement — ```executeQuery()```.
* Для второй группа запросов может использоваться другой метод интерфейса Statement — ```executeUpdate()``` - 
вместо ```ResultSet``` возвращает целое число

Особенности работы с SQL:

* Связь между таблицами.
```
CREATE TABLE public.users
   (
       id integer NOT NULL DEFAULT nextval('users_id_seq'::regclass),
       username character varying(63) COLLATE pg_catalog."default" NOT NULL,
       password character varying(255) COLLATE pg_catalog."default" NOT NULL,
       roles character varying(255) COLLATE pg_catalog."default" NOT NULL,
       CONSTRAINT users_pkey PRIMARY KEY (id)
   )

CREATE TABLE public.user_role
  (
      id integer REFERENCES public.users (id),
      name character varying(50) COLLATE pg_catalog."default",
      description character varying(500) COLLATE pg_catalog."default",
      category character varying(200) COLLATE pg_catalog."default",
      CONSTRAINT user_role_pkey PRIMARY KEY (id)
  )
```
* Порядок работы таблицами.
* ```INNER JOIN```, ```LEFT JOIN```
* Форматы данных, их отличия между разными СУБД 


#### Алгоритм действий для начала работы:

1. Установить СУБД *
2. Установить драйвер для СУБД
3. Создать базу данных
4. Инициализировать базу данных

#### Предметный указатель
* [TCP/IP](https://ru.wikipedia.org/wiki/TCP/IP)

| Уровень | Протокол |
| ------ | ------ |
|Прикладной|HTTP, FTP|
|Транспортный|TCP, UDP|
|Сетевой|Для TCP/IP это IP|
|Канальный| физическая Ethernet, IEEE 802.11|

* [PostgreSQL JDBC Driver](https://jdbc.postgresql.org/download.html)