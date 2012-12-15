package org.mifos.framework.fileupload.service;

import java.io.InputStream;
import java.util.List;

import org.mifos.dto.screen.UploadedFileDto;

public interface FileService {
    boolean create(Integer entityId, InputStream in, UploadedFileDto uploadedFileDto);

    UploadedFileDto read(Long fileId);
    
    List<UploadedFileDto> readAll(Integer entityId);

    boolean update(Integer entityId, InputStream in, UploadedFileDto uploadedFileDto);

    boolean delete(Integer entityId, Long fileId);

    byte[] getData(UploadedFileDto clientFile);

    boolean checkIfFileExists(Integer entityId, String fileName);
}
