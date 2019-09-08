package com.rexroth.eal.motionsample;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.boschrexroth.eal.EalConnection;
import com.boschrexroth.eal.EalException;
import com.boschrexroth.eal.EalServiceCommunicator;
import com.boschrexroth.eal.IEalServiceListener;
import com.boschrexroth.methods.eal.IEalMethods;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Timer;
import java.util.TimerTask;

public class Main3ActivityMv extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    ImageView pic;
    Button back,move,inizialize,set;
    Integer x1;
    private Handler handler1= new Handler();
    private Timer timer= new Timer();
    int slaveIndex=0;
    EalConnection connection;
    TextView lblvelocity, lblposition, lblpos;
    String ipAddress;
    EalServiceCommunicator communicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3_mv);
        Bundle extras= getIntent().getExtras();
        String getip=extras.getString("ipaddress");
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
            } catch (Exception e) {
            }
        }
        //connection.getAxes(slaveIndex).motion().movement().power(false);

        back = (Button)findViewById(R.id.btn_back);
        back.setOnClickListener(this);
        move =(Button)findViewById(R.id.btn_move);
        move.setOnClickListener(this);
        set = (Button)findViewById(R.id.btn_setpos) ;
        set.setOnClickListener(this);
        inizialize=(Button)findViewById(R.id.btn_ini);
        inizialize.setOnClickListener(this);
        pic = (ImageView)findViewById(R.id.img_pic);
        pic.setOnTouchListener(this);
        lblpos = (TextView)findViewById(R.id.txt_pos);
        lblposition = (TextView)findViewById(R.id.txt_pos3);
        lblvelocity = (TextView)findViewById(R.id.txt_vel3);

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
            //lblMessage.setText("" + e.getLocalizedMessage());
        } catch (RemoteException e) {
            e.printStackTrace();
            //lblMessage.setText("" + e.getLocalizedMessage());
        }
    }

    @Override
    public void onClick(View v) {
        if(v.equals(back)){
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
        else if(v.equals(move)){
            try {
                connection.getAxes(slaveIndex).motion().movement().moveAbsolute(pic.getRotation(), 400, 100, 100, 0);
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }
        else if(v.equals(set)){
            try {
                connection.getAxes(slaveIndex).motion().movement().setAbsoluteMeasurement();

            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }
        else if(v.equals(inizialize)){
            try {
                connection.initialize();
                //lblMessage.setText("Drive Initialized");
            } catch (EalException e) {
                e.printStackTrace();
                //lblMessage.setText("" + e.getLocalizedMessage());
            } catch (RemoteException e) {
                //lblMessage.setText(""+e.getLocalizedMessage());
                e.printStackTrace();
            }
        }

    }
    public void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle("Error!!!");
        builder.setPositiveButton("Ok",null);
        builder.create().show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.equals(pic)) {
            int x = (int) event.getX();
            if(event.getAction()==MotionEvent.ACTION_DOWN){
                x1 = (int) event.getX();
            }
            if(event.getAction() ==MotionEvent.ACTION_MOVE){

                if(x1<x){
                    pic.setRotation(pic.getRotation()+ 1);

                }
                else{
                    pic.setRotation(pic.getRotation()- 1);
                }
//                if(pic.getRotation()>360){
//                    pic.setRotation(0);
//                }
                lblpos.setText(lblpos.getText().subSequence(0,15)+" "+ Float.toString(pic.getRotation()));
                // break;

            }
//            if(event.getAction()==MotionEvent.ACTION_UP){
//                try {
//                    //connection.disconnect();
//
////                    Intent it=new Intent(this,Main3ActivityMv.class);
////                    Bundle extras = new Bundle();
////                    extras.putString("ipaddress", ipAddress);
////                    it.putExtras(extras);
////                    startActivityForResult(it,0);
//
////                connection.getAxes(slaveIndex).motion().setCondition(AxisCondition.AXIS_CONDITION_ACTIVE_PARAMETERIZATION);
////                connection.getAxes(slaveIndex).motion().setCondition(AxisCondition.AXIS_CONDITION_ACTIVE);
////                connection.getAxes(slaveIndex).motion().movement().home();
//             //   connection.getAxes(slaveIndex).motion().movement().moveAbsolute(pic.getRotation(), 400, 100, 100, 0);
//
//                } catch (Exception e) {
//                    Toast.makeText(getBaseContext(), "" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//                }
//
//            }
        }
        return true;    }
}
