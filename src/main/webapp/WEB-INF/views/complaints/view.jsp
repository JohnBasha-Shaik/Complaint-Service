<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Complaint Details</title>
</head>
<body>
<h2>Complaint #${complaint.id}</h2>
<p><b>Category:</b> ${complaint.category}</p>
<p><b>Status:</b> ${complaint.status}</p>
<p><b>Assigned Department:</b> <c:out value='${complaint.assignedDepartment != null ? complaint.assignedDepartment : "(unassigned)"}'/></p>
<p><b>Description:</b></p>
<p><pre>${complaint.description}</pre></p>
<c:if test="${complaint.attachment != null}">
    <p><b>Attachment:</b> ${complaint.attachment}</p>
</c:if>

<h3>Comments</h3>
<ul>
    <c:forEach var="c" items="${comments}">
        <li>
            <b>[${c.authorRole}]</b> ${c.message}
        </li>
    </c:forEach>
</ul>

<form method="post" action="/complaints/${complaint.id}/comments">
    <input type="text" name="message" required placeholder="Add a comment"/>
    <button type="submit">Add Comment</button>
</form>

<c:if test="${canAssign}">
    <h3>Assign to Department</h3>
    <form method="post" action="/staff/assign">
        <input type="hidden" name="complaintId" value="${complaint.id}"/>
        <input type="text" name="department" placeholder="e.g., Water Dept" required/>
        <button type="submit">Assign</button>
    </form>
</c:if>

</body>
</html>