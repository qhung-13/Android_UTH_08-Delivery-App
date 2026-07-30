# Android_UTH_08 — Delivery App

## Thông tin dự án
- **Project Code:** Android_UTH_08
- **Project Name:** Delivery App (Food Delivery — Client & Shipper)
- **Môn học:** Lập trình ứng dụng Mobile

## Thành viên nhóm
|        Họ và tên      |     MSSV     |       GitHub     |              Vai trò                 |
|-----------------------|--------------|------------------|--------------------------------------|
| Trần Nguyễn Quốc Hùng | 068206007286 | qhung-13         | Core Architecture, Data Layer & Auth |
| Võ Thanh Phát         | 087205017625 | vphat2755-James  | Client Flow (đặt hàng)               |
| Nguyễn Hoàng Phúc     | 2251120375   | nguyenphuc291004 | Client Tracking + Shipper Nhận đơn   |
| Nguyễn Trí Năng       | 087205010575 | NangNguyen14     | Shipper Update Status + QA/Report    |

## Proposal

### What we want to do
Xây dựng ứng dụng giao hàng (rút gọn từ bài toán food delivery) trên
Android, hỗ trợ hai vai trò: **Client** (người đặt giao hàng) và
**Shipper** (người giao hàng). Vai trò Restaurant chỉ tồn tại dưới dạng
dữ liệu có sẵn (mock), không có tính năng thao tác riêng — khi Client
chọn món ăn, địa chỉ quán ăn kèm theo món đó sẽ tự động được đẩy cho
Shipper làm điểm lấy hàng.

### What features we aim to complete
- Đăng ký/chọn tài khoản đơn giản (tên, SĐT, chọn role) — không dùng password thật, tự động đăng nhập lại lần sau nhờ lưu trạng thái cục bộ; hỗ trợ đổi tài khoản/role để tiện demo trên cùng thiết bị
- Hỗ trợ 2 role Client và Shipper với màn hình và quyền thao tác riêng biệt
- Client tạo yêu cầu giao hàng gồm điểm đi (địa chỉ quán, tự động), điểm đến, thông tin món ăn, thông tin liên hệ (tự điền từ tài khoản đang đăng nhập)
- Tính và hiển thị phí giao hàng theo công thức dựa trên khoảng cách và khối lượng/loại món ăn
- Client xác nhận hoặc huỷ đơn trước khi Shipper nhận (Pending)
- Shipper xem danh sách đơn được assign
- Luồng trạng thái đơn được kiểm soát: Pending → Accepted → Picked Up → In Transit → Delivered, hoặc Cancelled
- Shipper cập nhật trạng thái, hệ thống ghi lại thời gian cập nhật và chặn chuyển trạng thái không hợp lệ
- Client xem trạng thái hiện tại và lịch sử đơn hàng
- Validate địa chỉ, dữ liệu món ăn, quyền thao tác và các field bắt buộc
- Client và Shipper dùng chung một Repository/data source (Room, có seed sẵn dữ liệu Restaurant/FoodItem)

### What stack we are using
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Architecture:** MVVM (ViewModel + StateFlow) + Repository pattern
- **Local Database:** Room (dữ liệu có cấu trúc: User, Restaurant, FoodItem, DeliveryRequest, StatusLog)
- **Settings/State:** DataStore (tài khoản đang đăng nhập, role đang chọn, cấu hình đơn giản)
- **Concurrency:** Kotlin Coroutine + Flow
- **Navigation:** Navigation Compose
- **IDE:** Android Studio

### What will be achieved as final
Một ứng dụng Android hoàn chỉnh chạy được trên máy ảo hoặc thiết bị
thật, cho phép người dùng chọn vai trò Client hoặc Shipper ngay từ màn
hình đầu. Client có thể duyệt món ăn, tạo yêu cầu giao hàng và theo dõi
trạng thái đơn theo thời gian thực từ Room DB cục bộ. Shipper nhận đơn,
cập nhật trạng thái theo đúng luồng hợp lệ. Toàn bộ dữ liệu Restaurant
là mock có sẵn, các phần còn lại (đơn hàng, trạng thái, tính phí) là
logic thật, có unit test cho các business rule quan trọng.

