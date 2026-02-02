package backend.onmoim.global.utils;

import backend.onmoim.global.common.code.GeneralErrorCode;
import backend.onmoim.global.common.exception.GeneralException;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MinioUtil {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    public String uploadProfileImage(MultipartFile file, Long userId) {
        try {
            String ext = getFileExtension(file.getOriginalFilename());
            String filename = String.format("user/profile/%d%s", userId, ext);

            deleteObjectIfExists(filename);

            PutObjectArgs putArgs = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(filename)
                    .contentType(file.getContentType())
                    .stream(
                            file.getInputStream(),
                            file.getSize(),
                            10 * 1024 * 1024 // 파일 크기 10MB 제한 (추후에 수정 가능)
                    )
                    .build();

            minioClient.putObject(putArgs);
            return filename;

        } catch (Exception e) {
            throw new GeneralException(GeneralErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }

    public String getProfileImageUrl(Long userId) {
        try {
            String filename = String.format("user/profile/%d.png", userId);

            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucket)
                            .object(filename)
                            .expiry(7 * 24 * 60 * 60)
                            .build()
            );
        } catch (Exception e) {
            // 파일 없으면 null 반환 (정상 로직)
            return null;
        }
    }

    private String getFileExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return ".png";
        }
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }

    private void deleteObjectIfExists(String objectKey) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucket).object(objectKey).build()
            );
        } catch (Exception ignored) {
            // 파일 없으면 무시 (정상 로직)
        }
    }

}
