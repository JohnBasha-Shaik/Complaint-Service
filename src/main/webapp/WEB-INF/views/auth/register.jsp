<%@ include file="../layout/header.jsp" %>

<div class="row justify-content-center">
    <div class="col-md-8 col-lg-6">
        <div class="card shadow">
            <div class="card-header text-center">
                <h4><i class="fas fa-user-plus"></i> Register</h4>
                <small class="text-muted">Create your citizen account</small>
            </div>
            <div class="card-body">
                <form id="registrationForm">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label for="username" class="form-label">Username *</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fas fa-user"></i></span>
                                    <input type="text" class="form-control" id="username" name="username" required>
                                </div>
                                <div class="form-text">Choose a unique username (3-50 characters)</div>
                            </div>
                        </div>
                        
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label for="email" class="form-label">Email *</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fas fa-envelope"></i></span>
                                    <input type="email" class="form-control" id="email" name="email" required>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <div class="row">
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label for="password" class="form-label">Password *</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fas fa-lock"></i></span>
                                    <input type="password" class="form-control" id="password" name="password" required>
                                </div>
                                <div class="form-text">Minimum 6 characters</div>
                            </div>
                        </div>
                        
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label for="confirmPassword" class="form-label">Confirm Password *</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fas fa-lock"></i></span>
                                    <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <div class="mb-3">
                        <label for="fullName" class="form-label">Full Name *</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="fas fa-id-card"></i></span>
                            <input type="text" class="form-control" id="fullName" name="fullName" required>
                        </div>
                    </div>
                    
                    <div class="mb-3">
                        <label for="phoneNumber" class="form-label">Phone Number</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="fas fa-phone"></i></span>
                            <input type="tel" class="form-control" id="phoneNumber" name="phoneNumber">
                        </div>
                        <div class="form-text">Optional - for urgent notifications</div>
                    </div>
                    
                    <div class="mb-3 form-check">
                        <input type="checkbox" class="form-check-input" id="agreeTerms" required>
                        <label class="form-check-label" for="agreeTerms">
                            I agree to the <a href="#" data-bs-toggle="modal" data-bs-target="#termsModal">Terms of Service</a> and <a href="#" data-bs-toggle="modal" data-bs-target="#privacyModal">Privacy Policy</a> *
                        </label>
                    </div>
                    
                    <div class="d-grid">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-user-plus"></i> Create Account
                        </button>
                    </div>
                </form>
            </div>
            <div class="card-footer text-center">
                <small class="text-muted">
                    Already have an account? <a href="/login">Login here</a>
                </small>
            </div>
        </div>
    </div>
</div>

<!-- Terms Modal -->
<div class="modal fade" id="termsModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Terms of Service</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <h6>1. Acceptance of Terms</h6>
                <p>By using this Municipal Complaint Management System, you agree to these terms and conditions.</p>
                
                <h6>2. User Responsibilities</h6>
                <ul>
                    <li>Provide accurate and truthful information</li>
                    <li>Use the system only for legitimate municipal complaints</li>
                    <li>Respect the privacy and rights of others</li>
                    <li>Not submit false or malicious reports</li>
                </ul>
                
                <h6>3. System Usage</h6>
                <p>This system is provided for reporting legitimate municipal issues. Misuse may result in account suspension.</p>
                
                <h6>4. Data Protection</h6>
                <p>Your personal information will be handled according to our Privacy Policy and applicable data protection laws.</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<!-- Privacy Modal -->
<div class="modal fade" id="privacyModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Privacy Policy</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <h6>Information We Collect</h6>
                <ul>
                    <li>Account information (username, email, full name)</li>
                    <li>Complaint details and attachments</li>
                    <li>System usage logs for security purposes</li>
                </ul>
                
                <h6>How We Use Your Information</h6>
                <ul>
                    <li>To process and track your complaints</li>
                    <li>To communicate updates on your submissions</li>
                    <li>To improve municipal services</li>
                    <li>To maintain system security</li>
                </ul>
                
                <h6>Information Sharing</h6>
                <p>Your information is shared only with relevant municipal departments to resolve your complaints. We do not sell or share personal data with third parties.</p>
                
                <h6>Data Security</h6>
                <p>We implement industry-standard security measures to protect your personal information.</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<script>
document.getElementById('registrationForm').addEventListener('submit', function(e) {
    e.preventDefault();
    
    const formData = new FormData(this);
    const password = formData.get('password');
    const confirmPassword = formData.get('confirmPassword');
    
    // Validate passwords match
    if (password !== confirmPassword) {
        alert('Passwords do not match!');
        return;
    }
    
    // Create request body
    const requestBody = {
        username: formData.get('username'),
        email: formData.get('email'),
        password: password,
        fullName: formData.get('fullName'),
        phoneNumber: formData.get('phoneNumber')
    };
    
    // Submit registration
    fetch('/api/auth/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(requestBody)
    })
    .then(response => response.json())
    .then(data => {
        if (data.message) {
            alert('Registration successful! Please login with your credentials.');
            window.location.href = '/login';
        } else if (data.error) {
            alert('Registration failed: ' + data.error);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Registration failed. Please try again.');
    });
});
</script>

<%@ include file="../layout/footer.jsp" %>