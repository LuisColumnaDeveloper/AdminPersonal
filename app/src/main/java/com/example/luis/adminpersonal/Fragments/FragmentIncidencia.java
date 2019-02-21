package com.example.luis.adminpersonal.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.luis.adminpersonal.BuildConfig;
import com.example.luis.adminpersonal.MainActivity;
import com.example.luis.adminpersonal.R;
import com.example.luis.adminpersonal.SettingsActivity;
import com.example.luis.adminpersonal.Utils.Utils;
import com.frosquivel.magicalcamera.MagicalCamera;
import com.frosquivel.magicalcamera.MagicalPermissions;
import com.frosquivel.magicalcamera.Utilities.ConvertSimpleImage;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadService;
import net.gotev.uploadservice.UploadStatusDelegate;

import java.io.File;
import java.util.Map;
import java.util.UUID;

import static android.content.ContentValues.TAG;
import static com.example.luis.adminpersonal.Utils.Utils.viewSnackBar;


public class FragmentIncidencia extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


//    String url = "http://192.168.1.66/adminPersonal/api/incidencia";

    /*****************SERVER******************/
    String url = "http://142.93.194.178/adminPersonal/api/incidencia";
    TextView tvDatos;
    Toolbar toolbar;
    ActionBar actionBar;
    ImageView imagenView;
    SharedPreferences prefs;
    Context mContext;
    String TOKEN, ID_USUARIO;
    ProgressDialog progressDialog;
    RequestQueue queue;
    ImageButton btntakephoto, btnselectedphoto, btnGoTo, saveImage;
    EditText edtTextDescripcion;
    ImageView preview;
    Button btnSend;
    MagicalCamera magicalCamera;
    private FloatingActionMenu floatingBtnMenu;
    //see the information data of the picture
    private com.github.clans.fab.FloatingActionButton floatingBtnPhotoInformation;
    //button for rotate the image in bitmap and in image view
    private com.github.clans.fab.FloatingActionButton floatingBtnRotate;
    private MagicalPermissions magicalPermissions;
    private LinearLayout progressLoadingIndicator;
    private int RESIZE_PHOTO_PIXELS_PERCENTAGE = 70;
    Uri output;
    String foto;
    File file;
    private Activity activity;
    private View principalLayout;
    String path = "";
    Map<String, Boolean> mapPermissions = null;

    //UBICACION
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Double lat=0.0, lng=0.0;

    boolean banderaSaveImage=false;

    String TAG;
    public static final int REQUEST_LOCATION = 1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_incidencia, container, false);

        activity = getActivity();
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        mContext = container.getContext();
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());


        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)

                .enableAutoManage(activity, FragmentIncidencia.this)
                .build();


        activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("Incidencia");


        //Utils.setInitialSharedPreference(activity,true);
        prefs = mContext.getSharedPreferences("PreferenciasAdminPer", Context.MODE_PRIVATE);
        TOKEN = prefs.getString("token", "Default");
        ID_USUARIO = prefs.getString("idUsuario", "0");


        String[] permissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION

        };

        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;


        setUIComponents(view);

        magicalPermissions = new MagicalPermissions(this, permissions);
        magicalCamera = new MagicalCamera(activity, Integer.parseInt(Utils.getSharedPreference(mContext, Utils.C_PREFERENCE_MC_QUALITY_PICTURE)), magicalPermissions);

        //magicalCamera.selectFragmentPicture(FragmentIncidencia.this, "Selecciona la foto para enviar");

        btntakephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //magicalCamera.takeFragmentPhoto(FragmentIncidencia.this);

                if (isActivePermission(MagicalCamera.CAMERA)) {
                    //call the method of take the picture
                    magicalCamera.takeFragmentPhoto(FragmentIncidencia.this);
                    banderaSaveImage=true;
                } else {
                    Utils.viewSnackBar(getString(R.string.error_not_have_permissions), principalLayout);
                }

            }
        });

        floatingBtnRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.validateMagicalCameraNull(activity, principalLayout, magicalCamera)) {
                    //for once click rotate the picture in 90ª, and set in the image view.
                    magicalCamera.setPhoto(magicalCamera.rotatePicture(magicalCamera.getPhoto(), MagicalCamera.ORIENTATION_ROTATE_90));
                    preview.setImageBitmap(magicalCamera.getPhoto());
                }
            }
        });

        floatingBtnPhotoInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (Utils.validateMagicalCameraNull(activity, principalLayout, magicalCamera)) {
                    //for see the data you need to call this method ever
                    //if the function return true you have the posibility of see the data
                    if (magicalCamera.initImageInformation()) {

                        StringBuilder builderInformation = new StringBuilder();

                        //question in have data
//                        if (Utils.notNullNotFill(magicalCamera.getPrivateInformation().getLatitude() + ""))
//                            builderInformation.append(getString(R.string.info_data_latitude) + magicalCamera.getPrivateInformation().getLatitude() + "\n");
//                        if (Utils.notNullNotFill(magicalCamera.getPrivateInformation().getLatitudeReference()))
//                            builderInformation.append(getString(R.string.info_data_latitude_referene) + magicalCamera.getPrivateInformation().getLatitudeReference() + "\n");
//
//                        if (Utils.notNullNotFill(magicalCamera.getPrivateInformation().getLongitude() + ""))
//                            builderInformation.append(getString(R.string.info_data_longitude) + magicalCamera.getPrivateInformation().getLongitude() + "\n");
//
//                        if (Utils.notNullNotFill(magicalCamera.getPrivateInformation().getLongitudeReference()))
//                            builderInformation.append(getString(R.string.info_data_longitude_reference) + magicalCamera.getPrivateInformation().getLongitudeReference() + "\n");

                        if (Utils.notNullNotFill(magicalCamera.getPrivateInformation().getDateTimeTakePhoto()))
                            builderInformation.append(getString(R.string.info_data_date_time_photo) + magicalCamera.getPrivateInformation().getDateTimeTakePhoto() + "\n");

                        if (Utils.notNullNotFill(magicalCamera.getPrivateInformation().getDateStamp()))
                            builderInformation.append(getString(R.string.info_data_date_stamp_photo) + magicalCamera.getPrivateInformation().getDateStamp() + "\n");

                        if (Utils.notNullNotFill(magicalCamera.getPrivateInformation().getIso()))
                            builderInformation.append(getString(R.string.info_data_ISO) + magicalCamera.getPrivateInformation().getIso() + "\n");

                        if (Utils.notNullNotFill(magicalCamera.getPrivateInformation().getOrientation()))
                            builderInformation.append(getString(R.string.info_data_orientation_photo) + magicalCamera.getPrivateInformation().getOrientation() + "\n");

                        if (Utils.notNullNotFill(magicalCamera.getPrivateInformation().getImageLength()))
                            builderInformation.append(getString(R.string.info_data_image_lenght) + magicalCamera.getPrivateInformation().getImageLength() + "\n");

                        if (Utils.notNullNotFill(magicalCamera.getPrivateInformation().getImageWidth()))
                            builderInformation.append(getString(R.string.info_data_image_width) + magicalCamera.getPrivateInformation().getImageWidth() + "\n");

                        if (Utils.notNullNotFill(magicalCamera.getPrivateInformation().getModelDevice()))
                            builderInformation.append(getString(R.string.info_data_model_device) + magicalCamera.getPrivateInformation().getModelDevice() + "\n");

                        if (Utils.notNullNotFill(magicalCamera.getPrivateInformation().getMakeCompany()))
                            builderInformation.append(getString(R.string.info_data_make_company) + magicalCamera.getPrivateInformation().getMakeCompany() + "\n");

                        new MaterialDialog.Builder(activity)
                                .title(getString(R.string.message_see_photo_information))
                                .content(builderInformation.toString())
                                .positiveText(getString(R.string.message_ok))
                                .show();
                    } else {
                        Utils.viewSnackBar(getString(R.string.error_not_have_data), principalLayout);
                    }
                }
            }
        });


        btnselectedphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //this is the form to select picture of device
                if (isActivePermission(MagicalCamera.EXTERNAL_STORAGE)) {
                    magicalCamera.selectFragmentPicture(FragmentIncidencia.this, Utils.getSharedPreference(activity, Utils.C_PREFERENCE_MC_SELECTED_PICTURE));
                } else {
                    Utils.viewSnackBar(getString(R.string.error_not_have_permissions), principalLayout);
                }
            }
        });

        saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (Utils.validateMagicalCameraNull(activity, principalLayout, magicalCamera)) {
                    //save the photo in your memory external or internal of your device
                    new AsyncTask<Void, Void, String>() {
                        protected void onPreExecute() {
                            progressLoadingIndicator.setVisibility(View.VISIBLE);
                        }

                        protected String doInBackground(Void... params) {

                            String path = magicalCamera.savePhotoInMemoryDevice(magicalCamera.getPhoto(),
                                    Utils.getSharedPreference(activity, Utils.C_PREFERENCE_MC_PHOTO_NAME),
                                    Utils.getSharedPreference(activity, Utils.C_PREFERENCE_MC_DIRECTORY_NAME),
                                    Utils.getFormat(activity),
                                    Boolean.parseBoolean(Utils.getSharedPreference(activity, Utils.C_PREFERENCE_MC_AUTO_IC_NAME)));

                            if (path != null) {
                                Utils.viewSnackBar(getString(R.string.message_save_manual) + path, principalLayout);
                            } else {
                                Utils.viewSnackBar(getString(R.string.error_dont_save_photo), principalLayout);
                            }

                            return null;
                        }

                        protected void onPostExecute(String msg) {
                            progressLoadingIndicator.setVisibility(View.GONE);
                        }
                    }.execute();
                }
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isLocationPermissionGranted()){
                    if (mLastLocation != null) {
                        updateLocationUI();

                    } else {
                        Toast.makeText(mContext, "Ubicación no encontrada", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(mContext, "no hay permiso de ubicacion", Toast.LENGTH_LONG).show();
                }
                String descripcion = edtTextDescripcion.getText().toString();
                //Toast.makeText(mContext, "Lat: "+lat+" Lng: "+lng, Toast.LENGTH_LONG).show();

                if (!path.equals("")) {
                    sendScan(path, descripcion,String.valueOf(lat),String.valueOf(lng));
                    //Toast.makeText(getActivity().getApplicationContext(), TOKEN+"::"+ID_USUARIO, Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(getActivity().getApplicationContext(), "selecciona una imagen.", Toast.LENGTH_SHORT).show();

                    preview.setColorFilter(getContext().getResources().getColor(R.color.md_red_500));
                    AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(mContext);
                    alertDialog2.setTitle("Incidencia");
                    alertDialog2.setMessage("Debes seleccionar una imagen");
                    alertDialog2.setIcon(R.mipmap.ic_launcher);

                    alertDialog2.setPositiveButton("ACEPTAR",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
//                                            FragmentManager fragmentManager;
//                                            FragmentTransaction fragmentTransaction;
//                                            fragmentManager = getFragmentManager();
//                                            fragmentTransaction = fragmentManager.beginTransaction();
//
//                                            FragmentScan fragmentScan= new FragmentScan();
//
//                                            fragmentTransaction.replace(R.id.fragment, fragmentScan);
//                                            fragmentTransaction.commit();
                                }
                            });

                    alertDialog2.show();
                }

            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    private void setUIComponents(View rootView) {
        edtTextDescripcion = (EditText) rootView.findViewById(R.id.descripcion);
        preview = (ImageView) rootView.findViewById(R.id.previewFoto);
        btnSend = (Button) rootView.findViewById(R.id.btnEnviar);
        btntakephoto = (ImageButton) rootView.findViewById(R.id.btntakephoto);
        btnselectedphoto = (ImageButton) rootView.findViewById(R.id.btnselectedphoto);
        saveImage = (ImageButton) rootView.findViewById(R.id.saveImage);
        saveImage.setVisibility(View.GONE);
        principalLayout = rootView.findViewById(R.id.principalLayout);
        floatingBtnRotate = (com.github.clans.fab.FloatingActionButton) rootView.findViewById(R.id.floatingBtnRotate);
        floatingBtnPhotoInformation = (com.github.clans.fab.FloatingActionButton) rootView.findViewById(R.id.floatingBtnPhotoInformation);
        floatingBtnMenu = (FloatingActionMenu) rootView.findViewById(R.id.floatingBtnMenu);
        progressLoadingIndicator = (LinearLayout) rootView.findViewById(R.id.progressLoadingIndicator);
        floatingBtnMenu.setVisibility(View.GONE);
        saveImage.setVisibility(View.GONE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //this is for rotate picture in this method
        //magicalCamera.resultPhoto(requestCode, resultCode, data, MagicalCamera.ORIENTATION_ROTATE_180);

        if (resultCode == Activity.RESULT_OK) {
            //you should to call the method ever, for obtain the bitmap photo (= magicalCamera.getPhoto())
            magicalCamera.resultPhoto(requestCode, resultCode, data);

            //if (com.luiscolumna.qr.Utils.Utils.validateMagicalCameraNull(activity, principalLayout, magicalCamera)) {
            if (Utils.validateMagicalCameraNull(activity, principalLayout, magicalCamera)) {
                floatingBtnMenu.setVisibility(View.VISIBLE);


                        if(banderaSaveImage){
                            saveImage.setVisibility(View.VISIBLE);
                            banderaSaveImage=false;
                        }else{
                            saveImage.setVisibility(View.GONE);
                        }

                //set the photo in image view
                preview.setColorFilter(null);
                preview.setImageBitmap(magicalCamera.getPhoto());

                //save the picture inmemory device, and return the physical path of this photo

                //fotoBase64=ConvertSimpleImage.bytesToStringBase64(arrayBytes);

                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        progressLoadingIndicator.setVisibility(View.VISIBLE);
                    }

                    protected String doInBackground(Void... params) {

                        //save the picture inmemory device, and return the physical path of this photo
                        path = magicalCamera.savePhotoInMemoryDevice(magicalCamera.getPhoto(),
                                Utils.getSharedPreference(activity, Utils.C_PREFERENCE_MC_PHOTO_NAME),
                                Utils.getSharedPreference(activity, Utils.C_PREFERENCE_MC_DIRECTORY_NAME),

                                Utils.getFormat(activity),
                                Boolean.parseBoolean(Utils.getSharedPreference(activity, Utils.C_PREFERENCE_MC_AUTO_IC_NAME))
                        );

                        if (path != null) {
                            Utils.viewSnackBar(getString(R.string.message_save_manual) + path, principalLayout);
                        } else {
                            Utils.viewSnackBar(getString(R.string.error_dont_save_photo), principalLayout);
                        }

                        return null;
                    }

                    protected void onPostExecute(String msg) {
                        progressLoadingIndicator.setVisibility(View.GONE);
                    }
                }.execute();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mapPermissions = magicalPermissions.permissionResult(requestCode, permissions, grantResults);
        for (String permission : mapPermissions.keySet()) {
            Log.d("PERMISSIONS", permission + " was: " + mapPermissions.get(permission));
        }
        //isLocationPermissionGranted();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_setting:
                startActivity(new Intent(mContext, SettingsActivity.class));
                break;

            default:
                break;
        }
        return true;
    }

    public void sendScan(final String path, final String descripcion,final String lat,final String lng) {

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Enviando Incidencia");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();

        UploadNotificationConfig config = new UploadNotificationConfig();

        PendingIntent clickIntent = PendingIntent.getActivity(
                mContext, 1, new Intent(),
                PendingIntent.FLAG_UPDATE_CURRENT);

        config.setTitleForAllStatuses("Incidencia")
                .setRingToneEnabled(true)
                .setClickIntentForAllStatuses(clickIntent)
                .setClearOnActionForAllStatuses(true);

        config.getProgress().message = getString(R.string.uploadingIncidencia);
        config.getProgress().iconResourceID = R.drawable.ic_cloud_upload;
        config.getProgress().iconColorResourceID = Color.BLUE;

        config.getCompleted().message = getString(R.string.upload_success);
        config.getCompleted().iconResourceID = R.drawable.ic_cloud_done;
        config.getCompleted().iconColorResourceID = Color.GREEN;

        config.getError().message = getString(R.string.upload_error);
        config.getError().iconResourceID = R.drawable.ic_cloud_upload;
        config.getError().iconColorResourceID = Color.RED;

        try {
            String uploadId = UUID.randomUUID().toString();
            new MultipartUploadRequest(getActivity().getApplicationContext(), uploadId, url)
                    .addFileToUpload(path, "foto")
                    .addParameter("descripcion", descripcion)
                    .addParameter("idUsuario", ID_USUARIO)
                    .addParameter("lat", lat)
                    .addParameter("lng", lng)
                    .setMaxRetries(2)
                    .setNotificationConfig(config)
                    .addHeader("Authorization", "Bearer " + TOKEN)
                    .setDelegate(new UploadStatusDelegate() {
                        @Override
                        public void onProgress(Context context, UploadInfo uploadInfo) {
                            Log.d("On Progress", String.valueOf(uploadInfo));
                            int percent = uploadInfo.getProgressPercent();
                            progressDialog.setProgress(percent);
                        }

                        @Override
                        public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
                            //  Log.e(TAG, "Error in upload"+ exception.getLocalizedMessage(), exception);
                            progressDialog.dismiss();

                            AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(mContext);
                            alertDialog2.setTitle("Error");
                            alertDialog2.setMessage("Error al enviar incidencia");
                            alertDialog2.setIcon(R.mipmap.ic_launcher);

                            alertDialog2.setPositiveButton("ACEPTAR",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });

                            alertDialog2.show();
                        }

                        @Override
                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {

                            File eliminar = new File(path);
                            if (eliminar.exists()) {
                                if (eliminar.delete()) {
                                    System.out.println("archivo eliminado:" + path);
                                } else {
                                    System.out.println("archivo no eliminado" + path);
                                }
                            }

                            progressDialog.dismiss();
                            Log.d("success.Response", serverResponse.getBodyAsString());
                            AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(mContext);
                            alertDialog2.setTitle("Incidencia");
                            alertDialog2.setMessage("Incidencia registrada correctamente");
                            alertDialog2.setIcon(R.mipmap.ic_launcher);

                            alertDialog2.setPositiveButton("ACEPTAR",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });

                            alertDialog2.show();
                            //Toast.makeText(getActivity().getApplicationContext(), "Imagen subida exitosamente.", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(Context context, UploadInfo uploadInfo) {
                            progressDialog.dismiss();
                        }
                    })
                    .startUpload();

        } catch (Exception exc) {
            // System.out.println(exc.getMessage() + " " + exc.getLocalizedMessage());
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        magicalCamera.setResizePhoto(Integer.parseInt(Utils.getSharedPreference(activity, Utils.C_PREFERENCE_MC_QUALITY_PICTURE)));
    }


    private boolean isActivePermission(String permission) {
        //this map is return in method onRequestPermissionsResult for view what permissions are actives
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (mapPermissions != null) {
                if (mapPermissions.size() > 0) {//si ya iene permisos
                    //obtain the code of camera permissions
                    return mapPermissions.get(permission); //regresar el solictado
                } else {
                    return true;
                }
            } else {

                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(isLocationPermissionGranted()){
            if (mLastLocation != null) {
                updateLocationUI();
            } else {
                Toast.makeText(mContext, "Ubicación no encontrada", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(mContext, "no hay permiso de ubicacion", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();


    }

    private boolean isLocationPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {

                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    return true;
                } else {
                ActivityCompat.requestPermissions(activity, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, REQUEST_LOCATION);
                return false;
            }
        } else{
                    return true;
            }

    }

    private void updateLocationUI() {
        lat = mLastLocation.getLatitude();
        lng = mLastLocation.getLongitude();
        //DIALOGO DE UBICACION
//        android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(mContext);
//        dialog.setCancelable(false);
//        dialog.setTitle("Ubicación");
//        dialog.setMessage("Latitud: " + lat + "\n" + "Longitud: " + lng);
//        dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int id) {
//            }
//        });
//        dialog.show();
    }
}
