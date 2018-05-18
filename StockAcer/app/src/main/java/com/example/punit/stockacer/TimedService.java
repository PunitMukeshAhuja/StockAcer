package com.example.punit.stockacer;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by punit on 11/5/18.
 */

public class TimedService extends IntentService {
    String company=null;
    String header=null;
    Boolean target_reached=false;
    public TimedService() {
        super(TimedService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        HttpHandler sh = new HttpHandler();
        company = intent.getStringExtra("company");
        String dbName=intent.getStringExtra("dbName");
        String type=intent.getStringExtra("type");
        Float checkPrice=0f;
        Account account=new Account(this,dbName,1);
        Double lastPrice=0.0;
        String url="https://www.bloomberg.com/markets/api/bulk-time-series/price/"+company+":IN?timeFrame=1_DAY";

        // Making a request to url and getting response
        String jsonStr = sh.makeServiceCall(url);
        if(jsonStr!=null)
            jsonStr=jsonStr.substring(1,jsonStr.length()-1);
        else{
            Log.e("Unable to fetch data","response null");
        }

        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);

                // Getting JSON Array node
                JSONArray price = jsonObj.getJSONArray("price");
                JSONObject c = price.getJSONObject(price.length()-1);
                lastPrice=Double.parseDouble(c.getString("value"));
                Log.e("back json response",c.getString("value"));

            } catch (final JSONException e) {
                Log.e("json_parse_error", "Json parsing error: " + e.getMessage());
            }
        } else {
            Log.e("fetch_error", "Couldn't get json from server.");

        }
        if("buy_target".equals(type)){
            checkPrice=account.getBTarget(company);
            Log.e("BuyT status","checked");
            if(lastPrice<=checkPrice){
                header="Buy target reached for";
                target_reached=true;


            }
        }
        else if("sell_target".equals(type)){
            checkPrice=account.getSTarget(company);
            Log.e("SellT status","checked");
            if(lastPrice>=checkPrice && checkPrice!=-1){
                header="Sell target reached for";
                target_reached=true;

            }
        }
        if (target_reached) {
            Log.e("Target status","reached");
            IntentFilter inf = new IntentFilter("target.reached");
            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Intent int1 = new Intent(TimedService.this, Chart.class);
                    int1.putExtra("company", company);
                    PendingIntent in = PendingIntent.getActivity(context, 101, int1, PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification note = new Notification.Builder(context)
                            .setContentTitle(header)
                            .setContentText(company)
                            .setSmallIcon(R.mipmap.stock_acer_icon).setContentIntent(in)
                            .build();
                    nm.notify(1, note);

                }
            }, inf);
            sendBroadcast(new Intent("target.reached"));
        }
    }
}
