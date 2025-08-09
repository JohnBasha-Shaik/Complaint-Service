<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>My Complaints</title>
</head>
<body>
<h2>My Complaints</h2>
<p><a href="/complaints/new">File New Complaint</a></p>
<table border="1" cellpadding="6">
    <tr>
        <th>ID</th>
        <th>Category</th>
        <th>Status</th>
        <th>Assigned</th>
        <th>View</th>
    </tr>
    <c:forEach var="cmp" items="${complaints}">
        <tr>
            <td>${cmp.id}</td>
            <td>${cmp.category}</td>
            <td>${cmp.status}</td>
            <td><c:out value='${cmp.assignedDepartment != null ? cmp.assignedDepartment : "(unassigned)"}'/></td>
            <td><a href="/complaints/${cmp.id}">Open</a></td>
        </tr>
    </c:forEach>
</table>
</body>
</html>