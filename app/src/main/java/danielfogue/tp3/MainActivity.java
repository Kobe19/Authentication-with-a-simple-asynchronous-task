package danielfogue.tp3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    // declaration of my layouts
    private EditText txt1;
    private EditText txt2;
    private androidx.appcompat.widget.AppCompatButton Btn1;
    private TextView result;
    boolean res;
    String user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt1 = findViewById(R.id.txt1);
        txt2 = findViewById(R.id.txt2);
        Btn1 = findViewById(R.id.Btn1);
        result = findViewById(R.id.result);

        Btn1.setOnClickListener(Btn1Listener);
    }

    private View.OnClickListener Btn1Listener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {

            Thread thread = new Thread(new Runnable() {
                URL url = null;
                @Override
                public void run() {
                    try {
                        url = new URL("https://httpbin.org/basic-auth/bob/sympa");
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        String basicAuth = "Basic " + Base64.encodeToString("bob:sympa".getBytes(), Base64.NO_WRAP);
                        urlConnection.setRequestProperty ("Authorization", basicAuth);

                        try {
                            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                            String s = readStream(in);
                            Log.i("JFL", s);

                            JSONObject json = new JSONObject(s);
                            res = json.getBoolean("authenticated");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    result.setText(String.valueOf(res));
                                }
                            });

                        } finally {
                            urlConnection.disconnect();
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    };

    private String readStream(InputStream in)  throws IOException{
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(in),1000);
        for (String line = r.readLine(); line != null; line =r.readLine()){
            sb.append(line);
        }
        in.close();
        return sb.toString();
    }
}