using HanoiStudentGigs.API.DTOs;
using HanoiStudentGigs.ApiService.Services.Interface;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace HanoiStudentGigs.API.Controllers
{
    [Authorize(Roles = "Admin")]
    [Route("api/admin")]
    [ApiController]
    public class AdminController : ControllerBase
    {
        private readonly IJobService _jobService;

        public AdminController(IJobService jobService)
        {
            _jobService = jobService;
        }

        [HttpPost("jobs/{jobId}/approve")]
        public async Task<IActionResult> ApproveJob(int jobId)
        {
            await _jobService.ApproveJob(jobId);
            return NoContent();
        }
    }
}