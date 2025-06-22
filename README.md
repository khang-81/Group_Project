
Hanoi Student Gigs 💼✨
Hanoi Student Gigs là một ứng dụng di động được xây dựng trên nền tảng Android, đóng vai trò là cầu nối giữa sinh viên tại Hà Nội và các nhà tuyển dụng, giúp sinh viên dễ dàng tìm kiếm các công việc bán thời gian, thực tập, hoặc freelance phù hợp với lịch học và kỹ năng của mình.

🎯 Mục tiêu Dự án
Dành cho Sinh viên: Cung cấp một nền tảng tập trung, đáng tin cậy để tìm kiếm và ứng tuyển các công việc linh hoạt, giúp tích lũy kinh nghiệm và có thêm thu nhập.

Dành cho Nhà tuyển dụng: Tạo ra một kênh hiệu quả để tiếp cận nguồn nhân lực trẻ, năng động và tài năng từ các trường đại học tại Hà Nội.

Dành cho Quản trị viên: Đảm bảo chất lượng và sự an toàn của hệ thống thông qua việc kiểm duyệt nội dung và quản lý người dùng.

🚀 Tính năng chính
🎓 Dành cho Sinh viên
🔍 Tìm kiếm & Lọc nâng cao: Tìm kiếm công việc theo từ khóa, ngành nghề, địa điểm, loại hình công việc (Part-time, Internship, Freelance).

📄 Quản lý Hồ sơ: Tạo và cập nhật hồ sơ cá nhân, bao gồm thông tin học vấn, kinh nghiệm làm việc, và kỹ năng.

📁 Tải lên CV: Dễ dàng tải lên và quản lý file CV (PDF, DOCX) để sử dụng khi ứng tuyển.

💼 Ứng tuyển Nhanh chóng: Nộp đơn vào các công việc chỉ với vài cú nhấp chuột.

📊 Theo dõi Đơn ứng tuyển: Xem trạng thái của các công việc đã ứng tuyển (Đã nộp, Đã xem, Được mời phỏng vấn...).

🏢 Dành cho Nhà tuyển dụng (Employer)
📝 Đăng tin Tuyển dụng: Dễ dàng tạo và đăng các tin tuyển dụng chi tiết.

📋 Quản lý Tin đăng: Xem danh sách các tin đã đăng, phân loại theo trạng thái (Đang tuyển, Đã đóng).

🧑‍🤝‍🧑 Xem danh sách Ứng viên: Truy cập danh sách các sinh viên đã ứng tuyển vào mỗi tin đăng và xem hồ sơ/CV của họ.

✅ Tài khoản được Xác thực: Các nhà tuyển dụng uy tín sẽ được Admin xác thực để tăng độ tin cậy.

👑 Dành cho Quản trị viên (Admin)
🛡️ Kiểm duyệt Tin đăng: Duyệt hoặc từ chối các tin tuyển dụng mới để đảm bảo chất lượng và tính xác thực.

👤 Quản lý Người dùng: Xem danh sách tất cả người dùng, xóa các tài khoản vi phạm và xác thực tài khoản Nhà tuyển dụng.

⚙️ Quản lý Danh mục: Thêm, sửa, xóa các danh mục hệ thống như Ngành nghề, Kỹ năng, Địa điểm.

📈 Xem Thống kê: Theo dõi các số liệu quan trọng của ứng dụng (tổng số người dùng, số tin đăng, số lượt ứng tuyển...).

🛠️ Công nghệ sử dụng
Ngôn ngữ: Java

Nền tảng: Android (SDK 24+)

Kiến trúc: Model-View-ViewModel (MVVM)

Giao diện: XML Layouts với Material Design Components

Cơ sở dữ liệu & Backend:

Firebase Authentication: Quản lý đăng ký, đăng nhập.

Cloud Firestore: Lưu trữ dữ liệu NoSQL cho người dùng, công việc, ứng tuyển...

Firebase Storage: Lưu trữ file CV.

Thư viện:

FirebaseUI for Firestore: Dễ dàng kết nối Firestore với RecyclerView.

Picasso: Tải và hiển thị hình ảnh hiệu quả.

CircleImageView: Hiển thị ảnh đại diện bo tròn.

🗃️ Cấu trúc Cơ sở dữ liệu (Firestore)
Dự án sử dụng cấu trúc NoSQL linh hoạt của Firestore, bao gồm các collection chính:

users: Lưu trữ thông tin của tất cả người dùng (Student, Employer, Admin).

