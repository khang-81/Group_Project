using AutoMapper;
using HanoiStudentGigs.API.Data;
using HanoiStudentGigs.API.DTOs;
using HanoiStudentGigs.API.Models;
using HanoiStudentGigs.ApiService.Services.Interface;
using Microsoft.EntityFrameworkCore;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace HanoiStudentGigs.API.Services
{
    public class ApplicationService : IApplicationService
    {
        private readonly ApplicationDbContext _context;
        private readonly IMapper _mapper;

        public ApplicationService(ApplicationDbContext context, IMapper mapper)
        {
            _context = context;
            _mapper = mapper;
        }

        public async Task<ApplicationResponseDTO> CreateApplication(string studentId, ApplicationCreateDTO applicationCreateDTO)
        {
            // Check if job exists
            var job = await _context.Jobs.FindAsync(applicationCreateDTO.JobId);
            if (job == null || !job.IsApproved || job.Status != "Open")
            {
                throw new ApplicationException("Job is not available for application");
            }

            // Check if student has already applied
            var existingApplication = await _context.JobApplications
                .FirstOrDefaultAsync(ja => ja.JobId == applicationCreateDTO.JobId && ja.StudentUserId == studentId);

            if (existingApplication != null)
            {
                throw new ApplicationException("You have already applied for this job");
            }

            // Get student's CV URL
            var studentProfile = await _context.UserProfiles.FirstOrDefaultAsync(p => p.UserId == studentId);
            var cvUrl = studentProfile?.CvUrl;

            var application = new JobApplication
            {
                JobId = applicationCreateDTO.JobId,
                StudentUserId = studentId,
                CoverLetter = applicationCreateDTO.CoverLetter,
                CvUrl = cvUrl,
                Status = "Submitted"
            };

            _context.JobApplications.Add(application);
            await _context.SaveChangesAsync();

            return await GetApplicationResponse(application.ApplicationId);
        }

        public async Task<IEnumerable<ApplicationResponseDTO>> GetStudentApplications(string studentId)
        {
            var applications = await _context.JobApplications
                .Where(ja => ja.StudentUserId == studentId)
                .Include(ja => ja.Job)
                .Include(ja => ja.Student)
                .OrderByDescending(ja => ja.ApplicationDate)
                .ToListAsync();

            return _mapper.Map<IEnumerable<ApplicationResponseDTO>>(applications);
        }

        public async Task<IEnumerable<ApplicationResponseDTO>> GetJobApplications(int jobId, string employerId)
        {
            // Verify that the employer owns the job
            var job = await _context.Jobs.FirstOrDefaultAsync(j => j.JobId == jobId && j.EmployerUserId == employerId);
            if (job == null)
            {
                throw new UnauthorizedAccessException("You don't have permission to view these applications");
            }

            var applications = await _context.JobApplications
                .Where(ja => ja.JobId == jobId)
                .Include(ja => ja.Job)
                .Include(ja => ja.Student)
                .OrderByDescending(ja => ja.ApplicationDate)
                .ToListAsync();

            return _mapper.Map<IEnumerable<ApplicationResponseDTO>>(applications);
        }

        public async Task<ApplicationResponseDTO> UpdateApplicationStatus(int applicationId, string employerId, UpdateApplicationStatusDTO updateStatusDTO)
        {
            var application = await _context.JobApplications
                .Include(ja => ja.Job)
                .FirstOrDefaultAsync(ja => ja.ApplicationId == applicationId);

            if (application == null)
            {
                throw new KeyNotFoundException("Application not found");
            }

            // Verify that the employer owns the job
            if (application.Job.EmployerUserId != employerId)
            {
                throw new UnauthorizedAccessException("You don't have permission to update this application");
            }

            application.Status = updateStatusDTO.Status;
            await _context.SaveChangesAsync();

            return await GetApplicationResponse(application.ApplicationId);
        }

        private async Task<ApplicationResponseDTO> GetApplicationResponse(int applicationId)
        {
            var application = await _context.JobApplications
                .Include(ja => ja.Job)
                .Include(ja => ja.Student)
                .FirstOrDefaultAsync(ja => ja.ApplicationId == applicationId);

            return _mapper.Map<ApplicationResponseDTO>(application);
        }
    }
}