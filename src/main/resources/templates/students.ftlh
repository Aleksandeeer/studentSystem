<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Students</title>
</head>
<body>
<H1>Students list</H1>
<hr>
<#list students as student>
    <div>
        <p>
            <b>${student.name} ${student.surname} | </b>Возраст: ${student.age} | ID: ${student.id} <a
                    href="/student/${student.id}">Подробнее...</a>
        </p>
    </div>
</#list>

<hr>

<h2>Добавить нового студента</h2>

<#--TODO сделать автоинкремент айдишника-->
<form action="/student/create" method="post">
    ID: <label>
        <input type="text" name="id"/>
    </label><br><br>

    Имя: <label>
        <input type="text" name="name"/>
    </label><br><br>

    Фамилия: <label>
        <input type="text" name="surname"/>
    </label><br><br>

    Возраст: <label>
        <input type="number" name="age"/>
    </label><br><br>

    Город: <label>
        <input type="text" name="city"/>
    </label><br><br>

    <label>
        <select name="educationalDirection" required="required">
            <#list directions as direction>
                <option value="${direction}">${direction}</option>
            </#list>
        </select>
    </label>

    <input type="submit" value="Добавить студента"/>
</form>

</body>
</html>