package com.stonecode.elektro;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vishal on 15-Oct-16.
 */

public class ServerRequests {

    Context ctx;
    private final String HOST = "https://app-elektro.herokuapp.com";
    private final String LOGIN = HOST + "/login";
    private final String FRIEND_SONGS = HOST + "/friend-songs/";
    private final String MY_FRIENDS = HOST + "/my-friends/";
    private final String NEW_SONG = HOST + "/new-song";
    private final String NEW_FRIEND = HOST + "/new-friend";
    private final String NEW_USER = HOST + "/register";
    private static final String TAG = "ServerRequests";

    private ProgressDialog pd;

    ServerRequests(Context ctx) {
        this.ctx = ctx;
        pd=new ProgressDialog(ctx,ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        pd.setTitle("Crunching database....");
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Please wait...");
        pd.setIndeterminate(true);
//        pd.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
    }

    void newUser(final String name, final int age, final String email, final String pass, final RequestCallback callback) {
        StringRequest req = new StringRequest(
                Request.Method.POST,
                NEW_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d(TAG, "onResponse: " + response);
                            JSONObject jsonObject = new JSONObject(response);
                            if (callback != null) callback.response(jsonObject);
                            pd.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error.getMessage());
                        if (callback != null) callback.response(null);
                        pd.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("name", name);
                map.put("email", email);
                map.put("age", String.valueOf(age));
                map.put("pass", pass);
                return map;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };
        Volley.newRequestQueue(ctx).add(req);
        pd.show();
    }

    void newFriend(final int myID, final int fID,final RequestCallback callback) {
        StringRequest req = new StringRequest(
                Request.Method.POST,
                NEW_FRIEND,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d(TAG, "onResponse: " + response);
                            JSONObject jo=new JSONObject(response);
                            if (callback != null) callback.response(jo);
                            pd.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (callback != null) callback.response(null);
                        Log.d(TAG, "onErrorResponse: " + error.getMessage());
                        pd.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("u_id", String.valueOf(myID));
                map.put("f_id", String.valueOf(fID));
                return map;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };
        pd.show();
        Volley.newRequestQueue(ctx).add(req);
    }

    void newSong(final int myID, final String song,final RequestCallback callback) {
        StringRequest req = new StringRequest(
                Request.Method.POST,
                NEW_SONG,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d(TAG, "onResponse: " + response);
                            JSONObject jo=new JSONObject(response);
                            if (callback != null) callback.response(jo);
                            pd.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (callback != null) callback.response(null);
                        Log.d(TAG, "onErrorResponse: " + error.getMessage());
                        pd.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("u_id", String.valueOf(myID));
                map.put("song_name", song);
                return map;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };
        pd.show();
        Volley.newRequestQueue(ctx).add(req);
    }

    void login(final int myID, final String pass,final RequestCallback callback) {
        StringRequest req = new StringRequest(
                Request.Method.POST,
                LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d(TAG, "onResponse: " + response);
                            JSONObject jo=new JSONObject(response);
                            if (callback != null) callback.response(jo);
                            pd.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (callback != null) callback.response(null);
                        Log.d(TAG, "onErrorResponse: " + error.getMessage());
                        pd.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("u_id", String.valueOf(myID));
                map.put("pass", pass);
                return map;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };
        pd.show();
        Volley.newRequestQueue(ctx).add(req);
    }

    void getMyFriends(int myID,final RequestCallback callback) {
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                MY_FRIENDS + myID,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (callback != null) callback.response(response);
                        pd.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (callback != null) callback.response(null);
                        pd.dismiss();
                    }
                });
        pd.show();
        Volley.newRequestQueue(ctx).add(req);
    }

    void getFriendSongs(int fID,final RequestCallback callback) {
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                FRIEND_SONGS + fID,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pd.dismiss();
                        if (callback != null) callback.response(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.dismiss();
                        if (callback != null) callback.response(null);
                    }
                });
        Volley.newRequestQueue(ctx).add(req);
        pd.show();
    }

    interface RequestCallback {
        void response(Object data);
    }
}
