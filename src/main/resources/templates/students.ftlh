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
<img src="https://eng.kpfu.ru/wp-content/uploads/2021/01/kfu-gerb-chb.jpg" height="100">
<div style="text-align: center; display: inline; margin-left: 490px">
    <img src="https://cdn0.iconfinder.com/data/icons/users-android-l-lollipop-icon-pack/24/student-256.png"
         alt="student_logo" width="100" height="100">
    <img src="https://cdn4.iconfinder.com/data/icons/wirecons-free-vector-icons/32/book-256.png" alt="student_logo"
         width="100" height="100">
    <img src="https://cdn2.iconfinder.com/data/icons/asic/50/SLA-256.png" alt="student_logo" width="100" height="100">
    <H1>Students list</H1>

</div>

<#if codeRole == 1>
    <h3><span style="font-size: x-large; color: #ffca00; font-family: serif; ">TEACHER ACCESS</span></h3>
<#elseif codeRole == 2>
    <h3><span style="font-size: x-large; color: #308c00; font-family: serif; ">ADMIN ACCESS</span></h3>
<#elseif codeRole == 0>
    <h3><span style="font-size: x-large; color: #ff769a; font-family: serif; ">STUDENT/PARENT ACCESS</span></h3>
</#if>

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

        <#if codeRole == 1 || codeRole == 2>
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
        <#elseif codeRole == 0>
            <#list students as student>
                <tbody>
                <#attempt>
                    <#if user.studentID == student.id>
                        <tr>
                            <th><a href="/student/${student.id}">${student.id}</a></th>
                            <th>${student.name}</th>
                            <th>${student.surname}</th>
                            <th>${student.age}</th>
                        </tr>
                    </#if>
                    <#recover>
                        <tr>
                            <td colspan="4">Ошибка</td>
                        </tr>
                </#attempt>
                </tbody>
            </#list>
        </#if>

    </table>
</div>

<hr>

<#if codeRole == 2>
    <h2>Добавить нового студента</h2>
    <form action="/student/create" method="post">
        ID: <label>
            <input type="text" style="margin-left: 51px" name="id"
                   value="<#if students?has_content>${students?last.id + 1}<#else>1</#if>"/>
        </label><br><br>

        Имя: <label>
            <input type="text" style="margin-left: 36px" name="name"/>
        </label><br><br>

        Фамилия: <label>
            <input type="text" name="surname"/>
        </label><br><br>

        Возраст: <label>
            <input type="number" style="margin-left: 7px" name="age"/>
        </label><br><br>

        Город: <label>
            <input type="text" style="margin-left: 24px" name="city"/>
        </label><br><br>

        <label>
            <select class="select-css" name="educationalDirection" required="required">
                <#list directions as direction>
                    <option value="${direction}">${direction}</option>
                </#list>
            </select>
        </label>

        <input type="submit" value="Добавить студента"/>
    </form>

    <th><a href="/teacher">Добавить учителя</a></th>

<#elseif codeRole == 1>
    <div style="background-color: rgba(217, 237, 247, 1);
                color: rgba(49, 112, 143, 1);
                border-color: rgba(126, 182, 193, 1);
                width: 25%;
                padding: 12px 16px;
                border-radius: 4px;
                border-style: solid;
                border-width: 1px;
                font-size: 16px;">
        Обратитесь к администратору для изменения списка студентов
    </div>
<#elseif codeRole == 0>
    <div style="background-color: rgba(217, 237, 247, 1);
                color: rgba(49, 112, 143, 1);
                border-color: rgba(126, 182, 193, 1);
                width: 25%;
                padding: 12px 16px;
                border-radius: 4px;
                border-style: solid;
                border-width: 1px;
                font-size: 16px;">
        Просмотр оценок
    </div>
</#if>

</body>
</html>