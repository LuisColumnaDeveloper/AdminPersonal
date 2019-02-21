package com.example.luis.adminpersonal.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.luis.adminpersonal.Login;
import com.example.luis.adminpersonal.R;
import com.example.luis.adminpersonal.Utils.ErrorResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.bingoogolapple.qrcode.core.BarcodeType;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zbar.ZBarView;

import static android.content.Context.VIBRATOR_SERVICE;

public class FragmentScan extends Fragment implements QRCodeView.Delegate  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = FragmentScan.class.getSimpleName();
//    String url = "http://192.168.1.66/adminPersonal/api/personal/";

    /**************SERVER****************/
    String url = "http://142.93.194.178/adminPersonal/api/personal/";
    //private ZBarView mZBarView;
    ZBarView mZBarView;
    Toolbar toolbar;
    ActionBar actionBar;
    RequestQueue queue;
    Context mContext;

    SharedPreferences prefs;
    String TOKEN,ID_EMPRESA;

    ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_scan, container, false);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        mContext = container.getContext();

        ////ORIENTATION
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        prefs = mContext.getSharedPreferences("PreferenciasAdminPer", Context.MODE_PRIVATE);
        TOKEN=prefs.getString("token", "Default");
        ID_EMPRESA=prefs.getString("idEmpresa", "0");

        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("Scan");

        mZBarView =(ZBarView)view.findViewById(R.id.zbarview);
        mZBarView.setDelegate(this);
        //mZBarView.startCamera();
        //mZBarView.changeToScanQRCodeStyle();
       // mZBarView.setType(BarcodeType.ALL, null);
        //mZBarView.startSpotAndShowRect();

        // Inflate the layout for this fragment
        return view;
    }




    @Override
    public void onStart() {
        super.onStart();
        mZBarView.startCamera();
        mZBarView.startSpotAndShowRect();
    }

    @Override
    public void onStop() {
        mZBarView.stopCamera();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mZBarView.onDestroy();
        super.onDestroy();
    }
    private void vibrate() {
        Vibrator vibrator = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);

    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        //Toast.makeText( getActivity(), "result: "+result, Toast.LENGTH_SHORT).show();
        mZBarView.stopCamera();
        vibrate();
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Consultando a la Base de Datos");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        getDatosPersonal(result);
        //mZBarView.startSpot();

    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "error");
    }

    public void getDatosPersonal(String code) {
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url+ID_EMPRESA+"/"+code, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        //Toast.makeText(mContext, response.toString() , Toast.LENGTH_SHORT).show();
                        // display response
                        // JSONObject Resultado= new JSONObject((Map) response);
                        try {

                            String idPersonal = response.getString("idPersonal");
                            String numEmpleado = response.getString("numEmpleado");
                            String nombre = response.getString("nombre");
                            String apPaterno = response.getString("apePat");
                            String apMaterno = response.getString("apeMat");
                            String area = response.getString("area");
                            String cargo = response.getString("cargo");


                            String foto = response.getString("foto");


                            Bundle bundle = new Bundle();
                            bundle.putString("idPersonal",idPersonal); // Put anything what you want
                            bundle.putString("nombre",nombre); // Put anything what you want
                            bundle.putString("apPaterno",apPaterno); // Put anything what you want
                            bundle.putString("apMaterno",apMaterno); // Put anything what you want
                            bundle.putString("numEmpleado",numEmpleado); // Put anything what you want
                            bundle.putString("area",area); // Put anything what you want
                            bundle.putString("cargo",cargo); // Put anything what you want
                            bundle.putString("foto",foto); // Put anything what you want

                            //tvDatos.setText(escaneos);

                            FragmentManager fragmentManager;
                            FragmentTransaction fragmentTransaction;
                            fragmentManager = getFragmentManager();
                            fragmentTransaction = fragmentManager.beginTransaction();

                            FragmentDatosPersonal fragmentDatos= new FragmentDatosPersonal();
                            fragmentDatos.setArguments(bundle);
                            fragmentTransaction.replace(R.id.fragment, fragmentDatos);
                            fragmentTransaction.commit();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //String respuesta=response.getString("repuesta");
                        Log.d("Response", response.toString());
                        //Toast.makeText(MainActivity.this, response , Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        ErrorResponse errorResponse = new ErrorResponse();
                        errorResponse.errorResponse(mContext,error,"Consultando Personal");


                        FragmentManager fragmentManager;
                        FragmentTransaction fragmentTransaction;
                        fragmentManager = getFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();

                        FragmentScan fragmentScan= new FragmentScan();

                        fragmentTransaction.replace(R.id.fragment, fragmentScan);
                        fragmentTransaction.commit();

                        Log.d("Error.Response", error.toString());
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+TOKEN);

                return params;
            }
        }
        ;

// add it to the RequestQueue
        queue.add(getRequest);
    }}