package server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executor;

public class StreamServer
{

    public static void main(String[] args) throws IOException
    {
        System.out.println("here 1");
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 7777), 0);//to create a server running on inet address
        System.out.println("here 2");
        httpServer.createContext("/", new CustomHttpHandler("/D:/AmplifySongs"));
        System.out.println("here 3");
        Executor e = httpServer.getExecutor();
        System.out.println("here 3.1");
        httpServer.start();
        System.out.println("here 3.2");
    }
}
class CustomHttpHandler implements HttpHandler {

    private final String rootDirectory;

    public CustomHttpHandler(String rootDirectory) {
        System.out.println("here 4");
        this.rootDirectory = rootDirectory;
    }

    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("here 5");
        URI uri = httpExchange.getRequestURI();
        System.out.println(uri);
        System.out.println(uri.getPath());
        File file = new File(rootDirectory + uri.getPath()).getCanonicalFile();
        System.out.println("here 6");
        Headers responseHeaders = httpExchange.getResponseHeaders();
        System.out.println("here 7");
        if (uri.toString().contains(".mp3"))
        {
            responseHeaders.set("Content-Type", "audio/MP3");
            System.out.println("here 8");
        }
        else
        {
            responseHeaders.set("Content-Type", "application/vnd.apple.mpegurl");
            System.out.println("here 9");
        }

        if (file.exists())
        {
            byte[] bytes = Files.readAllBytes(Paths.get(file.toURI()));
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream outputStream = httpExchange.getResponseBody())
            {
                System.out.println("here 10");
                outputStream.write(bytes);
                System.out.println(bytes);
                System.out.println("here 11");
            }
        } else {
            System.out.println("here 12");

        }
    }
}


