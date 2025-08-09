<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Staff Dashboard</title>
</head>
<body>
<h2>Unassigned Complaints</h2>
<table border="1" cellpadding="6">
    <tr>
        <th>ID</th>
        <th>Category</th>
        <th>Description</th>
        <th>Assign</th>
    </tr>
    <c:forEach var="cmp" items="${unassigned}">
        <tr>
            <td>${cmp.id}</td>
            <td>${cmp.category}</td>
            <td>${cmp.description}</td>
            <td>
                <form method="post" action="/staff/assign">
                    <input type="hidden" name="complaintId" value="${cmp.id}"/>
                    <input type="text" name="department" placeholder="Department" required/>
                    <button type="submit">Assign</button>
                </form>
            </td>
        </tr>
    </c:forEach>
</table>
</body>
</html>