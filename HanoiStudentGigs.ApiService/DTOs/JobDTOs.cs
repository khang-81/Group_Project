using System;
using System.Collections.Generic;

namespace HanoiStudentGigs.API.DTOs
{
    public class JobCreateDTO
    {
        public string Title { get; set; }
        public string Description { get; set; }
        public int CategoryId { get; set; }
        public int? LocationId { get; set; }
        public string AddressDetail { get; set; }
        public bool IsRemote { get; set; }
        public string JobType { get; set; }
        public string SalaryDescription { get; set; }
        public string Requirements { get; set; }
        public string ContactInfo { get; set; }
        public DateTime? ApplicationDeadline { get; set; }
        public List<int> RequiredSkillIds { get; set; }
    }

    public class JobUpdateDTO
    {
        public string Title { get; set; }
        public string Description { get; set; }
        public int CategoryId { get; set; }
        public int? LocationId { get; set; }
        public string AddressDetail { get; set; }
        public bool IsRemote { get; set; }
        public string JobType { get; set; }
        public string SalaryDescription { get; set; }
        public string Requirements { get; set; }
        public string ContactInfo { get; set; }
        public DateTime? ApplicationDeadline { get; set; }
        public string Status { get; set; }
        public List<int> RequiredSkillIds { get; set; }
    }

    public class JobResponseDTO
    {
        public int JobId { get; set; }
        public string Title { get; set; }
        public string Description { get; set; }
        public string Category { get; set; }
        public string Location { get; set; }
        public string AddressDetail { get; set; }
        public bool IsRemote { get; set; }
        public string JobType { get; set; }
        public string SalaryDescription { get; set; }
        public string Requirements { get; set; }
        public string ContactInfo { get; set; }
        public DateTime? ApplicationDeadline { get; set; }
        public string Status { get; set; }
        public DateTime CreatedAt { get; set; }
        public bool IsApproved { get; set; }
        public string EmployerName { get; set; }
        public string EmployerCompany { get; set; }
        public List<string> RequiredSkills { get; set; }
    }

    public class JobFilterParams
    {
        public string SearchTerm { get; set; }
        public int? CategoryId { get; set; }
        public int? LocationId { get; set; }
        public string JobType { get; set; }
        public bool? IsRemote { get; set; }
        public string SalaryRange { get; set; }
        public bool? IsApproved { get; set; } = true;
        public string Status { get; set; } = "Open";
    }
}