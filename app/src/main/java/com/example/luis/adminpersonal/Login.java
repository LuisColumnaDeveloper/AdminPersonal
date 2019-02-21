package com.example.luis.adminpersonal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.luis.adminpersonal.Utils.ErrorResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Login extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener  {

//    String url = "http://192.168.1.66/adminPersonal/oauth/token";
//    String urlDatosUser = "http://192.168.1.66/adminPersonal/api/datosUser/";
    /**********SERVER***************/
    String url = "http://142.93.194.178/adminPersonal/oauth/token";
    String urlDatosUser = "http://142.93.194.178/adminPersonal/api/datosUser/";

    ProgressDialog progressDialog;
    RequestQueue queue;
    String CLIENT_ID= "2";
//    String CLIENT_SECRET= "Tgt7wBYui8GQ626057Nh1ec6HIUFqh5XlPWVXI06";
    //String CLIENT_SECRET= "V96fKuwQ5WP55aS4pMmEK09nY6I2z8WFWCyVT5G9";
    /***********SERVER***********/
    String CLIENT_SECRET= "g0QM9rNPq0yfxrYiQgTuA6s9RPdrn8KNlFosr4j8";
    String GRANT_TYPE= "password";
    String usuario="";

    Button buttLogin;
    EditText textUser,textPassword;
    SharedPreferences prefs;
    CircleImageView imgUser;


    private static final int PERCENTAGE_TO_SHOW_IMAGE = 20;
    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private boolean mIsAvatarShown = true;
    private int mMaxScrollSize;
    private boolean mIsImageHidden;

    CollapsingToolbarLayout collapsingToolbarLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        buttLogin =(Button)findViewById(R.id.button_login);
        textUser = (EditText) findViewById(R.id.user);
        textPassword = (EditText) findViewById(R.id.password);
        imgUser = (CircleImageView)findViewById(R.id.imgUser);
        queue = Volley.newRequestQueue(this);
        prefs = getSharedPreferences("PreferenciasAdminPer", Context.MODE_PRIVATE);


        collapsingToolbarLayout =(CollapsingToolbarLayout)findViewById(R.id.flexible_example_collapsing);

        collapsingToolbarLayout.setTitle("Administración de Personal");



        AppBarLayout appbar = (AppBarLayout)findViewById(R.id.flexible_example_appbar);
        appbar.addOnOffsetChangedListener(this);
        mMaxScrollSize = appbar.getTotalScrollRange();

        String email = prefs.getString("email", "por_defecto@email.com");
        String idUsuario=prefs.getString("idUsuario", "0");
        String user=prefs.getString("usuario", "Default");
        String idArea=prefs.getString("idArea", "0");
        String idEmpresa=prefs.getString("idEmpresa", "0");
        String token=prefs.getString("token", "Default");
        String avatar =prefs.getString("avatar", "Default");


        if(!avatar.equals("Default")){

            Glide.with(this)
                    .load(Base64.decode(avatar, Base64.DEFAULT))
                    .into(imgUser);
        }



        buttLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog = new ProgressDialog(Login.this);
                progressDialog.setMessage("Iniciando Sesión");
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();

                usuario = textUser.getText().toString();

                veryfyUser(usuario,textPassword.getText().toString());

            }
        });
    }

    public  void veryfyUser(final String usuario, final String password){

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        // Toast.makeText(Login.this, response , Toast.LENGTH_SHORT).show();

                        try {

                            SharedPreferences.Editor editor = prefs.edit();
                            JSONObject data= new JSONObject(response);
                            String token = data.getString("access_token");
                            editor.putString("token", token);

                            getDatosUser(usuario,token);

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
                        progressDialog.dismiss();
                        //Log.d("Error.Response", error.toString());

                        ErrorResponse errorResponse = new ErrorResponse();
                        errorResponse.errorResponse(Login.this,error,"Comprobando Usuario");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("client_id", CLIENT_ID);
                params.put("client_secret", CLIENT_SECRET);
                params.put("grant_type", GRANT_TYPE);
                params.put("username", usuario);
                params.put("password", password);
                return params;
            }
        };
        queue.add(postRequest);

    }

    public void getDatosUser(String email , final String token){
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, urlDatosUser+email, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            String email = response.getString("email");
                            String nombre = response.getString("nombre");
                            String apePat = response.getString("apePat");
                            String idUsuario = response.getString("idUsuario");
                            String avatar = response.getString("avatar");
                            String empresa = response.getString("empresa");
                            String idEmpresa = response.getString("idEmpresa");
                            // Toast.makeText(Login.this, email , Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("token", token);
                            editor.putString("idUsuario", idUsuario);
                            editor.putString("email", email);
                            editor.putString("nombre", nombre);
                            editor.putString("apePat", apePat);
                            editor.putString("avatar", avatar);
                            editor.putString("empresa", empresa);
                            editor.putString("idEmpresa", idEmpresa);
                            editor.commit();
                            progressDialog.dismiss();
                            Intent datos = new Intent(Login.this, MainActivity.class);
                            startActivity(datos);
                            overridePendingTransition(R.anim.left_in, R.anim.left_out);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //String respuesta=response.getString("repuesta");
                        Log.d("Response", response.toString());
                        //Toast.makeText(MainActivity.this, response , Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ErrorResponse errorResponse = new ErrorResponse();
                        errorResponse.errorResponse(Login.this,error,"Comprobando Usuario");
                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+token);

                return params;
            }
        };

// add it to the RequestQueue
        queue.add(getRequest);
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

            imgUser.animate()
                    .scaleY(0).scaleX(0)
                    .setDuration(200)
                    .start();

        }

        if (currentScrollPercentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
            mIsAvatarShown = true;

            imgUser.animate()
                    .scaleY(1).scaleX(1)
                    .start();
        }

    }
}

