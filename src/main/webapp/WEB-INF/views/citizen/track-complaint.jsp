<%@ include file="../layout/header.jsp" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="row">
    <div class="col-md-8">
        <!-- Complaint Details Card -->
        <div class="card complaint-card status-${complaint.status.name().toLowerCase()}">
            <div class="card-header">
                <div class="d-flex justify-content-between align-items-center">
                    <h5 class="mb-0">
                        <i class="fas fa-file-alt"></i> Complaint #${complaint.id}
                    </h5>
                    <span class="badge bg-${complaint.status == 'SUBMITTED' ? 'info' : 
                                         complaint.status == 'ACKNOWLEDGED' ? 'warning' :
                                         complaint.status == 'IN_PROGRESS' ? 'primary' :
                                         complaint.status == 'ASSIGNED' ? 'secondary' :
                                         complaint.status == 'RESOLVED' ? 'success' :
                                         complaint.status == 'CLOSED' ? 'dark' : 'danger'} status-badge">
                        ${complaint.status.displayName}
                    </span>
                </div>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-6">
                        <h6><i class="fas fa-tag"></i> Category</h6>
                        <p class="mb-2">${complaint.category.displayName}</p>
                        
                        <h6><i class="fas fa-calendar"></i> Date Submitted</h6>
                        <p class="mb-2">
                            <fmt:formatDate value="${complaint.createdAt}" pattern="MMMM dd, yyyy 'at' HH:mm" />
                        </p>
                        
                        <c:if test="${complaint.priority != 'MEDIUM'}">
                            <h6><i class="fas fa-exclamation-circle"></i> Priority</h6>
                            <p class="mb-2">
                                <span class="badge bg-${complaint.priority == 'HIGH' ? 'warning' : 
                                                       complaint.priority == 'URGENT' ? 'danger' : 'secondary'}">
                                    ${complaint.priority.displayName}
                                </span>
                            </p>
                        </c:if>
                    </div>
                    
                    <div class="col-md-6">
                        <c:if test="${not empty complaint.assignedDepartment}">
                            <h6><i class="fas fa-building"></i> Assigned Department</h6>
                            <p class="mb-2">${complaint.assignedDepartment.name}</p>
                        </c:if>
                        
                        <c:if test="${not empty complaint.assignedStaff}">
                            <h6><i class="fas fa-user-tie"></i> Assigned Staff</h6>
                            <p class="mb-2">${complaint.assignedStaff.fullName}</p>
                        </c:if>
                        
                        <c:if test="${complaint.status == 'RESOLVED' && not empty complaint.resolvedAt}">
                            <h6><i class="fas fa-check-circle"></i> Date Resolved</h6>
                            <p class="mb-2">
                                <fmt:formatDate value="${complaint.resolvedAt}" pattern="MMMM dd, yyyy 'at' HH:mm" />
                            </p>
                        </c:if>
                    </div>
                </div>
                
                <hr>
                
                <h6><i class="fas fa-align-left"></i> Description</h6>
                <p class="mb-3">${complaint.description}</p>
                
                <c:if test="${not empty complaint.locationAddress}">
                    <h6><i class="fas fa-map-marker-alt"></i> Location</h6>
                    <p class="mb-3">${complaint.locationAddress}</p>
                </c:if>
                

            </div>
        </div>
        
        <!-- Comments Section -->
        <div class="card mt-4" id="commentsSection">
            <div class="card-header">
                <h5 class="mb-0"><i class="fas fa-comments"></i> Updates & Comments</h5>
            </div>
            <div class="card-body">
                <!-- Comment List -->
                <div id="commentsList" class="mb-4">
                    <!-- Comments will be loaded here via JavaScript -->
                </div>
                
                <!-- Add Comment Form -->
                <div class="border-top pt-3">
                    <h6>Add a Comment</h6>
                    <form id="addCommentForm">
                        <div class="mb-3">
                            <textarea class="form-control" id="commentContent" rows="3" 
                                      placeholder="Add your comment or question..." required></textarea>
                        </div>
                        <button type="submit" class="btn btn-primary btn-sm">
                            <i class="fas fa-paper-plane"></i> Post Comment
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Status Timeline Sidebar -->
    <div class="col-md-4">
        <div class="card">
            <div class="card-header">
                <h6 class="mb-0"><i class="fas fa-history"></i> Status Timeline</h6>
            </div>
            <div class="card-body">
                <div class="timeline">
                    <!-- Timeline items will be generated based on complaint status -->
                    <div class="timeline-item ${complaint.status == 'SUBMITTED' || 
                                              complaint.status == 'ACKNOWLEDGED' || 
                                              complaint.status == 'IN_PROGRESS' || 
                                              complaint.status == 'ASSIGNED' || 
                                              complaint.status == 'RESOLVED' || 
                                              complaint.status == 'CLOSED' ? 'completed' : ''}">
                        <div class="timeline-marker bg-info"></div>
                        <div class="timeline-content">
                            <h6 class="timeline-title">Submitted</h6>
                            <p class="timeline-text text-muted small">
                                <fmt:formatDate value="${complaint.createdAt}" pattern="MMM dd, yyyy HH:mm" />
                            </p>
                        </div>
                    </div>
                    
                    <c:if test="${complaint.status == 'ACKNOWLEDGED' || 
                                complaint.status == 'IN_PROGRESS' || 
                                complaint.status == 'ASSIGNED' || 
                                complaint.status == 'RESOLVED' || 
                                complaint.status == 'CLOSED'}">
                        <div class="timeline-item completed">
                            <div class="timeline-marker bg-warning"></div>
                            <div class="timeline-content">
                                <h6 class="timeline-title">Acknowledged</h6>
                                <p class="timeline-text text-muted small">Complaint received and reviewed</p>
                            </div>
                        </div>
                    </c:if>
                    
                    <c:if test="${complaint.status == 'ASSIGNED' || 
                                complaint.status == 'IN_PROGRESS' || 
                                complaint.status == 'RESOLVED' || 
                                complaint.status == 'CLOSED'}">
                        <div class="timeline-item completed">
                            <div class="timeline-marker bg-secondary"></div>
                            <div class="timeline-content">
                                <h6 class="timeline-title">Assigned</h6>
                                <p class="timeline-text text-muted small">
                                    <c:if test="${not empty complaint.assignedDepartment}">
                                        To ${complaint.assignedDepartment.name}
                                    </c:if>
                                </p>
                            </div>
                        </div>
                    </c:if>
                    
                    <c:if test="${complaint.status == 'IN_PROGRESS' || 
                                complaint.status == 'RESOLVED' || 
                                complaint.status == 'CLOSED'}">
                        <div class="timeline-item completed">
                            <div class="timeline-marker bg-primary"></div>
                            <div class="timeline-content">
                                <h6 class="timeline-title">In Progress</h6>
                                <p class="timeline-text text-muted small">Work has begun</p>
                            </div>
                        </div>
                    </c:if>
                    
                    <c:if test="${complaint.status == 'RESOLVED' || complaint.status == 'CLOSED'}">
                        <div class="timeline-item completed">
                            <div class="timeline-marker bg-success"></div>
                            <div class="timeline-content">
                                <h6 class="timeline-title">Resolved</h6>
                                <p class="timeline-text text-muted small">
                                    <c:if test="${not empty complaint.resolvedAt}">
                                        <fmt:formatDate value="${complaint.resolvedAt}" pattern="MMM dd, yyyy HH:mm" />
                                    </c:if>
                                </p>
                            </div>
                        </div>
                    </c:if>
                    
                    <c:if test="${complaint.status == 'CLOSED'}">
                        <div class="timeline-item completed">
                            <div class="timeline-marker bg-dark"></div>
                            <div class="timeline-content">
                                <h6 class="timeline-title">Closed</h6>
                                <p class="timeline-text text-muted small">Case closed</p>
                            </div>
                        </div>
                    </c:if>
                    
                    <c:if test="${complaint.status == 'REJECTED'}">
                        <div class="timeline-item completed">
                            <div class="timeline-marker bg-danger"></div>
                            <div class="timeline-content">
                                <h6 class="timeline-title">Rejected</h6>
                                <p class="timeline-text text-muted small">Complaint was rejected</p>
                            </div>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>
        
        <!-- Quick Actions -->
        <div class="card mt-3">
            <div class="card-header">
                <h6 class="mb-0"><i class="fas fa-tools"></i> Quick Actions</h6>
            </div>
            <div class="card-body">
                <div class="d-grid gap-2">
                    <a href="/complaints/my" class="btn btn-outline-primary btn-sm">
                        <i class="fas fa-list"></i> View All My Complaints
                    </a>
                    <a href="/complaints/new" class="btn btn-outline-success btn-sm">
                        <i class="fas fa-plus"></i> File New Complaint
                    </a>
                    <button class="btn btn-outline-info btn-sm" onclick="window.print()">
                        <i class="fas fa-print"></i> Print This Page
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>

