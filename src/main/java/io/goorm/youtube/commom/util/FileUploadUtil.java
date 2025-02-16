package io.goorm.youtube.commom.util;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
public class FileUploadUtil {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.max-size.profile}")
    private long maxProfileSize;

    @Value("${file.max-size.thumbnail}")
    private long maxThumbnailSize;

    @Value("${file.max-size.video}")
    private long maxVideoSize;

    @Value("${file.allowed-extensions.profile}")
    private String allowedProfileExtensions;

    @Value("${file.allowed-extensions.image}")
    private String allowedImageExtensions;

    @Value("${file.allowed-extensions.video}")
    private String allowedVideoExtensions;

    private Set<String> profileExtensions;
    private Set<String> imageExtensions;
    private Set<String> videoExtensions;

    @PostConstruct
    public void init() {
        // 허용된 확장자 초기화
        profileExtensions = new HashSet<>(Arrays.asList(allowedProfileExtensions.split(",")));
        imageExtensions = new HashSet<>(Arrays.asList(allowedImageExtensions.split(",")));
        videoExtensions = new HashSet<>(Arrays.asList(allowedVideoExtensions.split(",")));

        // 업로드 디렉토리 생성
        try {
            createDirectoryIfNotExists(uploadDir + "/profile");
            createDirectoryIfNotExists(uploadDir + "/video");
            createDirectoryIfNotExists(uploadDir + "/thumbnail");
            log.info("Upload directories initialized at: {}", uploadDir);
        } catch (IOException e) {
            log.error("Failed to create upload directories", e);
            throw new RuntimeException("Failed to initialize upload directories", e);
        }
    }

    /**
     * 파일 업로드 처리
     * @param file MultipartFile
     * @param subDir 저장할 하위 디렉토리 (profile, video 또는 thumbnail)
     * @return 저장된 파일의 상대 경로
     */
    public String uploadFile(MultipartFile file, String subDir) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        validateFileSize(file, subDir);
        String extension = getFileExtension(file.getOriginalFilename());
        validateFileExtension(extension, subDir);

        String directory = uploadDir + File.separator + subDir;
        String uniqueFileName = UUID.randomUUID().toString() + "." + extension;
        Path targetPath = Paths.get(directory, uniqueFileName);

        try {
            Files.copy(file.getInputStream(), targetPath);
            log.info("File uploaded successfully: {}", targetPath);
            return subDir + "/" + uniqueFileName;
        } catch (IOException e) {
            log.error("Failed to upload file: {}", file.getOriginalFilename(), e);
            throw new IOException("파일 업로드에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 파일 크기 검증
     */
    private void validateFileSize(MultipartFile file, String subDir) {
        long maxSize = switch (subDir) {
            case "profile" -> maxProfileSize;
            case "thumbnail" -> maxThumbnailSize;
            case "video" -> maxVideoSize;
            default -> throw new IllegalArgumentException("지원하지 않는 디렉토리입니다: " + subDir);
        };

        if (file.getSize() > maxSize) {
            String sizeMessage = (maxSize / (1024 * 1024)) + "MB";
            throw new IllegalArgumentException("파일 크기가 제한(" + sizeMessage + ")을 초과했습니다.");
        }
    }

    /**
     * 파일 확장자 검증
     */
    private void validateFileExtension(String extension, String subDir) {
        boolean isValid = switch (subDir) {
            case "profile" -> profileExtensions.contains(extension);
            case "thumbnail" -> imageExtensions.contains(extension);
            case "video" -> videoExtensions.contains(extension);
            default -> throw new IllegalArgumentException("지원하지 않는 디렉토리입니다: " + subDir);
        };

        if (!isValid) {
            String allowedExts = switch (subDir) {
                case "profile" -> allowedProfileExtensions;
                case "thumbnail" -> allowedImageExtensions;
                case "video" -> allowedVideoExtensions;
                default -> "";
            };
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다. 허용된 확장자: " + allowedExts);
        }
    }

    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            throw new IllegalArgumentException("파일 확장자가 없습니다.");
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 디렉토리 생성
     */
    private void createDirectoryIfNotExists(String directory) throws IOException {
        Path path = Paths.get(directory);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            log.info("Directory created: {}", directory);
        }
    }

    /**
     * 파일 경로 유효성 검사
     */
    public boolean isValidFilePath(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }

        try {
            Path normalizedPath = Paths.get(filePath).normalize();
            if (normalizedPath.startsWith("..")) {
                return false;
            }

            // 시작 경로 검증
            boolean isValidStart = filePath.startsWith("profile/") ||
                    filePath.startsWith("thumbnail/") ||
                    filePath.startsWith("video/");
            if (!isValidStart) {
                return false;
            }

            // 확장자 검증
            String extension = getFileExtension(filePath);
            if (filePath.startsWith("profile/")) {
                return profileExtensions.contains(extension);
            } else if (filePath.startsWith("thumbnail/")) {
                return imageExtensions.contains(extension);
            } else {
                return videoExtensions.contains(extension);
            }
        } catch (Exception e) {
            log.warn("Failed to validate file path: {}", filePath, e);
            return false;
        }
    }

    /**
     * 파일 삭제
     */
    public boolean deleteFile(String filePath) {
        if (!isValidFilePath(filePath)) {
            log.warn("Invalid file path for deletion: {}", filePath);
            return false;
        }

        try {
            Path fullPath = Paths.get(uploadDir, filePath);
            return Files.deleteIfExists(fullPath);
        } catch (IOException e) {
            log.error("Failed to delete file: {}", filePath, e);
            return false;
        }
    }
}