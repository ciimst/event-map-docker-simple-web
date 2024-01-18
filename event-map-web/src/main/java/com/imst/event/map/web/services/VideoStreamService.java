package com.imst.event.map.web.services;
import static com.imst.event.map.web.constant.ApplicationConstants.ACCEPT_RANGES;
import static com.imst.event.map.web.constant.ApplicationConstants.BYTES;
import static com.imst.event.map.web.constant.ApplicationConstants.BYTE_RANGE;
import static com.imst.event.map.web.constant.ApplicationConstants.CONTENT_LENGTH;
import static com.imst.event.map.web.constant.ApplicationConstants.CONTENT_RANGE;
import static com.imst.event.map.web.constant.ApplicationConstants.CONTENT_TYPE;
import static com.imst.event.map.web.constant.ApplicationConstants.VIDEO_CONTENT;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class VideoStreamService {

    public ResponseEntity<byte[]> prepareContent(String fileAbsolutePath, String fileType, String range) {
    	
        long rangeStart = 0;
        long rangeEnd;
        byte[] data;
        Long fileSize;
        String fullFileName = fileAbsolutePath;
        
        try {
        	
            fileSize = getFileSize(fullFileName);
            if (range == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .header(CONTENT_TYPE, VIDEO_CONTENT + fileType)
                        .header(CONTENT_LENGTH, String.valueOf(fileSize))
                        .body(readByteRange(fullFileName, rangeStart, fileSize - 1)); // Read the object and convert it as bytes
            }
            String[] ranges = range.split("-");
            rangeStart = Long.parseLong(ranges[0].substring(6));
            
            if (ranges.length > 1) {
                rangeEnd = Long.parseLong(ranges[1]);
            } else {
                rangeEnd = rangeStart + BYTE_RANGE;
            }
            
            if (fileSize < rangeEnd) {
                rangeEnd = fileSize - 1;
            }
            
            data = readByteRange(fullFileName, rangeStart, rangeEnd);
        } catch (IOException e) {
            log.error("Exception while reading the file {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        
        String contentLength = String.valueOf((rangeEnd - rangeStart) + 1);
        ResponseEntity<byte[]> responseEntity = ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header(CONTENT_TYPE, VIDEO_CONTENT + fileType)
                .header(ACCEPT_RANGES, BYTES)
                .header(CONTENT_LENGTH, contentLength)
                .header(CONTENT_RANGE, BYTES + " " + rangeStart + "-" + rangeEnd + "/" + fileSize)
                .body(data);
        
        return responseEntity;
    }

    private byte[] readByteRange(String fileAbsolutePath, long start, long end) throws IOException {
        
    	Path path = Paths.get(fileAbsolutePath);
        
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(path.toString(), "r"))
        {
        	int length =  (int) (end - start + 1);
            byte[] buffer = new byte[length];
            randomAccessFile.seek(start);
            randomAccessFile.readFully(buffer);

            return buffer;
        }
    }

    
    private Long getFileSize(String fileAbsolutePath) {
    	return FileUtils.sizeOf(Paths.get(fileAbsolutePath).toFile());
    }

}