<%@ include file="../layout/header.jsp" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="row justify-content-center">
    <div class="col-md-8">
        <div class="card">
            <div class="card-header">
                <h4><i class="fas fa-plus-circle"></i> File New Complaint</h4>
                <small class="text-muted">Report municipal issues and track their resolution</small>
            </div>
            <div class="card-body">
                <form action="/complaints/submit" method="post" enctype="multipart/form-data" id="complaintForm">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    
                    <!-- Category Selection -->
                    <div class="mb-3">
                        <label for="category" class="form-label">Category *</label>
                        <select class="form-select" id="category" name="category" required>
                            <option value="">Select a category</option>
                            <c:forEach var="cat" items="${categories}">
                                <option value="${cat}">
                                    ${cat.displayName}
                                </option>
                            </c:forEach>
                        </select>
                        <div class="form-text">Choose the category that best describes your complaint</div>
                    </div>
                    
                    <!-- Description -->
                    <div class="mb-3">
                        <label for="description" class="form-label">Description *</label>
                        <textarea class="form-control" id="description" name="description" rows="5" 
                                  placeholder="Please provide a detailed description of the issue..." 
                                  minlength="10" maxlength="2000" required></textarea>
                        <div class="form-text">
                            <span id="charCount">0</span>/2000 characters (minimum 10 required)
                        </div>
                    </div>
                    
                    <!-- Location -->
                    <div class="mb-3">
                        <label for="locationAddress" class="form-label">Location Address</label>
                        <textarea class="form-control" id="locationAddress" name="locationAddress" rows="2" 
                                  placeholder="Provide the address or location where the issue occurs (optional)"></textarea>
                        <div class="form-text">Help us locate the problem by providing an address or landmark</div>
                    </div>
                    
                    <!-- File Attachment -->
                    <div class="mb-3">
                        <label for="attachment" class="form-label">Attachment (Optional)</label>
                        <input class="form-control" type="file" id="attachment" name="attachment" 
                               accept="image/*,.pdf,.doc,.docx">
                        <div class="form-text">
                            Upload photos or documents related to your complaint (Max: 10MB, Formats: Images, PDF, DOC)
                        </div>
                    </div>
                    
                    <!-- Priority Indicator -->
                    <div class="mb-3">
                        <div class="card border-info">
                            <div class="card-body py-2">
                                <h6 class="card-title mb-1">
                                    <i class="fas fa-info-circle text-info"></i> Priority Guidelines
                                </h6>
                                <small class="text-muted">
                                    <strong>Emergency:</strong> Use 911 for life-threatening situations. 
                                    <strong>Urgent:</strong> Health hazards, water/power outages. 
                                    <strong>Standard:</strong> General maintenance, non-urgent repairs.
                                </small>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Submit Buttons -->
                    <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                        <a href="/complaints/my" class="btn btn-secondary me-md-2">
                            <i class="fas fa-times"></i> Cancel
                        </a>
                        <button type="submit" class="btn btn-primary" id="submitBtn">
                            <i class="fas fa-paper-plane"></i> Submit Complaint
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Help Section -->
<div class="row justify-content-center mt-4">
    <div class="col-md-8">
        <div class="card border-success">
            <div class="card-header bg-success text-white">
                <h6 class="mb-0"><i class="fas fa-lightbulb"></i> Tips for Effective Complaints</h6>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-6">
                        <h6>Be Specific</h6>
                        <ul class="small">
                            <li>Provide exact location details</li>
                            <li>Include dates and times if relevant</li>
                            <li>Describe the impact on you/community</li>
                        </ul>
                    </div>
                    <div class="col-md-6">
                        <h6>Attach Evidence</h6>
                        <ul class="small">
                            <li>Photos showing the problem</li>
                            <li>Relevant documents</li>
                            <li>Before/after images if applicable</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
// Character counter for description
document.getElementById('description').addEventListener('input', function() {
    const count = this.value.length;
    document.getElementById('charCount').textContent = count;
    
    if (count < 10) {
        document.getElementById('charCount').className = 'text-danger';
    } else if (count > 1800) {
        document.getElementById('charCount').className = 'text-warning';
    } else {
        document.getElementById('charCount').className = 'text-success';
    }
});

// Form validation
document.getElementById('complaintForm').addEventListener('submit', function(e) {
    const description = document.getElementById('description').value.trim();
    const category = document.getElementById('category').value;
    const fileInput = document.getElementById('attachment');
    
    // Validate description length
    if (description.length < 10) {
        e.preventDefault();
        alert('Please provide a more detailed description (minimum 10 characters).');
        return;
    }
    
    // Validate category selection
    if (!category) {
        e.preventDefault();
        alert('Please select a complaint category.');
        return;
    }
    
    // Validate file size
    if (fileInput.files.length > 0) {
        const file = fileInput.files[0];
        const maxSize = 10 * 1024 * 1024; // 10MB
        
        if (file.size > maxSize) {
            e.preventDefault();
            alert('File size must be less than 10MB. Please choose a smaller file.');
            return;
        }
    }
    
    // Show loading state
    const submitBtn = document.getElementById('submitBtn');
    submitBtn.disabled = true;
    submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Submitting...';
});

// Category-specific descriptions
document.getElementById('category').addEventListener('change', function() {
    const category = this.value;
    const descriptions = {
        'WATER': 'Examples: Water supply issues, pipe leaks, low pressure, water quality problems',
        'SANITATION': 'Examples: Garbage collection issues, overflowing bins, sewage problems',
        'ROADS': 'Examples: Potholes, damaged sidewalks, traffic light malfunctions, road maintenance',
        'ELECTRICITY': 'Examples: Street light outages, electrical hazards, power line issues',
        'DRAINAGE': 'Examples: Blocked drains, flooding, stormwater issues',
        'WASTE_MANAGEMENT': 'Examples: Missed collections, illegal dumping, recycling issues',
        'PARKS': 'Examples: Damaged playground equipment, park maintenance, landscaping issues',
        'OTHERS': 'Examples: Any other municipal service not listed above'
    };
    
    const helpText = descriptions[category] || 'Please provide a detailed description of your complaint';
    document.getElementById('description').placeholder = helpText;
});
</script>

<%@ include file="../layout/footer.jsp" %>