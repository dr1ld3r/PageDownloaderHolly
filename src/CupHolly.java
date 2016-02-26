import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.InetAddress;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by marzabal on 04.02.2016.
 */
public class CupHolly {

    static String BASE_DIR = "C:/FILES/marzabal/test/Hcup/";

    public static void setAuthenticator(){
        Authenticator.setDefault(new CustomAuthenticator());
    }

    public static void downloadGaleries() throws IOException {


        String[] galleriesPage = getPageStringList(getPhotosPage());
        ArrayList<String[]> photoSets = getPosts(galleriesPage);
        for(int i=1;i<photoSets.size();i++)
            downloadImages(getPostLink(photoSets.get(i)),getPostTitle(photoSets.get(i)));
    }

    private static String[] getPageStringList(String pageURL) throws IOException {
        URL photoUpdatesPage  = new URL(pageURL);
        BufferedReader in = new BufferedReader(new InputStreamReader(photoUpdatesPage.openStream()));
        return in.lines().toArray(size -> new String[size]);
    }


    private static String getPhotosPage(){
            return "http://members.hcupholly.com/members/?page_id=4";
    }

    private static ArrayList<String[]> getPosts(String updatesPage[]){
        ArrayList<String[]> posts = new ArrayList();
        for(int i=0; i<updatesPage.length;i++)
            if(updatesPage[i].contains("<div class=\"ngg-album-compact\">")){
                ArrayList<String> post = new ArrayList();
                while(i < updatesPage.length && !updatesPage[i].contains("Photos</p>")){
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
            if(line.contains("title=\"Set"))
                return line.substring(line.indexOf("title=\"Set")+7, line.indexOf("\" href"));

        System.out.println("Title not found");
        return null;
    }

    private static void downloadImages(String galleryLink, String galleryName) throws IOException{
        if(!(new File(BASE_DIR + galleryName).exists()))
            new File(BASE_DIR + galleryName).mkdir();
        else{ //if(images.size() == (new File(BASE_DIR + galleryName).listFiles().length)){
            System.out.println(galleryName + " already downloaded and up to date");
            return;
        }
        String[] imagesPage = getPageStringList(galleryLink);
        ArrayList<String> images = getImagesLinks(imagesPage);
        int imagesDownloaded=0;


        for(String downloadLink: images){
            HttpDownloadUtility.downloadFile(downloadLink,BASE_DIR + galleryName);
            imagesDownloaded++;
        }
        if(imagesDownloaded != images.size())
            System.out.println("Not all images could be downloaded");
    }

    private static ArrayList<String> getImagesLinks(String[] imagesPage){
        ArrayList<String> imagesList = new ArrayList();
        ArrayList<String[]> setPages = new ArrayList();
        setPages.add(imagesPage);
        setPages.addAll(getSetPages(imagesPage));

        for(String[] page: setPages)
            for(String line: page)
                if(line.contains("<a href=\"http://members.hcupholly.com/members/wp-content/gallery"))
                    imagesList.add(line.substring(line.indexOf("\"")+1,line.indexOf("\" title")));

        return imagesList;
    }

    private static ArrayList<String[]> getSetPages(String[] firtImagesPage){
        ArrayList<String[]> pagesList = new ArrayList();
        String pagesLine = "";
        for(String line: firtImagesPage)
            if(line.contains("page-numbers"))
                pagesLine = line;

        String[] pagesLinkArray = Arrays.copyOfRange(pagesLine.split("page-numbers"),1,pagesLine.split("page-numbers").length);
        for(int i=0;i<pagesLinkArray.length;i++) {
            //System.out.println(pagesLinkArray[i]);
            if(pagesLinkArray[i].contains("href"))
                pagesLinkArray[i] = "http://members.hcupholly.com" + pagesLinkArray[i].substring(pagesLinkArray[i].indexOf("href=\"")+6, pagesLinkArray[i].indexOf("\">")).replace("amp;","");
        }

        for (String link : pagesLinkArray)
            try {
                pagesList.add(getPageStringList(link));
            }catch(IOException e){
                System.out.println("Error with link: " + link);
            }

        return pagesList;
    }


    private static String getPostLink(String post[]){
        for(String line: post)
            if(line.contains("Link")) {
                return "http://members.hcupholly.com" + line.substring(line.indexOf("href=\"") + 6, line.indexOf("\">")).replace("amp;","");
            }
        System.out.println("Link not found");
        return null;
    }

    private static class CustomAuthenticator extends Authenticator {

        protected PasswordAuthentication getPasswordAuthentication(){

            String prompt = getRequestingPrompt();
            String hostname = getRequestingHost();
            InetAddress ipaddr= getRequestingSite();
            int port = getRequestingPort();

            String username = "bazrama";
            String password = "YqfmiQZ$";

            return new PasswordAuthentication(username, password.toCharArray());
        }


    }
}
