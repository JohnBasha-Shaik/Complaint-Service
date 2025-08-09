# Municipal Complaint Management System

A comprehensive Spring Boot application for managing municipal complaints with role-based access control, real-time tracking, and departmental assignment workflows.

## Features

### For Citizens
- **Easy Complaint Filing**: Submit complaints with categories (Water, Sanitation, Roads, etc.)
- **File Attachments**: Upload photos and documents to support complaints
- **Real-time Tracking**: Track complaint status with detailed timeline
- **Comment System**: Add comments and communicate with municipal staff
- **Dashboard**: View all submitted complaints with status summary

### For Municipal Staff
- **Complaint Management**: Review, assign, and update complaint status
- **Department Assignment**: Assign complaints to appropriate departments
- **Priority Management**: Set complaint priorities (Low, Medium, High, Urgent)
- **Comment Threads**: Internal and public comments for communication
- **Dashboard**: Statistics and overview of complaint workload

### For Administrators
- **User Management**: Manage citizen, staff, and admin accounts
- **Department Management**: Create and manage municipal departments
- **System Overview**: Complete system statistics and analytics
- **Role Assignment**: Assign users to departments and roles

## Technology Stack

- **Backend**: Spring Boot 3.2.0, Java 17
- **Database**: MySQL 8.0
- **Security**: Spring Security with JWT authentication
- **Frontend**: JSP, Bootstrap 5, Font Awesome
- **ORM**: Spring Data JPA with Hibernate
- **File Upload**: Commons FileUpload
- **Build Tool**: Maven

## Project Structure

```
src/main/java/com/municipal/
├── config/                 # Configuration classes
│   ├── SecurityConfig.java # Security configuration
│   ├── WebConfig.java      # Web MVC configuration
│   └── DataInitializer.java# Database initialization
├── controller/             # REST and MVC controllers
│   ├── AuthController.java # Authentication endpoints
│   ├── ComplaintController.java # Complaint management API
│   ├── CommentController.java   # Comment management API
│   ├── FileController.java     # File upload/download
│   └── WebController.java      # JSP view controllers
├── entity/                 # JPA entities
│   ├── User.java          # User entity with roles
│   ├── Complaint.java     # Complaint entity
│   ├── Comment.java       # Comment entity
│   └── Department.java    # Department entity
├── repository/             # Spring Data JPA repositories
├── security/               # Security components
│   ├── JwtTokenProvider.java   # JWT token handling
│   ├── UserPrincipal.java      # Security user details
│   ├── JwtAuthenticationFilter.java # JWT filter
│   └── CustomUserDetailsService.java # User details service
└── service/                # Business logic services
    ├── ComplaintService.java    # Complaint management
    ├── CommentService.java      # Comment management
    ├── UserService.java         # User management
    ├── DepartmentService.java   # Department management
    └── FileStorageService.java  # File handling

src/main/webapp/WEB-INF/views/
├── layout/                 # Common JSP layouts
│   ├── header.jsp         # Common header with navigation
│   └── footer.jsp         # Common footer
├── auth/                   # Authentication pages
│   ├── login.jsp          # Login page
│   └── register.jsp       # Registration page
├── citizen/                # Citizen interface
│   ├── new-complaint.jsp  # Complaint submission form
│   ├── my-complaints.jsp  # Citizen complaint list
│   └── track-complaint.jsp # Complaint tracking page
├── staff/                  # Staff interface
│   ├── dashboard.jsp      # Staff dashboard
│   ├── assign-complaints.jsp # Complaint assignment
│   └── view-complaints.jsp    # Complaint management
└── index.jsp              # Home page
```

## Database Schema

### Key Entities

1. **Users**: Citizens, Staff, and Admins with role-based access
2. **Complaints**: Municipal issues with status tracking
3. **Comments**: Communication threads for complaints
4. **Departments**: Municipal departments for assignment

### Complaint Workflow

1. **Submitted** → Citizen files complaint
2. **Acknowledged** → Staff reviews complaint
3. **Assigned** → Complaint assigned to department
4. **In Progress** → Work begins on resolution
5. **Resolved** → Issue is fixed
6. **Closed** → Complaint is finalized

