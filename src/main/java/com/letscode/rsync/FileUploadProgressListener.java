package com.letscode.rsync;

import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import java.io.IOException;

public class FileUploadProgressListener
        implements MediaHttpUploaderProgressListener {
    private static final ProcessBar bar = new ProcessBar(100);
    public void progressChanged(MediaHttpUploader uploader)
            throws IOException {
        switch (uploader.getUploadState()) {
            case INITIATION_STARTED:
                System.out.println(">>>>Upload Initiation has started!");
                break;
            case INITIATION_COMPLETE:
                System.out.println(">>>>Upload Processing...");
                break;
            case MEDIA_IN_PROGRESS:
                bar.printProcessBar((int)(uploader.getProgress()*100));
                break;
            case MEDIA_COMPLETE:
                System.out.print("\n>>>>Upload is Complete!");
        }
    }
}
