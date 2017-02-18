package com.stonecode.elektro;

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

    private RequestCallback callback;
    Context ctx;
    private final String HOST = "localhost";
    private final String LOGIN = "http://" + HOST + "/Elektro/v1/login";
    private final String FRIEND_SONGS = "http://" + HOST + "/Elektro/v1/friend-songs/";
    private final String MY_FRIENDS = "http://" + HOST + "/Elektro/v1/my-friends/";
    private final String NEW_SONG = "http://" + HOST + "/Elektro/v1/new-song";
    private final String NEW_FRIEND = "http://" + HOST + "/Elektro/v1/new-friend";
    private final String NEW_USER = "http://" + HOST + "/Elektro/v1/register";
    private static final String TAG = "ServerRequests";


    public ServerRequests(Context ctx) {
        this.ctx = ctx;
    }

    void setCallback(RequestCallback callback) {
        this.callback = callback;
    }

    void newUser(final String name, final int age, final String email, final String pass) {
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
    }

    void newFriend(final int myID, final int fID) {
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
        Volley.newRequestQueue(ctx).add(req);
    }

    void newSong(final int myID, final String song) {
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
        Volley.newRequestQueue(ctx).add(req);
    }

    void login(final int myID, final String pass) {
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
        Volley.newRequestQueue(ctx).add(req);
    }

    void getMyFriends(int myID) {
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                MY_FRIENDS + myID,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (callback != null) callback.response(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (callback != null) callback.response(null);
                    }
                });
        Volley.newRequestQueue(ctx).add(req);
    }

    void getFriendSongs(int fID) {
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                FRIEND_SONGS + fID,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (callback != null) callback.response(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (callback != null) callback.response(null);
                    }
                });
        Volley.newRequestQueue(ctx).add(req);
    }

    interface RequestCallback {
        void response(Object data);
    }
}
