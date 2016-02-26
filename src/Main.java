import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by marzabal on 03.02.2016.
 */public class Main {

    public static void main(String[] args){

       // HttpDownloadUtility downloader = new HttpDownloadUtility();
        try{

            Cosmid.setAuthenticator();
            //Cosmid.downloadImages();
            Cosmid.downloadVideos();
            /*CupHolly.setAuthenticator();
            CupHolly.downloadGaleries();*/

            /*Lilly.setAuthenticator();
            Lilly.downloadGaleries();*/

            //URL url  = new URL("http://cosmid.net/members/posts.html");

            /*URL url  = new URL(getPhotosPage(1));

            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            System.out.println("Logged In!");
            String line;
            while((line = in.readLine()) != null){
                System.out.println(line);
            }
            in.close();*/
            System.out.println("Finished");
        }catch(MalformedURLException e){
            System.out.println("Malformed URL: " + e.getMessage());
        }catch (IOException e){
            System.out.println("I/O Error: " + e.getMessage());
        }

    }


}
