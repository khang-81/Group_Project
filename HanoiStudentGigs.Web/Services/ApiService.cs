using HanoiStudentGigs.Web.Models;
using System.Net.Http.Json;

namespace HanoiStudentGigs.Web.Services
{
    public interface IApiService
    {
        Task<AuthResponseDTO> Login(LoginDTO loginDTO);
        Task<AuthResponseDTO> RegisterStudent(RegisterStudentDTO registerDTO);
        // Thêm các method API khác...
    }

    public class ApiService : IApiService
    {
        private readonly HttpClient _httpClient;
        private readonly IConfiguration _configuration;

        public ApiService(HttpClient httpClient, IConfiguration configuration)
        {
            _httpClient = httpClient;
            _configuration = configuration;
            _httpClient.BaseAddress = new Uri(_configuration["ApiSettings:BaseUrl"]);
        }

        public async Task<AuthResponseDTO> Login(LoginDTO loginDTO)
        {
            var response = await _httpClient.PostAsJsonAsync("/api/auth/login", loginDTO);
            response.EnsureSuccessStatusCode();
            return await response.Content.ReadFromJsonAsync<AuthResponseDTO>();
        }

        // Các phương thức API khác...
    }
}