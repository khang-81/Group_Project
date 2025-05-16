using HanoiStudentGigs.API.DTOs;
using HanoiStudentGigs.API.Services;
using Microsoft.AspNetCore.Mvc;

namespace HanoiStudentGigs.API.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class AuthController : ControllerBase
    {
        private readonly IAuthService _authService;

        public AuthController(IAuthService authService)
        {
            _authService = authService;
        }

        [HttpPost("register/student")]
        public async Task<IActionResult> RegisterStudent(RegisterStudentDTO registerStudentDTO)
        {
            var response = await _authService.RegisterStudent(registerStudentDTO);
            return Ok(response);
        }

        [HttpPost("register/employer")]
        public async Task<IActionResult> RegisterEmployer(RegisterEmployerDTO registerEmployerDTO)
        {
            var response = await _authService.RegisterEmployer(registerEmployerDTO);
            return Ok(response);
        }

        [HttpPost("login")]
        public async Task<IActionResult> Login(LoginDTO loginDTO)
        {
            var response = await _authService.Login(loginDTO);
            return Ok(response);
        }
    }
}