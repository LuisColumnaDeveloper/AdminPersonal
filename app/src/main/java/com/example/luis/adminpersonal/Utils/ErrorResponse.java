package com.example.luis.adminpersonal.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.example.luis.adminpersonal.R;

/**
 * Created by luis_ on 01/09/2018.
 */

public class ErrorResponse {

    public  void errorResponse(Context context, VolleyError error,String titulo){

        NetworkResponse networkResponse = error.networkResponse;
        //final int httpStatusCode = error.networkResponse.statusCode;
        int status=503;
        if(networkResponse!=null){
             status =error.networkResponse.statusCode;
        }

        String mensaje ="";
        switch (status) {
            case 400://bad request
                mensaje ="Introduce un usuario y contraseña valido";
                break;
            case 401://unauthorized
                mensaje ="Usuario ó Contraseña incorrectos";
                break;
            case 404:
                mensaje ="Datos no encontrados";
                break;
            case 405:
                mensaje ="Method Not Allowed";
                break;
            case 503:
                mensaje ="Service Unavailable";
                break;
        }

        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(context);
        alertDialog2.setTitle(titulo);
        alertDialog2.setMessage(mensaje);
        alertDialog2.setIcon(R.mipmap.ic_launcher);
        alertDialog2.setPositiveButton("ACEPTAR",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        alertDialog2.show();
    }
}