jobs: Lưu trữ thông tin chi tiết của tất cả các tin tuyển dụng.

applications: Một collection riêng biệt để lưu tất cả các đơn ứng tuyển, liên kết userId và jobId.

categories, locations, skills: Các collection để Admin quản lý dữ liệu cho bộ lọc.

🏁 Hướng dẫn Cài đặt và Chạy dự án
Để chạy dự án này trên máy của bạn, hãy làm theo các bước sau:

Clone Repository:

git clone https://github.com/khang-81/Group_Project.git

Mở bằng Android Studio:

Mở Android Studio, chọn Open an existing project và trỏ đến thư mục bạn vừa clone.

Kết nối với Firebase:

Tạo một dự án mới trên Firebase Console.

Thêm một ứng dụng Android vào dự án Firebase với tên package là com.example.hanoistudentgigs.

Tải file google-services.json về và đặt nó vào thư mục app của dự án trong Android Studio.

Kích hoạt các dịch vụ Authentication (Email/Password), Firestore Database, và Cloud Storage trên Firebase Console.

Đồng bộ Gradle:

Nhấn vào nút "Sync Now" trên thanh thông báo hoặc File > Sync Project with Gradle Files.

Chạy ứng dụng:

Chọn một máy ảo hoặc kết nối thiết bị thật và nhấn nút Run 'app' (▶️).

✨ Tác Giả
👨‍💻 TaDucKhang - Leader
Tạo khung cho ứng dụng, đảm bảo tính dễ sử dụng và giao diện thân thiện.

Trang chủ (Homepage): Thiết kế giao diện chính cho các vai trò Sinh viên 👨‍🎓, Nhà tuyển dụng 💼, và Quản trị viên 🛡️.

Đăng nhập, đăng ký, quên mật khẩu: Xây dựng hệ thống đăng nhập và đăng ký người dùng, sử dụng Firebase để quản lý tài khoản.

Thiết kế Firebase: Cấu hình và triển khai Firebase Authentication và Firestore cho việc lưu trữ và xác thực người dùng.

Kiểm thử: Thực hiện kiểm thử chức năng của ứng dụng để đảm bảo các tính năng hoạt động như mong đợi. 

👩‍💻 NguyenKhanhDuy - Member 1 (Sinh viên)
Xem Profile (CRUD): Quản lý hồ sơ của Sinh viên 📜, cho phép tạo, đọc, cập nhật và xóa thông tin cá nhân.

Tìm kiếm và lọc công việc: Tính năng tìm kiếm công việc theo từ khóa 🔍, lọc công việc theo yêu cầu, và sắp xếp công việc theo tiêu chí.

Xem chi tiết công việc và ứng tuyển: Xem thông tin chi tiết về công việc 💼 và gửi CV để ứng tuyển ngay từ ứng dụng.

👨‍💻 LeThiTuyetNhung - Member 2 (Nhà tuyển dụng)
Xem Profile (CRUD): Quản lý hồ sơ của Nhà tuyển dụng 🏢, cho phép tạo, đọc, cập nhật và xóa thông tin công ty.

Đăng tin tuyển dụng: Cung cấp tính năng cho Nhà tuyển dụng để đăng công việc mới 📢.

Quản lý tin tuyển dụng (CRUD): Quản lý các tin tuyển dụng đã đăng, cho phép chỉnh sửa hoặc xóa các tin tuyển dụng không còn phù hợp.

Xem danh sách ứng viên: Quản lý danh sách ứng viên đã ứng tuyển vào công việc 📋 và xem chi tiết hồ sơ.

👨‍💻 LeThaiHieu - Member 3 (Quản trị viên)
Quản lý người dùng: Quản lý quyền và thông tin người dùng trong hệ thống 👥, đảm bảo tính bảo mật và phân quyền hợp lý.

Quản lý danh mục ngành nghề/kỹ năng: Cung cấp danh mục ngành nghề và kỹ năng cho các công việc 📑, giúp sinh viên dễ dàng tìm kiếm công việc phù hợp.

Kiểm duyệt tin tuyển dụng: Quản lý và kiểm duyệt các tin tuyển dụng 🔍, đảm bảo thông tin chính xác và phù hợp với tiêu chuẩn.

Quản lý hệ thống: Xây dựng và duy trì hệ thống backend Firebase, bảo mật và tối ưu hiệu suất.

Cảm ơn bạn đã xem dự án!