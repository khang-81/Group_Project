namespace HanoiStudentGigs.API.Models
{
    public class UserProfile
    {
        public int Id { get; set; }
        public string UserId { get; set; }
        public User User { get; set; }

        // For Students
        public string Major { get; set; } // Chuyên ngành
        public string Year { get; set; } // Năm học
        public string SkillsDescription { get; set; }
        public string Experience { get; set; }
        public string CvUrl { get; set; }

        // For Employers
        public string CompanyDescription { get; set; }
        public string Website { get; set; }
    }
}