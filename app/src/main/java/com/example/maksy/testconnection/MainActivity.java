package com.example.maksy.testconnection;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ArrayList<ClassListItems> itemArrayList;
    private MyAppAdapter myAppAdapter;
    private ListView listView;
    private boolean success = false;
    private ConnectionClass connectionClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        connectionClass = new ConnectionClass();

        SyncData orderData = new SyncData();
        orderData.execute("");
    }

    private class SyncData extends AsyncTask<String, String, String>
    {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details!";
        ProgressDialog progress;

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(MainActivity.this, "Synchronising",
                    "Listview Loading! Please Wait...", true);
        }

        @Override
        protected String doInBackground(String... strings)  // Connect to the database, write query and add items to array list
        {
            try
            {
                Connection conn = connectionClass.CONN(); //Connection Object
                if (conn == null)
                {
                    success = false;
                }
                else {
                    // Change below query according to your own database.
                    String query = "SELECT SensorName FROM Data_from_sensors";
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs != null)
                    {
                        while (rs.next())
                        {
                            try {
                                itemArrayList.add(new ClassListItems(rs.getString("sensorName")));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        msg = "Found";
                        success = true;
                    } else {
                        msg = "No Data found!";
                        success = false;
                    }
                }
            } catch (Exception e)
            {
                e.printStackTrace();
                Writer writer = new StringWriter();
                e.printStackTrace(new PrintWriter(writer));
                msg = writer.toString();
                success = false;
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg)
        {
            progress.dismiss();
            Toast.makeText(MainActivity.this, msg + "", Toast.LENGTH_LONG).show();
            if (!success)
            {
            }
            else {
                try {
                    myAppAdapter = new MyAppAdapter(itemArrayList, MainActivity.this);
                    listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    listView.setAdapter(myAppAdapter);
                } catch (Exception ex)
                {

                }
            }
        }
    }
    public class MyAppAdapter extends BaseAdapter         //has a class viewholder which holds
    {
        public class ViewHolder
        {
            TextView textName;
        }

        public List<ClassListItems> sensorList;

        public Context context;
        ArrayList<ClassListItems> arraylist;

        private MyAppAdapter(List<ClassListItems> sensorList, Context context)
        {
            this.sensorList = sensorList;
            this.context = context;

            arraylist.addAll(sensorList);
        }

        @Override
        public int getCount() {
            return sensorList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) // inflating the layout and initializing widgets
        {

            View rowView = convertView;
            ViewHolder viewHolder;
            if (rowView == null)
            {
                LayoutInflater inflater = getLayoutInflater();
                rowView = inflater.inflate(R.layout.list_content, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.textName = (TextView) rowView.findViewById(R.id.textName);
                rowView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.textName.setText(sensorList.get(position).getSensorName()+"");
            return rowView;
        }
    }

}
