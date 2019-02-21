package com.example.luis.adminpersonal.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.luis.adminpersonal.R;
import com.example.luis.adminpersonal.Utils.ErrorResponse;
import com.github.clans.fab.Label;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultFillFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.github.mikephil.charting.utils.Utils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.mikephil.charting.components.Legend.LegendPosition.BELOW_CHART_CENTER;


public class FragmentDashboard extends Fragment {

//    String urlIncidencia = "http://192.168.1.66/adminPersonal/api/incidenciaGrafica/";
//    String urlEscaneo = "http://192.168.1.66/adminPersonal/api/escaneoGrafica/";

    /*************SERVER******************/

    String urlIncidencia = "http://142.93.194.178/adminPersonal/api/incidenciaGrafica/";
    String urlEscaneo = "http://142.93.194.178/adminPersonal/api/escaneoGrafica/";

    RequestQueue queue;
    TextView tvDatos;
    Toolbar toolbar;
    ActionBar actionBar;
    ImageView imagenView;
    Context mContext;
    LineChart chart ;
    BarChart barChart;

    SharedPreferences prefs;
    String TOKEN,ID_USUARIO;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        mContext = container.getContext();
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        activity.getSupportActionBar().setTitle("Dashboard");
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        prefs = mContext.getSharedPreferences("PreferenciasAdminPer", Context.MODE_PRIVATE);
        TOKEN=prefs.getString("token", "Default");
        ID_USUARIO=prefs.getString("idUsuario", "0");

        /***Grafica***/
        chart =(LineChart)view.findViewById(R.id.chart);
        barChart= (BarChart)view.findViewById(R.id.chartEcaneos);

        /***************************GRAFICAS*******************************/
        getDatosGraficaIncidencia(ID_USUARIO);
        getDatosGraficaEscaneo(ID_USUARIO);


        return view;
    }


    public void getDatosGraficaIncidencia(String idUsuario) {
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, urlIncidencia+idUsuario, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        // JSONObject Resultado= new JSONObject((Map) response);

                        try {


                            JSONArray labels = response.getJSONArray("labels");
                            JSONArray totales = response.getJSONArray("totales");

//                            Toast.makeText(mContext, "labels: "+labels.toString(), Toast.LENGTH_LONG).show();
//                            Toast.makeText(mContext, "totales: "+totales.toString(), Toast.LENGTH_LONG).show();
                            //CREAMOS LA GRAFICA

                            if(labels.length()>0){
                                createChartIncidencia(labels,totales);
                            }


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
                        ErrorResponse errorResponse = new ErrorResponse();
                        errorResponse.errorResponse(mContext,error,"Error al consultar Información");
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
    }

    public void createChartIncidencia(JSONArray labels, JSONArray totales) throws JSONException {
        ArrayList<Entry> incidencia = new ArrayList<Entry>();
        final ArrayList<String> labelsdias = new ArrayList<>();

        final String [] lDias = new String[labels.length()];

        for (int i=0; i<totales.length();i++){
            Entry  total= new Entry(i,Integer.valueOf(totales.getString(i)));

            incidencia.add(total);

        }
        for (int i=0; i<labels.length();i++){
            //Log.d("Dia", labels.getString(i));
           //labelsdias.add(labels.getString(i));

            lDias[i]=labels.getString(i);


        }

        LineDataSet setComp1 = new LineDataSet(incidencia, "Incidencia");

        setComp1.setLabel(null);
        setComp1.setHighlightEnabled(true);
        setComp1.setDrawHighlightIndicators(true);
        setComp1.setHighLightColor(Color.BLACK);


        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp1.setDrawFilled(true);
        setComp1.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.fade_red);
            setComp1.setFillDrawable(drawable);
        } else {
            setComp1.setFillColor(Color.BLACK);

        }
        // use the interface ILineDataSet
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(setComp1);

        LineData data = new LineData(dataSets);
        data.setHighlightEnabled(true);

        chart.setData(data);
        chart.invalidate(); // refresh

//        IAxisValueFormatter lineFormatter = new IAxisValueFormatter() {
//
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
//                if(lDias.length > (int) value) {
//                    return lDias[(int) value];
//                } else return null;
//            }
//
//        };

        XAxis xAxis = chart.getXAxis();
        //xAxis.setMultiLineLabel(true);
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        //xAxis.setDrawGridLines(true);
        xAxis.setTextSize(5f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (value >= 0) {
                    if (lDias.length > (int) value) {
                        return lDias[(int) value];
                    } else return "";
                } else {
                    return "";
                }
            }
        });
        xAxis.setLabelRotationAngle(-60f);


        chart.getAxisRight().setEnabled(false);
        chart.animateX(2000);
        chart.animateY(2000);


        chart.setDescription(null);
        chart.setExtraOffsets(-10,-10,-10,60);

        //LEGEND

        Legend l = chart.getLegend();
        l.setEnabled(false);

    }

    public void getDatosGraficaEscaneo(String idUsuario) {
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, urlEscaneo+idUsuario, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        // JSONObject Resultado= new JSONObject((Map) response);

                        try {

                            JSONArray labels = response.getJSONArray("labels");
                            JSONArray totales = response.getJSONArray("totales");

                            //Toast.makeText(mContext, labels.toString(), Toast.LENGTH_LONG).show();
                            //Toast.makeText(mContext, totales.toString(), Toast.LENGTH_LONG).show();
                            //CREAMOS LA GRAFICA
                            createChartEscaneo(labels,totales);

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
                        ErrorResponse errorResponse = new ErrorResponse();
                        errorResponse.errorResponse(mContext,error,"Error al consultar Información");
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
    }


    public void createChartEscaneo(JSONArray labels, JSONArray totales) throws JSONException {

        List<BarEntry> entries = new ArrayList<>();
        final ArrayList<String> quarters = new ArrayList<>();

        //VALUES
        for (int i=0;i<totales.length();i++){
            entries.add(new BarEntry(Float.valueOf(i),Float.valueOf(totales.getString(i))));


        }
        //LABELS

        for (int x=0;x<labels.length();x++){
            quarters.add(labels.getString(x));
        }

        BarDataSet set = new BarDataSet(entries, null);

        set.setBarBorderWidth(1);
        set.setBarBorderColor(getResources().getColor(R.color.borderBar));
        set.setColor(getResources().getColor(R.color.backroundBar));
        //set.setBarShadowColor(getResources().getColor(R.color.md_green_500));
        set.setHighlightEnabled(true);
        set.setHighLightColor(getResources().getColor(R.color.colorspecial));
        BarData data = new BarData(set);

        //labels
        //final String[] quarters = new String[] { "Q1", "Q2", "Q3", "Q4","Q5" ,"Q6"};//

        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return quarters.get((int) value);
            }

        };

        XAxis xAxis = barChart.getXAxis();

        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(formatter);
        xAxis.setLabelRotationAngle(-60f);
        data.setBarWidth(0.9f); // set custom bar width
        barChart.setData(data);
        barChart.setFitBars(true); // make the x-axis fit exactly all bars
        barChart.invalidate(); // refresh

        barChart.getAxisRight().setEnabled(false);
        barChart.animateX(2000);
        barChart.animateY(2000);


        barChart.setDescription(null);
        //MARGEN
        barChart.setExtraOffsets(5,-10,-10,60);

        //LEGEND
        Legend l = barChart.getLegend();
        l.setEnabled(false);

    }
}
