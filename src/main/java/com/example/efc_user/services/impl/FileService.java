package com.example.efc_user.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    private final String baseDirectory = "C:\\Users\\sansk\\OneDrive\\Desktop\\Crix1";

    public String saveFile(MultipartFile file) throws IOException {

        Path directory = Paths.get(baseDirectory);
        Files.createDirectories(directory);

        // Save file
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = directory.resolve(fileName);
        Files.write(filePath, file.getBytes());

        // Return relative file path for database storage
        return fileName;
    }
}
