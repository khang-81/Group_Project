// Services/IAuthService.cs
using HanoiStudentGigs.API.DTOs;
using System.Threading.Tasks;

namespace HanoiStudentGigs.API.Services
{
    public interface IAuthService
    {
        Task<AuthResponseDTO> RegisterStudent(RegisterStudentDTO registerStudentDTO);
        Task<AuthResponseDTO> RegisterEmployer(RegisterEmployerDTO registerEmployerDTO);
        Task<AuthResponseDTO> Login(LoginDTO loginDTO);
    }
}