package org.voidness.dseries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

public class DownloadService {

    public static final int TIMEOUT_MILLIS = 10000;

    protected static InputStream getRemoteInputStream(String url) throws ClientProtocolException, IOException, URISyntaxException {

        HttpGet request = new HttpGet(url);

        HttpContext localContext = new BasicHttpContext();
        HttpClient client = getClient();
        HttpResponse response = client.execute(request, localContext);

        return response.getEntity().getContent();
    }

    public static void downloadFile(String url, String targetFolder) throws ClientProtocolException, IOException, URISyntaxException {

        System.out.println("Downloading file " + url + " to " + targetFolder); //$NON-NLS-1$ //$NON-NLS-2$

        InputStream input = getRemoteInputStream(url);
        String file = url.substring(url.lastIndexOf("/") + 1); //$NON-NLS-1$

        OutputStream output;
        try {

            output = new FileOutputStream(targetFolder + "/" + file); //$NON-NLS-1$

        } catch (FileNotFoundException fne) {

            System.out.println("Oops, could not save file, maybe the folder does not exist. Attempting to create."); //$NON-NLS-1$
            new File(targetFolder).mkdirs();

            // Attempt to open the stream now
            output = new FileOutputStream(targetFolder + "/" + file); //$NON-NLS-1$
        }

        byte data[] = new byte[1024];
        int count = 0;
        while ((count = input.read(data)) != -1) {

            output.write(data, 0, count);
        }

        output.flush();
        output.close();
        input.close();
    }

    public static HttpClient getClient() {

        //return HttpClients.custom().setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(TIMEOUT_MILLIS).build())
        //        .setDefaultRequestConfig(RequestConfig.custom().setConnectTimeout(TIMEOUT_MILLIS).build()).build();
        HttpClient client = new DefaultHttpClient();
        HttpParams params = client.getParams();
        HttpConnectionParams.setConnectionTimeout(params, TIMEOUT_MILLIS);
        HttpConnectionParams.setSoTimeout(params, TIMEOUT_MILLIS);
        return client;
    }
}
