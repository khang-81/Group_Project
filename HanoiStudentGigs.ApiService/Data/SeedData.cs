using HanoiStudentGigs.API.Models;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using static Google.Protobuf.Reflection.SourceCodeInfo.Types;

namespace HanoiStudentGigs.API.Data
{
    public static class SeedData
    {
        public static async Task Initialize(ApplicationDbContext context,
            UserManager<User> userManager,
            RoleManager<IdentityRole> roleManager)
        {
            context.Database.EnsureCreated();

            // Seed roles
            string[] roleNames = { "Admin", "Employer", "Student" };
            foreach (var roleName in roleNames)
            {
                if (!await roleManager.RoleExistsAsync(roleName))
                {
                    await roleManager.CreateAsync(new IdentityRole(roleName));
                }
            }

            // Seed admin user
            if (await userManager.FindByEmailAsync("admin@hanoistudentgigs.com") == null)
            {
                var adminUser = new User
                {
                    UserName = "admin@hanoistudentgigs.com",
                    Email = "admin@hanoistudentgigs.com",
                    FullName = "System Admin",
                    PhoneNumber = "0123456789",
                    UserType = "Admin"
                };

                var result = await userManager.CreateAsync(adminUser, "Admin@123");
                if (result.Succeeded)
                {
                    await userManager.AddToRoleAsync(adminUser, "Admin");
                }
            }

            // Seed job categories if empty
            if (!context.JobCategories.Any())
            {
                var categories = new List<JobCategory>
                {
                    new JobCategory { Name = "Dạy kèm/Gia sư" },
                    new JobCategory { Name = "Phục vụ/Nhà hàng" },
                    new JobCategory { Name = "Bán hàng" },
                    new JobCategory { Name = "Thiết kế đồ họa" },
                    new JobCategory { Name = "Lập trình/IT" },
                    new JobCategory { Name => "Dịch thuật" },
                    new JobCategory { Name => "Tiếp thị/Marketing" },
                    new JobCategory { Name => "Viết lách/Nội dung" },
                    new JobCategory { Name => "Hỗ trợ văn phòng" },
                    new JobCategory { Name => "Khác" }
                };

                await context.JobCategories.AddRangeAsync(categories);
            }

            // Seed locations if empty
            if (!context.Locations.Any())
            {
                var locations = new List<Location>
                {
                    new Location { Name = "Ba Đình" },
                    new Location { Name = "Hoàn Kiếm" },
                    new Location { Name = "Hai Bà Trưng" },
                    new Location { Name = "Đống Đa" },
                    new Location { Name = "Tây Hồ" },
                    new Location { Name = "Cầu Giấy" },
                    new Location { Name = "Thanh Xuân" },
                    new Location { Name = "Hoàng Mai" },
                    new Location { Name = "Long Biên" },
                    new Location { Name = "Nam Từ Liêm" },
                    new Location { Name = "Bắc Từ Liêm" },
                    new Location { Name = "Hà Đông" },
                    new Location { Name = "Sơn Tây" },
                    new Location { Name = "Ba Vì" },
                    new Location { Name = "Remote" }
                };

                await context.Locations.AddRangeAsync(locations);
            }

            // Seed skills if empty
            if (!context.Skills.Any())
            {
                var skills = new List<Skill>
                {
                    new Skill { Name = "Tiếng Anh" },
                    new Skill { Name = "Tiếng Nhật" },
                    new Skill { Name = "Tiếng Hàn" },
                    new Skill { Name = "Tiếng Trung" },
                    new Skill { Name = "Photoshop" },
                    new Skill { Name = "Illustrator" },
                    new Skill { Name = "C#" },
                    new Skill { Name = "Java" },
                    new Skill { Name = "Python" },
                    new Skill { Name = "JavaScript" },
                    new Skill { Name = "HTML/CSS" },
                    new Skill { Name = "React" },
                    new Skill { Name = "Node.js" },
                    new Skill { Name = "SQL" },
                    new Skill { Name = "Giao tiếp tốt" },
                    new Skill { Name = "Làm việc nhóm" },
                    new Skill { Name = "Quản lý thời gian" }
                };

                await context.Skills.AddRangeAsync(skills);
            }

            await context.SaveChangesAsync();
        }
    }
}