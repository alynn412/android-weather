package my.edu.utem.weather;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText cityEditText;
    CustomAdapter adapter;

    RecyclerView weatherView;
    TextView type, tempTextView, date;
    ImageView picture;
    //ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityEditText = findViewById(R.id.cityEditText);
        adapter = new CustomAdapter();

        RecyclerView recyclerView = findViewById(R.id.weatherrecyclerview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    public void getWeather(View view) {
        // Instantiate the RequestQueue.

        String city = cityEditText.getText().toString();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://api.openweathermap.org/data/2.5/forecast/daily?q="+city+",My&appid=9fd7a449d055dba26a982a3220f32aa2";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("debug",response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray weatherArray = jsonObject.getJSONArray("list");
                            for (int i = 0; i < weatherArray.length(); i++){
                                adapter.addWeather(weatherArray.getJSONObject(i));
                                //tempTextView.setText(weatherArray.getJSONObject(i).getString("temp"));
                            }

                            //refresh recyclerView
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView weatherTextView;
        TextView dateTextView;
        TextView tempTextView;

        public CustomViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.weatherlist, parent, false));
            imageView = itemView.findViewById(R.id.pictImageView);
            weatherTextView = itemView.findViewById(R.id.weatherTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            tempTextView = itemView.findViewById(R.id.tempTextView);
        }
    }

    public class CustomAdapter extends RecyclerView.Adapter<CustomViewHolder> {

        List<JSONObject> weatherList = new ArrayList<>();

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            return new CustomViewHolder(LayoutInflater.from(viewGroup.getContext()), viewGroup);
        }

        @Override
        public void onBindViewHolder(CustomViewHolder holder, int position) {

            JSONObject currentWeather = weatherList.get(position);
            try {
                holder.dateTextView.setText(""+currentWeather.getInt("dt"));
                holder.tempTextView.setText(", "+currentWeather.getJSONObject("temp").getDouble("day"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return weatherList.size();
        }

        public void addWeather(JSONObject weather){
            weatherList.add(weather);
        }
    }

}
