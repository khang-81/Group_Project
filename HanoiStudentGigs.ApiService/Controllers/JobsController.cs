using HanoiStudentGigs.API.DTOs;
using HanoiStudentGigs.ApiService.Services.Interface;
using Microsoft.AspNetCore.Mvc;

namespace HanoiStudentGigs.API.Controllers
{
    [Route("api/jobs")]
    [ApiController]
    public class JobsController : ControllerBase
    {
        private readonly IJobService _jobService;

        public JobsController(IJobService jobService)
        {
            _jobService = jobService;
        }

        [HttpGet]
        public async Task<IActionResult> GetJobs([FromQuery] JobFilterParams filterParams)
        {
            var jobs = await _jobService.GetJobs(filterParams);
            return Ok(jobs);
        }

        [HttpGet("{jobId}")]
        public async Task<IActionResult> GetJob(int jobId)
        {
            var job = await _jobService.GetJob(jobId);
            return Ok(job);
        }
    }
}