## Planning

### Phase 1 — Tuần 1 & 2
- Thiết lập project structure, git repo, cấu trúc thư mục Code/DOCX/PPTX/Extra
- Định nghĩa data model (User, Restaurant, FoodItem, DeliveryRequest, StatusLog) và Room Entity/DAO
- Thiết kế Repository interface dùng chung cho cả Client và Shipper
- Màn hình đăng ký/chọn tài khoản (mock login) + lưu trạng thái đăng nhập bằng DataStore
- Seed dữ liệu Restaurant/FoodItem mẫu (10-15 món)
- Thiết kế UI wireframe cho các màn hình chính, thống nhất công thức tính phí ship và state machine trạng thái đơn

### Phase 2 — Tuần 3 & 4
- Hoàn thiện Client Flow: Home, chi tiết món ăn, form tạo đơn, xác nhận/huỷ đơn
- Hoàn thiện Client Tracking: theo dõi trạng thái, lịch sử đơn hàng
- Hoàn thiện Shipper Flow: danh sách đơn nhận, chi tiết đơn, cập nhật trạng thái
- Các màn hình Client/Shipper lấy dữ liệu theo tài khoản đang đăng nhập (clientId/shipperId)
- Áp dụng UI state (Loading/Content/Empty/Error) cho các màn hình có gọi dữ liệu

### Phase 3 — Tuần 5 & 6
- Validate input toàn diện (địa chỉ, cân nặng, field bắt buộc)
- Đảm bảo cập nhật trạng thái atomic (Room transaction), xử lý configuration change
- Viết unit test cho công thức tính phí ship và state machine chuyển trạng thái
- Test tình huống lỗi (input không hợp lệ, mất kết nối giả lập)
- Test đổi tài khoản/role qua lại trên cùng thiết bị để phục vụ demo
- Bắt đầu viết báo cáo DOCX (data model, luồng điều hướng, mô tả mock/logic thật)

### Phase 4 — Tuần 7 & 8
- Hoàn thiện báo cáo DOCX (bao gồm kết quả kiểm thử: thiết bị, input, kết quả mong đợi/thực tế)
- Hoàn thiện slide PPTX
- Quay video demo (luồng Client đặt hàng + Shipper giao hàng)
- Polish UI, fix bug cuối
- Commit toàn bộ lên GitHub

### Tuần 9 — Buffer (dự phòng)
- Fix bug phát sinh
- Chuẩn bị thuyết trình
- Nộp bài

## Mô tả
Ứng dụng giao hàng (delivery app) hỗ trợ 2 vai trò Client và Shipper,
xây dựng bằng Kotlin và Jetpack Compose trên Android. Người dùng đăng
ký/chọn tài khoản đơn giản (không cần password thật) để phân quyền theo
role. Vai trò Restaurant chỉ là dữ liệu có sẵn, không có tính năng thao
tác riêng.

## Cấu trúc repo
- `Code/` — Source code Android Studio (Kotlin)
- `DOCX/` — Báo cáo Word
- `Extra/` — Ảnh demo, video, tài liệu tham khảo, minh chứng test
- `PPTX/` — Slide thuyết trình

## Công nghệ sử dụng
- Kotlin
- Jetpack Compose
- Room (local database)
- DataStore
- Kotlin Coroutine + Flow
- Navigation Compose

## Hướng dẫn chạy
1. Mở thư mục `Code/` bằng Android Studio (File → Open)
2. Đợi Gradle sync hoàn tất
3. Chọn máy ảo (Emulator) hoặc kết nối thiết bị thật qua USB debugging
4. Bấm Run ▶ để build và chạy ứng dụng
5. Ở lần chạy đầu, nhập tên/SĐT và chọn vai trò (Client hoặc Shipper) để đăng ký tài khoản
6. Các lần chạy sau app sẽ tự động vào thẳng theo tài khoản đã lưu; có thể dùng chức năng đổi tài khoản để chuyển role khi cần
