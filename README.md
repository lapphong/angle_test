# Face Lens

Ứng dụng Android Kotlin mở camera trước, phát hiện khuôn mặt theo thời gian thực,
vẽ bounding box cho từng khuôn mặt và hiển thị trạng thái `Có người` / `Không có người`.

## Công nghệ

- Kotlin, Jetpack Compose, minSdk 24
- CameraX 1.6.2 với `LifecycleCameraController`
- ML Kit Face Detection (model bundled, chế độ `FAST`, bật face tracking)
- `MlKitAnalyzer` với `COORDINATE_SYSTEM_VIEW_REFERENCED` để bounding box khớp với
  preview sau khi CameraX crop, scale, xoay và mirror camera trước

## Chức năng

- Preview camera trước toàn màn hình, khóa portrait
- Xin quyền camera khi chạy lần đầu
- Có màn hình giải thích và nút thử lại/mở cài đặt khi người dùng từ chối quyền
- Vẽ đồng thời nhiều bounding box
- Vẽ landmark khuôn mặt và nhãn biểu cảm từ xác suất nụ cười
- Overlay vùng quét vuông với nền tối và bốn góc định hướng
- Camera trước được mở mặc định; có thể chuyển đổi camera trước/sau
- Hiển thị `Có người` khi phát hiện ít nhất một khuôn mặt, ngược lại hiển thị
  `Không có người`
- Xử lý trường hợp thiết bị không có hoặc không mở được camera đã chọn

## Cấu trúc source

Cấu trúc được port theo cách chia lớp của project Flutter tham khảo, dùng thành
phần Android native tương đương:

```text
com.example.face_lens/
├── core/permission/              # Luồng xin quyền và mở App Settings
├── domain/model/                 # CameraLens, DetectedFace và model tọa độ
└── ui/face_detection/
    ├── FaceDetectionScreen.kt    # Ghép UI và nhận action
    ├── FaceDetectionViewModel.kt # StateFlow, tương đương BLoC
    ├── FaceDetectionUiState.kt   # Trạng thái immutable của màn hình
    └── widgets/
        ├── CameraPreview.kt      # CameraX + ML Kit Analyzer
        └── FaceDetectionOverlay.kt
```

## Build và kiểm thử

```bash
./gradlew testDebugUnitTest assembleDebug
./gradlew lintDebug
```

APK debug được tạo tại `app/build/outputs/apk/debug/app-debug.apk`.
