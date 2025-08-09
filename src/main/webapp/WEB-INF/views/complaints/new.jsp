<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>File New Complaint</title>
</head>
<body>
<h2>File New Complaint</h2>
<form method="post" action="/complaints" enctype="multipart/form-data">
    <label>Category:</label>
    <select name="category" required>
        <option value="water">Water</option>
        <option value="sanitation">Sanitation</option>
        <option value="roads">Roads</option>
        <option value="other">Other</option>
    </select>
    <br/>
    <label>Description:</label><br/>
    <textarea name="description" rows="6" cols="60" required></textarea>
    <br/>
    <label>Attachment (optional):</label>
    <input type="file" name="attachment" accept="image/*"/>
    <br/>
    <button type="submit">Submit</button>
</form>
</body>
</html>