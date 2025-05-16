using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Identity;
using System.ComponentModel.DataAnnotations;

namespace HanoiStudentGigs.API.Models
{
    public class User : IdentityUser
    {
        [Required]
        [MaxLength(100)]
        public string FullName { get; set; }

        [MaxLength(20)]
        public string UserType { get; set; } // "Student", "Employer", "Admin"

        // For Students
        [MaxLength(100)]
        public string SchoolName { get; set; }

        // For Employers
        [MaxLength(100)]
        public string CompanyName { get; set; }
        public string Address { get; set; }

        public string ProfileImageUrl { get; set; }
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

        // Navigation properties
        public UserProfile Profile { get; set; }
        public ICollection<Job> PostedJobs { get; set; }
        public ICollection<JobApplication> Applications { get; set; }
    }
}