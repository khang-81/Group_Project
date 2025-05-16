using HanoiStudentGigs.API.DTOs;
using HanoiStudentGigs.ApiService.Services.Interface;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;

namespace HanoiStudentGigs.API.Controllers
{
    [Authorize(Roles = "Student")]
    [Route("api/students")]
    [ApiController]
    public class StudentsController : ControllerBase
    {
        private readonly IUserService _userService;
        private readonly IApplicationService _applicationService;

        public StudentsController(IUserService userService, IApplicationService applicationService)
        {
            _userService = userService;
            _applicationService = applicationService;
        }

        [HttpGet("profile")]
        public async Task<IActionResult> GetProfile()
        {
            var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            var profile = await _userService.GetUserProfile(userId);
            return Ok(profile);
        }

        [HttpPut("profile")]
        public async Task<IActionResult> UpdateProfile(UpdateProfileDTO updateProfileDTO)
        {
            var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            await _userService.UpdateUserProfile(userId, updateProfileDTO);
            return NoContent();
        }

        [HttpPost("upload-cv")]
        public async Task<IActionResult> UploadCv(UploadCvDTO uploadCvDTO)
        {
            var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            await _userService.UploadCv(userId, uploadCvDTO);
            return NoContent();
        }

        [HttpGet("applications")]
        public async Task<IActionResult> GetApplications()
        {
            var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            var applications = await _applicationService.GetStudentApplications(userId);
            return Ok(applications);
        }

        [HttpPost("applications")]
        public async Task<IActionResult> CreateApplication(ApplicationCreateDTO applicationCreateDTO)
        {
            var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            var application = await _applicationService.CreateApplication(userId, applicationCreateDTO);
            return CreatedAtAction(nameof(GetApplications), application);
        }
    }
}