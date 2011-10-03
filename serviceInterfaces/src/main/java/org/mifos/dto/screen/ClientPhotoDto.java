package org.mifos.dto.screen;

public class ClientPhotoDto {

    private final String contentType;
    private final Long contentLength;
    private final byte[] out;

    public ClientPhotoDto(final String contentType,final Long contentLength,final byte[] out) {
        super();
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.out = out.clone();
    }

    public Long getContentLength() {
        return contentLength;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getOut() {
        return out.clone();
    }

}
