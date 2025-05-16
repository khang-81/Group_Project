using HanoiStudentGigs.Web.Models;
using HanoiStudentGigs.Web.Services;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;

namespace HanoiStudentGigs.Web.Controllers
{
    public class AuthController : Controller
    {
        private readonly IApiService _apiService;

        public AuthController(IApiService apiService)
        {
            _apiService = apiService;
        }

        [HttpGet]
        public IActionResult Login(string returnUrl = null)
        {
            ViewData["ReturnUrl"] = returnUrl;
            return View();
        }

        [HttpPost]
        public async Task<IActionResult> Login(LoginViewModel model, string returnUrl = null)
        {
            if (ModelState.IsValid)
            {
                var result = await _apiService.Login(new LoginDTO
                {
                    Email = model.Email,
                    Password = model.Password
                });

                if (result != null)
                {
                    var claims = new List<Claim>
                    {
                        new Claim(ClaimTypes.NameIdentifier, result.UserId),
                        new Claim(ClaimTypes.Email, model.Email),
                        new Claim(ClaimTypes.Role, result.UserType)
                    };

                    var identity = new ClaimsIdentity(claims, CookieAuthenticationDefaults.AuthenticationScheme);
                    var principal = new ClaimsPrincipal(identity);

                    await HttpContext.SignInAsync(CookieAuthenticationDefaults.AuthenticationScheme, principal);

                    if (!string.IsNullOrEmpty(returnUrl))
                    {
                        return LocalRedirect(returnUrl);
                    }
                    return RedirectToAction("Index", "Home");
                }
                ModelState.AddModelError("", "Đăng nhập thất bại");
            }
            return View(model);
        }

        [HttpGet]
        public IActionResult Register()
        {
            return View();
        }

        [HttpPost]
        public async Task<IActionResult> Register(RegisterStudentViewModel model)
        {
            if (ModelState.IsValid)
            {
                var result = await _apiService.RegisterStudent(new RegisterStudentDTO
                {
                    Email = model.Email,
                    Password = model.Password,
                    FullName = model.FullName,
                    PhoneNumber = model.PhoneNumber,
                    SchoolName = model.SchoolName
                });

                if (result != null)
                {
                    // Tự động đăng nhập sau khi đăng ký
                    return RedirectToAction("Login");
                }
                ModelState.AddModelError("", "Đăng ký thất bại");
            }
            return View(model);
        }

        [HttpPost]
        public async Task<IActionResult> Logout()
        {
            await HttpContext.SignOutAsync(CookieAuthenticationDefaults.AuthenticationScheme);
            return RedirectToAction("Index", "Home");
        }
    }
}