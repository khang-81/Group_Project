// Xử lý filter jobs
$(document).ready(function () {
    $('#job-filter-form').on('submit', function (e) {
        e.preventDefault();
        const formData = $(this).serialize();
        window.location.href = '/Jobs?' + formData;
    });

    // Xử lý apply job
    $('.apply-job-btn').click(function () {
        const jobId = $(this).data('jobid');
        // Gọi API apply job
    });
});