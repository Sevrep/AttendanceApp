package com.example.attendanceapp;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class SiteProxyEditActivity extends AppCompatActivity {

    RadioButton rb_ivanlaptophotspot, rb_ivanmobilehotspot, rb_rmcipayslip, rb_others;
    EditText et_ipaddress;
    Button bt_submit;
    TextView tv_details;
    String site_proxy_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_proxy_edit);

        rb_rmcipayslip = findViewById(R.id.rb_siteproxy_rmcipayslip);
        rb_rmcipayslip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rb_rmcipayslip.isChecked()) {
                    et_ipaddress.setEnabled(false);
                    bt_submit.setEnabled(true);
                }
            }
        });

        rb_others = findViewById(R.id.rb_siteproxy_others);
        rb_others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rb_others.isChecked()) {
                    et_ipaddress.setEnabled(true);
                    bt_submit.setEnabled(true);
                }
            }
        });

        et_ipaddress = findViewById(R.id.et_siteproxy_others);
        et_ipaddress.setEnabled(false);

        bt_submit = findViewById(R.id.bt_siteproxy_submit);
        bt_submit.setEnabled(false);

        SharedPrefHandler sharedPrefHandler = new SharedPrefHandler(this);

        tv_details = findViewById(R.id.tv_siteproxy_details);
        tv_details.setText(sharedPrefHandler.getSharedPref("site_proxy"));

    }

    public void submitipaddress(View v) {
        if (rb_rmcipayslip.isChecked()) {
            site_proxy_edit = "rmcipayslip.info";

        } else {
            site_proxy_edit = et_ipaddress.getText().toString().trim();
            if (TextUtils.isEmpty(site_proxy_edit)) {
                et_ipaddress.setError("Enter ip address...");
                return;
            }
        }

        SharedPrefHandler sharedPrefHandler = new SharedPrefHandler(this);

        sharedPrefHandler.removeSharedPref("site_proxy");
        sharedPrefHandler.removeSharedPref("root_url");
        sharedPrefHandler.removeSharedPref("url_timein");
        sharedPrefHandler.removeSharedPref("url_register_project_employees");
        sharedPrefHandler.removeSharedPref("url_login");
        sharedPrefHandler.removeSharedPref("url_getemployees");
        sharedPrefHandler.removeSharedPref("url_syncattendance");
        sharedPrefHandler.removeSharedPref("url_gettimecard");


        sharedPrefHandler.setSharedPref("site_proxy", site_proxy_edit);
        sharedPrefHandler.setSharedPref("root_url", "http://" + sharedPrefHandler.getSharedPref("site_proxy") + "/Thesis/v1/");
        sharedPrefHandler.setSharedPref("url_timein", sharedPrefHandler.getSharedPref("root_url") + "timeinEmployee.php");
        sharedPrefHandler.setSharedPref("url_register_project_employees", sharedPrefHandler.getSharedPref("root_url") + "registerProjectEmployees.php");
        sharedPrefHandler.setSharedPref("url_login", sharedPrefHandler.getSharedPref("root_url") + "officerLogin.php");
        sharedPrefHandler.setSharedPref("url_getemployees", sharedPrefHandler.getSharedPref("root_url") + "getSiteEmployees.php?projectsite=");
        sharedPrefHandler.setSharedPref("url_syncattendance", sharedPrefHandler.getSharedPref("root_url") + "syncAttendance.php");
        sharedPrefHandler.setSharedPref("url_gettimecard", sharedPrefHandler.getSharedPref("root_url") + "getEmployeeAttendance.php?empid=");

        tv_details.setText(sharedPrefHandler.getSharedPref("site_proxy"));
        /*Toast.makeText(getApplicationContext(),sharedPrefHandler.getSharedPref("site_proxy")+"\n"+
                sharedPrefHandler.getSharedPref("root_url")+"\n"+
                sharedPrefHandler.getSharedPref("url_timein")+"\n"+
                sharedPrefHandler.getSharedPref("url_register_project_employees")+"\n"+
                sharedPrefHandler.getSharedPref("url_login")+"\n"+
                sharedPrefHandler.getSharedPref("url_getemployees")+"\n"+
                sharedPrefHandler.getSharedPref("url_syncattendance"), Toast.LENGTH_LONG).show();*/

        //sharedPrefHandler.clear();
            /*
            Toast.makeText(getApplicationContext(),sharedPrefHandler.getSharedPref("site_proxy")+"\n"+
                    sharedPrefHandler.getSharedPref("root_url")+"\n"+
                    sharedPrefHandler.getSharedPref("url_timein")+"\n"+
                    sharedPrefHandler.getSharedPref("url_register_project_employees")+"\n"+
                    sharedPrefHandler.getSharedPref("url_login")+"clear", Toast.LENGTH_LONG).show();
            */

    }

    @Override
    public void onBackPressed() {
        Intent iMain = new Intent(SiteProxyEditActivity.this, MainActivity.class);
        startActivity(iMain);
        SiteProxyEditActivity.this.finish();

    }
}
