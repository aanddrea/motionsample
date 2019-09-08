package com.rexroth.eal.motionsample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.os.RemoteException;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.boschrexroth.eal.EalConnection;
import com.boschrexroth.eal.EalException;
import com.boschrexroth.eal.IEalServiceListener;
import com.boschrexroth.eal.ParameterListLenght;
import com.boschrexroth.eal.EalServiceCommunicator;
import com.boschrexroth.methods.eal.IEalMethods;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener,IEalServiceListener {
    Button btnConnect;
    String ipAddress;
    EditText txtIpAddress;
    protected IEalMethods methods;
    EalServiceCommunicator serviceComm;
    TextView test;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
       // test = (TextView)findViewById(R.id.lbl);
        txtIpAddress=(EditText)findViewById(R.id.txt_ip_address);

        String address= PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("address",null);

        if(address!=null)
        {
            txtIpAddress.setText(address);
        }

        btnConnect = (Button)findViewById(R.id.btn_connect);
        btnConnect.setOnClickListener(this);

    }
    protected void onDestroy() {
        super.onDestroy();
    };
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //When user clicks connect button
            case R.id.btn_connect:
                //get IP Address from text field
                ipAddress = txtIpAddress.getText().toString();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                //Putting IP address in Shared preference
                editor.putString("address",ipAddress);
                editor.commit();

                serviceComm=new EalServiceCommunicator();
                try {
                    serviceComm.initConnection(getBaseContext(), this);
                    //test.setText("!!!");
                } catch (EalException e) {
                    e.printStackTrace();
                }
            }
        }
    @Override
    public void serviceConnected(IEalMethods iEalMethods) {
        try {
            Toast.makeText(getBaseContext(),"EAL Service Connected",Toast.LENGTH_LONG).show();

            EalConnection con = new EalConnection(getBaseContext(), iEalMethods);
            con.connect(ipAddress);
            con.disconnect();
            serviceComm.closeConnection();

            Intent it = new Intent(this,MainActivity.class);
            Bundle extras = new Bundle();
            extras.putString("ipaddress", ipAddress);
            it.putExtras(extras);
            startActivityForResult(it,0);
        }
        catch(EalException e)
        {
            Toast.makeText(getBaseContext(),"Error:"+e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }
        catch(RemoteException e)
        {
            Toast.makeText(getBaseContext(),"Error:"+e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void serviceDisconnected() {
    }



}
