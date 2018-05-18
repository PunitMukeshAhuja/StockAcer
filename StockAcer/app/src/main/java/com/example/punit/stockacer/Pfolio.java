package com.example.punit.stockacer;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by punit on 15/5/18.
 */

public class Pfolio extends android.support.v4.app.Fragment {
    Account account;
    ArrayList<HashMap<String,String>> list;
    TableLayout table;
    String dbName=null;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Portfolio");
        table=(TableLayout)getView().findViewById(R.id.portfolio);
        TextView tv=(TextView)getView().findViewById(R.id.tv);
        tv.setTextColor(Color.YELLOW);
        //String dbName=getIntent().getStringExtra("dbName");
        account=new Account(getActivity(),dbName,1);
        list=account.getPortfolio();
        if(list.size()>0) {
            TextView company = new TextView(getActivity());
            company.setText("Stock| ");
            company.setTextColor(Color.WHITE);
            TextView n = new TextView(getActivity());
            n.setText("No.OfShares| ");
            n.setTextColor(Color.WHITE);
            TextView c = new TextView(getActivity());
            c.setText("Cost| ");
            c.setTextColor(Color.WHITE);
            TextView s = new TextView(getActivity());
            s.setText("SellingPrice| ");
            s.setTextColor(Color.WHITE);
            TextView pl = new TextView(getActivity());
            pl.setText("Profit/Loss ");
            pl.setTextColor(Color.WHITE);
            TableRow header = new TableRow(getActivity());
            header.addView(company);
            header.addView(n);
            header.addView(c);
            header.addView(s);
            header.addView(pl);
            table.addView(header);

            for (HashMap<String, String> m : list) {
                TableRow row = new TableRow(getActivity());
                TextView co = new TextView(getActivity());
                co.setText(m.get("CName"));
                co.setTextColor(Color.WHITE);
                TextView ns = new TextView(getActivity());
                ns.setText(m.get("Units"));
                ns.setTextColor(Color.WHITE);
                TextView cprice = new TextView(getActivity());
                cprice.setText(m.get("CPrice"));
                cprice.setTextColor(Color.WHITE);
                TextView sprice = new TextView(getActivity());
                sprice.setText(m.get("SPrice"));
                sprice.setTextColor(Color.WHITE);
                TextView pnl = new TextView(getActivity());
                pnl.setText(m.get("PL"));
                pnl.setTextColor(Color.WHITE);
                row.addView(co);
                row.addView(ns);
                row.addView(cprice);
                row.addView(sprice);
                row.addView(pnl);
                table.addView(row);



            }
        }else {
            tv.setText("No transactions performed ");
        }


        tv.setText(tv.getText()+"Balance : "+account.getBalance());





    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        dbName=getArguments().getString("dbName");
        return inflater.inflate(R.layout.activity_portfolio,container,false);
    }
}
