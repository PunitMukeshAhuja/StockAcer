package com.example.punit.stockacer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by punit on 18/5/18.
 */

public class HomeScreen extends Fragment {
    ListView ll;
    String dbName=null;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Home");
        ll=(ListView)getView().findViewById(R.id.ll);
        String stocks[]={"Nestle-NEST","SBI bank-SBIN","Wipro-WPRO","ICICI bank-ICICIBC","Reliance-RIL","L&T-LT"};

        ll.setAdapter(new ArrayAdapter<String>(getActivity(),R.layout.stock,R.id.label,stocks));
                ll.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String company=((TextView)view).getText().toString();
                Intent intent=new Intent(getActivity(),Chart.class);
                String display[]=company.split("-");
                String symbol=display[1];
                intent.putExtra("company",symbol);
                intent.putExtra("dbName",dbName);
                startActivity(intent);
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        dbName=getArguments().getString("dbName");
        return inflater.inflate(R.layout.content_home_screen,container,false);
    }
}