<style>
.timeline {
    position: relative;
    padding-left: 2rem;
}

.timeline::before {
    content: '';
    position: absolute;
    left: 1rem;
    top: 0;
    bottom: 0;
    width: 2px;
    background-color: #e9ecef;
}

.timeline-item {
    position: relative;
    padding-bottom: 1.5rem;
}

.timeline-marker {
    position: absolute;
    left: -1.75rem;
    top: 0.25rem;
    width: 1rem;
    height: 1rem;
    border-radius: 50%;
    border: 2px solid white;
    box-shadow: 0 0 0 2px #e9ecef;
}

.timeline-item.completed .timeline-marker {
    box-shadow: 0 0 0 2px #28a745;
}

.timeline-title {
    font-size: 0.9rem;
    margin-bottom: 0.25rem;
}

.timeline-text {
    font-size: 0.8rem;
    margin-bottom: 0;
}
</style>

<script>
// Load comments when page loads
document.addEventListener('DOMContentLoaded', function() {
    loadComments();
});

// Load comments function
function loadComments() {
    fetch('/api/comments/complaint/${complaint.id}', {
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('token')
        }
    })
    .then(response => response.json())
    .then(comments => {
        displayComments(comments);
    })
    .catch(error => {
        console.error('Error loading comments:', error);
    });
}

