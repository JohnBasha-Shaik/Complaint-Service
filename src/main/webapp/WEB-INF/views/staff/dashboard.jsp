<%@ include file="../layout/header.jsp" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="d-flex justify-content-between align-items-center mb-4">
    <h3><i class="fas fa-tachometer-alt"></i> Staff Dashboard</h3>
    <div>
        <sec:authorize access="hasRole('ADMIN')">
            <a href="/dashboard/admin" class="btn btn-outline-secondary me-2">
                <i class="fas fa-user-shield"></i> Admin Panel
            </a>
        </sec:authorize>
        <a href="/staff/complaints" class="btn btn-primary">
            <i class="fas fa-tasks"></i> Manage Complaints
        </a>
    </div>
</div>

<!-- Statistics Cards -->
<div class="row mb-4">
    <div class="col-lg-3 col-md-6">
        <div class="card dashboard-card bg-info text-white">
            <div class="card-body text-center">
                <i class="fas fa-inbox fa-2x mb-2"></i>
                <h3>${stats.totalComplaints}</h3>
                <p class="card-text">Total Complaints</p>
            </div>
        </div>
    </div>
    
    <div class="col-lg-3 col-md-6">
        <div class="card dashboard-card bg-warning text-white">
            <div class="card-body text-center">
                <i class="fas fa-exclamation-triangle fa-2x mb-2"></i>
                <h3>${stats.newComplaints}</h3>
                <p class="card-text">New Complaints</p>
                <a href="/staff/complaints?status=SUBMITTED" class="btn btn-light btn-sm mt-2">
                    <i class="fas fa-eye"></i> Review
                </a>
            </div>
        </div>
    </div>
    
    <div class="col-lg-3 col-md-6">
        <div class="card dashboard-card bg-primary text-white">
            <div class="card-body text-center">
                <i class="fas fa-cogs fa-2x mb-2"></i>
                <h3>${stats.inProgressComplaints}</h3>
                <p class="card-text">In Progress</p>
                <a href="/staff/complaints?status=IN_PROGRESS" class="btn btn-light btn-sm mt-2">
                    <i class="fas fa-tasks"></i> Manage
                </a>
            </div>
        </div>
    </div>
    
    <div class="col-lg-3 col-md-6">
        <div class="card dashboard-card bg-success text-white">
            <div class="card-body text-center">
                <i class="fas fa-check-circle fa-2x mb-2"></i>
                <h3>${stats.resolvedComplaints}</h3>
                <p class="card-text">Resolved</p>
                <a href="/staff/complaints?status=RESOLVED" class="btn btn-light btn-sm mt-2">
                    <i class="fas fa-history"></i> View
                </a>
            </div>
        </div>
    </div>
</div>

<!-- Urgent Complaints Alert -->
<c:if test="${stats.urgentComplaints > 0}">
    <div class="alert alert-danger d-flex align-items-center" role="alert">
        <i class="fas fa-exclamation-triangle fa-2x me-3"></i>
        <div>
            <h5 class="alert-heading">Urgent Attention Required!</h5>
            <p class="mb-0">
                There are <strong>${stats.urgentComplaints}</strong> urgent complaints that need immediate attention.
                <a href="/staff/complaints?priority=URGENT" class="alert-link">View urgent complaints &raquo;</a>
            </p>
        </div>
    </div>
</c:if>

