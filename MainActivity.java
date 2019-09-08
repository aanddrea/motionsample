/*
 * Author bns7kor
 * This is the MainActivity or first activity to be launched when the app is opened
 */

package com.rexroth.eal.motionsample;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.boschrexroth.eal.AxisCondition;
import com.boschrexroth.eal.AxisRef;

import com.boschrexroth.eal.CallBackType;
import com.boschrexroth.eal.DriveState;
import com.boschrexroth.eal.EalConnection;
import com.boschrexroth.eal.EalException;
import com.boschrexroth.eal.IDownloadListener;
import com.boschrexroth.eal.IEalServiceListener;
import com.boschrexroth.eal.Parameter;
import com.boschrexroth.eal.ParameterListLenght;
import com.boschrexroth.eal.EalServiceCommunicator;
import com.boschrexroth.eal.ParameterSelection;
import com.boschrexroth.methods.eal.IEalMethods;
import java.util.Timer;
import java.util.TimerTask;
import java.math.BigDecimal;
import java.math.RoundingMode;


public class MainActivity extends Activity implements View.OnClickListener {

    TextView lblMessage, lblvelocity, lblposition, lblname;
    Button btnDisconnect, btnOn, btnOff, btnMovev,btnStop, btnClr, btnJogp, btnJogm, btnMovea,btnpara, btninfo;// btnSetPos, btnIni;
    ImageView on, off;
    String ipAddress;

    EditText txtIpAddress, inpvelocity, inpacceleration, inpdeceleration;

    EalConnection connection;
    private Handler handler1= new Handler();
    private Timer timer= new Timer();
    EalServiceCommunicator communicator;

    int slaveIndex;
    double velocity, acceleraton, deceleration;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle extras = getIntent().getExtras();
        String getip = extras.getString("ipaddress");
        if (extras!=null){
            ipAddress = getip;
            try {
                communicator=new EalServiceCommunicator();
                communicator.initConnection(getBaseContext(), new IEalServiceListener() {
                    @Override
                    public void serviceConnected(IEalMethods iEalMethods) {
                        try {
                            connection = new EalConnection(getBaseContext(), iEalMethods);
                            connection.connect(ipAddress);
                            btnDisconnect.setEnabled(true);
                            lblMessage.setText("connected");
                            lblname.setText(lblname.getText().subSequence(0,5) + " " + connection.getAxes(slaveIndex).motion().getName());
                        } catch (EalException e) {
                            e.printStackTrace();
                            showErrorDialog(e.getMessage());
                        } catch (RemoteException e) {
                            e.printStackTrace();
                            showErrorDialog(e.getMessage());
                        }
                    }
                    /* This method is called when the disconnected from service*/
                    @Override
                    public void serviceDisconnected() {

                    }
                });
            }
            catch (Exception e)
            {
            }
        }
       // txtIpAddress=(EditText)findViewById(R.id.txt_ip_address);

        inpvelocity = (EditText) findViewById(R.id.inp_vel);

        inpvelocity.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {
                    inpvelocity.setText("");
                }

                return false;
            }
        });

        inpacceleration = (EditText) findViewById(R.id.inp_acc);
        inpacceleration.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {
                    inpacceleration.setText("");
                }
                return false;
            }
        });

//        lblStatus=(TextView)findViewById(R.id.lbl_status);
//        lblStatus.setText("status");

        lblMessage=(TextView)findViewById(R.id.lbl_message);
        lblMessage.setText("");

        lblvelocity = (TextView)findViewById(R.id.txt_avel);
        lblvelocity.setText("");

        lblname = (TextView)findViewById(R.id.lbl_name);

        lblposition = (TextView) findViewById(R.id.txt_apos) ;
        lblposition.setText("");

        btnDisconnect=(Button)findViewById(R.id.btn_disconnect);
        btnDisconnect.setOnClickListener(this);
//
//        btnConnect=(Button)findViewById(R.id.btn_connect);
//        btnConnect.setOnClickListener(this);

        btnMovea = (Button)findViewById(R.id.btn_movea);
        btnMovea.setOnClickListener(this);

        btnOn = (Button) findViewById(R.id.btn_on);
        btnOn.setOnClickListener(this);

        btnpara = (Button) findViewById(R.id.btn_parameters);
        btnpara.setOnClickListener(this);

        btnOff = (Button) findViewById(R.id.btn_off);
        btnOff.setOnClickListener(this);

