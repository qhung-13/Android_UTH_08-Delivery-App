# UTH Delivery — Báo cáo audit, sửa lỗi và changelog

**Ngày hoàn tất triển khai:** 02/09/2026  
**Phạm vi:** Toàn bộ mã nguồn Android/Kotlin trong `Code.zip`  
**Tài liệu chuẩn:** `SYSTEM_DESIGN(1).md`  
**Trạng thái:** **Đã hoàn tất thay đổi mã nguồn; build và chạy thiết bị chưa được xác minh do giới hạn môi trường.**

## 1. Tóm tắt điều hành

Dự án được giữ nguyên hướng kiến trúc **Jetpack Compose + MVVM + Repository + Room + DataStore**, hai role thật là **Client** và **Shipper**, dữ liệu nhà hàng là dữ liệu mock gắn với món ăn. Không thêm Firebase, backend, Retrofit, MVI hay một tầng kiến trúc mới.

Đợt sửa tập trung vào bốn nhóm rủi ro chính:

1. Hoàn thiện các route đang là placeholder và hợp nhất những màn hình trùng lặp.
2. Bảo vệ tính nhất quán dữ liệu: khóa ngoại, migration, transaction và thao tác nhận đơn kiểu compare-and-set.
3. Chuẩn hóa session, role gate, Flow, `UiState` và xử lý lỗi/cancellation.
4. Thiết kế lại giao diện Material 3 thành một sản phẩm nhất quán, có loading/empty/error, timeline thật và chặn double tap.

Kết quả kiểm tra tĩnh: **17/17 hợp đồng kiến trúc đạt**, **9/9 XML parse thành công**, **79/79 tham chiếu `R.string` có định nghĩa**, mô phỏng migration SQLite bảo toàn đơn/log và không có vi phạm khóa ngoại. Bộ test hiện có **18 test case được khai báo**; chưa được thực thi vì Gradle wrapper không thể tải Gradle 9.5.0 trong môi trường mạng hiện tại.

## 2. Hướng dự án được bảo toàn

| Yêu cầu từ SYSTEM_DESIGN | Kết quả |
|---|---|
| UI → ViewModel → Repository → Room/DataStore | Giữ nguyên và siết lại; Composable không truy cập repository/DAO/DataStore |
| Repository interface + implementation | Giữ đủ `UserRepository`, `FoodRepository`, `DeliveryRepository` |
| Entity tách Domain model | Giữ nguyên mapper tập trung trong `Mappers.kt` |
| Client và Shipper dùng chung Room | Giữ một `AppDatabase` và một `DeliveryRepository` |
| State machine sáu trạng thái | Giữ đúng bảng transition trong tài liệu thiết kế |
| Status + StatusLog phải atomic | Thực hiện bằng `@Transaction` |
| Session bằng Preferences DataStore | Giữ nguyên; bổ sung xử lý stale user ID |
| Manual dependency injection | Giữ nguyên trong `FoodDeliveryApp`; không thêm framework DI |
| Route công khai trong `Screen.kt` | Giữ tên route; thêm mã hóa route argument |

Không có thay đổi kiến trúc hoặc business model trái với `SYSTEM_DESIGN`. `account_switch` là phần mở rộng UX đã được tài liệu cho phép.

## 3. Các vấn đề phát hiện ban đầu

| ID | Mức độ | Phát hiện | Tác động |
|---|---|---|---|
| AUD-001 | Critical | `client_tracking` và `client_history` chỉ hiển thị placeholder dù đã có code thật | Luồng Client không hoàn chỉnh |
| AUD-002 | High | Nhiều màn hình Tracking/Shipper và test seeder bị trùng | Dễ gọi nhầm implementation, tăng nợ kỹ thuật |
| AUD-003 | High | `NavGraph` collect `UserRepository` trực tiếp | Vi phạm UI → ViewModel → Repository |
| AUD-004 | Critical | Shipper nhận đơn theo kiểu đọc rồi ghi | Hai Shipper có thể cùng nhận một đơn PENDING |
| AUD-005 | Critical | `DeliveryRequest` và `StatusLog` thiếu khóa ngoại trong schema thực tế | Có thể sinh dữ liệu mồ côi |
| AUD-006 | High | Danh sách đơn map Domain mà bỏ `statusHistory` | Timeline/history không phản ánh DB |
| AUD-007 | High | DataStore có thể giữ user ID không tồn tại; đổi tài khoản điều hướng trước khi ghi xong | Session lỗi hoặc chuyển sai role |
| AUD-008 | High | Route argument dùng `checkNotNull` và route builder không encode ID | Invalid argument có thể làm crash |
| AUD-009 | High | ViewModel khởi động thêm collector sau mutation; có nơi nuốt exception | Collector trùng và lỗi bị ẩn |
| AUD-010 | High | Không có role gate tại navigation | Client/Shipper có thể vào nhầm khu vực bằng back stack |
| AUD-011 | Medium | CTA tạo/nhận/cập nhật đơn không khóa khi đang chạy | Double tap có thể tạo thao tác lặp |
| AUD-012 | Medium | UI không đồng nhất, nhiều chuỗi hard-code, thiếu bottom navigation/timeline/confirmation | Khó demo và khó sử dụng |
| AUD-013 | Medium | `gradle.properties` chứa `java.io.tmpdir` cố định theo Windows | Build không portable |

