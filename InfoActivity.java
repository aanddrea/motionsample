package com.rexroth.eal.motionsample;

import android.content.Intent;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.boschrexroth.eal.EalConnection;
import com.boschrexroth.eal.EalException;
import com.boschrexroth.eal.EalServiceCommunicator;
import com.boschrexroth.eal.IEalServiceListener;
import com.boschrexroth.methods.eal.IEalMethods;

import java.util.TimerTask;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener {
    String ipAddress;
    TextView name,firmware,apptype,hardware;
    Button btnback;
    EalServiceCommunicator communicator;
    int slaveIndex=0;
    EalConnection connection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Bundle extras= getIntent().getExtras();
        String getip=extras.getString("ipaddress");
        btnback = (Button)findViewById(R.id.btn_back2);
        btnback.setOnClickListener(this);

        name=(TextView)findViewById(R.id.lbl_name);
        firmware = (TextView)findViewById(R.id.lbl_firmware);
        apptype  = (TextView)findViewById(R.id.lbl_apptype);
        //hardware =(TextView)findViewById(R.id.lbl_hardware);
        if(extras!=null) {
            ipAddress = getip;
            try {
                communicator = new EalServiceCommunicator();
                communicator.initConnection(getBaseContext(), new IEalServiceListener() {
                    @Override
                    public void serviceConnected(IEalMethods iEalMethods) {
                        try {
                            connection = new EalConnection(getBaseContext(), iEalMethods);
                            connection.connect(ipAddress);
                            connection.getAxes(slaveIndex).motion().movement().power(true);
                            name.setText(name.getText().subSequence(0,13) +" "+ connection.getAxes(slaveIndex).motion().getName());
                            firmware.setText(firmware.getText().subSequence(0,9) +" "+ connection.getAxes(slaveIndex).motion().getFirmware());
                            apptype.setText(apptype.getText().subSequence(0,5)+" " + connection.getAxes(slaveIndex).motion().getDiagnosisText());
                            //hardware.setText(hardware.getText().subSequence(0,10)+" "+ connection.getAxes(slaveIndex).system().getSerialNumber());
                        } catch (EalException e) {
                            e.printStackTrace();
                            //showErrorDialog(e.getMessage());
                        } catch (RemoteException e) {
                            e.printStackTrace();
                            //showErrorDialog(e.getMessage());
                        }
                    }

                    /* This method is called when the disconnected from service*/
                    @Override
                    public void serviceDisconnected() {

                    }
                });
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v.equals(btnback)){
            try {
                connection.disconnect();
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }

            Intent it=new Intent(this,MainActivity.class);
            Bundle extras = new Bundle();
            extras.putString("ipaddress", ipAddress);
            it.putExtras(extras);
            startActivityForResult(it,0);
        }
    }
}
