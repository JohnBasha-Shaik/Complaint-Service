<%@ include file="../layout/header.jsp" %>

<div class="row justify-content-center">
    <div class="col-md-6 col-lg-4">
        <div class="card shadow">
            <div class="card-header text-center">
                <h4><i class="fas fa-sign-in-alt"></i> Login</h4>
            </div>
            <div class="card-body">
                <c:if test="${param.error}">
                    <div class="alert alert-danger">
                        <i class="fas fa-exclamation-triangle"></i> Invalid username or password.
                    </div>
                </c:if>
                
                <c:if test="${param.logout}">
                    <div class="alert alert-success">
                        <i class="fas fa-check-circle"></i> You have been logged out successfully.
                    </div>
                </c:if>
                
                <form action="/login" method="post">
                    <div class="mb-3">
                        <label for="username" class="form-label">Username</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="fas fa-user"></i></span>
                            <input type="text" class="form-control" id="username" name="username" required>
                        </div>
                    </div>
                    
                    <div class="mb-3">
                        <label for="password" class="form-label">Password</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="fas fa-lock"></i></span>
                            <input type="password" class="form-control" id="password" name="password" required>
                        </div>
                    </div>
                    
                    <div class="mb-3 form-check">
                        <input type="checkbox" class="form-check-input" id="remember-me" name="remember-me">
                        <label class="form-check-label" for="remember-me">Remember me</label>
                    </div>
                    
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    
                    <div class="d-grid">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-sign-in-alt"></i> Login
                        </button>
                    </div>
                </form>
            </div>
            <div class="card-footer text-center">
                <small class="text-muted">
                    Don't have an account? <a href="/register">Register here</a>
                </small>
            </div>
        </div>
    </div>
</div>

<!-- Demo Accounts Information -->
<div class="row justify-content-center mt-4">
    <div class="col-md-8">
        <div class="card border-info">
            <div class="card-header bg-info text-white">
                <h6 class="mb-0"><i class="fas fa-info-circle"></i> Demo Accounts (for testing)</h6>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-4">
                        <h6 class="text-primary">Citizen Account</h6>
                        <small>
                            Username: <code>citizen</code><br>
                            Password: <code>password</code>
                        </small>
                    </div>
                    <div class="col-md-4">
                        <h6 class="text-warning">Staff Account</h6>
                        <small>
                            Username: <code>staff</code><br>
                            Password: <code>password</code>
                        </small>
                    </div>
                    <div class="col-md-4">
                        <h6 class="text-danger">Admin Account</h6>
                        <small>
                            Username: <code>admin</code><br>
                            Password: <code>password</code>
                        </small>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="../layout/footer.jsp" %>