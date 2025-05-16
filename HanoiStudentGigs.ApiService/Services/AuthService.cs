using HanoiStudentGigs.API.Data;
using HanoiStudentGigs.API.DTOs;
using HanoiStudentGigs.API.Helpers;
using HanoiStudentGigs.API.Models;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using System.Threading.Tasks;

namespace HanoiStudentGigs.API.Services
{
    public class AuthService : IAuthService
    {
        private readonly UserManager<User> _userManager;
        private readonly ApplicationDbContext _context;
        private readonly JwtHelper _jwtHelper;

        public AuthService(UserManager<User> userManager, ApplicationDbContext context, JwtHelper jwtHelper)
        {
            _userManager = userManager;
            _context = context;
            _jwtHelper = jwtHelper;
        }

        public async Task<AuthResponseDTO> RegisterStudent(RegisterStudentDTO registerStudentDTO)
        {
            var user = new User
            {
                UserName = registerStudentDTO.Email,
                Email = registerStudentDTO.Email,
                FullName = registerStudentDTO.FullName,
                PhoneNumber = registerStudentDTO.PhoneNumber,
                SchoolName = registerStudentDTO.SchoolName,
                UserType = "Student"
            };

            var result = await _userManager.CreateAsync(user, registerStudentDTO.Password);

            if (!result.Succeeded)
            {
                throw new ApplicationException(string.Join(", ", result.Errors.Select(e => e.Description)));
            }

            await _userManager.AddToRoleAsync(user, "Student");

            // Create user profile
            var profile = new UserProfile { UserId = user.Id };
            _context.UserProfiles.Add(profile);
            await _context.SaveChangesAsync();

            return await GenerateAuthResponse(user);
        }

        public async Task<AuthResponseDTO> RegisterEmployer(RegisterEmployerDTO registerEmployerDTO)
        {
            var user = new User
            {
                UserName = registerEmployerDTO.Email,
                Email = registerEmployerDTO.Email,
                FullName = registerEmployerDTO.FullName,
                PhoneNumber = registerEmployerDTO.PhoneNumber,
                CompanyName = registerEmployerDTO.CompanyName,
                Address = registerEmployerDTO.Address,
                UserType = "Employer"
            };

            var result = await _userManager.CreateAsync(user, registerEmployerDTO.Password);

            if (!result.Succeeded)
            {
                throw new ApplicationException(string.Join(", ", result.Errors.Select(e => e.Description)));
            }

            await _userManager.AddToRoleAsync(user, "Employer");

            // Create user profile
            var profile = new UserProfile { UserId = user.Id };
            _context.UserProfiles.Add(profile);
            await _context.SaveChangesAsync();

            return await GenerateAuthResponse(user);
        }

        public async Task<AuthResponseDTO> Login(LoginDTO loginDTO)
        {
            var user = await _userManager.FindByEmailAsync(loginDTO.Email);

            if (user == null || !await _userManager.CheckPasswordAsync(user, loginDTO.Password))
            {
                throw new ApplicationException("Invalid email or password");
            }

            return await GenerateAuthResponse(user);
        }

        private async Task<AuthResponseDTO> GenerateAuthResponse(User user)
        {
            var token = _jwtHelper.GenerateJwtToken(user);

            return new AuthResponseDTO
            {
                Token = token,
                Expiration = DateTime.UtcNow.AddMinutes(double.Parse(_configuration["JwtSettings:DurationInMinutes"])),
                UserId = user.Id,
                UserType = user.UserType,
                FullName = user.FullName,
                ProfileImageUrl = user.ProfileImageUrl
            };
        }
    }
}