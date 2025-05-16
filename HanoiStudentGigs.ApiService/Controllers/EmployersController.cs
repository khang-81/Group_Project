using HanoiStudentGigs.API.DTOs;
using HanoiStudentGigs.ApiService.Services.Interface;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;

namespace HanoiStudentGigs.API.Controllers
{
    [Authorize(Roles = "Employer")]
    [Route("api/employers")]
    [ApiController]
    public class EmployersController : ControllerBase
    {
        private readonly IUserService _userService;
        private readonly IJobService _jobService;
        private readonly IApplicationService _applicationService;

        public EmployersController(
            IUserService userService,
            IJobService jobService,
            IApplicationService applicationService)
        {
            _userService = userService;
            _jobService = jobService;
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

        [HttpGet("jobs")]
        public async Task<IActionResult> GetJobs()
        {
            var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            var jobs = await _jobService.GetEmployerJobs(userId);
            return Ok(jobs);
        }

        [HttpPost("jobs")]
        public async Task<IActionResult> CreateJob(JobCreateDTO jobCreateDTO)
        {
            var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            var job = await _jobService.CreateJob(userId, jobCreateDTO);
            return CreatedAtAction(nameof(GetJobs), job);
        }

        [HttpPut("jobs/{jobId}")]
        public async Task<IActionResult> UpdateJob(int jobId, JobUpdateDTO jobUpdateDTO)
        {
            var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            var job = await _jobService.UpdateJob(jobId, userId, jobUpdateDTO);
            return Ok(job);
        }

        [HttpDelete("jobs/{jobId}")]
        public async Task<IActionResult> DeleteJob(int jobId)
        {
            var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            await _jobService.DeleteJob(jobId, userId);
            return NoContent();
        }

        [HttpGet("jobs/{jobId}/applications")]
        public async Task<IActionResult> GetJobApplications(int jobId)
        {
            var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            var applications = await _applicationService.GetJobApplications(jobId, userId);
            return Ok(applications);
        }

        [HttpPut("applications/{applicationId}/status")]
        public async Task<IActionResult> UpdateApplicationStatus(int applicationId, UpdateApplicationStatusDTO updateStatusDTO)
        {
            var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            var application = await _applicationService.UpdateApplicationStatus(applicationId, userId, updateStatusDTO);
            return Ok(application);
        }
    }
}