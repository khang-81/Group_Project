public class JobListViewModel
{
    public IEnumerable<JobResponseDTO> Jobs { get; set; }
    public JobFilterParams FilterParams { get; set; }
}