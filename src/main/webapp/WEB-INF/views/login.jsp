<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
</head>
<body>
<h2>Login</h2>
<form method="post" action="/login">
    <label>Username</label>
    <input type="text" name="username" required/>
    <br/>
    <label>Password</label>
    <input type="password" name="password" required/>
    <br/>
    <button type="submit">Login</button>
</form>
<hr/>
<h3>Or obtain JWT token (for API use)</h3>
<form method="post" action="/auth/token">
    <label>Username</label>
    <input type="text" name="username" required/>
    <br/>
    <label>Password</label>
    <input type="password" name="password" required/>
    <br/>
    <button type="submit">Get Token</button>
</form>
</body>
</html>