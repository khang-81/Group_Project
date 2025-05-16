using FluentValidation;
using HanoiStudentGigs.API.DTOs;

namespace HanoiStudentGigs.API.Validators
{
    public class JobCreateDTOValidator : AbstractValidator<JobCreateDTO>
    {
        public JobCreateDTOValidator()
        {
            // Title validation
            RuleFor(x => x.Title)
                .NotEmpty().WithMessage("Job title is required")
                .MaximumLength(200).WithMessage("Title cannot exceed 200 characters")
                .MinimumLength(10).WithMessage("Title should be at least 10 characters long")
                .Matches(@"^[a-zA-Z0-9\s.,!?-]+$").WithMessage("Title contains invalid characters");

            // Description validation
            RuleFor(x => x.Description)
                .NotEmpty().WithMessage("Job description is required")
                .MinimumLength(50).WithMessage("Description should be at least 50 characters long")
                .MaximumLength(5000).WithMessage("Description cannot exceed 5000 characters");

            // Category validation
            RuleFor(x => x.CategoryId)
                .GreaterThan(0).WithMessage("Invalid job category")
                .LessThanOrEqualTo(100).WithMessage("Invalid job category ID");

            // Location validation (if provided)
            When(x => x.LocationId.HasValue, () =>
            {
                RuleFor(x => x.LocationId)
                    .GreaterThan(0).WithMessage("Invalid location ID")
                    .LessThanOrEqualTo(100).WithMessage("Invalid location ID");
            });

            // Address detail validation (if not remote)
            When(x => !x.IsRemote && x.LocationId.HasValue, () =>
            {
                RuleFor(x => x.AddressDetail)
                    .NotEmpty().WithMessage("Address detail is required for non-remote jobs")
                    .MaximumLength(200).WithMessage("Address detail cannot exceed 200 characters");
            });

            // Job type validation
            RuleFor(x => x.JobType)
                .NotEmpty().WithMessage("Job type is required")
                .Must(BeAValidJobType).WithMessage("Invalid job type. Must be PartTime, Freelance, or Internship");

            // Salary validation
            RuleFor(x => x.SalaryDescription)
                .NotEmpty().WithMessage("Salary description is required")
                .MaximumLength(100).WithMessage("Salary description cannot exceed 100 characters")
                .Must(BeAValidSalaryDescription).WithMessage("Invalid salary format. Examples: '15-20k/hour', '500k-1M/month', 'Negotiable'");

            // Requirements validation
            RuleFor(x => x.Requirements)
                .NotEmpty().WithMessage("Requirements are required")
                .MinimumLength(20).WithMessage("Requirements should be at least 20 characters long")
                .MaximumLength(2000).WithMessage("Requirements cannot exceed 2000 characters");

            // Contact info validation
            RuleFor(x => x.ContactInfo)
                .NotEmpty().WithMessage("Contact information is required")
                .MaximumLength(200).WithMessage("Contact information cannot exceed 200 characters")
                .Must(BeAValidContactInfo).WithMessage("Contact info must contain either email or phone number");

            // Application deadline validation (if provided)
            When(x => x.ApplicationDeadline.HasValue, () =>
            {
                RuleFor(x => x.ApplicationDeadline)
                    .GreaterThan(DateTime.Now).WithMessage("Deadline must be in the future")
                    .LessThan(DateTime.Now.AddYears(1)).WithMessage("Deadline cannot be more than 1 year in the future");
            });

            // Required skills validation
            RuleFor(x => x.RequiredSkillIds)
                .NotNull().WithMessage("Required skills cannot be null")
                .Must(skills => skills == null || skills.Count <= 10).WithMessage("Cannot specify more than 10 required skills")
                .Must(skills => skills == null || !skills.Contains(0)).WithMessage("Skill ID cannot be 0");
        }

        private bool BeAValidJobType(string jobType)
        {
            return jobType == "PartTime" || jobType == "Freelance" || jobType == "Internship";
        }

        private bool BeAValidSalaryDescription(string salaryDescription)
        {
            if (salaryDescription.Equals("Negotiable", StringComparison.OrdinalIgnoreCase) ||
                salaryDescription.Equals("Thỏa thuận", StringComparison.OrdinalIgnoreCase))
            {
                return true;
            }

            // Check for common salary patterns like "15-20k/hour", "500k-1M/month", etc.
            return System.Text.RegularExpressions.Regex.IsMatch(salaryDescription,
                @"^(\d+k?-\d+k?|\d+[kM]?-\d+[kM]?)\/(hour|day|week|month)$") ||
                System.Text.RegularExpressions.Regex.IsMatch(salaryDescription,
                @"^\d+([.,]\d+)?[kM]?\s*(to|-)\s*\d+([.,]\d+)?[kM]?\s*(per\s*(hour|day|week|month))?$");
        }

        private bool BeAValidContactInfo(string contactInfo)
        {
            // Check for email or phone number pattern
            return System.Text.RegularExpressions.Regex.IsMatch(contactInfo,
                @"\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}\b") ||  // Email pattern
                System.Text.RegularExpressions.Regex.IsMatch(contactInfo,
                @"(\+?(\d{1,3}))?[-. (]*(\d{3})[-. )]*(\d{3})[-. ]*(\d{4})");  // Phone number pattern
        }
    }
}