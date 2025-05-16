using FluentValidation;
using HanoiStudentGigs.API.DTOs;

namespace HanoiStudentGigs.API.Validators
{
    public class RegisterStudentDTOValidator : AbstractValidator<RegisterStudentDTO>
    {
        public RegisterStudentDTOValidator()
        {
            RuleFor(x => x.Email)
                .NotEmpty().WithMessage("Email is required")
                .EmailAddress().WithMessage("Invalid email format");

            RuleFor(x => x.Password)
                .NotEmpty().WithMessage("Password is required")
                .MinimumLength(8).WithMessage("Password must be at least 8 characters")
                .Matches("[A-Z]").WithMessage("Password must contain at least one uppercase letter")
                .Matches("[a-z]").WithMessage("Password must contain at least one lowercase letter")
                .Matches("[0-9]").WithMessage("Password must contain at least one number")
                .Matches("[^a-zA-Z0-9]").WithMessage("Password must contain at least one special character");

            RuleFor(x => x.FullName)
                .NotEmpty().WithMessage("Full name is required")
                .MaximumLength(100).WithMessage("Full name cannot exceed 100 characters");

            RuleFor(x => x.PhoneNumber)
                .NotEmpty().WithMessage("Phone number is required")
                .Matches(@"^\+?(\d[\d-. ]+)?(\([\d-. ]+\))?[\d-. ]+\d$")
                .WithMessage("Invalid phone number format");

            RuleFor(x => x.SchoolName)
                .NotEmpty().WithMessage("School name is required")
                .MaximumLength(100).WithMessage("School name cannot exceed 100 characters");
        }
    }
}