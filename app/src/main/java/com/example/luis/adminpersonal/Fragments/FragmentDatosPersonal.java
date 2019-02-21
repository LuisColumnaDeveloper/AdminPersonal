package com.example.luis.adminpersonal.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.luis.adminpersonal.Login;
import com.example.luis.adminpersonal.R;
import com.example.luis.adminpersonal.Utils.ErrorResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class FragmentDatosPersonal extends Fragment implements AppBarLayout.OnOffsetChangedListener{
//    String url = "http://192.168.1.66/adminPersonal/api/escaneo";
    /**************SERVER********************/
    String url = "http://142.93.194.178/adminPersonal/api/escaneo";
    private static final int PERCENTAGE_TO_SHOW_IMAGE = 20;
    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private boolean mIsAvatarShown = true;
    private View mFab;
    private int mMaxScrollSize;
    private boolean mIsImageHidden;
    private ImageView mProfileImage;
    private FragmentActivity myContext;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;
    TextView textNumEmpleado,textCargo,textArea;
    EditText editObservacion;
    Button btnEnviar;
    String idPersonal;

    SharedPreferences prefs;
    Context mContext;
    String TOKEN,ID_USUARIO;
    ProgressDialog progressDialog;
    RequestQueue queue ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_datos_personal, container, false);


        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        mContext = container.getContext();
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        prefs = mContext.getSharedPreferences("PreferenciasAdminPer", Context.MODE_PRIVATE);
        TOKEN=prefs.getString("token", "Default");
        ID_USUARIO=prefs.getString("idUsuario", "0");


        activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbarLayout =(CollapsingToolbarLayout) view.findViewById(R.id.flexible_example_collapsing);
        mProfileImage = (ImageView) view.findViewById(R.id.materialup_profile_image);

        textNumEmpleado=(TextView)view.findViewById(R.id.textNumEmpleado);
        textCargo=(TextView)view.findViewById(R.id.textCargo);
        textArea=(TextView)view.findViewById(R.id.textArea);
        editObservacion = (EditText)view.findViewById(R.id.observacion);
        btnEnviar = (Button)view.findViewById(R.id.btnEnviar);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager;
                FragmentTransaction fragmentTransaction;
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();

                FragmentScan fragmentScan= new FragmentScan();
                fragmentTransaction.replace(R.id.fragment, fragmentScan);
                fragmentTransaction.commit();
            }
        });

        AppBarLayout appbar = (AppBarLayout) view.findViewById(R.id.flexible_example_appbar);
        appbar.addOnOffsetChangedListener(this);
        mMaxScrollSize = appbar.getTotalScrollRange();

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.user);
        requestOptions.centerCrop();

        Bundle bundle =this.getArguments();

        if(bundle != null){

            idPersonal      = bundle.getString("idPersonal");
            String nombre      = bundle.getString("nombre");
            String apPaterno   = bundle.getString("apPaterno");
            String apMaterno   = bundle.getString("apMaterno");
            String area   = bundle.getString("area");
            String cargo   = bundle.getString("cargo");
            String numEmpleado   = bundle.getString("numEmpleado");
            String foto   = bundle.getString("foto");
            Glide.with(this)
                    .load(Base64.decode(foto, Base64.DEFAULT))
                    .apply(requestOptions)
                    .into(mProfileImage);

            collapsingToolbarLayout.setTitle(nombre+" "+apPaterno+" "+apMaterno);
            textNumEmpleado.setText(numEmpleado);
            textCargo.setText(cargo);
            textArea.setText(area);


            btnEnviar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(getActivity(), editObservacion.getText().toString()+" "+idPersonal+" "+ID_USUARIO , Toast.LENGTH_LONG).show();

                    progressDialog = new ProgressDialog(mContext);
                    progressDialog.setMessage("Enviando Escaneo");
                    progressDialog.setCancelable(false);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.show();
                    sendScan(idPersonal,editObservacion.getText().toString());
                }
            });

        }

        // Inflate the layout for this fragment
        return view;
    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {

        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int currentScrollPercentage = (Math.abs(i)) * 100
                / mMaxScrollSize;

        if (currentScrollPercentage >= PERCENTAGE_TO_SHOW_IMAGE) {
            if (!mIsImageHidden) {
                mIsImageHidden = true;


            }
        }

        if (currentScrollPercentage < PERCENTAGE_TO_SHOW_IMAGE) {
            if (mIsImageHidden) {
                mIsImageHidden = false;

            }
        }

        if (currentScrollPercentage >= PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
            mIsAvatarShown = false;

            mProfileImage.animate()
                    .scaleY(0).scaleX(0)
                    .setDuration(200)
                    .start();

        }

        if (currentScrollPercentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
            mIsAvatarShown = true;

            mProfileImage.animate()
                    .scaleY(1).scaleX(1)
                    .start();
        }

    }


    public  void sendScan(final String idPersonal,final String observacion){

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        //Toast.makeText(getActivity(), response , Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                        try {
                            JSONObject Resultado= new JSONObject(response);
                            String mensaje = Resultado.getString("mensaje");
                                AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(mContext);
                                alertDialog2.setTitle("Registro");
                                alertDialog2.setMessage(mensaje);
                                alertDialog2.setIcon(R.mipmap.ic_launcher);

                                alertDialog2.setPositiveButton("ACEPTAR",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                FragmentManager fragmentManager;
                                                FragmentTransaction fragmentTransaction;
                                                fragmentManager = getFragmentManager();
                                                fragmentTransaction = fragmentManager.beginTransaction();

                                                FragmentScan fragmentScan= new FragmentScan();

                                                fragmentTransaction.replace(R.id.fragment, fragmentScan);
                                                fragmentTransaction.commit();
                                            }
                                        });

                                alertDialog2.show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        // error
                        progressDialog.dismiss();
                        //Log.d("Error.Response", error.toString());

                        ErrorResponse errorResponse = new ErrorResponse();
                        errorResponse.errorResponse(mContext,error,"Error al enviar Escaneo");
                    }
                }
        ) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+TOKEN);

                return params;
            }
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("observacion", observacion);
                params.put("idPersonal", idPersonal);
                params.put("idUsuario", ID_USUARIO);


                return params;
            }
        }
        ;
        queue.add(postRequest);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }


}