## 4. Lỗi đã sửa

### BUG-001 — Race condition khi nhận đơn

- **Nguyên nhân:** repository đọc trạng thái PENDING rồi cập nhật ở hai thao tác tách rời.
- **Sửa:** DAO dùng câu lệnh `UPDATE ... WHERE id = :requestId AND status = :expectedStatus`; chỉ một caller nhận được `changed == 1`.
- **Bảo vệ bổ sung:** update và insert `StatusLog` nằm trong cùng `@Transaction`; conflict trả lỗi thân thiện để tải lại.
- **Xác minh:** test instrumented cạnh tranh hai Shipper được bổ sung; kiểm tra tĩnh xác nhận conditional update và transaction.

### BUG-002 — Status và lịch sử có thể lệch nhau

- **Nguyên nhân:** cập nhật entity và ghi log không được ràng buộc thành một thao tác nguyên tử.
- **Sửa:** `transitionStatusWithLog()` và `insertWithInitialLog()` là transaction.
- **Kết quả mong đợi:** nếu ghi log thất bại, toàn bộ mutation rollback.

### BUG-003 — Thiếu toàn vẹn tham chiếu

- **Nguyên nhân:** schema v1 không khai báo đầy đủ foreign key/index cho delivery và status log.
- **Sửa:** thêm FK từ delivery tới Client, Shipper, FoodItem; FK cascade từ StatusLog tới DeliveryRequest; thêm index cho các cột query.
- **Migration:** nâng DB từ 1 lên 2, phục hồi an toàn bản ghi mồ côi, copy dữ liệu, đổi bảng và tạo index.
- **Xác minh:** mô phỏng SQLite bảo toàn 1/1 đơn và 1/1 log; `PRAGMA foreign_key_check` trả danh sách rỗng.

### BUG-004 — Status history bị mất ở list flow

- **Nguyên nhân:** mapper mặc định nhận danh sách log rỗng.
- **Sửa:** mọi flow đơn hàng được enrich bằng `getStatusHistory()` trước khi phát Domain model.
- **Tác động:** Tracking, History và Shipper Detail hiển thị timeline từ dữ liệu thật.

### BUG-005 — Session stale và race đổi tài khoản

- **Nguyên nhân:** DataStore ID không được kiểm tra với Room; callback điều hướng chạy trước `setCurrentUser()`.
- **Sửa:** repository tự xóa session nếu user không còn tồn tại, xác minh user trước khi lưu; AuthViewModel chỉ callback sau khi write thành công và cập nhật state role trước navigation.

### BUG-006 — Truy cập sai role

- **Nguyên nhân:** route không kiểm tra Client/Shipper.
- **Sửa:** mọi route nghiệp vụ được bọc `RoleGate`; ViewModel và repository cũng kiểm tra role ở các thao tác quan trọng.

### BUG-007 — Route argument có thể crash

- **Nguyên nhân:** `checkNotNull(savedStateHandle[...])` và ID ghép trực tiếp vào URL route.
- **Sửa:** argument nullable được chuyển thành lỗi `UiState.Error`; `Uri.encode()` được dùng ở cả ba route có ID.

### BUG-008 — Collector trùng và exception bị nuốt

- **Nguyên nhân:** mutation gọi lại hàm load tạo collector mới; `MyOrders` có catch rỗng.
- **Sửa:** mỗi list ViewModel giữ một `Job`, hủy collector cũ trước retry; Room Flow tự phát state sau mutation; lỗi action được đưa vào UI.

