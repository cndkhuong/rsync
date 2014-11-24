package com.letscode.rsync;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.letscode.rsync.FileUploadProgressListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author kevin
 */
public class Main {
    
      
    private static String CLIENT_ID ,CLIENT_SECRET ,REDIRECT_URI ,APPLICATION_NAME;
    
    static {
        ResourceBundle rb=ResourceBundle.getBundle("app"); 
        CLIENT_ID = rb.getString("CLIENT_ID");
        CLIENT_SECRET = rb.getString("CLIENT_SECRET");
        REDIRECT_URI = rb.getString("REDIRECT_URI");
        APPLICATION_NAME = rb.getString("APPLICATION_NAME");
    }
    
    private static Drive drive;
    private static String filePath = "";

    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        if (args.length <= 0) {
            System.out.println("Please input your upload file path : ");
            filePath = br.readLine();
            if ("".equals(filePath)) {
                System.exit(0);
            }
        } else {
            filePath = args[0];
        }

        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, CLIENT_ID, CLIENT_SECRET,
                Arrays.asList(new String[]{"https://www.googleapis.com/auth/drive"}))
                .setAccessType("online")
                .setApprovalPrompt("auto")
                .build();

        String url = flow.newAuthorizationUrl().setRedirectUri("urn:ietf:wg:oauth:2.0:oob").build();
        System.out.println("****Please open the following URL in your browser then type the authorization code");
        System.out.println("[COPY] " + url);
        System.out.print("[PASTE] Authen code : ");
        String code = br.readLine();

        GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(REDIRECT_URI).execute();
        GoogleCredential credential = new GoogleCredential().setFromTokenResponse(response);

        drive = new Drive.Builder(httpTransport, jsonFactory, credential).setApplicationName(APPLICATION_NAME).build();

        uploadFolder(false, filePath);
    }

    private static void uploadFolder(boolean useDirectUpload, String filePath) throws IOException{
        java.io.File file = new java.io.File(filePath);
        if(file.isDirectory()){
            for(java.io.File childFile : file.listFiles()){
                uploadFolder(useDirectUpload, childFile.getPath());
            }
        }else{
            uploadFile(useDirectUpload, file);
        }
    }
    
    private static com.google.api.services.drive.model.File uploadFile(boolean useDirectUpload, java.io.File fileContent)
            throws IOException {
        System.out.println("-------------" + fileContent.getName()+"-------------");
        com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
        fileMetadata.setTitle(fileContent.getName());
        Date lastModify = new Date(fileContent.lastModified());
        fileMetadata.setDescription(lastModify.toString());
        fileMetadata.setMimeType("text/plain");
        fileMetadata.set("uploadType", "resumable");

        FileContent mediaContent = new FileContent("text/plain", fileContent);

        Drive.Files.Insert insert = drive.files().insert(fileMetadata, mediaContent);
        MediaHttpUploader uploader = insert.getMediaHttpUploader();
        uploader.setDirectUploadEnabled(useDirectUpload);
        uploader.setProgressListener(new FileUploadProgressListener());
        com.google.api.services.drive.model.File file = (com.google.api.services.drive.model.File) insert.execute();
        System.out.println("Successfull ! File ID: " + file.getId()); 
        return file;
    }
    
    
}
