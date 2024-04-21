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
    <link rel="stylesheet"
          href="../css/login.css">

</head>
<body>

<div class="main">
    <form action="/login" method="post">
        Имя пользователя: <label>
            <input type="text" name="UserLogin"/>
        </label><br><br>

        Пароль: <label>
            <input type="password" name="UserPasswordSHA256"/>
        </label><br><br>

        <div class="wrap">
            <input type="submit" value="Войти"/>
        </div>
    </form>
</div>



</body>
</html>