### BUG-009 — Cancellation bị biến thành Result failure

- **Nguyên nhân:** bắt Throwable chung có thể làm mất tín hiệu hủy coroutine.
- **Sửa:** `runSuspendCatching` luôn rethrow `CancellationException` và chỉ đóng gói lỗi thật.

### BUG-010 — Fee/input chưa bảo vệ đầy đủ

- **Nguyên nhân:** khoảng cách NaN/Infinity chưa bị chặn; số điện thoại có khoảng trắng bị từ chối không nhất quán.
- **Sửa:** khoảng cách phải finite và không âm; cân nặng phải dương; phone được trim trước regex; repository kiểm tra lại dữ liệu trước persistence.

## 5. Cải tiến kiến trúc và trạng thái

- `FoodDeliveryApp` expose repository theo interface, implementation chỉ nằm tại composition root.
- Seed nhà hàng và món ăn chạy trong một transaction Room.
- `SessionManager` chỉ giữ `applicationContext`.
- Composable collect state bằng `collectAsStateWithLifecycle()`.
- Mỗi màn data-driven expose một state chính, bao quát Loading, Empty, Success và Error.
- ViewModel quản lý input tạo đơn, busy state và action error; UI không chứa business logic.
- Repository là nơi cuối cùng kiểm tra state transition, role và dữ liệu đầu vào.
- Không còn `!!`, `checkNotNull` route, `GlobalScope`, catch rỗng hoặc test seeder trong main source.

## 6. Business logic sau sửa

### 6.1 State machine

| Từ | Được chuyển sang |
|---|---|
| PENDING | ACCEPTED, CANCELLED |
| ACCEPTED | PICKED_UP, CANCELLED |
| PICKED_UP | IN_TRANSIT |
| IN_TRANSIT | DELIVERED |
| DELIVERED | Không có |
| CANCELLED | Không có |

UI chỉ hiển thị action hợp lệ tại trạng thái hiện tại, nhưng repository vẫn là lớp cưỡng chế cuối cùng.

### 6.2 Công thức phí

`fee = 10.000 + distanceKm × 3.000 + 5.000 nếu weightGram > 2.000`

Ví dụ kiểm tra tay:

- 5 km, 550 g: `10.000 + 5 × 3.000 + 0 = 25.000đ`.
- 5 km, 2.001 g: `10.000 + 5 × 3.000 + 5.000 = 30.000đ`.
- Ngưỡng 2.000 g không tính phụ phí, đúng quy ước “lớn hơn ngưỡng”.

Các tham số vẫn là số liệu mẫu đã có trong thiết kế. Khoảng cách hiện là **5 km mock** vì dự án không có Maps/API; không phát sinh business rule mới.

### 6.3 Validation

| Dữ liệu | Cưỡng chế |
|---|---|
| Name | Không blank |
| Phone | 10 chữ số, bắt đầu 0, trim trước kiểm tra |
| Destination | Trim và tối thiểu 5 ký tự |
| Weight | Lớn hơn 0 |
| Distance | Finite và không âm |
| New request | PENDING, chưa có Shipper, history rỗng, timestamp hợp lệ |
| Status change | Phải qua `OrderStatusValidator` và compare-and-set |

## 7. Thiết kế UI/UX mới

- Tên hiển thị thống nhất thành **UTH Delivery**; package ID không đổi.
- Material 3 scaffold, top app bar và bottom navigation nhất quán cho Client/Shipper.
- Home có greeting, tìm kiếm và grid responsive bằng `GridCells.Adaptive`.
- Food Detail có artwork fallback, giá, nhà hàng, địa chỉ và khối lượng.
- Create Order nhóm pickup, destination và fee breakdown; input ở ViewModel; nút submit có progress và chống double tap.
- Tracking dùng dữ liệu StatusLog thật, timeline và dialog xác nhận hủy.
- History chỉ hiển thị trạng thái terminal, có card thông tin và timeline.
- Available Orders ưu tiên pickup/destination/fee, progress riêng cho đơn đang nhận và xử lý conflict.
- My Orders tách “Đang thực hiện” và “Đã hoàn tất”, chỉ hiện next action hợp lệ.
- Shipper Detail hiển thị món, tuyến giao, timeline và một CTA theo trạng thái.
- Status badge tập trung, màu có độ tương phản tốt hơn và luôn có nhãn chữ.
- Chuỗi hiển thị được tập trung trong `strings.xml`; font, shape, spacing, color và formatter dùng chung.

