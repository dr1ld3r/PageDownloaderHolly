import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import java.net.*;
import java.util.ArrayList;

/**
 * Created by marzabal on 04.02.2016.
 */
public class Cosmid {

    static String BASE_DIR = "E:/cosmid/";//"C:/FILES/marzabal/test/cosmid/";

    public static void setAuthenticator(){
        Authenticator.setDefault(new CustomAuthenticator());
    }

    public static void downloadVideos() throws IOException {

        for (int i=3;i<=55;i++) {
            System.out.println("Page Number: " + i);
            String[] videoUpdatesPageLines = getPageStringList(getVideosPage(i));
            ArrayList<String[]> videoSets = getPosts(videoUpdatesPageLines);
            for(String[] post: videoSets){
                downloadVideo(post);
            }
        }

    }



    public static void downloadImages() throws IOException {

        for (int i=1;i<=55;i++) {
            System.out.println("Page Number: " + i);
            String[] photoUpdatesPageLines = getPageStringList(getPhotosPage(i));
            ArrayList<String[]> photoSets = getPosts(photoUpdatesPageLines);
            for(String[] post: photoSets){
                downloadPhotoSet(post);
            }
        }

    }

    private static void downloadPhotoSet(String[] post) throws IOException {
        String zipFileLink = getZipFileLink(getPageStringList(getPostLink(post)));
        String modelName = getModelName(post);
        if(modelName.endsWith(" "))
            modelName = modelName.replace(" ", "");
        String zipFileName = zipFileLink.substring(zipFileLink.lastIndexOf("/") + 1,
                zipFileLink.length());
        System.out.println(BASE_DIR + modelName);
        if(!(new File(BASE_DIR + modelName).exists()))
            new File(BASE_DIR + modelName).mkdir();
        else if(new File(BASE_DIR + modelName + "/" + zipFileName).exists()){
            System.out.println("File: " + zipFileName + " already exist");
            return;
        }
        HttpDownloadUtility.downloadFile(zipFileLink, BASE_DIR + modelName);
    }

    private static void downloadVideo (String[] post) throws IOException {
        String videoFileLink = getVideoFileLinkHD(getPageStringList(getPostLink(post)));
        if (videoFileLink == null)
            videoFileLink = getVideoFileLinkWMV(getPageStringList(getPostLink(post)));
        String modelName = getModelName(post);
        if(modelName.endsWith(" "))
            modelName = modelName.replace(" ", "");
        String videoFileName = videoFileLink.substring(videoFileLink.lastIndexOf("/") + 1,
                videoFileLink.length());
        System.out.println(BASE_DIR + modelName);
        if(!(new File(BASE_DIR + modelName).exists()))
            new File(BASE_DIR + modelName).mkdir();
        else if(new File(BASE_DIR + modelName + "/" + videoFileName).exists()){
            System.out.println("File: " + videoFileName + " already exist");
            return;
        }
        HttpDownloadUtility.downloadFile(videoFileLink, BASE_DIR + modelName);
    }


    private static String[] getPageStringList(String photoUpdatesPageLink) throws IOException {
        URL photoUpdatesPage  = new URL(photoUpdatesPageLink);
        BufferedReader in = new BufferedReader(new InputStreamReader(photoUpdatesPage.openStream()));
        return in.lines().toArray(size -> new String[size]);
    }


    private static String getPhotosPage(int pageNumber){
        if(pageNumber==1)
            return "http://www.cosmid.net/members/posts.html?view=photos";
        return "http://www.cosmid.net/members/posts.html?page=" + pageNumber + "&view=photos";
    }

    private static ArrayList<String[]> getPosts(String updatesPage[]){
        ArrayList<String[]> posts = new ArrayList();
        for(int i=0; i<updatesPage.length;i++)
            if(updatesPage[i].contains("<div class=\"column")){
                ArrayList<String> post = new ArrayList();
                while(!updatesPage[i].contains("</div>")){
                    post.add(updatesPage[i]);
                    i++;
                }
                String[] postArray = new String[post.size()];
                postArray = post.toArray(postArray);
                posts.add(postArray);
            }
        return posts;
    }
    private static String getPostTitle(String post[]){
        for(String line: post)
            if(line.contains("<strong><a href=\"photoset.html?title="))
                return line.substring(line.indexOf(">")+1, line.indexOf("</a>"));

        System.out.println("Title not found");
        return null;
    }

    private static String getPostDate(String post[]){
        for(String line: post)
            if(line.contains("DATE ADDED:"))
                return line.substring(line.indexOf(":")+1, line.indexOf("</"));

        System.out.println("Date not found");
        return null;
    }

    private static String getPostLink(String post[]){
        for(String line: post)
            if(line.contains("<a href=\"")) {

                return "http://www.cosmid.net/members/" + line.substring(line.indexOf("=\"") + 2, line.indexOf("\">"));
            }
        System.out.println("Link not found");
        return null;
    }

    private static String getZipFileLink(String photoSetFile[]){
        for(String line: photoSetFile){
            if(line.contains("<a href=\"/members/zips")) {
                System.out.println(line + line.indexOf("\""));
                return "http://www.cosmid.net" + line.substring(line.indexOf("=\"") + 2, line.indexOf("\" "));
            }
        }
        System.out.println("Zip not found");
        return null;
    }

    private static String getVideoFileLinkHD(String photoSetFile[]){
        for(String line: photoSetFile){
            if(line.contains("720")) {
                System.out.println(line + line.indexOf("href=\"")+5);
                return "http://www.cosmid.net/members/" + line.substring(line.indexOf("href=") + 5, line.indexOf(">HD "));
            }
        }
        System.out.println("HD not found");
        return null;
    }

    private static String getVideoFileLinkWMV(String photoSetFile[]){
        for(String line: photoSetFile){
            if(line.contains("wmv")) {
                System.out.println(line + line.indexOf("href=\"")+5);
                return "http://www.cosmid.net" + line.substring(line.indexOf("href=") + 5, line.indexOf("\">Download "));
            }
        }
        System.out.println("WMV video not found");
        return null;
    }


    private static String getModelName(String post[]){
        for(String line: post)
            if (line.contains("MODELS:"))
                return line.substring(line.indexOf("\">")+2, line.indexOf("</a>"));

        System.out.println("Model name not found");
        return null;
    }

    private static String getVideosPage(int pageNumber){
        if(pageNumber==1)
            return "http://www.cosmid.net/members/posts.html?view=videos";
        return "http://www.cosmid.net/members/posts.html?page=" + pageNumber + "&view=videos";
    }

    private static class CustomAuthenticator extends Authenticator {

        protected PasswordAuthentication getPasswordAuthentication(){

            String prompt = getRequestingPrompt();
            String hostname = getRequestingHost();
            InetAddress ipaddr= getRequestingSite();
            int port = getRequestingPort();

            String username = "dr1ld3r";
            String password = "168046fr";

            return new PasswordAuthentication(username, password.toCharArray());
        }


    }
}
