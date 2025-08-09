<%@ include file="../layout/header.jsp" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="d-flex justify-content-between align-items-center mb-4">
    <h3><i class="fas fa-list"></i> My Complaints</h3>
    <a href="/complaints/new" class="btn btn-primary">
        <i class="fas fa-plus"></i> File New Complaint
    </a>
</div>

<!-- Complaints Summary -->
<div class="row mb-4">
    <div class="col-md-3">
        <div class="card dashboard-card bg-info text-white">
            <div class="card-body text-center">
                <i class="fas fa-file-alt fa-2x mb-2"></i>
                <h4>${complaints.totalElements}</h4>
                <p class="card-text">Total Complaints</p>
            </div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="card dashboard-card bg-warning text-white">
            <div class="card-body text-center">
                <i class="fas fa-clock fa-2x mb-2"></i>
                <h4 id="pendingCount">-</h4>
                <p class="card-text">Pending</p>
            </div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="card dashboard-card bg-primary text-white">
            <div class="card-body text-center">
                <i class="fas fa-cogs fa-2x mb-2"></i>
                <h4 id="activeCount">-</h4>
                <p class="card-text">In Progress</p>
            </div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="card dashboard-card bg-success text-white">
            <div class="card-body text-center">
                <i class="fas fa-check-circle fa-2x mb-2"></i>
                <h4 id="resolvedCount">-</h4>
                <p class="card-text">Resolved</p>
            </div>
        </div>
    </div>
</div>

