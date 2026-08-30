# Face Lens

Face Lens là ứng dụng Android viết bằng Kotlin, sử dụng camera và ML Kit để phát
hiện khuôn mặt theo thời gian thực. Ứng dụng vẽ bounding box cho từng khuôn mặt
và hiển thị trạng thái `Có người` hoặc `Không có người`.

Ứng dụng chỉ thực hiện **face detection**, không nhận diện danh tính người dùng.

## Chức năng

- Preview camera toàn màn hình và khóa giao diện ở chế độ portrait.
- Camera trước được chọn khi mở ứng dụng.
- Cho phép chuyển đổi giữa camera trước và camera sau.
- Phát hiện đồng thời nhiều khuôn mặt và cập nhật bounding box theo thời gian thực.
- Xin quyền camera, hỗ trợ yêu cầu lại hoặc mở App Settings khi quyền bị từ chối.
- Chuyển đổi trực tiếp giữa tiếng Việt và tiếng Anh bằng nút `VI`/`EN`.
- Ghi nhớ ngôn ngữ đã chọn cho lần mở ứng dụng tiếp theo.

## Cấu trúc source

```text
com.example.face_lens/
├── domain/model/
│   └── DetectedFace.kt
├── ui/
│   ├── feature/face_detection/
│   │   ├── FaceDetectionScreen.kt       # Kết nối ViewModel với UI
│   │   ├── FaceDetectionContent.kt      # Bố cục chính của màn hình
│   │   ├── components/                  # Camera, overlay, status và controls
│   │   └── viewmodel/                   # UI state và ViewModel
│   └── theme/                           # Compose theme
└── utils/
    ├── ContextUtils.kt
    ├── locale/LocaleUtils.kt            # Lưu và áp dụng VI/EN
    └── permission/                      # Permission utils và gate tái sử dụng
```

## Cách build và chạy

### Yêu cầu

- Android Studio hoặc Android SDK có API 37.
- JDK tương thích với phiên bản Android Gradle Plugin của project.
- Thiết bị Android hoặc emulator từ API 24, có camera khả dụng.

Khi mở project bằng Android Studio, chờ Gradle Sync hoàn tất rồi chọn build
variant `angleaiDebug`.

Build APK debug bằng command line:

```bash
./gradlew assembleAngleaiDebug
```

APK được tạo trong:

```text
app/build/outputs/apk/angleai/debug/
```

Chạy trực tiếp trên thiết bị hoặc emulator đang kết nối:

```bash
./gradlew installAngleaiDebug
```

Khi ứng dụng khởi động, cấp quyền camera. Nếu đã chọn “Không cho phép” vĩnh viễn,
ứng dụng sẽ hiển thị nút mở App Settings để người dùng cấp lại quyền.

Chạy unit test và lint:

```bash
./gradlew testAngleaiDebugUnitTest
./gradlew lintAngleaiDebug
```

### Build release APK/AAB

Release signing hiện tại cần keystore cục bộ tại:

```text
app/angleai.jks
```

Keystore được loại khỏi Git để tránh công khai signing key. Sau khi cung cấp
keystore phù hợp với `app/signingConfigs.gradle`, chạy:

```bash
./gradlew assembleAngleaiRelease
./gradlew bundleAngleaiRelease
```

Project tắt language split trong Android App Bundle để cả tiếng Việt và tiếng
Anh luôn có sẵn cho nút chuyển ngôn ngữ trong ứng dụng.

## Thư viện đã sử dụng và lý do lựa chọn

| Thư viện | Mục đích và lý do |
| --- | --- |
| Kotlin | Ngôn ngữ chính thức, phù hợp với Android hiện đại và coroutine/Flow. |
| Jetpack Compose + Material 3 | Xây dựng UI theo state, dễ tách screen và component. |
| CameraX Camera2 | Cung cấp camera backend tương thích trên nhiều phiên bản Android. |
| CameraX Lifecycle | Tự động bind/unbind camera theo lifecycle của Activity. |
| CameraX View | Cung cấp `PreviewView` và `LifecycleCameraController` để quản lý preview, camera selector và analyzer. |
| CameraX ML Kit Vision | Cung cấp `MlKitAnalyzer` và phép biến đổi tọa độ từ ảnh phân tích sang `PreviewView`. |
| ML Kit Face Detection | Phát hiện khuôn mặt on-device, không cần gửi ảnh lên server; chế độ `FAST` phù hợp với xử lý real-time. |
| AndroidX Lifecycle | Quản lý `StateFlow` từ ViewModel an toàn theo lifecycle Compose. |
| JUnit 4 | Kiểm thử UI state, chuyển camera và logic chuyển đổi ngôn ngữ. |

Face detector được cấu hình với `PERFORMANCE_MODE_FAST`, kích thước khuôn mặt tối
thiểu `0.10f` và bật tracking. Cấu hình này ưu tiên độ trễ thấp để bounding box
bám theo khuôn mặt tốt hơn khi người dùng di chuyển.

## Chuyển tọa độ bounding box sang màn hình

Khung hình đầu vào của ML Kit và vùng hiển thị camera thường không có cùng hệ
tọa độ. `PreviewView` còn có thể crop, scale, xoay ảnh và mirror camera trước do
ứng dụng dùng `FILL_CENTER`.

Ứng dụng xử lý vấn đề này bằng cách tạo `MlKitAnalyzer` với:

```kotlin
ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED
```

Với coordinate system này, CameraX kết hợp thông tin transform của
`PreviewView` để chuyển kết quả ML Kit sang hệ tọa độ pixel của chính view đang
hiển thị. Vì vậy `face.boundingBox` nhận được đã phản ánh rotation, crop, scale
và hướng hiển thị của camera.

Luồng xử lý:

1. CameraX đưa frame vào `MlKitAnalyzer`.
2. ML Kit trả về bounding box của từng khuôn mặt.
3. CameraX ánh xạ bounding box sang hệ tọa độ `PreviewView`.
4. Ứng dụng chuyển `Rect` thành `FaceBounds` dạng `Float`.
5. `FaceDetectionOverlay` vẽ trực tiếp các giá trị `left`, `top`, `width` và
   `height` lên `Canvas` phủ đúng kích thước preview.

Nhờ đó code không cần tự tính tỉ lệ, offset crop, rotation hoặc mirror camera
trước. Cách này cũng tránh sai lệch bounding box khi đổi camera hoặc khi kích
thước preview khác kích thước frame phân tích.

## Phần chưa hoàn thành và hướng phát triển

Các yêu cầu chức năng chính hiện đã hoàn thành. Một số phần có thể cải thiện nếu
có thêm thời gian:

- Bổ sung instrumented/UI test cho luồng xin quyền, chuyển camera và chuyển ngôn
  ngữ. Hiện project tập trung vào unit test cho state và logic thuần.
- Kiểm thử trên nhiều thiết bị thật, tỉ lệ màn hình và phần cứng camera khác nhau.
- Tự động fallback sang camera còn khả dụng trên thiết bị chỉ có một camera.
- Đo FPS và thời gian xử lý analyzer; bổ sung temporal smoothing nếu bounding box
  còn rung trong điều kiện thiếu sáng hoặc khuôn mặt di chuyển nhanh.
- Chuyển thông tin signing release sang biến môi trường hoặc file properties cục
  bộ để quy trình phát hành thuận tiện và an toàn hơn.

Face recognition, lưu ảnh và quay video không nằm trong phạm vi ứng dụng hiện
tại nên không được xem là phần còn thiếu.
