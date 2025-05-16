using System;

namespace HanoiStudentGigs.API.DTOs
{
    public class ApplicationCreateDTO
    {
        public int JobId { get; set; }
        public string CoverLetter { get; set; }
    }

    public class ApplicationResponseDTO
    {
        public int ApplicationId { get; set; }
        public int JobId { get; set; }
        public string JobTitle { get; set; }
        public string StudentName { get; set; }
        public string StudentSchool { get; set; }
        public DateTime ApplicationDate { get; set; }
        public string CoverLetter { get; set; }
        public string CvUrl { get; set; }
        public string Status { get; set; }
    }

    public class UpdateApplicationStatusDTO
    {
        public string Status { get; set; }
    }
}