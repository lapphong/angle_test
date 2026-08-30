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
- Hiển thị `Có người` khi phát hiện ít nhất một khuôn mặt, ngược lại hiển thị
  `Không có người`
- Xử lý trường hợp thiết bị không có hoặc không mở được camera trước

## Build và kiểm thử

```bash
./gradlew testDebugUnitTest assembleDebug
./gradlew lintDebug
```

APK debug được tạo tại `app/build/outputs/apk/debug/app-debug.apk`.
