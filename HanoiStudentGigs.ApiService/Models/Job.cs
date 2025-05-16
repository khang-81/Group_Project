using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using Microsoft.AspNetCore.Builder;
using static Google.Protobuf.Reflection.SourceCodeInfo.Types;

namespace HanoiStudentGigs.API.Models
{
    public class Job
    {
        public int JobId { get; set; }

        [Required]
        public string EmployerUserId { get; set; }
        public User Employer { get; set; }

        [Required]
        [MaxLength(200)]
        public string Title { get; set; }

        [Required]
        public string Description { get; set; }

        public int CategoryId { get; set; }
        public JobCategory Category { get; set; }

        public int? LocationId { get; set; }
        public Location Location { get; set; }

        public string AddressDetail { get; set; }
        public bool IsRemote { get; set; } = false;

        [Required]
        [MaxLength(50)]
        public string JobType { get; set; } // "PartTime", "Freelance", "Internship"

        [Required]
        [MaxLength(100)]
        public string SalaryDescription { get; set; }

        public string Requirements { get; set; }
        public string ContactInfo { get; set; }
        public DateTime? ApplicationDeadline { get; set; }

        [MaxLength(20)]
        public string Status { get; set; } = "Open"; // "Open", "Closed"

        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
        public bool IsApproved { get; set; } = false;

        // Navigation properties
        public ICollection<JobSkill> JobSkills { get; set; }
        public ICollection<JobApplication> Applications { get; set; }
    }
}