<%@ include file="layout/header.jsp" %>

<!-- Hero Section -->
<div class="jumbotron bg-primary text-white text-center py-5 mb-4">
    <div class="container">
        <h1 class="display-4">Municipal Complaint Management System</h1>
        <p class="lead">Your voice matters. Report issues, track progress, and help improve our city together.</p>
        
        <sec:authorize access="!isAuthenticated()">
            <div class="mt-4">
                <a href="/register" class="btn btn-light btn-lg me-3">Get Started</a>
                <a href="/login" class="btn btn-outline-light btn-lg">Sign In</a>
            </div>
        </sec:authorize>
        
        <sec:authorize access="hasRole('CITIZEN')">
            <div class="mt-4">
                <a href="/complaints/new" class="btn btn-warning btn-lg me-3">
                    <i class="fas fa-plus"></i> File New Complaint
                </a>
                <a href="/complaints/my" class="btn btn-outline-light btn-lg">
                    <i class="fas fa-list"></i> My Complaints
                </a>
            </div>
        </sec:authorize>
        
        <sec:authorize access="hasRole('STAFF') or hasRole('ADMIN')">
            <div class="mt-4">
                <a href="/dashboard/staff" class="btn btn-warning btn-lg me-3">
                    <i class="fas fa-tachometer-alt"></i> Dashboard
                </a>
                <a href="/staff/complaints" class="btn btn-outline-light btn-lg">
                    <i class="fas fa-tasks"></i> Manage Complaints
                </a>
            </div>
        </sec:authorize>
    </div>
</div>

<!-- Features Section -->
<div class="row mb-5">
    <div class="col-md-4">
        <div class="card dashboard-card h-100">
            <div class="card-body text-center">
                <i class="fas fa-file-alt fa-3x text-primary mb-3"></i>
                <h4>Easy Reporting</h4>
                <p class="text-muted">Submit complaints quickly with our user-friendly interface. Attach photos and provide detailed descriptions.</p>
            </div>
        </div>
    </div>
    
    <div class="col-md-4">
        <div class="card dashboard-card h-100">
            <div class="card-body text-center">
                <i class="fas fa-search fa-3x text-success mb-3"></i>
                <h4>Real-time Tracking</h4>
                <p class="text-muted">Track the status of your complaints in real-time and receive updates on progress.</p>
            </div>
        </div>
    </div>
    
    <div class="col-md-4">
        <div class="card dashboard-card h-100">
            <div class="card-body text-center">
                <i class="fas fa-users fa-3x text-info mb-3"></i>
                <h4>Collaborative Resolution</h4>
                <p class="text-muted">Municipal staff work together to resolve issues efficiently with proper department assignment.</p>
            </div>
        </div>
    </div>
</div>

<!-- Quick Stats Section (for authenticated users) -->
<sec:authorize access="isAuthenticated()">
    <div class="row mb-5">
        <div class="col-12">
            <h3 class="mb-4">Quick Overview</h3>
        </div>
        
        <sec:authorize access="hasRole('CITIZEN')">
            <div class="col-md-6">
                <div class="card dashboard-card">
                    <div class="card-body">
                        <h5 class="card-title">
                            <i class="fas fa-clipboard-list text-primary"></i> Your Complaints
                        </h5>
                        <p class="card-text">View and track all your submitted complaints in one place.</p>
                        <a href="/complaints/my" class="btn btn-primary">View My Complaints</a>
                    </div>
                </div>
            </div>
            
            <div class="col-md-6">
                <div class="card dashboard-card">
                    <div class="card-body">
                        <h5 class="card-title">
                            <i class="fas fa-plus-circle text-success"></i> New Complaint
                        </h5>
                        <p class="card-text">Report a new issue to the municipal authorities.</p>
                        <a href="/complaints/new" class="btn btn-success">File Complaint</a>
                    </div>
                </div>
            </div>
        </sec:authorize>
        
        <sec:authorize access="hasRole('STAFF') or hasRole('ADMIN')">
            <div class="col-md-4">
                <div class="card dashboard-card bg-info text-white">
                    <div class="card-body text-center">
                        <i class="fas fa-inbox fa-2x mb-2"></i>
                        <h4>New Complaints</h4>
                        <p class="card-text">Review and assign newly submitted complaints.</p>
                        <a href="/staff/complaints?status=SUBMITTED" class="btn btn-light">View New</a>
                    </div>
                </div>
            </div>
            
            <div class="col-md-4">
                <div class="card dashboard-card bg-warning text-white">
                    <div class="card-body text-center">
                        <i class="fas fa-clock fa-2x mb-2"></i>
                        <h4>In Progress</h4>
                        <p class="card-text">Monitor complaints currently being resolved.</p>
                        <a href="/staff/complaints?status=IN_PROGRESS" class="btn btn-light">View Active</a>
                    </div>
                </div>
            </div>
            
            <div class="col-md-4">
                <div class="card dashboard-card bg-success text-white">
                    <div class="card-body text-center">
                        <i class="fas fa-check-circle fa-2x mb-2"></i>
                        <h4>Resolved</h4>
                        <p class="card-text">View successfully resolved complaints.</p>
                        <a href="/staff/complaints?status=RESOLVED" class="btn btn-light">View Resolved</a>
                    </div>
                </div>
            </div>
        </sec:authorize>
    </div>
</sec:authorize>

<!-- Contact Information -->
<div class="row">
    <div class="col-12">
        <div class="card">
            <div class="card-header">
                <h5><i class="fas fa-info-circle"></i> Important Information</h5>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-6">
                        <h6>Emergency Contacts</h6>
                        <ul class="list-unstyled">
                            <li><i class="fas fa-phone text-danger"></i> Emergency: 911</li>
                            <li><i class="fas fa-phone text-info"></i> Municipal Office: (555) 123-4567</li>
                            <li><i class="fas fa-envelope text-info"></i> Email: complaints@municipal.gov</li>
                        </ul>
                    </div>
                    <div class="col-md-6">
                        <h6>Complaint Categories</h6>
                        <ul class="list-unstyled">
                            <li><i class="fas fa-tint text-primary"></i> Water Supply Issues</li>
                            <li><i class="fas fa-recycle text-success"></i> Sanitation & Waste</li>
                            <li><i class="fas fa-road text-warning"></i> Roads & Infrastructure</li>
                            <li><i class="fas fa-bolt text-danger"></i> Electricity Issues</li>
                            <li><i class="fas fa-ellipsis-h text-secondary"></i> Other Municipal Services</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="layout/footer.jsp" %>