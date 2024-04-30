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
    <link rel="stylesheet" href="../css/input_type_text.css">

</head>

<body>

<div style="text-align: center; display: inline; margin-left: 490px">
    <H1>Teacher list</H1>
</div>

<div class="table-wrapper">
    <table class="fl-table">
        <#if codeRole == 2>
            <tr>
                <thead>
                <th>Login</th>
                <th>Роль</th>
                <th>Пароль (защифрован)</th>
                </thead>
            </tr>
            <#list teachers as teacher>
                <tbody>
                <#attempt>
                    <tr>
                        <th>${teacher.getUserLogin()}</th>
                        <th>${teacher.getUserRole()}</th>
                        <th>${teacher.getUserPasswordSHA256()}</th>
                    </tr>
                    <#recover>
                        <tr>
                            <td colspan="3">Ошибка</td>
                        </tr>
                </#attempt>
                </tbody>
            </#list>
        <#else>
            <tr>
                <td colspan="3">Ошибка: отказ в доступе</td>
            </tr>
        </#if>
    </table>
</div>


<h2>Добавить нового учителя</h2>
<form action="/teacher/create" method="post">
    Логин: <label>
        <input type="text" style="margin-left: 12px;" name="UserLogin"/>
    </label><br><br>

    Пароль: <label>
        <input type="password" name="UserPasswordSHA256"/>
    </label><br><br>

    <input style="margin-top: 20px;" type="submit" value="Добавить учителя"/>
</form>

</body>