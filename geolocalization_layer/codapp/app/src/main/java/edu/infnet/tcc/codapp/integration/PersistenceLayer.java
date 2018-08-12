package edu.infnet.tcc.codapp.integration;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import edu.infnet.tcc.codapp.model.CarbonMonoxideData;


public class PersistenceLayer extends AsyncTask<CarbonMonoxideData, Void, String> {

    private String destinationURL;
    private final RequestQueue queue;

    public PersistenceLayer(String URL, Context context) {
        destinationURL = URL;
        queue = Volley.newRequestQueue(context);
    }

    @Override
    protected String doInBackground(final CarbonMonoxideData... collectedData) {
        final Gson gson = new Gson();
        for (int i = 0; i < collectedData.length; i++) {
            final CarbonMonoxideData data = collectedData[i];
            StringRequest postRequest = new StringRequest(Request.Method.POST, destinationURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("x-api-key", "XXXX");
                    return headers;
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    return gson.toJson(data).getBytes();
                }
            };

            queue.add(postRequest);
        }

        return null;
    }

}