<!-- Complaints List -->
<div class="card">
    <div class="card-header">
        <h5 class="mb-0">Complaint History</h5>
    </div>
    <div class="card-body">
        <c:choose>
            <c:when test="${complaints.totalElements > 0}">
                <div class="row">
                    <c:forEach var="complaint" items="${complaints.content}">
                        <div class="col-md-6 mb-3">
                            <div class="card complaint-card status-${complaint.status.name().toLowerCase()} h-100">
                                <div class="card-header d-flex justify-content-between align-items-center">
                                    <h6 class="mb-0">
                                        <i class="fas fa-hashtag"></i> ${complaint.id}
                                    </h6>
                                    <span class="badge bg-${complaint.status == 'SUBMITTED' ? 'info' : 
                                                           complaint.status == 'ACKNOWLEDGED' ? 'warning' :
                                                           complaint.status == 'IN_PROGRESS' ? 'primary' :
                                                           complaint.status == 'ASSIGNED' ? 'secondary' :
                                                           complaint.status == 'RESOLVED' ? 'success' :
                                                           complaint.status == 'CLOSED' ? 'dark' : 'danger'} status-badge">
                                        ${complaint.status.displayName}
                                    </span>
                                </div>
                                <div class="card-body">
                                    <h6 class="card-title">${complaint.category.displayName}</h6>
                                    <p class="card-text">
                                        ${complaint.description.length() > 100 ? 
                                          complaint.description.substring(0, 100).concat('...') : 
                                          complaint.description}
                                    </p>
                                    
                                    <div class="row text-center">
                                        <div class="col-6">
                                            <small class="text-muted">Submitted</small><br>
                                            <small>
                                                <fmt:formatDate value="${complaint.createdAt}" pattern="MMM dd, yyyy" />
                                            </small>
                                        </div>
                                        <div class="col-6">
                                            <small class="text-muted">Priority</small><br>
                                            <small>
                                                <span class="badge bg-${complaint.priority == 'HIGH' ? 'warning' : 
                                                                       complaint.priority == 'URGENT' ? 'danger' : 'secondary'}">
                                                    ${complaint.priority.displayName}
                                                </span>
                                            </small>
                                        </div>
                                    </div>
                                    
                                    <c:if test="${not empty complaint.locationAddress}">
                                        <div class="mt-2">
                                            <small class="text-muted">
                                                <i class="fas fa-map-marker-alt"></i> 
                                                ${complaint.locationAddress.length() > 50 ? 
                                                  complaint.locationAddress.substring(0, 50).concat('...') : 
                                                  complaint.locationAddress}
                                            </small>
                                        </div>
                                    </c:if>
                                    
                                    <c:if test="${not empty complaint.assignedDepartment}">
                                        <div class="mt-2">
                                            <small class="text-muted">
                                                <i class="fas fa-building"></i> 
                                                Assigned to ${complaint.assignedDepartment.name}
                                            </small>
                                        </div>
                                    </c:if>
                                </div>
                                <div class="card-footer">
                                    <div class="d-flex justify-content-between align-items-center">
                                        <small class="text-muted">
                                            <i class="fas fa-clock"></i> 
                                            <fmt:formatDate value="${complaint.updatedAt}" pattern="MMM dd, HH:mm" />
                                        </small>
                                        <a href="/complaints/track/${complaint.id}" class="btn btn-outline-primary btn-sm">
                                            <i class="fas fa-eye"></i> Track
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
                
                <!-- Pagination -->
                <c:if test="${complaints.totalPages > 1}">
                    <nav aria-label="Complaints pagination" class="mt-4">
                        <ul class="pagination justify-content-center">
                            <c:if test="${currentPage > 0}">
                                <li class="page-item">
                                    <a class="page-link" href="?page=${currentPage - 1}">
                                        <i class="fas fa-chevron-left"></i> Previous
                                    </a>
                                </li>
                            </c:if>
                            
                            <c:forEach var="i" begin="0" end="${totalPages - 1}">
                                <c:if test="${i == currentPage}">
                                    <li class="page-item active">
                                        <span class="page-link">${i + 1}</span>
                                    </li>
                                </c:if>
                                <c:if test="${i != currentPage && (i <= currentPage + 2 && i >= currentPage - 2)}">
                                    <li class="page-item">
                                        <a class="page-link" href="?page=${i}">${i + 1}</a>
                                    </li>
                                </c:if>
                            </c:forEach>
                            
                            <c:if test="${currentPage < totalPages - 1}">
                                <li class="page-item">
                                    <a class="page-link" href="?page=${currentPage + 1}">
                                        Next <i class="fas fa-chevron-right"></i>
                                    </a>
                                </li>
                            </c:if>
                        </ul>
                    </nav>
                </c:if>
            </c:when>
            <c:otherwise>
                <!-- Empty State -->
                <div class="text-center py-5">
                    <i class="fas fa-inbox fa-4x text-muted mb-3"></i>
                    <h4 class="text-muted">No Complaints Yet</h4>
                    <p class="text-muted">You haven't filed any complaints yet. Start by reporting an issue that needs attention.</p>
                    <a href="/complaints/new" class="btn btn-primary mt-3">
                        <i class="fas fa-plus"></i> File Your First Complaint
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<!-- Help Section -->
<div class="card mt-4">
    <div class="card-header">
        <h6 class="mb-0"><i class="fas fa-question-circle"></i> Need Help?</h6>
    </div>
    <div class="card-body">
        <div class="row">
            <div class="col-md-4">
                <h6>Status Meanings</h6>
                <ul class="small list-unstyled">
                    <li><span class="badge bg-info me-2">Submitted</span> - We received your complaint</li>
                    <li><span class="badge bg-warning me-2">Acknowledged</span> - Under review</li>
                    <li><span class="badge bg-secondary me-2">Assigned</span> - Assigned to department</li>
                    <li><span class="badge bg-primary me-2">In Progress</span> - Work has started</li>
                    <li><span class="badge bg-success me-2">Resolved</span> - Issue is fixed</li>
                </ul>
            </div>
            <div class="col-md-4">
                <h6>What You Can Do</h6>
                <ul class="small list-unstyled">
                    <li><i class="fas fa-eye text-primary"></i> Track complaint progress</li>
                    <li><i class="fas fa-comment text-success"></i> Add comments and questions</li>
                    <li><i class="fas fa-download text-info"></i> View submitted attachments</li>
                    <li><i class="fas fa-print text-secondary"></i> Print complaint details</li>
                </ul>
            </div>
            <div class="col-md-4">
                <h6>Contact Support</h6>
                <ul class="small list-unstyled">
                    <li><i class="fas fa-phone text-info"></i> Call: (555) 123-4567</li>
                    <li><i class="fas fa-envelope text-info"></i> Email: support@municipal.gov</li>
                    <li><i class="fas fa-clock text-muted"></i> Mon-Fri: 8AM-5PM</li>
                </ul>
            </div>
        </div>
    </div>
</div>

<script>
// Calculate status counts
document.addEventListener('DOMContentLoaded', function() {
    let pendingCount = 0;
    let activeCount = 0;
    let resolvedCount = 0;
    
    // Count complaints by status (this would normally come from the server)
    <c:forEach var="complaint" items="${complaints.content}">
        <c:choose>
            <c:when test="${complaint.status == 'SUBMITTED' || complaint.status == 'ACKNOWLEDGED'}">
                pendingCount++;
            </c:when>
            <c:when test="${complaint.status == 'IN_PROGRESS' || complaint.status == 'ASSIGNED'}">
                activeCount++;
            </c:when>
            <c:when test="${complaint.status == 'RESOLVED' || complaint.status == 'CLOSED'}">
                resolvedCount++;
            </c:when>
        </c:choose>
    </c:forEach>
    
    document.getElementById('pendingCount').textContent = pendingCount;
    document.getElementById('activeCount').textContent = activeCount;
    document.getElementById('resolvedCount').textContent = resolvedCount;
});

// Smooth hover effects
document.querySelectorAll('.complaint-card').forEach(card => {
    card.addEventListener('mouseenter', function() {
        this.style.transform = 'translateY(-2px)';
    });
    
    card.addEventListener('mouseleave', function() {
        this.style.transform = 'translateY(0)';
    });
});
</script>

<%@ include file="../layout/footer.jsp" %>