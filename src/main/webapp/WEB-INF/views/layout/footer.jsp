    </div> <!-- End Container -->

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <div class="row">
                <div class="col-md-6">
                    <h5>Municipal Complaint Management System</h5>
                    <p class="mb-0">Streamlining citizen services and municipal operations.</p>
                </div>
                <div class="col-md-6 text-md-end">
                    <p class="mb-0">&copy; 2024 Municipal CMS. All rights reserved.</p>
                    <small class="text-muted">Powered by Spring Boot & MySQL</small>
                </div>
            </div>
        </div>
    </footer>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- Custom JavaScript -->
    <script>
        // Auto-hide alerts after 5 seconds
        setTimeout(function() {
            var alerts = document.querySelectorAll('.alert');
            alerts.forEach(function(alert) {
                var bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            });
        }, 5000);
        
        // Confirmation dialogs for dangerous actions
        function confirmAction(message) {
            return confirm(message || 'Are you sure you want to perform this action?');
        }
        
        // Format dates in user's timezone
        document.addEventListener('DOMContentLoaded', function() {
            var dateElements = document.querySelectorAll('[data-date]');
            dateElements.forEach(function(element) {
                var date = new Date(element.getAttribute('data-date'));
                element.textContent = date.toLocaleString();
            });
        });
    </script>
</body>
</html>