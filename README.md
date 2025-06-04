📱 Hanoi Student Gigs – Việc Làm Sinh Viên Hà Nội
Hanoi Student Gigs là một nền tảng việc làm trực tuyến kết nối sinh viên tại Hà Nội với các cơ hội việc làm bán thời gian và freelance. Ứng dụng giúp sinh viên dễ dàng tìm kiếm công việc phù hợp với kỹ năng và lịch trình, đồng thời hỗ trợ nhà tuyển dụng tiếp cận nguồn nhân lực trẻ trung, năng động.

🧰 Công Nghệ Sử Dụng
Thành phần	Công nghệ sử dụng
Back-end	Java Spring Boot
Front-end	React
Cơ sở dữ liệu	Microsoft SQL Server
Ngôn ngữ	Java (Back-end), JavaScript (Front-end)

🎯 Mục Tiêu Dự Án
Cung cấp nền tảng tin cậy, tiện lợi cho sinh viên Hà Nội tìm việc làm thêm hoặc dự án freelance.
Tạo kênh tuyển dụng hiệu quả, nhanh chóng cho nhà tuyển dụng.
Tích hợp nhiều chức năng tiện ích như đăng tin tuyển dụng, ứng tuyển, quản lý hồ sơ, lọc tìm việc thông minh...

🧩 Tính Năng Chính
1. Quản lý Tài Khoản
Đăng ký/Đăng nhập cho:
Sinh viên (Student)
Nhà tuyển dụng (Employer)
Quản trị viên (Admin)
Thông tin hồ sơ:
Sinh viên có thể tạo và cập nhật CV, kỹ năng, học vấn...
Nhà tuyển dụng có thể chỉnh sửa thông tin doanh nghiệp/cá nhân.
Phân quyền người dùng: Student, Employer, Admin.

2. Nhà Tuyển Dụng
Đăng tin tuyển dụng (Part-time, Freelance, Internship)
Quản lý tin đăng: sửa, xóa, đóng/mở trạng thái tuyển dụng
(Tùy chọn) Xem danh sách ứng viên đã ứng tuyển

3. Sinh Viên
Tìm kiếm và lọc việc làm theo:
Ngành nghề, loại công việc, địa điểm, mức lương...

Ứng tuyển bằng:
Cách 1: Tự liên hệ nhà tuyển dụng
Cách 2: Ứng tuyển trực tiếp trên hệ thống (CV + Lời giới thiệu)

4. Quản trị Hệ thống (Admin)
Quản lý người dùng
Duyệt tin tuyển dụng
Quản lý danh mục ngành nghề, kỹ năng, địa điểm
Xem thống kê tổng quan

🗃️ Cấu Trúc Cơ Sở Dữ Liệu (Gợi ý)
Users: Thông tin người dùng (SV, Employer, Admin)
Jobs: Tin tuyển dụng
JobCategories, Locations, Skills: Danh mục
UserProfiles: Hồ sơ sinh viên
JobApplications: Lịch sử ứng tuyển
JobSkills: Kỹ năng yêu cầu của mỗi công việc

⚠️ Lưu Ý Bảo Mật & Kiểm Duyệt
Xác thực Nhà Tuyển Dụng: Tránh đăng tin giả mạo/lừa đảo.

Kiểm duyệt tin đăng: Mỗi tin phải được Admin kiểm tra trước khi hiển thị.

Bảo vệ thông tin sinh viên: Sinh viên được kiểm soát thông tin chia sẻ khi ứng tuyển.

Chức năng Báo cáo: Cho phép phản hồi/tố cáo các công việc không trung thực (mở rộng tương lai).

💡 Tính Ứng Dụng Thực Tiễn
Dự án đáp ứng nhu cầu cấp thiết về việc làm cho sinh viên, đồng thời giúp doanh nghiệp tiếp cận nhanh chóng nhân lực chất lượng cao, linh hoạt – là một hướng triển khai khả thi và giàu tiềm năng.

🔧 Cài Đặt Dự Án
Yêu Cầu
Java 17+
Node.js v16+
Microsoft SQL Server

Hướng Dẫn
# Clone repository
git clone https://github.com/khang-81/Group_Project.git

# Cấu hình backend
cd backend
# Cập nhật application.properties với thông tin kết nối SQL Server

# Chạy Spring Boot
./mvnw spring-boot:run

# Cấu hình frontend
cd frontend
npm install
npm start
⚠ Đảm bảo bạn đã cài đặt Java 17+, Node.js, và SQL Server.