//        on = (ImageView)findViewById(R.id.img_green);
//        off =(ImageView) findViewById(R.id.img_red);

//        btnSetPos =(Button) findViewById(R.id.btn_setpos);
//        btnSetPos.setOnClickListener(this);

        btnMovev = (Button) findViewById(R.id.btn_movev);
        btnMovev.setOnClickListener(this);

        btnStop = (Button) findViewById(R.id.btn_stop) ;
        btnStop.setOnClickListener(this);

        btnClr = (Button) findViewById(R.id.btn_clr);
        btnClr.setOnClickListener(this);

        btninfo = (Button)findViewById(R.id.btn_info);
        btninfo.setOnClickListener(this);

//        btnIni = (Button)findViewById(R.id.btn_ini);
//        btnIni.setOnClickListener(this);

        btnJogp = (Button) findViewById(R.id.btn_jogp);

        btnJogm = (Button) findViewById(R.id.btn_jogm);

        btnDisconnect.setEnabled(true);
        on =(ImageView)findViewById(R.id.img_on);
        //on.setVisibility(on.INVISIBLE);

        off =(ImageView)findViewById(R.id.img_off);
        //off.setVisibility(off.INVISIBLE);
        setActivityBackgroundColor(0xc4fffa);

        btnJogp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {
                    try {
                        String vel= inpvelocity.getText().toString();
                        String accel = inpacceleration.getText().toString();
                        if (vel.equals("") || vel.equals("velocity")){
                            vel ="100.0";
                        }
                        if (accel.equals("") || accel.equals("acceleration")){
                            accel ="300.0";
                        }
                        velocity = Double.parseDouble(vel);
                        acceleraton = Double.parseDouble(accel);
                        deceleration = 0.0;
                        connection.getAxes(slaveIndex).motion().movement().moveVelocity(velocity, acceleraton, deceleration, 0);


                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), "" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    //lblMessage.setText("let go");
                    try {
                        velocity = 0;
                        acceleraton = 0.0;
                        deceleration = 0.0;

                        connection.getAxes(slaveIndex).motion().movement().moveVelocity(velocity, acceleraton, deceleration, 0);
//

                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), "" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                return false;
            }
        });
        btnJogm.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {
                    try {
                        String vel= inpvelocity.getText().toString();
                        String accel = inpacceleration.getText().toString();
                        if (vel.equals("") || vel.equals("velocity")){
                            vel ="100.0";
                        }
                        if (accel.equals("") || accel.equals("acceleration")){
                            accel ="300.0";
                        }
                        if (Double.parseDouble(vel) > 0){
                            velocity = Double.parseDouble(vel) *-1;
                        }
                        else{
                            velocity = Double.parseDouble(vel);
                        }
                        acceleraton = Double.parseDouble(accel);
                        deceleration = 0.0;
                        connection.getAxes(slaveIndex).motion().movement().moveVelocity(velocity, acceleraton, deceleration, 0);
                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), "" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    try {
                        velocity = 0;
                        acceleraton = 0.0;
                        deceleration = 0.0;
                        connection.getAxes(slaveIndex).motion().movement().moveVelocity(velocity, acceleraton, deceleration, 0);

                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), "" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                return false;
            }
        });
    }
    public void showPics(){

    }

    public void disconnectDialog()
    {
        //Alert box for user confirmation before disconnecting
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Are you Sure To Close?");
        builder.setTitle("Efc Demo App");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    connection.disconnect();
                    communicator.closeConnection();

                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), "" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    finish();
                }
                finish();
            }

        });
        builder.setNegativeButton("No",null);
        builder.create().show();
    }
    public void setActivityBackgroundColor(int color) {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(color);
    }
	//Connection is closed when back button is pressed
    @Override
    public void onBackPressed()
    {
        try
        {
            boolean connected=false;
            if(connection!=null) {
                if (connection.isConnected()) {
                    connection.disconnect();
                    communicator.closeConnection();
                }
                else
                {
                    if(communicator!=null)
                    {
                        communicator.closeConnection();
                    }
                }

            }

        } catch (EalException e) {
            e.printStackTrace();
            showErrorDialog(e.getMessage());
        } catch (RemoteException e) {
            e.printStackTrace();
            showErrorDialog(e.getMessage());
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

		//Populating the error dialog box
    public void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle("Error!!!");
        builder.setPositiveButton("Ok",null);
        builder.create().show();
    }
    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    public void updatePosandVel(){
        try {
            lblvelocity.setText(Double.toString(round(connection.getAxes(slaveIndex).motion().getActualVelocity(), 2)));
            lblposition.setText(Double.toString(round(connection.getAxes(slaveIndex).motion().getActualPosition(),2)));
        } catch (EalException e) {
            e.printStackTrace();
            lblMessage.setText("" + e.getLocalizedMessage());
        } catch (RemoteException e) {
            e.printStackTrace();
            lblMessage.setText("" + e.getLocalizedMessage());
        }
    }

    @Override
    public void onClick(View v) {
        slaveIndex =0;
        //if(v.equals(btnConnect))
        //{
//            ipAddress=txtIpAddress.getText().toString();
//
//            try {
//                communicator=new EalServiceCommunicator();
//                communicator.initConnection(getBaseContext(), new IEalServiceListener() {
//                    @Override
//                    public void serviceConnected(IEalMethods iEalMethods) {
//
//
//                        try {
//                            connection = new EalConnection(getBaseContext(), iEalMethods);
//                            connection.connect(ipAddress);
//                            btnDisconnect.setEnabled(true);
//                            btnConnect.setEnabled(false);
//                            lblMessage.setText("connected");
//                            lblname.setText(lblname.getText().subSequence(0,5) + " " + connection.getAxes(slaveIndex).motion().getName());
//
//                        } catch (EalException e) {
//                            e.printStackTrace();
//                            showErrorDialog(e.getMessage());
//                        } catch (RemoteException e) {
//                            e.printStackTrace();
//                            showErrorDialog(e.getMessage());
//                        }
//                    }
//
//                    /* This method is called when the disconnected from service*/
//                    @Override
//                    public void serviceDisconnected() {
//
//                    }
//                });
//            }
//            catch (Exception e)
//            {
//            }

      //  }
		//Enters the block if user presses "Connect" button in the activity
        if(v.equals(btnDisconnect))
        {
           //disconnectDialog();
            try {
                connection.disconnect();
                communicator.closeConnection();

            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
            finish();
            Intent it = new Intent(this,Main2Activity.class);
            Bundle extras = new Bundle();
            extras.putString("ipaddress", ipAddress);
            it.putExtras(extras);
            startActivityForResult(it,0);

//            Intent it = new Intent(this,Main2Activity.class);
//            Bundle extras = new Bundle();
//            extras.putString("ipaddress", ipAddress);
//            it.putExtras(extras);
//            startActivityForResult(it,0);
//            try {
//                connection.getAxes(slaveIndex).motion().movement().power(false);
//                connection.disconnect();
//                lblMessage.setText("");
//                Intent it= new Intent(this,Main2Activity.class);
//                Bundle extras = new Bundle();
//                //it.putExtra("address",ipAddress);
//                extras.putString("ipaddress", ipAddress);
//                it.putExtras(extras);
//                startActivityForResult(it,0);
//
//               // btnDisconnect.setEnabled(false);
//                //btnConnect.setEnabled(true);
////                btnExecute.setEnabled(false);
//            } catch (EalException e) {
//
//                e.printStackTrace();
//                showErrorDialog(e.getMessage());
//            } catch (RemoteException e) {
//
//                e.printStackTrace();
//                showErrorDialog(e.getMessage());
//            }
        }
        else if(v.equals(btnOn))
        {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler1.post(new Runnable() {
                        @Override
                        public void run() {
                            updatePosandVel();
                        }
                    });
                }
            },0,500);
            try {
                connection.getAxes(slaveIndex).motion().movement().power(true);
                lblMessage.setText("Powered On " + connection.getAxes(slaveIndex).motion().getCondition());
                lblvelocity.setText(Double.toString(connection.getAxes(slaveIndex).motion().getActualVelocity()));
                lblposition.setText(Double.toString( connection.getAxes(slaveIndex).motion().getActualPosition()));
                on.setVisibility(on.VISIBLE);
                off.setVisibility(off.INVISIBLE);
            } catch (EalException e) {
                e.printStackTrace();
                lblMessage.setText("" + e.getLocalizedMessage());
            } catch (RemoteException e) {
                e.printStackTrace();
                lblMessage.setText("" + e.getLocalizedMessage());
            }

        }
        else if(v.equals(btnOff)){
            try {

                connection.getAxes(slaveIndex).motion().movement().power(false);
                on.setVisibility(on.INVISIBLE);
                off.setVisibility(off.VISIBLE);
                //updateDriveState();
                lblMessage.setText("Powered Off ");
            } catch (EalException e) {
                e.printStackTrace();
                lblMessage.setText("" + e.getLocalizedMessage());
            } catch (RemoteException e) {
                e.printStackTrace();
                lblMessage.setText("" + e.getLocalizedMessage());
            }
        }
        else if(v.equals(btnMovev)){
            //moveVelocityDialog();
            try {
                String vel= inpvelocity.getText().toString();
                String accel = inpacceleration.getText().toString();
                if (vel.equals("") || vel.equals("velocity")){
                    vel ="100.0";
                }
                if (accel.equals("") || accel.equals("acceleration")){
                    accel ="300.0";
                }
                velocity = Double.parseDouble(vel);
                acceleraton = Double.parseDouble(accel);
                deceleration = 0.0;
                connection.getAxes(slaveIndex).motion().movement().moveVelocity(velocity, acceleraton, deceleration, 0);

            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }
        else if(v.equals(btnStop)){

            try {
                velocity = 0;
                acceleraton = 0;
                deceleration = 0;
                connection.getAxes(slaveIndex).motion().movement().moveVelocity(velocity, acceleraton, deceleration, 0);
                lblposition.setText(Double.toString( connection.getAxes(slaveIndex).motion().getActualPosition()));
                lblvelocity.setText(Double.toString(connection.getAxes(slaveIndex).motion().getActualVelocity()));
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }
        else if(v.equals(btnClr)){
            try {
                connection.getAxes(slaveIndex).motion().clearError();
                //updateError();
                lblMessage.setText("Error Cleared..");
            } catch (EalException e) {
                e.printStackTrace();
                lblMessage.setText("" + e.getLocalizedMessage());
            } catch (RemoteException e) {
                e.printStackTrace();
                lblMessage.setText("" + e.getLocalizedMessage());
            }
        }
        else if(v.equals(btnMovea)){
            try {

                connection.disconnect();
                Intent it=new Intent(this,Main3ActivityMv.class);
                Bundle extras = new Bundle();
                extras.putString("ipaddress", ipAddress);
                it.putExtras(extras);
                startActivityForResult(it,0);

//                connection.getAxes(slaveIndex).motion().setCondition(AxisCondition.AXIS_CONDITION_ACTIVE_PARAMETERIZATION);
//                connection.getAxes(slaveIndex).motion().setCondition(AxisCondition.AXIS_CONDITION_ACTIVE);
//                connection.getAxes(slaveIndex).motion().movement().home();
               // connection.getAxes(slaveIndex).motion().movement().moveAbsolute(90, 400, 100, 100, 0);

            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }

        }
        else if(v.equals(btninfo)){
            try {

                connection.disconnect();
                Intent it=new Intent(this,InfoActivity.class);
                Bundle extras = new Bundle();
                extras.putString("ipaddress", ipAddress);
                it.putExtras(extras);
                startActivityForResult(it,0);


            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }


        }
        else if(v.equals(btnpara)){
            try {
                Parameter parameter=connection.getAxes(0).parameter();
                String filePath = "D:\\Default3.par";
                parameter.saveParameters(filePath,ParameterSelection.All, true,new IDownloadListener() { public void onComplete(CallBackType callBackType, long l) {
                }
                    public void onError(CallBackType callBackType, EalException e) {

                    }

                    public void onProgress(CallBackType callBackType, long l, long l1) {

                    } });

            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }

        }
//        else if(v.equals(btnSetPos)){
//                try {
//                    connection.getAxes(slaveIndex).motion().movement().setAbsoluteMeasurement();
//
//                } catch (Exception e) {
//                    Toast.makeText(getBaseContext(), "" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//                }
//
//        }
//        else if(v.equals(btnIni)){
//            try {
//                connection.initialize();
//                lblMessage.setText("Drive Initialized");
//            } catch (EalException e) {
//                e.printStackTrace();
//                lblMessage.setText("" + e.getLocalizedMessage());
//            } catch (RemoteException e) {
//                lblMessage.setText(""+e.getLocalizedMessage());
//                e.printStackTrace();
//            }
        }
//        else if(v.equals(btnExecute))
//        {
//            new MotionTask().execute(connection);
//        }

    //}

}
