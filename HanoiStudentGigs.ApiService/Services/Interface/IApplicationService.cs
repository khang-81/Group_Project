using HanoiStudentGigs.API.DTOs;

namespace HanoiStudentGigs.ApiService.Services.Interface
{
    public interface IApplicationService
    {
        Task<ApplicationResponseDTO> CreateApplication(string studentId, ApplicationCreateDTO applicationCreateDTO);
        Task<IEnumerable<ApplicationResponseDTO>> GetStudentApplications(string studentId);
        Task<IEnumerable<ApplicationResponseDTO>> GetJobApplications(int jobId, string employerId);
        Task<ApplicationResponseDTO> UpdateApplicationStatus(int applicationId, string employerId, UpdateApplicationStatusDTO updateStatusDTO);
    }
}