<div class="row">
    <!-- Unassigned Complaints -->
    <div class="col-lg-6">
        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <h5 class="mb-0">
                    <i class="fas fa-clipboard-list"></i> Unassigned Complaints
                </h5>
                <a href="/staff/complaints/assign" class="btn btn-outline-primary btn-sm">
                    <i class="fas fa-plus"></i> Assign All
                </a>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${not empty unassignedComplaints}">
                        <div class="list-group list-group-flush">
                            <c:forEach var="complaint" items="${unassignedComplaints}" varStatus="status">
                                <c:if test="${status.index < 5}">
                                    <div class="list-group-item d-flex justify-content-between align-items-start">
                                        <div class="ms-2 me-auto">
                                            <div class="fw-bold">
                                                #${complaint.id} - ${complaint.category.displayName}
                                            </div>
                                            <small class="text-muted">
                                                <i class="fas fa-user"></i> ${complaint.citizen.fullName} | 
                                                <i class="fas fa-clock"></i> 
                                                <fmt:formatDate value="${complaint.createdAt}" pattern="MMM dd, HH:mm" />
                                            </small>
                                            <p class="mb-1 small">
                                                ${complaint.description.length() > 80 ? 
                                                  complaint.description.substring(0, 80).concat('...') : 
                                                  complaint.description}
                                            </p>
                                        </div>
                                        <div class="text-end">
                                            <span class="badge bg-${complaint.priority == 'HIGH' ? 'warning' : 
                                                                   complaint.priority == 'URGENT' ? 'danger' : 'secondary'} mb-1">
                                                ${complaint.priority.displayName}
                                            </span><br>
                                            <a href="/staff/complaints/${complaint.id}" class="btn btn-outline-primary btn-sm">
                                                <i class="fas fa-eye"></i> View
                                            </a>
                                        </div>
                                    </div>
                                </c:if>
                            </c:forEach>
                        </div>
                        <c:if test="${unassignedComplaints.size() > 5}">
                            <div class="text-center mt-3">
                                <a href="/staff/complaints/assign" class="btn btn-outline-primary">
                                    View all ${unassignedComplaints.size()} unassigned complaints
                                </a>
                            </div>
                        </c:if>
                    </c:when>
                    <c:otherwise>
                        <div class="text-center py-4">
                            <i class="fas fa-check-circle fa-3x text-success mb-3"></i>
                            <h6 class="text-muted">All complaints are assigned!</h6>
                            <p class="small text-muted">Great job! No complaints are waiting for assignment.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
    
    <!-- Recent Complaints -->
    <div class="col-lg-6">
        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <h5 class="mb-0">
                    <i class="fas fa-history"></i> Recent Activity
                </h5>
                <a href="/staff/complaints" class="btn btn-outline-primary btn-sm">
                    <i class="fas fa-list"></i> View All
                </a>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${not empty recentComplaints}">
                        <div class="timeline">
                            <c:forEach var="complaint" items="${recentComplaints}" varStatus="status">
                                <c:if test="${status.index < 8}">
                                    <div class="timeline-item">
                                        <div class="timeline-marker bg-${complaint.status == 'SUBMITTED' ? 'info' : 
                                                                         complaint.status == 'ACKNOWLEDGED' ? 'warning' :
                                                                         complaint.status == 'IN_PROGRESS' ? 'primary' :
                                                                         complaint.status == 'ASSIGNED' ? 'secondary' :
                                                                         complaint.status == 'RESOLVED' ? 'success' : 'dark'}"></div>
                                        <div class="timeline-content">
                                            <div class="d-flex justify-content-between align-items-start">
                                                <div>
                                                    <h6 class="timeline-title mb-1">
                                                        <a href="/staff/complaints/${complaint.id}" class="text-decoration-none">
                                                            #${complaint.id} - ${complaint.category.displayName}
                                                        </a>
                                                    </h6>
                                                    <p class="timeline-text text-muted small mb-1">
                                                        ${complaint.citizen.fullName}
                                                    </p>
                                                    <small class="text-muted">
                                                        <fmt:formatDate value="${complaint.updatedAt}" pattern="MMM dd, HH:mm" />
                                                    </small>
                                                </div>
                                                <span class="badge bg-${complaint.status == 'SUBMITTED' ? 'info' : 
                                                                       complaint.status == 'ACKNOWLEDGED' ? 'warning' :
                                                                       complaint.status == 'IN_PROGRESS' ? 'primary' :
                                                                       complaint.status == 'ASSIGNED' ? 'secondary' :
                                                                       complaint.status == 'RESOLVED' ? 'success' : 'dark'} status-badge">
                                                    ${complaint.status.displayName}
                                                </span>
                                            </div>
                                        </div>
                                    </div>
                                </c:if>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="text-center py-4">
                            <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
                            <h6 class="text-muted">No recent activity</h6>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<!-- Quick Actions -->
<div class="row mt-4">
    <div class="col-12">
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0"><i class="fas fa-bolt"></i> Quick Actions</h5>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-3">
                        <div class="d-grid">
                            <a href="/staff/complaints?status=SUBMITTED" class="btn btn-outline-warning">
                                <i class="fas fa-eye"></i><br>
                                Review New<br>
                                <small>Complaints</small>
                            </a>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="d-grid">
                            <a href="/staff/complaints/assign" class="btn btn-outline-primary">
                                <i class="fas fa-user-check"></i><br>
                                Assign<br>
                                <small>Complaints</small>
                            </a>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="d-grid">
                            <a href="/staff/complaints?status=IN_PROGRESS" class="btn btn-outline-info">
                                <i class="fas fa-tasks"></i><br>
                                Active<br>
                                <small>Work Items</small>
                            </a>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="d-grid">
                            <a href="/staff/complaints" class="btn btn-outline-success">
                                <i class="fas fa-search"></i><br>
                                Search &<br>
                                <small>Filter</small>
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<style>
.timeline {
    position: relative;
    max-height: 400px;
    overflow-y: auto;
}

.timeline-item {
    position: relative;
    padding-left: 2rem;
    padding-bottom: 1rem;
    border-left: 2px solid #e9ecef;
}

.timeline-item:last-child {
    border-left: none;
}

.timeline-marker {
    position: absolute;
    left: -0.5rem;
    top: 0.25rem;
    width: 1rem;
    height: 1rem;
    border-radius: 50%;
    border: 2px solid white;
}

.timeline-content {
    margin-left: 0.5rem;
}

.timeline-title {
    font-size: 0.9rem;
    color: #495057;
}

.timeline-text {
    font-size: 0.8rem;
    margin-bottom: 0;
}

.list-group-item:hover {
    background-color: #f8f9fa;
}

.quick-action-card {
    transition: all 0.3s;
    cursor: pointer;
}

.quick-action-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}
</style>

<script>
// Auto-refresh dashboard every 5 minutes
setInterval(function() {
    location.reload();
}, 300000);

// Add smooth animations to cards
document.addEventListener('DOMContentLoaded', function() {
    const cards = document.querySelectorAll('.dashboard-card');
    cards.forEach((card, index) => {
        setTimeout(() => {
            card.style.opacity = '0';
            card.style.transform = 'translateY(20px)';
            card.style.transition = 'all 0.5s ease';
            
            setTimeout(() => {
                card.style.opacity = '1';
                card.style.transform = 'translateY(0)';
            }, 100);
        }, index * 100);
    });
});

// Notification for urgent complaints
<c:if test="${stats.urgentComplaints > 0}">
document.addEventListener('DOMContentLoaded', function() {
    // Flash urgent notification
    const urgentAlert = document.querySelector('.alert-danger');
    if (urgentAlert) {
        urgentAlert.classList.add('animate__animated', 'animate__pulse');
    }
});
</c:if>
</script>

<%@ include file="../layout/footer.jsp" %>