## Формат файла connection.xml - файл подключении к бд
Обязательно должен быть 1 connect с ключом name main - это главный connect который используется по умолчанию.

Также в файле не должно быть несколько connect с одинаковым именем.
```
    type: postgresql | 
```
```xml
<?xml version="1.0" encoding="UTF-8"?>
<connections>
    <connect>
        <type>type</type>  <!-- Наименование СУБД -->
        <url>url</url> <!-- Путь подключение к БД -->
        <user>user</user><!-- Пользователь БД -->
        <password>password</password><!-- Пароль БД -->
        <name>main</name><!-- Уникальное наименование по которому будет происходить обращение к соединению к бд -->
    </connect>
    <connect>
        <type>type2</type>
        <url>url2</url>
        <user>user2</user>
        <password>password2</password>
        <name>name2</name>
    </connect>
</connections>
```