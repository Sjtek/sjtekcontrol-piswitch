package nl.sjtek.control.piswitch;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;

public class PingThread extends Thread {

    private final OnAvailableListener onAvailableListener;
    private final String url;

    public PingThread(String url, OnAvailableListener onAvailableListener) {
        this.onAvailableListener = onAvailableListener;
        this.url = url;
    }

    @Override
    public void run() {
        super.run();
        OkHttpClient okHttpClient = new OkHttpClient();

        boolean connected = false;
        while (true) {
            okhttp3.Response response = null;
            try {
                Thread.sleep(1000);
                Call call = okHttpClient.newCall(new Request.Builder().url(url).build());
                response = call.execute();
                if (response.isSuccessful()) {
                    onAvailableListener.onAvailable();
                    return;
                }

            } catch (IOException | InterruptedException e) {
                System.out.println("Connection failed on " + url + " (" + e.getMessage() + ")");
            } finally {
                if (response != null) {
                    response.body().close();
                }
            }
        }
    }

    public interface OnAvailableListener {
        void onAvailable();
    }
}
