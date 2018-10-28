package info.xivdb.xivdb;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

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
            System.out.println(response);
        }
    }

    protected class XivdbErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            System.out.println(error.toString());
        }
    }
}
