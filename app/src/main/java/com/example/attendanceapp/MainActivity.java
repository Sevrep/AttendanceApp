package com.example.attendanceapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ImageView iv_logo;
    Button bt_login;
    EditText et_user, et_pass;
    int securityCheckCtr = 3;
    String adminUser = "admin", adminPass = "admin";
    String User = "user";
    String Pass = "pass";

    SharedPrefHandler sharedPrefHandler = new SharedPrefHandler(this);

    RelativeLayout rellay1;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            rellay1.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String isProjectsiteIn = sharedPrefHandler.getSharedPref("projectsite");

        if (isProjectsiteIn == null) {
            initSplashLoginScreen();
        } else {
            goToRecyclerView();
        }

    }

    public void login(View v) {
        User = et_user.getText().toString().trim();
        Pass = et_pass.getText().toString().trim();

        if (TextUtils.isEmpty(User)) {
            et_user.setError("Enter username.");
            return;
        }
        if (TextUtils.isEmpty(Pass)) {
            et_pass.setError("Enter password.");
            return;
        }

        if ((!User.equals(adminUser)) && (!Pass.equals(adminPass))) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, sharedPrefHandler.getSharedPref("url_login"), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    onlineLogin(response);
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    offlineLogin();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("projectsite", User);
                    params.put("password", Pass);
                    return params;
                }
            };
            RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(getApplicationContext(), "Welcome executor!", Toast.LENGTH_LONG).show();
            Intent iSiteProxyEdit = new Intent(this, SiteProxyEditActivity.class);
            startActivity(iSiteProxyEdit);
            this.finish();
        }

    }

    public void initSplashLoginScreen() {
        rellay1 = findViewById(R.id.rellay1);
        handler.postDelayed(runnable, 2000);

        iv_logo = findViewById(R.id.iv_main_logo);
        Animation myAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_animation);
        iv_logo.startAnimation(myAnimation);

        et_user = findViewById(R.id.et_main_username);
        et_pass = findViewById(R.id.et_main_password);
        bt_login = findViewById(R.id.bt_main_login);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void goToRecyclerView() {
        Intent iViewRecycler = new Intent(MainActivity.this, RecyclerviewEmployee.class);
        startActivity(iViewRecycler);
        MainActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                }).create().show();
    }

    public void onlineLogin(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            if (!obj.getBoolean("error")) {
                sharedPrefHandler.setSharedPref("projectsite", obj.getString("projectsite"));
                sharedPrefHandler.setSharedPref("manager", obj.getString("manager"));
                Toast.makeText(getApplicationContext(), "Welcome " + sharedPrefHandler.getSharedPref("manager") + "!", Toast.LENGTH_LONG).show();
                goToRecyclerView();
            } else {
                if (securityCheckCtr > 0) {
                    if (securityCheckCtr == 1) {
                        Toast.makeText(getApplicationContext(), obj.getString("message") + "\n" + "You have " + securityCheckCtr + " try left before lockout!", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(getApplicationContext(), obj.getString("message") + "\n" + "You have " + securityCheckCtr + " tries left before lockout!", Toast.LENGTH_SHORT).show();
                } else {
                    et_user.setEnabled(false);
                    et_pass.setEnabled(false);
                    bt_login.setEnabled(false);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Account locked!");
                    builder.setCancelable(false);
                    final AlertDialog alert = builder.create();
                    alert.show();
                    new CountDownTimer(5000, 1000) {
                        @Override
                        public void onTick(long l) {
                            Toast.makeText(getApplicationContext(), "Please wait: " + ((l / 1000) + 1) + " seconds.", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFinish() {
                            alert.cancel();
                            et_user.setEnabled(true);
                            et_pass.setEnabled(true);
                            bt_login.setEnabled(true);
                        }
                    }.start();
                    securityCheckCtr = 4;
                }
                securityCheckCtr--;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void offlineLogin() {
        Toast.makeText(getApplicationContext(), "Offline. Using previous credentials.", Toast.LENGTH_LONG).show();
        if ((User.equals(sharedPrefHandler.getSharedPref("projectsite"))) &&
                (Pass.equals(sharedPrefHandler.getSharedPref("password")))) {
            Toast.makeText(getApplicationContext(), "Welcome " + sharedPrefHandler.getSharedPref("projectsite") + "!", Toast.LENGTH_LONG).show();
            goToRecyclerView();
        } else {
            if (securityCheckCtr > 0) {
                if (securityCheckCtr == 1) {
                    Toast.makeText(getApplicationContext(), "Error. \n" + "You have " + securityCheckCtr + " try left before lockout!", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(getApplicationContext(), "Error. \n" + "You have " + securityCheckCtr + " tries left before lockout!", Toast.LENGTH_SHORT).show();
            } else {
                et_user.setEnabled(false);
                et_pass.setEnabled(false);
                bt_login.setEnabled(false);
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Account locked!");
                builder.setCancelable(false);
                final AlertDialog alert = builder.create();
                alert.show();
                new CountDownTimer(5000, 1000) {
                    @Override
                    public void onTick(long l) {
                        Toast.makeText(getApplicationContext(), "Please wait: " + ((l / 1000) + 1) + " seconds.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFinish() {
                        alert.cancel();
                        et_user.setEnabled(true);
                        et_pass.setEnabled(true);
                        bt_login.setEnabled(true);
                    }
                }.start();
                securityCheckCtr = 4;
            }
            securityCheckCtr--;
        }
    }

}
