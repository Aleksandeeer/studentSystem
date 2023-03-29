<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Students</title>

    <#--css-->
    <link rel="stylesheet" href="../css/table.css">
    <link rel="stylesheet" href="../css/generalStyles.css">

</head>

<body>

<div style="text-align: center;">
    <H1>Students list</H1>
</div>

<div class="table-wrapper">
    <table class="fl-table">

        <tr>
            <thead>
            <th>ID</th>
            <th>Имя</th>
            <th>Фамилия</th>
            <th>Возраст</th>
            </thead>
        </tr>

        <#list students as student>
            <tbody>
            <#attempt>
                <tr>
                    <th><a href="/student/${student.id}">${student.id}</a></th>
                    <th>${student.name}</th>
                    <th>${student.surname}</th>
                    <th>${student.age}</th>
                </tr>
                <#recover>
                    <tr>
                        <td colspan="4">Ошибка</td>
                    </tr>
            </#attempt>
            </tbody>
        </#list>

    </table>
</div>

<hr>

<h2>Добавить нового студента</h2>


<#--TODO если поставить в айди disabled, то спринг не переваривает данные-->
<form action="/student/create" method="post">
    ID: <label>
        <input type="text" name="id" value="<#if students?has_content>${students?last.id + 1}<#else>1</#if>"/>
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