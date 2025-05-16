using AutoMapper;
using HanoiStudentGigs.API.Data;
using HanoiStudentGigs.API.DTOs;
using HanoiStudentGigs.API.Models;
using HanoiStudentGigs.ApiService.Services.Interface;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using System.Threading.Tasks;

namespace HanoiStudentGigs.API.Services
{
    public class UserService : IUserService
    {
        private readonly ApplicationDbContext _context;
        private readonly UserManager<User> _userManager;
        private readonly IMapper _mapper;

        public UserService(ApplicationDbContext context, UserManager<User> userManager, IMapper mapper)
        {
            _context = context;
            _userManager = userManager;
            _mapper = mapper;
        }

        public async Task<UserProfileDTO> GetUserProfile(string userId)
        {
            var user = await _context.Users
                .Include(u => u.Profile)
                .FirstOrDefaultAsync(u => u.Id == userId);

            if (user == null)
            {
                throw new KeyNotFoundException("User not found");
            }

            return _mapper.Map<UserProfileDTO>(user);
        }

        public async Task UpdateUserProfile(string userId, UpdateProfileDTO updateProfileDTO)
        {
            var user = await _userManager.FindByIdAsync(userId);
            if (user == null)
            {
                throw new KeyNotFoundException("User not found");
            }

            _mapper.Map(updateProfileDTO, user);

            var profile = await _context.UserProfiles.FirstOrDefaultAsync(p => p.UserId == userId);
            if (profile == null)
            {
                profile = new UserProfile { UserId = userId };
                _context.UserProfiles.Add(profile);
            }

            _mapper.Map(updateProfileDTO, profile);

            await _context.SaveChangesAsync();
            await _userManager.UpdateAsync(user);
        }

        public async Task UploadCv(string userId, UploadCvDTO uploadCvDTO)
        {
            var profile = await _context.UserProfiles.FirstOrDefaultAsync(p => p.UserId == userId);
            if (profile == null)
            {
                throw new KeyNotFoundException("User profile not found");
            }

            profile.CvUrl = uploadCvDTO.CvUrl;
            await _context.SaveChangesAsync();
        }
    }
}