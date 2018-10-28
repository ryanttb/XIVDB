package info.xivdb.xivdb;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        public DownloadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap urlBitmap = null;

            try {
                InputStream inputStream = new URL(url).openStream();
                urlBitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException ex) {
                //log.e("Error", ex.getMessage());
                ex.printStackTrace();
            }
            return urlBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = findViewById(R.id.searchView);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            searchView.setQuery(query, false);
            searchXivdb(query);
        }
    }

    protected void searchXivdb(String query) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String searchUrl = String.format(getResources().getString(R.string.xivdb_api_search_format), query);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, searchUrl, null, new XivdbSearchListener(), new XivdbErrorListener());

        queue.add(jsonRequest);
    }

    protected class XivdbSearchListener implements Response.Listener<JSONObject> {
        @Override
        public void onResponse(JSONObject response) {
            try {
                String output = "";

                JSONObject items = response.getJSONObject("items");
                JSONArray results = items.getJSONArray("results");
                for (int i = 0; i < results.length(); i++) {
                    JSONObject o = results.getJSONObject(i);
                    output += o.getString("name") + "\n";
                }

                TextView t = findViewById(R.id.textView);
                t.setText(output);

                if (results.length() > 0) {
                    JSONObject firstResult = results.getJSONObject(0);
                    ImageView firstCardImage = findViewById(R.id.firstCardImage);
                    TextView firstCardText = findViewById(R.id.firstCardText);

                    firstCardText.setText(firstResult.getString("name"));

                    DownloadImageTask downloadImageTask = new DownloadImageTask(firstCardImage);
                    downloadImageTask.execute(firstResult.getString("icon"));
                }
            } catch (JSONException ex) {
                // suppress
            }

        }
    }

    protected class XivdbErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            System.out.println(error.toString());
        }
    }
}
