package com.airport.flightsschedule.flightstatus.flights;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airport.flightsschedule.flightstatus.R;
import com.airport.flightsschedule.flightstatus.activities.ArrivalsDeparturesActivity;
import com.airport.flightsschedule.flightstatus.utils.CSVFileReader;
import com.airport.flightsschedule.flightstatus.utils.ShowNativeAd;
import com.airport.flightsschedule.flightstatus.utils.ShowNativeAd.AdMobNativeAdLoadListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetFlights implements AdMobNativeAdLoadListener {
    private final Context context;
    private final ProgressBar loading;
    private final List<Object> recyclerViewItemsArrDep;

    private RecyclerView arrivalsRV, departuresRV;
    private RecyclerView.LayoutManager layoutManager;
    private JSONArray jsonArrayArrival, jsonArrayDeparture;
    private ArrDepAdapter arrDepAdapter;
    private OnAPIResponse onAPIResponse;
    private List<String[]> listAirports, listAirlines;
    private String responseType;

    private int index = 2;
    private int firstIndex = 2;
    private int count = -1;

    private int startingIndex = 0;
    private int startIndexArrival = 0;
    private int offsetIndexArrival = 20;
    private int startIndexDeparture = 0;
    private int offsetIndexDeparture = 20;

    private boolean isLoading;
    private boolean isFirstTime = true;

    public GetFlights(Context context, ProgressBar loading) {
        this.context = context;
        this.loading = loading;
        layoutManager = new LinearLayoutManager(context);
        recyclerViewItemsArrDep = new ArrayList<>();
        CSVFileReader csvReaderAirports = new CSVFileReader(context, "airports.csv");
        CSVFileReader csvReaderAirlines = new CSVFileReader(context, "airlines.csv");
        try {
            listAirports = csvReaderAirports.readCSV();
            listAirlines = csvReaderAirlines.readCSV();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private FlightClient createRetrofitClient() {
        String url = "https://aviation-edge.com/v2/public/";

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        return retrofit.create(FlightClient.class);
    }

    public void getAirportSchedule(RecyclerView flightsRecyclerView, String APIKey, String iataCode, String type) {
        FlightClient flightClient = createRetrofitClient();
        responseType = type;

        flightClient.getAirportSchedule(APIKey, iataCode, type).enqueue(new Callback<List<ArrivalsDepartures>>() {
            @Override
            public void onResponse(@NonNull Call<List<ArrivalsDepartures>> call, @NonNull Response<List<ArrivalsDepartures>> response) {
                if (type.equals("arrival")) {
                    if (response.body() != null) {
                        String jsonResponse = new Gson().toJson(response.body());
                        try {
                            jsonArrayArrival = new JSONArray(jsonResponse);
                            if (arrivalsRV == null)
                                arrivalsRV = flightsRecyclerView;
                            new LoadArrivalData(GetFlights.this).execute();
                        } catch (JSONException | RuntimeException e) {
                            e.printStackTrace();
                            Log.d("airport", "Exception: " + e.toString());
                        }
                    } else {
                        Snackbar.make(flightsRecyclerView, context.getString(R.string.no_flights_found), Snackbar.LENGTH_SHORT).show();
                        onAPIResponse.onAPIResponse("arrival");
                        ArrivalsDeparturesActivity.isLoading = false;
                    }
                } else {
                    if (response.body() != null) {
                        String jsonResponse = new Gson().toJson(response.body());
                        try {
                            jsonArrayDeparture = new JSONArray(jsonResponse);
                            if (departuresRV == null)
                                departuresRV = flightsRecyclerView;
                            new LoadDepartureData(GetFlights.this).execute();
                        } catch (JSONException | RuntimeException e) {
                            e.printStackTrace();
                            Log.d("airport", "Exception: " + e.toString());
                        }
                    } else {
                        Snackbar.make(flightsRecyclerView, context.getString(R.string.no_flights_found), Snackbar.LENGTH_SHORT).show();
                        onAPIResponse.onAPIResponse("departure");
                        ArrivalsDeparturesActivity.isLoading = false;
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ArrivalsDepartures>> call, @NonNull Throwable t) {
                loading.setVisibility(View.GONE);
                if (type.equals("arrival"))
                    onAPIResponse.onAPIResponse("arrival");
                else
                    onAPIResponse.onAPIResponse("departure");
                Snackbar.make(flightsRecyclerView, context.getString(R.string.failed_get_flights), Snackbar.LENGTH_SHORT).show();
                ArrivalsDeparturesActivity.isLoading = false;
                Log.d("onFailure", t.toString());
            }
        });
    }

    private void getAirportFlights(RecyclerView flightsRecyclerView, JSONArray jsonArray, int startIndex, int offsetIndex) {
        try {
            if (isFirstTime && offsetIndex > jsonArray.length()) offsetIndex = jsonArray.length();
            else count = 0;

            if (!isFirstTime)
                startingIndex = recyclerViewItemsArrDep.size();

            for (int i = startIndex; i < offsetIndex; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                JSONObject airlineObj = jsonObject.getJSONObject("airline");
                Airline airline = new Airline();
                airline.setIataCode(airlineObj.getString("iataCode"));
                airline.setIcaoCode(airlineObj.getString("icaoCode"));
                if (airlineObj.has("name"))
                    airline.setName(airlineObj.getString("name"));
                else {
                    for (int j = 0; j < listAirlines.size(); j++) {
                        String icaoCode = listAirlines.get(j)[1];
                        try {
                            if (icaoCode != null && icaoCode.matches(airlineObj.getString("icaoCode"))) {
                                airline.setName(listAirlines.get(j)[0]);
                                break;
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }

                JSONObject arrivalObj = jsonObject.getJSONObject("arrival");
                Arrival arrival = new Arrival();
                arrival.setIataCode(arrivalObj.getString("iataCode"));
                arrival.setIcaoCode(arrivalObj.getString("icaoCode"));
                if (arrivalObj.has("actualRunway")) {
                    try {
                        arrival.setActualRunway(arrivalObj.getString("actualRunway"));
                    } catch (RuntimeException e) {
                        arrival.setActualRunway("N/A");
                    }
                } else {
                    arrival.setActualRunway("N/A");
                }
                if (arrivalObj.has("actualTime")) {
                    try {
                        arrival.setActualTime(arrivalObj.getString("actualTime"));
                    } catch (RuntimeException e) {
                        arrival.setActualTime("N/A");
                    }
                } else {
                    arrival.setActualTime("N/A");
                }
                if (arrivalObj.has("estimatedRunway")) {
                    try {
                        arrival.setEstimatedRunway(arrivalObj.getString("estimatedRunway"));
                    } catch (RuntimeException e) {
                        arrival.setEstimatedRunway("N/A");
                    }
                } else {
                    arrival.setEstimatedRunway("N/A");
                }
                if (arrivalObj.has("estimatedTime")) {
                    try {
                        arrival.setEstimatedTime(arrivalObj.getString("estimatedTime"));
                    } catch (RuntimeException e) {
                        arrival.setEstimatedTime("N/A");
                    }
                } else {
                    arrival.setEstimatedTime("N/A");
                }
                if (arrivalObj.has("scheduledTime")) {
                    try {
                        arrival.setScheduledTime(arrivalObj.getString("scheduledTime"));
                    } catch (RuntimeException e) {
                        arrival.setScheduledTime("N/A");
                    }
                } else {
                    arrival.setScheduledTime("N/A");
                }
                if (arrivalObj.has("gate")) {
                    try {
                        arrival.setGate(arrivalObj.getString("gate"));
                    } catch (RuntimeException e) {
                        arrival.setGate("N/A");
                    }
                } else {
                    arrival.setGate("N/A");
                }
                if (arrivalObj.has("terminal")) {
                    try {
                        arrival.setTerminal(arrivalObj.getString("terminal"));
                    } catch (RuntimeException e) {
                        arrival.setTerminal("N/A");
                    }
                } else {
                    arrival.setTerminal("N/A");
                }
                if (arrivalObj.has("baggage")) {
                    try {
                        arrival.setBaggage(arrivalObj.getString("baggage"));
                    } catch (RuntimeException e) {
                        arrival.setBaggage("N/A");
                    }
                } else {
                    arrival.setBaggage("N/A");
                }
                arrival.setAirportName("N/A");
                arrival.setArrivalCity("N/A");
                arrival.setArrivalCountry("N/A");
                for (int j = 0; j < listAirports.size(); j++) {
                    String codeICAO = listAirports.get(j)[2];
                    String codeIATA = listAirports.get(j)[3];
                    try {
                        if (codeICAO != null && !codeICAO.matches("") && codeICAO.matches(arrivalObj.getString("icaoCode"))) {
                            arrival.setArrivalCity(listAirports.get(j)[0]);
                            arrival.setArrivalCountry(listAirports.get(j)[1]);
                            arrival.setAirportName(listAirports.get(j)[4]);
                            break;
                        } else if (codeIATA != null && !codeIATA.matches("") && codeIATA.matches(arrivalObj.getString("iataCode"))) {
                            arrival.setArrivalCity(listAirports.get(j)[0]);
                            arrival.setArrivalCountry(listAirports.get(j)[1]);
                            arrival.setAirportName(listAirports.get(j)[4]);
                            break;
                        }
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                JSONObject departureObj = jsonObject.getJSONObject("departure");
                Departure departure = new Departure();
                departure.setIataCode(departureObj.getString("iataCode"));
                departure.setIcaoCode(departureObj.getString("icaoCode"));
                if (departureObj.has("actualRunway")) {
                    try {
                        departure.setActualRunway(departureObj.getString("actualRunway"));
                    } catch (RuntimeException e) {
                        departure.setActualRunway("N/A");
                    }
                } else {
                    departure.setActualRunway("N/A");
                }
                if (departureObj.has("actualTime")) {
                    try {
                        departure.setActualTime(departureObj.getString("actualTime"));
                    } catch (RuntimeException e) {
                        departure.setActualTime("N/A");
                    }
                } else {
                    departure.setActualTime("N/A");
                }
                if (departureObj.has("estimatedRunway")) {
                    try {
                        departure.setEstimatedRunway(departureObj.getString("estimatedRunway"));
                    } catch (RuntimeException e) {
                        departure.setEstimatedRunway("N/A");
                    }
                } else {
                    departure.setEstimatedRunway("N/A");
                }
                if (departureObj.has("estimatedTime")) {
                    try {
                        departure.setEstimatedTime(departureObj.getString("estimatedTime"));
                    } catch (RuntimeException e) {
                        departure.setEstimatedTime("N/A");
                    }
                } else {
                    departure.setEstimatedTime("N/A");
                }
                if (departureObj.has("scheduledTime")) {
                    try {
                        departure.setScheduledTime(departureObj.getString("scheduledTime"));
                    } catch (RuntimeException e) {
                        departure.setScheduledTime("N/A");
                    }
                } else {
                    departure.setScheduledTime("N/A");
                }
                if (departureObj.has("gate")) {
                    try {
                        departure.setGate(departureObj.getString("gate"));
                    } catch (RuntimeException e) {
                        departure.setGate("N/A");
                    }
                } else {
                    departure.setGate("N/A");
                }
                if (departureObj.has("terminal")) {
                    try {
                        departure.setTerminal(departureObj.getString("terminal"));
                    } catch (RuntimeException e) {
                        departure.setTerminal("N/A");
                    }
                } else {
                    departure.setTerminal("N/A");
                }
                if (departureObj.has("baggage")) {
                    try {
                        departure.setBaggage(departureObj.getString("baggage"));
                    } catch (RuntimeException e) {
                        departure.setBaggage("N/A");
                    }
                } else {
                    departure.setBaggage("N/A");
                }
                departure.setAirportName("N/A");
                departure.setCity("N/A");
                departure.setCountry("N/A");
                for (int j = 0; j < listAirports.size(); j++) {
                    String codeICAO = listAirports.get(j)[2];
                    String codeIATA = listAirports.get(j)[3];
                    try {
                        if (codeICAO != null && !codeICAO.matches("") && codeICAO.matches(departureObj.getString("icaoCode"))) {
                            departure.setCity(listAirports.get(j)[0]);
                            departure.setCountry(listAirports.get(j)[1]);
                            departure.setAirportName(listAirports.get(j)[4]);
                            break;
                        } else if (codeIATA != null && !codeIATA.matches("") && codeIATA.matches(departureObj.getString("iataCode"))) {
                            departure.setCity(listAirports.get(j)[0]);
                            departure.setCountry(listAirports.get(j)[1]);
                            departure.setAirportName(listAirports.get(j)[4]);
                            break;
                        }
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                JSONObject flightObj = jsonObject.getJSONObject("flight");
                Flight flight = new Flight();
                flight.setIataNumber(flightObj.getString("iataNumber"));
                flight.setIcaoNumber(flightObj.getString("icaoNumber"));
                if (flightObj.has("number"))
                    flight.setNumber(flightObj.getString("number"));
                else
                    flight.setNumber("N/A");

                String status = "N/A";
                if (jsonObject.has("status")) {
                    status = jsonObject.getString("status");
                }

                ArrivalsDepartures arrivalsDeparture = new ArrivalsDepartures();
                arrivalsDeparture.setAirline(airline);
                arrivalsDeparture.setArrival(arrival);
                arrivalsDeparture.setDeparture(departure);
                arrivalsDeparture.setFlight(flight);
                arrivalsDeparture.setStatus(status);

                this.recyclerViewItemsArrDep.add(arrivalsDeparture);
                if (count != -1) count++;
            }
            addAdMobNativeAds();
            loadAdMobNativeAdsArrDep(firstIndex);
            if (isFirstTime) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    arrDepAdapter = new ArrDepAdapter(context, recyclerViewItemsArrDep, loading);
                    layoutManager = new LinearLayoutManager(context);
                    flightsRecyclerView.setLayoutManager(layoutManager);
                    flightsRecyclerView.setAdapter(arrDepAdapter);
                    if (responseType.equals("arrival"))
                        onAPIResponse.onAPIResponse("arrival");
                    else
                        onAPIResponse.onAPIResponse("departure");
                    ArrivalsDeparturesActivity activity = (ArrivalsDeparturesActivity) context;
                    activity.runOnUiThread(() -> loading.setVisibility(View.GONE));
                }, 2000);
                flightsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (arrDepAdapter != null) {
                            if (!isLoading && ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition() == arrDepAdapter.getItemCount() - 1) {
                                isLoading = true;
                                isFirstTime = false;
                                firstIndex = index;
                                if (responseType.equals("arrival")) {
                                    startIndexArrival += 20;
                                    offsetIndexArrival += 20;
                                    if (startIndexArrival < jsonArrayArrival.length()) {
                                        if (offsetIndexArrival > jsonArrayArrival.length()) {
                                            offsetIndexArrival = jsonArrayArrival.length();
                                        }
                                        onAPIResponse.onDataRequest("arrival");
                                        loading.setVisibility(View.VISIBLE);
                                        new LoadArrivalData(GetFlights.this).execute();
                                    }
                                } else {
                                    startIndexDeparture += 20;
                                    offsetIndexDeparture += 20;
                                    if (startIndexDeparture < jsonArrayDeparture.length()) {
                                        if (offsetIndexDeparture > jsonArrayDeparture.length()) {
                                            offsetIndexDeparture = jsonArrayDeparture.length();
                                        }
                                        onAPIResponse.onDataRequest("departure");
                                        loading.setVisibility(View.VISIBLE);
                                        new LoadDepartureData(GetFlights.this).execute();
                                    }
                                }
                            }
                        }
                    }
                });
            } else {
                if (responseType.equals("arrival")) {
                    arrDepAdapter.notifyItemRangeInserted(startingIndex, count);
                    onAPIResponse.onAPIResponse("arrival");
                } else {
                    arrDepAdapter.notifyItemRangeInserted(startingIndex, count);
                    onAPIResponse.onAPIResponse("departure");
                }
                loading.setVisibility(View.GONE);
                count = 0;
            }
            isLoading = false;
        } catch (JSONException | RuntimeException e) {
            isLoading = false;
            ArrivalsDeparturesActivity activity = (ArrivalsDeparturesActivity) context;
            activity.runOnUiThread(() -> loading.setVisibility(View.GONE));
            if (responseType.equals("arrival"))
                onAPIResponse.onAPIResponse("arrival");
            else
                onAPIResponse.onAPIResponse("departure");
            Snackbar.make(flightsRecyclerView, context.getString(R.string.error_occurred), Snackbar.LENGTH_SHORT).show();
            ArrivalsDeparturesActivity.isLoading = false;
            Log.d("Exception", e.toString());
        }
    }

    public void setArrivalsRV(RecyclerView arrivalsRV) {
        this.arrivalsRV = arrivalsRV;
    }

    public void setDeparturesRV(RecyclerView departuresRV) {
        this.departuresRV = departuresRV;
    }

    public interface OnAPIResponse {
        void onAPIResponse(String type);

        void onDataRequest(String type);
    }

    public void setOnAPIResponse(OnAPIResponse onAPIResponse) {
        this.onAPIResponse = onAPIResponse;
    }

    private void addAdMobNativeAds() {
        if (recyclerViewItemsArrDep.size() > index) {
            while (index < recyclerViewItemsArrDep.size()) {
                LinearLayout adUnifiedLayout = new LinearLayout(context);
                recyclerViewItemsArrDep.add(index, adUnifiedLayout);
                if (count != -1) count++;
                index += 3;
            }
        }
    }

    private void loadAdMobNativeAdsArrDep(final int index) {
        if (index >= recyclerViewItemsArrDep.size()) {
            return;
        }
        Object item = recyclerViewItemsArrDep.get(index);
        if (!(item instanceof LinearLayout)) {
            LinearLayout adUnifiedLayout = new LinearLayout(context);
            recyclerViewItemsArrDep.set(index, adUnifiedLayout);
        } else {
            LinearLayout adUnifiedLayout = (LinearLayout) item;
            ShowNativeAd showNativeAd = new ShowNativeAd(context);
            showNativeAd.setNativeAdLoadListener(this);
            showNativeAd.showAdMobNativeAd(adUnifiedLayout);
        }
    }

    @Override
    public void onNativeAdLoaded() {
        int ITEMS_PER_AD = 3;
        if (recyclerViewItemsArrDep.size() > 3)
            loadAdMobNativeAdsArrDep(firstIndex += ITEMS_PER_AD);
    }

    private static class LoadArrivalData extends AsyncTask<Void, Void, Void> {

        private final WeakReference<GetFlights> weakReference;

        public LoadArrivalData(GetFlights getFlights) {
            weakReference = new WeakReference<>(getFlights);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ArrivalsDeparturesActivity.isLoading = true;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            GetFlights getFlights = weakReference.get();
            getFlights.getAirportFlights(getFlights.arrivalsRV, getFlights.jsonArrayArrival, getFlights.startIndexArrival, getFlights.offsetIndexArrival);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            ArrivalsDeparturesActivity.isLoading = false;
        }
    }

    private static class LoadDepartureData extends AsyncTask<Void, Void, Void> {

        private final WeakReference<GetFlights> weakReference;

        public LoadDepartureData(GetFlights getFlights) {
            weakReference = new WeakReference<>(getFlights);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ArrivalsDeparturesActivity.isLoading = true;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            GetFlights getFlights = weakReference.get();
            getFlights.getAirportFlights(getFlights.departuresRV, getFlights.jsonArrayDeparture, getFlights.startIndexDeparture, getFlights.offsetIndexDeparture);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            ArrivalsDeparturesActivity.isLoading = false;
        }
    }
}
