using HanoiStudentGigs.API.DTOs;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace HanoiStudentGigs.ApiService.Services.Interface
{
    public interface IUserService
    {
        Task<UserProfileDTO> GetUserProfile(string userId);
        Task UpdateUserProfile(string userId, UpdateProfileDTO updateProfileDTO);
        Task UploadCv(string userId, UploadCvDTO uploadCvDTO);
    }
}''