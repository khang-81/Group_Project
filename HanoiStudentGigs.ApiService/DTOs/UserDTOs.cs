namespace HanoiStudentGigs.API.DTOs
{
    public class UserProfileDTO
    {
        public string FullName { get; set; }
        public string PhoneNumber { get; set; }
        public string ProfileImageUrl { get; set; }

        // For Students
        public string SchoolName { get; set; }
        public string Major { get; set; }
        public string Year { get; set; }
        public string SkillsDescription { get; set; }
        public string Experience { get; set; }
        public string CvUrl { get; set; }

        // For Employers
        public string CompanyName { get; set; }
        public string Address { get; set; }
        public string CompanyDescription { get; set; }
        public string Website { get; set; }
    }

    public class UpdateProfileDTO
    {
        public string FullName { get; set; }
        public string PhoneNumber { get; set; }

        // For Students
        public string SchoolName { get; set; }
        public string Major { get; set; }
        public string Year { get; set; }
        public string SkillsDescription { get; set; }
        public string Experience { get; set; }

        // For Employers
        public string CompanyName { get; set; }
        public string Address { get; set; }
        public string CompanyDescription { get; set; }
        public string Website { get; set; }
    }

    public class UploadCvDTO
    {
        public string CvUrl { get; set; }
    }
}