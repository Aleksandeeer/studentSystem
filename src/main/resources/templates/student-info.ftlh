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


    <#--js-->
    <#--TODO не работает импорт скрипта-->
    <script src="../js/subjectRequest.js"></script>
</head>
<body>

<div style="text-align: center;">
    <H1>Student</H1>

    <hr>

    <b>Имя: </b> ${student.name}<br/>
    <b>Фамилия: </b> ${student.surname}<br/>
    <b>Возраст: </b> ${student.age}<br/>
    <b>Город: </b> ${student.city}<br/>
    <b>Направление: </b> ${student.educationalDirection}<br/>
</div>

<div class="table-wrapper">
    <table class="fl-table">
        <tr>
            <thead>
            <th>Дата</th>
            <th>Предмет</th>
            <th>Оценка</th>
            </thead>
        </tr>

        <#--TODO: при создании нового студента не работает таблица оценок до перезапуска проги-->
        <#if student.dateList?size == 0>
            <tbody>
            <tr>
                <td colspan="3">Оценки отсутствуют"</td>
            </tr>
            </tbody>
        <#else>
            <#list student.dateList as date>
                <tbody>
                <#attempt>
                    <tr>
                        <#assign index = date?index>
                        <th>${date}</th>
                        <th>${student.subjectList[index]}</th>
                        <th>${student.markList[index]}</th>
                    </tr>
                    <#recover>
                        <tr>
                            <td colspan="3">Ошибка</td>
                        </tr>
                </#attempt>
                </tbody>
            </#list>
        </#if>

    </table>
</div>

<button onclick="createRow()">Добавить запись</button>
<div id="form-container"></div>

<script>
    //TODO не отрабатывает метод
    function checkValue() {
        const inputValue = Number(document.getElementById("input2").value);
        const validationMessage = validateInput(inputValue);

        if (validationMessage) {
            alert(validationMessage);
        } else {
            // Proceed to the next step
            console.log("Input value is valid");
        }
    }

    function validateInput(inputValue) {
        const minValue = 1;
        const maxValue = 5;

        if (inputValue < minValue || inputValue > maxValue) {
            return "Некорректная оценка";
        }

        return null;
    }

    function createRow() {
        let form = document.createElement("form");
        form.action = "/student/insert/" + encodeURIComponent("${student.id}");
        form.method = "post";

        //Дата
        let input1 = document.createElement("input");
        input1.type = "text";
        input1.placeholder = "Date";
        input1.name = "Date";
        input1.id = "Date";

        //Выбор предмета из выпадающего списка в соответствии с учебным направлением
        let select = document.createElement("select");
        select.id = "Subject";
        select.name = "Subject";
        let xhr = new XMLHttpRequest();
        xhr.open("GET", "/subjects?direction=" + encodeURIComponent("${student.educationalDirection}"));
        xhr.onload = function () {
            if (xhr.status === 200) {
                let data = JSON.parse(xhr.responseText);
                for (let i = 0; i < data.length; i++) {
                    let option = document.createElement("option");
                    option.value = data[i];
                    option.text = data[i];
                    select.add(option);
                }
            }
        };
        xhr.send();

        //Оценка
        let input2 = document.createElement("input");
        input2.type = "text";
        input2.placeholder = "Mark";
        input2.name = "Mark"
        input2.id = "Mark";

        //Сохранить
        let button = document.createElement("button");
        button.textContent = "Сохранить";
        button.type = "submit";
        button.id = "submitButton"
        button.class = "addButton";
        button.addEventListener("click", checkValue)

        //Добавление всех элементов в форму
        form.appendChild(input1);
        form.appendChild(select);
        form.appendChild(input2);
        form.appendChild(document.createElement("br"));
        form.appendChild(button);

        //Добавление формы в контейнер
        let container = document.getElementById("form-container");
        container.appendChild(form);
    }
</script>

<br>
<hr>
<br>

<form action="/student/delete/${student.id}" method="post">
    <input type="submit" value="Удалить студента"/>
</form>

</body>
</html>