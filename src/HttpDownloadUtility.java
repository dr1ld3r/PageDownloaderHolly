/**
 * Created by marzabal on 03.02.2016.
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpDownloadUtility {
    private static final int BUFFER_SIZE = /*1024*512;//*/4096;
    private static int printedBars = -1;
    /**
     * Downloads a file from a URL
     * @param fileURL HTTP URL of the file to be downloaded
     * @param saveDir path of the directory to save the file
     * @throws IOException
     */
    public static void downloadFile(String fileURL, String saveDir)
            throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();

        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();

            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 9,
                            disposition.length());
                }
            } else {
                // extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                        fileURL.length());
            }

            System.out.println("Content-Type = " + contentType);
            System.out.println("Content-Disposition = " + disposition);
            System.out.println("Content-Length = " + contentLength);
            System.out.println("fileName = " + fileName);

            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = saveDir + File.separator + fileName;

            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            int bytesRead = -1;
            int bytesReadSum = 0;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                bytesReadSum += BUFFER_SIZE;
                //calculateProgressBar(contentLength,bytesReadSum);
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            System.out.println("File downloaded");
        } else {
            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();
    }

 /*   private static void calculateProgressBar(int contentLength,int bytesReadSum){
        double percentage = bytesReadSum*100/contentLength;
        if(percentage < 1) percentage *= 100;
        if(percentage < 0) percentage /= -100;
        int numberOfBars = (int)Math.floor(percentage/5);
       // System.out.print(percentage + "/5=" +  numberOfBars + ">>>>>>" );
        if (printedBars < numberOfBars){
            if(numberOfBars==0)
                System.out.print("0");
            if(numberOfBars==10)
                System.out.print("50");
            if(numberOfBars==20)
                System.out.print("100");
            for(int i= 0; i< numberOfBars-printedBars;i++)
                System.out.print("-");
            printedBars = numberOfBars;
        }

    }*/
}