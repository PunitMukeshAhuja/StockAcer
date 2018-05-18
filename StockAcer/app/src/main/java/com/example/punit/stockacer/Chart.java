package com.example.punit.stockacer;

import android.*;
import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static java.lang.Float.parseFloat;

public class Chart extends AppCompatActivity{
    private String TAG = MainActivity.class.getSimpleName();
    private LineChart lineChart;
    private ProgressDialog pDialog;
    String company;

    private ArrayList<String> dates=new ArrayList<>();
    private ArrayList<String> rates=new ArrayList<>();
    ArrayList<Entry> entries=new ArrayList<>();
    TextView t;
    String dbName=null;
    // URL to get price JSON
     String url = null;
     Account account;
     EditText trate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        company=getIntent().getStringExtra("company");
        Log.e("got company name",company);


        t=(TextView)findViewById(R.id.chart_head);
        dbName=getIntent().getStringExtra("dbName");
        account=new Account(this,dbName,1);
        url="https://www.bloomberg.com/markets/api/bulk-time-series/price/"+company+":IN?timeFrame=1_DAY";

        t.setText(t.getText().toString()+" "+company);
        Toolbar tool=(Toolbar)findViewById(R.id.chart_toolbar);
        setSupportActionBar(tool);
        new GetRate().execute();
    }



    private class GetRate extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Chart.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);
            if(jsonStr!=null)
                jsonStr=jsonStr.substring(1,jsonStr.length()-1);
            else{
                Toast.makeText(Chart.this,"Json response null",Toast.LENGTH_LONG);
            }

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray price = jsonObj.getJSONArray("price");

                    // looping through All rate values
                    for (int i = 0; i < price.length(); i=i+10) {
                        JSONObject c = price.getJSONObject(i);

                        String date = c.getString("dateTime");
                        String value = c.getString("value");


                        dates.add(date);
                        rates.add(value);


                        Entry entry=new Entry(i/10,Float.parseFloat(value));
                        entries.add(entry);




                    }
                } catch (final JSONException e) {
                    Log.e("json_parse_error", "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e("fetch_error", "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }
            /**
             * Updating parsed JSON data into ListView
             * */
            lineChart=(LineChart)findViewById(R.id.lineChart);
            ArrayList<String> xAXES = new ArrayList<>();
            ArrayList<Entry> yAXESrate = new ArrayList<>();
            //ArrayList<Entry> yAXEScos = new ArrayList<>();
            String x = null ;
            int numDataPoints = dates.size();
            for(int i=0;i<numDataPoints;i++){
                float sinFunction = parseFloat(rates.get(i));
                x = dates.get(i);
                yAXESrate.add(new Entry(sinFunction,i));
                xAXES.add((x));
            }

            IAxisValueFormatter iformat=new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    int index=(int)value;
                    String timestamp[]=dates.get(index).split("T");
                    String full_time[]=timestamp[1].split(":");
                    String time=full_time[0]+":"+full_time[1];
                    return time;
                }
            };

            XAxis xAxis=lineChart.getXAxis();
            xAxis.setValueFormatter(iformat);

            xAxis.setLabelCount(8,true);


            if(numDataPoints>0) {
                ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();
                LineDataSet lineDataSet2 = new LineDataSet(entries, "rate");
                lineDataSet2.setDrawCircles(true);
                lineDataSet2.setColor(Color.BLUE);
                lineDataSets.add(lineDataSet2);
                lineChart.setData(new LineData(lineDataSets));
                lineChart.setVisibleYRangeMaximum(1200f, YAxis.AxisDependency.RIGHT);
                lineChart.setVisibleXRangeMaximum(100f);

                           }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(entries.size()>0){
            t.setText(t.getText().toString()+" rate(in Rs.): "+rates.get(rates.size()-1));
            lineChart.notifyDataSetChanged();}
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
        }

    }

    public void Buy(View view) {
        final Dialog dialog = new Dialog(view.getContext());
        dialog.setContentView(R.layout.connect_dialog);


        Button dialogButton = (Button) dialog.findViewById(R.id.okButton);
        trate = (EditText) dialog.findViewById(R.id.cname);
        TextView heading=(TextView) dialog.findViewById(R.id.heading);
        heading.setText("Enter no. of units ");


        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer no_of_shares=Integer.parseInt(trate.getText().toString());
                double balance=account.getBalance();

                Log.e("Chart buy balance as: ",balance+"");
                float rate=Float.parseFloat(rates.get(rates.size()-1));
                float cprice=no_of_shares*rate;
                balance=balance-cprice;
                if(balance>=0){
                account.setBalance(balance);
                account.buy(no_of_shares,company,cprice);
                    Toast.makeText(Chart.this, no_of_shares+" shares bought of "+company, Toast.LENGTH_SHORT).show();}
                else{
                    Toast.makeText(Chart.this,"Insufficient Balance",Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();

            }
        });
        dialog.show();

    }

    public void Sell(View view) {
        final Dialog dialog = new Dialog(view.getContext());
        dialog.setContentView(R.layout.connect_dialog);


        Button dialogButton = (Button) dialog.findViewById(R.id.okButton);
        trate = (EditText) dialog.findViewById(R.id.cname);
        TextView heading=(TextView) dialog.findViewById(R.id.heading);
        heading.setText("Enter no. of units ");


        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int no_of_shares=Integer.parseInt(trate.getText().toString());
                double balance=account.getBalance();

                Log.e("Chart sell balance as: ",balance+"");
                float rate=Float.parseFloat(rates.get(rates.size()-1));
                float sprice=no_of_shares*rate;
                balance=balance+sprice;
                int n=account.getUnits(company);
                float cp=account.getCost(company);
                float profit=sprice-(cp/n)*no_of_shares;
                float updateCost=(cp/n)*no_of_shares;
                if(n>=no_of_shares){
                account.sell(no_of_shares,company,sprice,profit,updateCost);
                account.setBalance(balance);
                    Toast.makeText(Chart.this, no_of_shares+" shares sold of "+company, Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(Chart.this,"Insufficient shares",Toast.LENGTH_LONG).show();

                }
            dialog.dismiss();

            }
        });
        dialog.show();

    }

    public void bTgt(View view) {
        final Dialog dialog = new Dialog(view.getContext());
        dialog.setContentView(R.layout.connect_dialog);


        Button dialogButton = (Button) dialog.findViewById(R.id.okButton);
        trate = (EditText) dialog.findViewById(R.id.cname);
        TextView heading=(TextView) dialog.findViewById(R.id.heading);
        heading.setText("Enter buy target price ");

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Float value=Float.parseFloat(trate.getText().toString());
                account.setBTarget(company,value);
                dialog.dismiss();

            }
        });
        dialog.show();
        Calendar cal=Calendar.getInstance();
        Intent intent = new Intent(Chart.this, TimedService.class);
        intent.putExtra("company",company);
        intent.putExtra("type","buy_target");
        intent.putExtra("dbName",dbName);
        PendingIntent pintent = PendingIntent.getService(Chart.this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 300*1000, pintent);
    }

    public void sTgt(View view) {
        final Dialog dialog = new Dialog(view.getContext());
        dialog.setContentView(R.layout.connect_dialog);


        Button dialogButton = (Button) dialog.findViewById(R.id.okButton);
        trate = (EditText) dialog.findViewById(R.id.cname);
        TextView heading=(TextView) dialog.findViewById(R.id.heading);
        heading.setText("Enter sell target price ");


        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Float value=Float.parseFloat(trate.getText().toString());
                account.setSTarget(company,value);
                dialog.dismiss();

            }
        });
        dialog.show();
        Calendar cal=Calendar.getInstance();
        Intent intent = new Intent(Chart.this, TimedService.class);
        intent.putExtra("company",company);
        intent.putExtra("type","sell_target");
        intent.putExtra("dbName",dbName);
        PendingIntent pintent = PendingIntent.getService(Chart.this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 300*1000, pintent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chart_bar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.icon_refresh:
            {
                Intent intent=getIntent();
                finish();
                overridePendingTransition(0,0);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
            break;
        }
        return true;
    }
}
