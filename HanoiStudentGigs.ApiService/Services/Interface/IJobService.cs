using HanoiStudentGigs.API.DTOs;

namespace HanoiStudentGigs.ApiService.Services.Interface
{
    public interface IJobService
    {
        Task<JobResponseDTO> CreateJob(string employerId, JobCreateDTO jobCreateDTO);
        Task<IEnumerable<JobResponseDTO>> GetJobs(JobFilterParams filterParams);
        Task<JobResponseDTO> GetJob(int jobId);
        Task<IEnumerable<JobResponseDTO>> GetEmployerJobs(string employerId);
        Task<JobResponseDTO> UpdateJob(int jobId, string employerId, JobUpdateDTO jobUpdateDTO);
        Task DeleteJob(int jobId, string employerId);
        Task ApproveJob(int jobId);
    }
}