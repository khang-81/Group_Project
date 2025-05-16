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
    public class JobService : IJobService
    {
        private readonly ApplicationDbContext _context;
        private readonly IMapper _mapper;

        public JobService(ApplicationDbContext context, IMapper mapper)
        {
            _context = context;
            _mapper = mapper;
        }

        public async Task<JobResponseDTO> CreateJob(string employerId, JobCreateDTO jobCreateDTO)
        {
            var job = _mapper.Map<Job>(jobCreateDTO);
            job.EmployerUserId = employerId;
            job.IsApproved = false; // Needs admin approval

            _context.Jobs.Add(job);
            await _context.SaveChangesAsync();

            // Add required skills
            if (jobCreateDTO.RequiredSkillIds != null && jobCreateDTO.RequiredSkillIds.Any())
            {
                foreach (var skillId in jobCreateDTO.RequiredSkillIds)
                {
                    _context.JobSkills.Add(new JobSkill { JobId = job.JobId, SkillId = skillId });
                }
                await _context.SaveChangesAsync();
            }

            return await GetJob(job.JobId);
        }

        public async Task<IEnumerable<JobResponseDTO>> GetJobs(JobFilterParams filterParams)
        {
            var query = _context.Jobs
                .Include(j => j.Category)
                .Include(j => j.Location)
                .Include(j => j.Employer)
                .Include(j => j.JobSkills)
                .ThenInclude(js => js.Skill)
                .AsQueryable();

            // Apply filters
            if (!string.IsNullOrEmpty(filterParams.SearchTerm))
            {
                query = query.Where(j => j.Title.Contains(filterParams.SearchTerm) ||
                                       j.Description.Contains(filterParams.SearchTerm));
            }

            if (filterParams.CategoryId.HasValue)
            {
                query = query.Where(j => j.CategoryId == filterParams.CategoryId.Value);
            }

            if (filterParams.LocationId.HasValue)
            {
                query = query.Where(j => j.LocationId == filterParams.LocationId.Value);
            }

            if (!string.IsNullOrEmpty(filterParams.JobType))
            {
                query = query.Where(j => j.JobType == filterParams.JobType);
            }

            if (filterParams.IsRemote.HasValue)
            {
                query = query.Where(j => j.IsRemote == filterParams.IsRemote.Value);
            }

            if (filterParams.IsApproved.HasValue)
            {
                query = query.Where(j => j.IsApproved == filterParams.IsApproved.Value);
            }

            if (!string.IsNullOrEmpty(filterParams.Status))
            {
                query = query.Where(j => j.Status == filterParams.Status);
            }

            var jobs = await query.OrderByDescending(j => j.CreatedAt).ToListAsync();
            return _mapper.Map<IEnumerable<JobResponseDTO>>(jobs);
        }

        public async Task<JobResponseDTO> GetJob(int jobId)
        {
            var job = await _context.Jobs
                .Include(j => j.Category)
                .Include(j => j.Location)
                .Include(j => j.Employer)
                .Include(j => j.JobSkills)
                .ThenInclude(js => js.Skill)
                .FirstOrDefaultAsync(j => j.JobId == jobId);

            if (job == null)
            {
                throw new KeyNotFoundException("Job not found");
            }

            return _mapper.Map<JobResponseDTO>(job);
        }

        public async Task<IEnumerable<JobResponseDTO>> GetEmployerJobs(string employerId)
        {
            var jobs = await _context.Jobs
                .Where(j => j.EmployerUserId == employerId)
                .Include(j => j.Category)
                .Include(j => j.Location)
                .Include(j => j.Employer)
                .Include(j => j.JobSkills)
                .ThenInclude(js => js.Skill)
                .OrderByDescending(j => j.CreatedAt)
                .ToListAsync();

            return _mapper.Map<IEnumerable<JobResponseDTO>>(jobs);
        }

        public async Task<JobResponseDTO> UpdateJob(int jobId, string employerId, JobUpdateDTO jobUpdateDTO)
        {
            var job = await _context.Jobs
                .Include(j => j.JobSkills)
                .FirstOrDefaultAsync(j => j.JobId == jobId && j.EmployerUserId == employerId);

            if (job == null)
            {
                throw new KeyNotFoundException("Job not found or you don't have permission to update it");
            }

            _mapper.Map(jobUpdateDTO, job);
            job.IsApproved = false; // Needs re-approval after update

            // Update skills
            if (jobUpdateDTO.RequiredSkillIds != null)
            {
                // Remove existing skills
                _context.JobSkills.RemoveRange(job.JobSkills);

                // Add new skills
                foreach (var skillId in jobUpdateDTO.RequiredSkillIds)
                {
                    _context.JobSkills.Add(new JobSkill { JobId = job.JobId, SkillId = skillId });
                }
            }

            await _context.SaveChangesAsync();
            return await GetJob(job.JobId);
        }

        public async Task DeleteJob(int jobId, string employerId)
        {
            var job = await _context.Jobs
                .FirstOrDefaultAsync(j => j.JobId == jobId && j.EmployerUserId == employerId);

            if (job == null)
            {
                throw new KeyNotFoundException("Job not found or you don't have permission to delete it");
            }

            _context.Jobs.Remove(job);
            await _context.SaveChangesAsync();
        }

        public async Task ApproveJob(int jobId)
        {
            var job = await _context.Jobs.FindAsync(jobId);
            if (job == null)
            {
                throw new KeyNotFoundException("Job not found");
            }

            job.IsApproved = true;
            await _context.SaveChangesAsync();
        }
    }
}