package com.github.ioloolo.onlinejudge.domain.board.controller;

import com.github.ioloolo.onlinejudge.common.security.impl.UserDetailsImpl;
import com.github.ioloolo.onlinejudge.common.validation.OrderChecks;
import com.github.ioloolo.onlinejudge.domain.board.controller.payload.request.FileDownloadRequest;
import com.github.ioloolo.onlinejudge.domain.board.controller.payload.response.FileInfoResponse;
import com.github.ioloolo.onlinejudge.domain.board.service.BoardFileService;
import com.github.ioloolo.onlinejudge.domain.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board/file")
public class BoardFileController {

    private final BoardFileService service;
    private final UserRepository userRepository;

    @PutMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<FileInfoResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws Exception {

        String fileName = service.uploadFile(file, userDetails.toUser());

        return ResponseEntity.ok(new FileInfoResponse(fileName));
    }

    @PostMapping
    public ResponseEntity<Resource> downloadFile(
            @Validated(OrderChecks.class) @RequestBody FileDownloadRequest request
    ) throws Exception {

        String fileId = request.getFileId();

        Map.Entry<String, Resource> entry = service.downloadFile(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("File-Name", entry.getKey())
                .body(entry.getValue());
    }

    @PostConstruct
    public void init() {

        File folder = new File(service.getUploadDir());

        if (!folder.exists()) {
            //noinspection ResultOfMethodCallIgnored
            folder.mkdirs();
        }
    }
}
