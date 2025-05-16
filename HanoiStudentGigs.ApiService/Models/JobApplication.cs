namespace HanoiStudentGigs.API.Models
{
    public class JobApplication
    {
        public int ApplicationId { get; set; }
        public int JobId { get; set; }
        public Job Job { get; set; }
        public string StudentUserId { get; set; }
        public User Student { get; set; }
        public DateTime ApplicationDate { get; set; } = DateTime.UtcNow;
        public string CoverLetter { get; set; }
        public string CvUrl { get; set; }
        public string Status { get; set; } = "Submitted"; // "Submitted", "Viewed", "Interviewing", "Hired", "Rejected"
    }
}