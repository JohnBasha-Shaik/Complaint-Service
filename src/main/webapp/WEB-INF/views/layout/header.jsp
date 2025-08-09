<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Municipal Complaint Management System</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <!-- Custom CSS -->
    <style>
        .navbar-brand {
            font-weight: bold;
        }
        .status-badge {
            font-size: 0.8em;
        }
        .priority-high {
            color: #dc3545;
        }
        .priority-urgent {
            color: #fd7e14;
            font-weight: bold;
        }
        .complaint-card {
            border-left: 4px solid #007bff;
            transition: all 0.3s;
        }
        .complaint-card:hover {
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        }
        .status-submitted { border-left-color: #17a2b8; }
        .status-acknowledged { border-left-color: #ffc107; }
        .status-in_progress { border-left-color: #fd7e14; }
        .status-assigned { border-left-color: #6f42c1; }
        .status-resolved { border-left-color: #28a745; }
        .status-closed { border-left-color: #6c757d; }
        .status-rejected { border-left-color: #dc3545; }
        
        .footer {
            background-color: #343a40;
            color: white;
            padding: 20px 0;
            margin-top: 50px;
        }
        
        .dashboard-card {
            border: none;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            transition: transform 0.3s;
        }
        
        .dashboard-card:hover {
            transform: translateY(-5px);
        }
    </style>
</head>
<body>
    <!-- Navigation Bar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="/">
                <i class="fas fa-city"></i> Municipal CMS
            </a>
            
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="/">Home</a>
                    </li>
                    
                    <sec:authorize access="hasRole('CITIZEN')">
                        <li class="nav-item">
                            <a class="nav-link" href="/complaints/new">File Complaint</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="/complaints/my">My Complaints</a>
                        </li>
                    </sec:authorize>
                    
                    <sec:authorize access="hasRole('STAFF') or hasRole('ADMIN')">
                        <li class="nav-item">
                            <a class="nav-link" href="/dashboard/staff">Dashboard</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="/staff/complaints">All Complaints</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="/staff/complaints/assign">Assign Complaints</a>
                        </li>
                    </sec:authorize>
                    
                    <sec:authorize access="hasRole('ADMIN')">
                        <li class="nav-item">
                            <a class="nav-link" href="/dashboard/admin">Admin Panel</a>
                        </li>
                    </sec:authorize>
                </ul>
                
                <ul class="navbar-nav">
                    <sec:authorize access="!isAuthenticated()">
                        <li class="nav-item">
                            <a class="nav-link" href="/login">Login</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="/register">Register</a>
                        </li>
                    </sec:authorize>
                    
                    <sec:authorize access="isAuthenticated()">
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown">
                                <i class="fas fa-user"></i> 
                                <sec:authentication property="principal.username" />
                                <span class="badge bg-secondary ms-1">
                                    <sec:authentication property="principal.role" />
                                </span>
                            </a>
                            <ul class="dropdown-menu">
                                <li><a class="dropdown-item" href="/profile">Profile</a></li>
                                <li><hr class="dropdown-divider"></li>
                                <li>
                                    <form action="/logout" method="post" class="d-inline">
                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                        <button type="submit" class="dropdown-item">Logout</button>
                                    </form>
                                </li>
                            </ul>
                        </li>
                    </sec:authorize>
                </ul>
            </div>
        </div>
    </nav>

    <!-- Alert Messages -->
    <div class="container mt-3">
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="fas fa-check-circle"></i> ${successMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-circle"></i> ${errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        
        <c:if test="${not empty infoMessage}">
            <div class="alert alert-info alert-dismissible fade show" role="alert">
                <i class="fas fa-info-circle"></i> ${infoMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
    </div>

    <!-- Main Content Area -->
    <div class="container mt-4">