package com.familybooks.service;

import com.familybooks.exception.InvalidOperationException;
import com.familybooks.repository.BookRepository;
import com.familybooks.repository.file.FileStorageSupport;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Stores book cover uploads under {@code data/uploads/books/} and exposes them at {@code /uploads/books/**}.
 * Bundled classpath images under {@code /images/**} are never deleted.
 */
@Service
public class BookCoverStorageService {

    public static final String PLACEHOLDER = "/images/books/placeholder.jpg";
    public static final String PUBLIC_PREFIX = "/uploads/books/";
    public static final String RELATIVE_DIR = "uploads/books";
    public static final long MAX_BYTES = 5L * 1024 * 1024;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/webp"
    );

    private static final Map<String, String> EXT_BY_CONTENT_TYPE = Map.of(
            "image/jpeg", ".jpg",
            "image/jpg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp"
    );

    private final Path uploadRoot;
    private final BookRepository bookRepository;

    public BookCoverStorageService(FileStorageSupport fileStorageSupport, BookRepository bookRepository) {
        this.uploadRoot = fileStorageSupport.getDataDirectory()
                .resolve(RELATIVE_DIR)
                .toAbsolutePath()
                .normalize();
        this.bookRepository = bookRepository;
        ensureUploadDirectory();
    }

    public Path getUploadRoot() {
        return uploadRoot;
    }

    public boolean isManagedUpload(String coverImage) {
        return coverImage != null && coverImage.startsWith(PUBLIC_PREFIX);
    }

    /**
     * Stores a validated cover and returns the public URL ({@code /uploads/books/...}).
     */
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidOperationException("Please choose an image file to upload.");
        }
        if (file.getSize() > MAX_BYTES) {
            throw new InvalidOperationException("Cover image must be 5 MB or smaller.");
        }

        String contentType = normalizeContentType(file.getContentType());
        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new InvalidOperationException("Only JPG, PNG, and WebP images are allowed.");
        }

        String extension = EXT_BY_CONTENT_TYPE.get(contentType);
        byte[] header = readHeader(file);
        if (!matchesMagic(header, contentType)) {
            throw new InvalidOperationException("The selected file is not a valid image.");
        }

        String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "";
        if (originalName.contains("..") || originalName.contains("/") || originalName.contains("\\")) {
            throw new InvalidOperationException("Invalid image file name.");
        }

        ensureUploadDirectory();
        String filename = "cover-" + UUID.randomUUID().toString().replace("-", "") + extension;
        Path target = uploadRoot.resolve(filename).normalize();
        if (!target.startsWith(uploadRoot)) {
            throw new InvalidOperationException("Invalid upload destination.");
        }

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new InvalidOperationException("Unable to save the cover image. Please try again.");
        }

        return PUBLIC_PREFIX + filename;
    }

    /**
     * Deletes an uploaded cover only when it lives under {@code /uploads/books/}
     * and is not referenced by any remaining book.
     */
    public void deleteIfUnusedUpload(String coverImage) {
        if (!isManagedUpload(coverImage)) {
            return;
        }
        boolean stillUsed = bookRepository.findAll().stream()
                .anyMatch(book -> coverImage.equals(book.getCoverImage()));
        if (stillUsed) {
            return;
        }
        deleteUploadFile(coverImage);
    }

    public void deleteUploadFile(String coverImage) {
        if (!isManagedUpload(coverImage)) {
            return;
        }
        String filename = coverImage.substring(PUBLIC_PREFIX.length());
        if (filename.isBlank() || filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            return;
        }
        Path target = uploadRoot.resolve(filename).normalize();
        if (!target.startsWith(uploadRoot)) {
            return;
        }
        try {
            Files.deleteIfExists(target);
        } catch (IOException ignored) {
            // Best-effort cleanup; book record remains consistent.
        }
    }

    private void ensureUploadDirectory() {
        try {
            Files.createDirectories(uploadRoot);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create uploads directory: " + uploadRoot, e);
        }
    }

    private static String normalizeContentType(String contentType) {
        if (contentType == null) {
            return "";
        }
        return contentType.toLowerCase(Locale.ROOT).split(";", 2)[0].trim();
    }

    private static byte[] readHeader(MultipartFile file) {
        try (InputStream in = file.getInputStream()) {
            return in.readNBytes(16);
        } catch (IOException e) {
            throw new InvalidOperationException("Unable to read the selected image.");
        }
    }

    private static boolean matchesMagic(byte[] header, String contentType) {
        if (header == null || header.length < 3) {
            return false;
        }
        return switch (contentType) {
            case "image/jpeg", "image/jpg" ->
                    (header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8 && (header[2] & 0xFF) == 0xFF;
            case "image/png" ->
                    header.length >= 8
                            && (header[0] & 0xFF) == 0x89
                            && header[1] == 0x50
                            && header[2] == 0x4E
                            && header[3] == 0x47
                            && header[4] == 0x0D
                            && header[5] == 0x0A
                            && header[6] == 0x1A
                            && header[7] == 0x0A;
            case "image/webp" ->
                    header.length >= 12
                            && header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[3] == 'F'
                            && header[8] == 'W' && header[9] == 'E' && header[10] == 'B' && header[11] == 'P';
            default -> false;
        };
    }
}