// Display comments function
function displayComments(comments) {
    const commentsList = document.getElementById('commentsList');
    
    if (comments.length === 0) {
        commentsList.innerHTML = '<p class="text-muted text-center">No comments yet.</p>';
        return;
    }
    
    let commentsHtml = '';
    comments.forEach(comment => {
        const commentDate = new Date(comment.createdAt).toLocaleString();
        const isInternal = comment.isInternal;
        
        commentsHtml += `
            <div class="comment-item border-bottom pb-3 mb-3">
                <div class="d-flex justify-content-between align-items-start">
                    <div class="d-flex align-items-center">
                        <i class="fas fa-user-circle fa-lg text-muted me-2"></i>
                        <div>
                            <strong>${comment.author.fullName}</strong>
                            <small class="text-muted"> - ${comment.author.role}</small>
                            ${isInternal ? '<span class="badge bg-warning ms-2">Internal</span>' : ''}
                        </div>
                    </div>
                    <small class="text-muted">${commentDate}</small>
                </div>
                <div class="mt-2 ms-4">
                    <p class="mb-0">${comment.content}</p>
                    ${comment.commentType !== 'GENERAL' ? 
                        `<small class="text-muted"><em>${comment.commentType.replace('_', ' ')}</em></small>` : ''}
                </div>
            </div>
        `;
    });
    
    commentsList.innerHTML = commentsHtml;
}

// Add comment form handler
document.getElementById('addCommentForm').addEventListener('submit', function(e) {
    e.preventDefault();
    
    const content = document.getElementById('commentContent').value.trim();
    
    if (!content) {
        alert('Please enter a comment.');
        return;
    }
    
    const requestData = {
        complaintId: ${complaint.id},
        content: content,
        isInternal: false,
        commentType: 'GENERAL'
    };
    
    fetch('/api/comments/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem('token')
        },
        body: JSON.stringify(requestData)
    })
    .then(response => response.json())
    .then(data => {
        if (data.message) {
            document.getElementById('commentContent').value = '';
            loadComments(); // Reload comments
            // Show success message
            const alert = document.createElement('div');
            alert.className = 'alert alert-success alert-dismissible fade show';
            alert.innerHTML = `
                <i class="fas fa-check-circle"></i> Comment added successfully!
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            `;
            document.querySelector('.container').insertBefore(alert, document.querySelector('.row'));
        } else {
            alert('Failed to add comment: ' + (data.error || 'Unknown error'));
        }
    })
    .catch(error => {
        console.error('Error adding comment:', error);
        alert('Failed to add comment. Please try again.');
    });
});

// Auto-refresh comments every 30 seconds
setInterval(loadComments, 30000);
</script>

<%@ include file="../layout/footer.jsp" %>