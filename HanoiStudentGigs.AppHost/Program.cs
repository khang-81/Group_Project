var builder = DistributedApplication.CreateBuilder(args);

var apiService = builder.AddProject<Projects.HanoiStudentGigs_ApiService>("apiservice");

builder.AddProject<Projects.HanoiStudentGigs_Web>("webfrontend")
    .WithExternalHttpEndpoints()
    .WithReference(apiService)
    .WaitFor(apiService);

builder.Build().Run();
