package com.familybooks.repository.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Shared UTF-8 file I/O helpers with locking and atomic replacement.
 * Internal details are hidden from controllers and services (information hiding).
 */
@Component
public class FileStorageSupport {

    public static final String FIELD_SEPARATOR = "|";
    public static final String ESCAPED_SEPARATOR = "&#124;";

    private final Path dataDirectory;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public FileStorageSupport(@Value("${familybooks.data.directory:data}") String dataDirectory) {
        this.dataDirectory = Paths.get(dataDirectory).toAbsolutePath().normalize();
        ensureDirectory();
    }

    public Path resolve(String fileName) {
        return dataDirectory.resolve(fileName);
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

    public void ensureFile(String fileName) {
        lock.writeLock().lock();
        try {
            ensureDirectory();
            Path file = resolve(fileName);
            if (!Files.exists(file)) {
                Files.writeString(file, "", StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create data file: " + fileName, e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<String> readLines(String fileName) {
        lock.readLock().lock();
        try {
            ensureFileUnlocked(fileName);
            List<String> lines = Files.readAllLines(resolve(fileName), StandardCharsets.UTF_8);
            List<String> result = new ArrayList<>();
            for (String line : lines) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty() && !trimmed.startsWith("#")) {
                    result.add(trimmed);
                }
            }
            return result;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read data file: " + fileName, e);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void writeLines(String fileName, List<String> lines) {
        lock.writeLock().lock();
        try {
            ensureDirectory();
            Path target = resolve(fileName);
            Path temp = resolve(fileName + ".tmp");
            List<String> output = new ArrayList<>();
            output.add("# Family Books data file — UTF-8 — do not edit while the app is running");
            output.addAll(lines);
            Files.write(temp, output, StandardCharsets.UTF_8);
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            try {
                Path target = resolve(fileName);
                Path temp = resolve(fileName + ".tmp");
                if (Files.exists(temp)) {
                    Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException ignored) {
                // fall through to original error
            }
            throw new IllegalStateException("Unable to write data file: " + fileName, e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public String escape(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\r", " ")
                .replace("\n", " ")
                .replace(FIELD_SEPARATOR, ESCAPED_SEPARATOR);
    }

    public String unescape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace(ESCAPED_SEPARATOR, FIELD_SEPARATOR);
    }

    public String[] splitFields(String line, int expectedMin) {
        String[] parts = line.split("\\" + FIELD_SEPARATOR, -1);
        if (parts.length < expectedMin) {
            throw new IllegalArgumentException("Corrupted record (expected at least "
                    + expectedMin + " fields): " + line);
        }
        return parts;
    }

    private void ensureDirectory() {
        try {
            Files.createDirectories(dataDirectory);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create data directory: " + dataDirectory, e);
        }
    }

    private void ensureFileUnlocked(String fileName) throws IOException {
        ensureDirectory();
        Path file = resolve(fileName);
        if (!Files.exists(file)) {
            Files.writeString(file, "", StandardCharsets.UTF_8);
        }
    }
}