### Component dùng lại được bổ sung

`DeliveryTopBar`, `DeliveryBottomBar`, `PrimaryButton`, `StatusBadge`, `OrderSummaryCard`, `OrderTimeline`, `FoodArtwork`, `InfoRow`, `UiStateContent`, `LoadingIndicator`, `EmptyState`, `ErrorState`.

## 8. Tệp thay đổi

### Data, domain và cấu hình

- `app/build.gradle.kts`
- `gradle.properties`
- `FoodDeliveryApp.kt`
- `SessionManager.kt`
- `AppDatabase.kt`
- `DeliveryRequestDao.kt`, `UserDao.kt`
- `DeliveryRequestEntity.kt`, `StatusLogEntity.kt`
- `DeliveryRepository.kt`, `UserRepository.kt`
- `SeedDataProvider.kt`
- `FeeCalculator.kt`, `InputValidator.kt`
- Thêm `CoroutineResult.kt`

### Navigation, auth và feature

- `Screen.kt`, `NavGraph.kt`
- `AuthViewModel.kt`, `LoginScreen.kt`, `AccountSwitchScreen.kt`
- Toàn bộ Home, Food Detail, Create Order, Tracking, History
- Toàn bộ Available Orders, My Orders, Shipper Order Detail

### UI foundation

- Sửa `Color.kt`, `Theme.kt`, `PrimaryButton.kt`, `StatusBadge.kt`, các state component và `strings.xml`.
- Thêm `Spacing.kt`, `Shape.kt`, `Formatters.kt`, `AppBars.kt`, `FoodArtwork.kt`, `InfoRow.kt`, `OrderSummaryCard.kt`, `OrderTimeline.kt`.

### Xóa dead/duplicate code

- `client/ClientHistoryScreen.kt`
- `client/ClientTrackingScreen.kt`
- `client/ordertracking/*`
- `shipper/ShipperAssignedOrdersScreen.kt`
- `shipper/ShipperPendingOrdersScreen.kt`
- `shipper/test/*`
- `ExampleUnitTest.kt`
- `OrderStatusTest.kt` cũ, thay bằng test exhaustive.

## 9. Kiểm thử

### 9.1 Test inventory

| Nhóm | Số `@Test` | Nội dung |
|---|---:|---|
| FeeCalculator | 9 | Base/distance/threshold/surcharge/breakdown/negative/non-finite/zero/negative weight |
| InputValidator | 4 | Name, phone hợp lệ/sai, address và weight |
| OrderStatusValidator | 2 | Exhaustive 36 cặp và terminal states |
| CoroutineResult | 1 | Cancellation phải được rethrow |
| DAO instrumented | 1 | Hai Shipper cạnh tranh, chỉ một update thành công và chỉ một ACCEPTED log |
| Context instrumented | 1 | Package name |
| **Tổng** | **18** | 16 local + 2 instrumented |

**Lưu ý:** đây là inventory từ source, không phải kết quả chạy test.

### 9.2 Kiểm tra đã thực thi

| Kiểm tra | Kết quả thực tế |
|---|---|
| Parse XML resources | PASS — 9 file |
| Parse version catalog TOML | PASS |
| Đối chiếu `R.string` | PASS — 79 reference, 0 thiếu |
| Bracket/string/comment scan cho Kotlin | PASS — không lệch delimiter |
| Hợp đồng kiến trúc tự động | PASS — 17/17 |
| Mô phỏng migration SQLite v1 → v2 | PASS — dữ liệu/log bảo toàn, FK check rỗng |
| Scan `!!`, `checkNotNull`, `GlobalScope`, catch rỗng, placeholder/test seeder | PASS — không còn trong main Kotlin |

### 9.3 Build/test command

Đã gọi thật:

```bash
./gradlew clean testDebugUnitTest assembleDebug lintDebug --stacktrace
./gradlew --offline clean testDebugUnitTest assembleDebug lintDebug --stacktrace
```

Cả hai dừng trước configuration/build vì wrapper cố tải:

`https://services.gradle.org/distributions/gradle-9.5.0-bin.zip`

và môi trường trả `java.net.SocketException: Network is unreachable`. Gradle 9.5.0 không có sẵn trong cache. Vì vậy:

- **Không có cơ sở để tuyên bố build thành công.**
- **Không có cơ sở để tuyên bố 18 tests đã pass.**
- **Không tạo APK trong đợt chạy này.**

## 10. Ma trận luồng chức năng

