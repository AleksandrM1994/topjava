<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html lang="ru">
<head>
    <title>Meals</title>
    <meta charset="UTF-8">
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>
<table border="1">
    <thead>
    <tr>
        <th>Время</th>
        <th>Описание</th>
        <th>Калории</th>
        <th>Превышение</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="meal" items="${meals}">
        <tr style="${meal.excess == true ? 'color:green' : 'color:red'}">
            <td><c:out value="${meal.dateTime}"/></td>
            <td><c:out value="${meal.description}"/></td>
            <td><c:out value="${meal.calories}"/></td>
            <td><c:out value="${meal.excess}"/></td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>