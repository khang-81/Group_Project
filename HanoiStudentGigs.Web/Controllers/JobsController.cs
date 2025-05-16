using HanoiStudentGigs.Web.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace HanoiStudentGigs.Web.Controllers
{
    public class JobsController : Controller
    {
        private readonly IApiService _apiService;

        public JobsController(IApiService apiService)
        {
            _apiService = apiService;
        }

        public async Task<IActionResult> Index(JobFilterParams filterParams)
        {
            var jobs = await _apiService.GetJobs(filterParams);
            var model = new JobListViewModel
            {
                Jobs = jobs,
                FilterParams = filterParams
            };
            return View(model);
        }

        [Authorize(Roles = "Employer")]
        public IActionResult Create()
        {
            return View();
        }

        [Authorize(Roles = "Employer")]
        [HttpPost]
        public async Task<IActionResult> Create(JobCreateDTO model)
        {
            if (ModelState.IsValid)
            {
                var result = await _apiService.CreateJob(User.GetUserId(), model);
                if (result)
                {
                    return RedirectToAction("Dashboard", "Employers");
                }
                ModelState.AddModelError("", "Có lỗi khi đăng tin");
            }
            return View(model);
        }

        public async Task<IActionResult> Details(int id)
        {
            var job = await _apiService.GetJob(id);
            if (job == null)
            {
                return NotFound();
            }
            return View(job);
        }
    }
}