## Setup Instructions

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Git

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd municipal-complaint-management
   ```

2. **Setup MySQL Database**
   ```sql
   CREATE DATABASE municipal_complaints;
   CREATE USER 'municipal_user'@'localhost' IDENTIFIED BY 'municipal_pass';
   GRANT ALL PRIVILEGES ON municipal_complaints.* TO 'municipal_user'@'localhost';
   FLUSH PRIVILEGES;
   ```

3. **Configure Database Connection**
   
   Update `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/municipal_complaints
   spring.datasource.username=municipal_user
   spring.datasource.password=municipal_pass
   ```

4. **Build and Run**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

5. **Access the Application**
   - URL: http://localhost:8080
   - The application will automatically create default users and departments

## Default User Accounts

The system creates default accounts for testing:

| Role | Username | Password | Description |
|------|----------|----------|-------------|
| Admin | admin | admin123 | System administrator |
| Staff | staff | staff123 | Municipal staff member |
| Citizen | citizen | citizen123 | Regular citizen user |

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration

### Complaints (REST API)
- `POST /api/complaints/submit` - Submit new complaint (Citizens)
- `GET /api/complaints/my` - Get user's complaints (Citizens)
- `GET /api/complaints/{id}` - Get complaint details
- `GET /api/complaints/all` - Get all complaints (Staff/Admin)
- `POST /api/complaints/{id}/assign/department` - Assign to department (Staff/Admin)
- `PUT /api/complaints/{id}/status` - Update status (Staff/Admin)

### Comments
- `POST /api/comments/add` - Add comment
- `GET /api/comments/complaint/{id}` - Get complaint comments
- `PUT /api/comments/{id}` - Update comment
- `DELETE /api/comments/{id}` - Delete comment

### File Management
- `GET /api/files/{fileName}` - Download attachment

## Web Interface Routes

### Public Pages
- `/` - Home page
- `/login` - Login page
- `/register` - Registration page

### Citizen Pages
- `/complaints/new` - File new complaint
- `/complaints/my` - View my complaints
- `/complaints/track/{id}` - Track specific complaint

### Staff Pages
- `/dashboard/staff` - Staff dashboard
- `/staff/complaints` - Manage all complaints
- `/staff/complaints/assign` - Assign complaints
- `/staff/complaints/{id}` - View complaint details

### Admin Pages
- `/dashboard/admin` - Admin dashboard

## Security Features

### Authentication & Authorization
- JWT-based API authentication
- Session-based web authentication
- Role-based access control (RBAC)
- CSRF protection for web forms

### Data Protection
- Password encryption using BCrypt
- File upload validation and size limits
- SQL injection prevention through JPA
- XSS protection in JSP views

## File Upload Configuration

- **Maximum file size**: 10MB
- **Supported formats**: Images (JPG, PNG, GIF), PDF, DOC, DOCX
- **Storage location**: `uploads/` directory
- **Access**: Authenticated users only

## Complaint Categories

The system supports the following complaint categories:

- **Water Supply**: Water issues, pipe leaks, pressure problems
- **Sanitation**: Garbage collection, sewage problems
- **Roads & Infrastructure**: Potholes, damaged sidewalks, traffic lights
- **Electricity**: Street lights, electrical hazards, power issues
- **Drainage**: Blocked drains, flooding, stormwater
- **Waste Management**: Missed collections, illegal dumping
- **Parks & Recreation**: Playground equipment, park maintenance
- **Others**: Any other municipal services

## Priority Levels

- **Low**: Non-urgent maintenance issues
- **Medium**: Standard municipal services (default)
- **High**: Issues affecting multiple residents
- **Urgent**: Health hazards, safety concerns, infrastructure failures

## Development Guidelines

### Code Style
- Follow Spring Boot best practices
- Use proper exception handling
- Implement comprehensive logging
- Write meaningful commit messages

### Testing
- Unit tests for service layer
- Integration tests for REST endpoints
- JSP view testing for user workflows

### Security Considerations
- Validate all user inputs
- Implement proper error handling
- Use parameterized queries
- Regular security updates

## Deployment

### Production Configuration

1. **Environment Variables**
   ```bash
   export DB_HOST=production-db-host
   export DB_USER=production-db-user
   export DB_PASSWORD=production-db-password
   export JWT_SECRET=production-jwt-secret
   ```

2. **Application Properties**
   ```properties
   spring.profiles.active=prod
   server.port=80
   logging.level.com.municipal=INFO
   ```

3. **Database Migration**
   - Use Flyway or Liquibase for production migrations
   - Backup existing data before updates

### Docker Deployment

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/complaint-management-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

## Monitoring & Maintenance

### Health Checks
- Spring Boot Actuator endpoints
- Database connection monitoring
- File system space monitoring

### Logging
- Application logs in `logs/` directory
- Error tracking and alerts
- Performance monitoring

### Backup Strategy
- Daily database backups
- File attachment backups
- Configuration backups

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Support

For support and questions:
- **Email**: support@municipal.gov
- **Phone**: (555) 123-4567
- **Hours**: Monday-Friday, 8:00 AM - 5:00 PM

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Spring Boot team for the excellent framework
- Bootstrap team for the responsive UI framework
- Font Awesome for the icon library
- MySQL team for the reliable database system

---

**Municipal Complaint Management System** - Streamlining citizen services and municipal operations.