| Luồng | Xác minh từ code | Cần chạy thiết bị |
|---|---|---|
| App mở lại session và route theo role | Có | Có |
| Đăng ký Client/Shipper và chống double submit | Có | Có |
| Chọn tài khoản cũ / logout | Có | Có |
| Client tìm, xem món, tạo đơn | Có | Có |
| Validate địa chỉ và hiển thị fee breakdown | Có | Có |
| Client tracking/timeline/hủy có xác nhận | Có | Có |
| Client history terminal orders | Có | Có |
| Shipper xem và nhận đơn | Có | Có |
| Hai Shipper cạnh tranh cùng đơn | CAS + test được viết | Chạy instrumented test |
| Shipper cập nhật ACCEPTED → PICKED_UP → IN_TRANSIT → DELIVERED | Có | Có |
| Invalid transition bị repository chặn | Có + exhaustive test được viết | Chạy tests |
| Role khác truy cập route | RoleGate + kiểm tra ViewModel/repository | Có |
| Xoay màn hình khi nhập | Login `rememberSaveable`; Create Order ở ViewModel | Có |

## 11. Vấn đề còn lại và giới hạn

1. **Build execution:** cần máy có Gradle 9.5.0, Android SDK/compile SDK 36.1 và quyền tải dependency.
2. **Device QA:** chưa chạy emulator/device nên chưa có bằng chứng ảnh, kiểm tra landscape, font scale, TalkBack hay keyboard thực tế.
3. **Khoảng cách:** vẫn là mock 5 km theo phạm vi dự án; cần Maps/location hoặc input khoảng cách nếu muốn dữ liệu thật.
4. **Ảnh món ăn:** seed `imageUrl` rỗng; UI dùng artwork fallback nhẹ, không thêm Coil chỉ để tải URL không tồn tại.
5. **Auth:** đúng thiết kế là mock local account, không password và không bảo mật cho production.
6. **Migration:** đã mô phỏng bằng SQLite, nhưng vẫn cần instrumented migration test trên Android/Room runtime trước release.
7. **Order list item:** model DeliveryRequest chỉ lưu `foodItemId`; detail Shipper resolve tên món, còn summary hiển thị mã tham chiếu để không thay đổi data model.

## 12. Before vs After

| Trước | Sau |
|---|---|
| Tracking/History là placeholder trong NavGraph | Route nối ViewModel và màn thật |
| Đọc rồi ghi khi nhận đơn | Conditional update + transaction |
| Không FK cho delivery/log | FK, cascade, index và migration v2 |
| List bỏ status history | Mọi list Domain có history |
| UI/NavGraph truy cập repository | UI chỉ qua ViewModel |
| Không role gate | Role gate toàn bộ route nghiệp vụ |
| Unsafe ID parsing | Graceful error + URI encoding |
| Collector trùng, catch rỗng | Một collector/job, lỗi hiện ra UI |
| CTA có thể double tap | Busy state và disabled/loading CTA |
| UI rời rạc/hard-code | Design system Material 3 và resource tập trung |
| Test rời rạc | 18 test inventory, state machine exhaustive |

## 13. Khuyến nghị bàn giao

Trên máy phát triển có Android SDK:

1. Mở project bằng Android Studio phù hợp với AGP 9.3.1.
2. Để IDE tự tạo `local.properties`; không commit file này.
3. Chạy `./gradlew clean testDebugUnitTest assembleDebug lintDebug`.
4. Chạy hai instrumented tests trên emulator API 28+.
5. Thực hiện đầy đủ Client flow và Shipper flow bằng ít nhất hai tài khoản Shipper.
6. Kiểm tra small phone, large phone, landscape, font scale 1.3–1.5 và TalkBack.
7. Chỉ ký release sau khi build, lint, tests và device smoke test đều xanh.

## 14. Release gate

| Gate | Trạng thái |
|---|---|
| Mã nguồn đã sửa và đóng gói sạch | Sẵn sàng |
| Kiểm tra tĩnh và migration simulation | Đạt |
| Gradle build | Bị chặn bởi môi trường |
| Unit tests | Chưa chạy |
| Instrumented tests | Chưa chạy |
| Emulator/manual QA | Chưa chạy |
| APK release | Chưa tạo |

**Kết luận:** phần implementation và hồ sơ audit đã hoàn thành. Project chưa nên được tuyên bố “release-ready” cho đến khi hoàn tất các gate thực thi nêu trên.
