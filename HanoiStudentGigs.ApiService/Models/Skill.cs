namespace HanoiStudentGigs.API.Models
{
    public class Skill
    {
        public int SkillId { get; set; }
        public string Name { get; set; }
        public ICollection<JobSkill> JobSkills { get; set; }
    }
}