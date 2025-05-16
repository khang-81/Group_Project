using AutoMapper;
using HanoiStudentGigs.API.DTOs;
using HanoiStudentGigs.API.Models;
using static System.Runtime.InteropServices.JavaScript.JSType;

namespace HanoiStudentGigs.API.Helpers
{
    public class AutoMapperProfile : Profile
    {
        public AutoMapperProfile()
        {
            // Auth mappings
            CreateMap<RegisterStudentDTO, User>();
            CreateMap<RegisterEmployerDTO, User>();

            // User mappings
            CreateMap<User, UserProfileDTO>()
                .ForMember(dest => dest.Major, opt => opt.MapFrom(src => src.Profile.Major))
                .ForMember(dest => dest.Year, opt => opt.MapFrom(src => src.Profile.Year))
                .ForMember(dest => dest.SkillsDescription, opt => opt.MapFrom(src => src.Profile.SkillsDescription))
                .ForMember(dest => dest.Experience, opt => opt.MapFrom(src => src.Profile.Experience))
                .ForMember(dest => dest.CvUrl, opt => opt.MapFrom(src => src.Profile.CvUrl))
                .ForMember(dest => dest.CompanyDescription, opt => opt.MapFrom(src => src.Profile.CompanyDescription))
                .ForMember(dest => dest.Website, opt => opt.MapFrom(src => src.Profile.Website));

            CreateMap<UpdateProfileDTO, User>();
            CreateMap<UpdateProfileDTO, UserProfile>();

            // Job mappings
            CreateMap<JobCreateDTO, Job>();
            CreateMap<JobUpdateDTO, Job>();

            CreateMap<Job, JobResponseDTO>()
                .ForMember(dest => dest.Category, opt => opt.MapFrom(src => src.Category.Name))
                .ForMember(dest => dest.Location, opt => opt.MapFrom(src => src.Location != null ? src.Location.Name : "Remote"))
                .ForMember(dest => dest.EmployerName, opt => opt.MapFrom(src => src.Employer.FullName))
                .ForMember(dest => dest.EmployerCompany, opt => opt.MapFrom(src => src.Employer.CompanyName))
                .ForMember(dest => dest.RequiredSkills, opt => opt.MapFrom(src => src.JobSkills.Select(js => js.Skill.Name).ToList()));

            // Application mappings
            CreateMap<JobApplication, ApplicationResponseDTO>()
                .ForMember(dest => dest.JobTitle, opt => opt.MapFrom(src => src.Job.Title))
                .ForMember(dest => dest.StudentName, opt => opt.MapFrom(src => src.Student.FullName))
                .ForMember(dest => dest.StudentSchool, opt => opt.MapFrom(src => src.Student.SchoolName));
        }
    }
}