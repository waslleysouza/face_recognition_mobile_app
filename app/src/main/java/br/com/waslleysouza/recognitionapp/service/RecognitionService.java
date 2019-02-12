package br.com.waslleysouza.recognitionapp.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import br.com.waslleysouza.recognitionapp.util.ImageUtils;
import br.com.waslleysouza.recognitionapp.util.Utils;
import br.com.waslleysouza.recognitionapp.util.VolleyMultipartRequest;

public class RecognitionService {

    private static final String TAG = RecognitionService.class.getSimpleName();
    private static final String RECOGNIZE_URL = "/face/classify";
    private static final String ADD_URL = "/face/add";
    private static final String TRAIN_URL = "/face/train";

    private static String getHost(final Context context) {
        return Utils.getServerURL(context);
    }

    public static void add(final Context context, final File file, final String rotate, final String name) {
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, getHost(context) + ADD_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(context, obj.get("result").toString() + ": " + obj.get("message").toString(), Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("personName", name);
                params.put("rotate", rotate);
                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long filename = System.currentTimeMillis();
                byte[] bytes = new byte[(int) file.length()];

                try {
                    InputStream input = new FileInputStream(file);
                    input.read(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                params.put("file", new DataPart(filename + ".mp4", bytes, "video/mp4"));
                return params;
            }
        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(context).add(volleyMultipartRequest);
    }

    public static void train(final Context context) {
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, getHost(context) + TRAIN_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(context, obj.get("result").toString() + ": " + obj.get("message").toString(), Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                return params;
            }
        };

        Volley.newRequestQueue(context).add(volleyMultipartRequest);
    }

    public static void recognize(final Context context, final Bitmap bitmap, final TextView responseMessageText) {
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, getHost(context) + RECOGNIZE_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));

                            StringBuffer info = new StringBuffer();
                            info.append("Result: ").append(obj.get("result")).append("\n");

                            if (obj.get("result").equals("success")) {
                                info.append("Name: ").append(obj.get("name"));
                            } else {
                                info.append("Message: ").append(obj.get("message"));
                            }

                            responseMessageText.setText(info.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long image_name = System.currentTimeMillis();
                params.put("file", new DataPart(image_name + ".jpg", ImageUtils.getFileDataFromDrawable(bitmap), "image/jpeg"));
                return params;
            }
        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(context).add(volleyMultipartRequest);
    }

}
