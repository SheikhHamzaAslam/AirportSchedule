package com.airport.flightsschedule.flightstatus.flights;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.airport.flightsschedule.flightstatus.activities.SplashActivity;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.airport.flightsschedule.flightstatus.activities.ArrivalsDeparturesActivity;
import com.airport.flightsschedule.flightstatus.activities.FlightDetailsActivity;
import com.airport.flightsschedule.flightstatus.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ArrDepAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final ProgressBar loading;
    private final List<Object> recyclerViewItems;
    private InterstitialAd mAdMobInterstitial;

    private int pos;
    private static final int ITEM_VIEW_TYPE = 0;
    private static final int ADMOB_NATIVE_AD_VIEW = 2;

    public ArrDepAdapter(Context context, List<Object> recyclerViewItems, ProgressBar loading) {
        this.context = context;
        this.loading = loading;
        this.recyclerViewItems = recyclerViewItems;
        requestAdMobInterstitial();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case ADMOB_NATIVE_AD_VIEW:
                View nativeAdView = LayoutInflater.from(context).inflate(R.layout.native_ad_item, parent, false);
                return new AdMobNativeAdHolder(nativeAdView);

            case ITEM_VIEW_TYPE:
            default:
                View view = LayoutInflater.from(context).inflate(R.layout.flight_item, parent, false);
                return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ADMOB_NATIVE_AD_VIEW:
                AdMobNativeAdHolder nativeAdHolder = (AdMobNativeAdHolder) holder;
                try {
                    LinearLayout adUnifiedLayout = (LinearLayout) recyclerViewItems.get(position);
                    ViewGroup viewGroup = (ViewGroup) nativeAdHolder.itemView;
                    if (viewGroup.getChildCount() > 0) {
                        viewGroup.removeAllViews();
                    }
                    if (adUnifiedLayout.getParent() != null) {
                        ((ViewGroup) adUnifiedLayout.getParent()).removeView(adUnifiedLayout);
                    }
                    viewGroup.addView(adUnifiedLayout);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
                break;

            case ITEM_VIEW_TYPE:
            default:
                ViewHolder viewHolder = (ViewHolder) holder;
                ArrivalsDepartures arrivalsDeparture = (ArrivalsDepartures) recyclerViewItems.get(position);
                String iataCode = arrivalsDeparture.getFlight().getIataNumber();
                String icaoCode = arrivalsDeparture.getFlight().getIataNumber();
                String not_ava = "N/A";
                if (iataCode != null && !iataCode.isEmpty())
                    viewHolder.callSignTV.setText(iataCode);
                else if (icaoCode != null && !icaoCode.isEmpty())
                    viewHolder.callSignTV.setText(icaoCode);
                else
                    viewHolder.callSignTV.setText(not_ava);
                viewHolder.depAirportCode.setText(arrivalsDeparture.getDeparture().getIataCode());
                viewHolder.arrAirportCode.setText(arrivalsDeparture.getArrival().getIataCode());
                viewHolder.depAirportNameTV.setText(arrivalsDeparture.getDeparture().getAirportName());
                viewHolder.arrAirportNameTV.setText(arrivalsDeparture.getArrival().getAirportName());

                viewHolder.planeDetails.setOnClickListener(v -> {
                    pos = holder.getAdapterPosition();
                    if (mAdMobInterstitial != null) {
                        ArrivalsDeparturesActivity activity = (ArrivalsDeparturesActivity) context;
                        mAdMobInterstitial.show(activity);
                        mAdMobInterstitial.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                super.onAdFailedToShowFullScreenContent(adError);
                                sendFlightData(pos);
                                requestAdMobInterstitial();
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                sendFlightData(pos);
                                requestAdMobInterstitial();
                            }
                        });
                    } else
                        sendFlightData(pos);
                    loading.setVisibility(View.VISIBLE);
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return recyclerViewItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (recyclerViewItems.get(position) instanceof LinearLayout)
            return ADMOB_NATIVE_AD_VIEW;
        else
            return ITEM_VIEW_TYPE;
    }

    private void sendFlightData(int finalI) {
        Intent flightDetails = new Intent(context, FlightDetailsActivity.class);
        ArrivalsDepartures arrivalsDeparture = (ArrivalsDepartures) recyclerViewItems.get(finalI);
        flightDetails.putExtra("flightNumberIata", arrivalsDeparture.getFlight().getIataNumber());
        flightDetails.putExtra("flightNumberIcao", arrivalsDeparture.getFlight().getIcaoNumber());
        flightDetails.putExtra("airlineIata", arrivalsDeparture.getAirline().getIataCode());
        flightDetails.putExtra("airlineIcao", arrivalsDeparture.getAirline().getIcaoCode());
        flightDetails.putExtra("airlineName", arrivalsDeparture.getAirline().getName());
        flightDetails.putExtra("depAirportIata", arrivalsDeparture.getDeparture().getIataCode());
        flightDetails.putExtra("depAirportName", arrivalsDeparture.getDeparture().getAirportName());
        flightDetails.putExtra("depCity", arrivalsDeparture.getDeparture().getCity());
        flightDetails.putExtra("depCountry", arrivalsDeparture.getDeparture().getCountry());
        flightDetails.putExtra("arrAirportIata", arrivalsDeparture.getArrival().getIataCode());
        flightDetails.putExtra("arrAirportName", arrivalsDeparture.getArrival().getAirportName());
        flightDetails.putExtra("arrCity", arrivalsDeparture.getArrival().getArrivalCity());
        flightDetails.putExtra("arrCountry", arrivalsDeparture.getArrival().getArrivalCountry());
        flightDetails.putExtra("departureTerminal", arrivalsDeparture.getDeparture().getTerminal());
        flightDetails.putExtra("departureGate", arrivalsDeparture.getDeparture().getGate());
        flightDetails.putExtra("departureBaggage", arrivalsDeparture.getDeparture().getBaggage());
        flightDetails.putExtra("estimatedDeparture", arrivalsDeparture.getDeparture().getEstimatedTime());
        flightDetails.putExtra("scheduledDeparture", arrivalsDeparture.getDeparture().getScheduledTime());
        flightDetails.putExtra("departureTime", arrivalsDeparture.getDeparture().getActualTime());
        flightDetails.putExtra("arrivalTerminal", arrivalsDeparture.getArrival().getTerminal());
        flightDetails.putExtra("arrivalGate", arrivalsDeparture.getArrival().getGate());
        flightDetails.putExtra("arrivalBaggage", arrivalsDeparture.getArrival().getBaggage());
        flightDetails.putExtra("estimatedArrival", arrivalsDeparture.getArrival().getEstimatedTime());
        flightDetails.putExtra("scheduledArrival", arrivalsDeparture.getArrival().getScheduledTime());
        flightDetails.putExtra("arrivalTime", arrivalsDeparture.getArrival().getActualTime());
        flightDetails.putExtra("status", arrivalsDeparture.getStatus());

        FlightClient flightClient = createRetrofitClient();
        flightClient.getLiveFlightsByFlightNumber(SplashActivity.APIKey, arrivalsDeparture.getFlight().getIataNumber()).
                enqueue(new Callback<List<LiveFlight>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<LiveFlight>> call, @NonNull Response<List<LiveFlight>> response) {
                        if (response.body() != null) {
                            ArrayList<LiveFlight> liveFlights = new ArrayList<>(response.body());
                            flightDetails.putExtra("aircraftRegistration", liveFlights.get(0).getAircraft().getRegNumber());
                            loading.setVisibility(View.GONE);
                            context.startActivity(flightDetails);
                            AppCompatActivity activity = (AppCompatActivity) context;
                            activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                        } else {
                            sendFlightData(flightDetails);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<LiveFlight>> call, @NonNull Throwable t) {
                        sendFlightData(flightDetails);
                    }
                });
    }

    private void sendFlightData(Intent flightDetails) {
        flightDetails.putExtra("aircraftRegistration", "N/A");
        flightDetails.putExtra("planeSpeed", "N/A");
        flightDetails.putExtra("heading", "N/A");
        flightDetails.putExtra("altitude", "N/A");
        flightDetails.putExtra("latitude", "N/A");
        flightDetails.putExtra("longitude", "N/A");
        flightDetails.putExtra("status", "N/A");
        loading.setVisibility(View.GONE);
        context.startActivity(flightDetails);
        AppCompatActivity activity = (AppCompatActivity) context;
        activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
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

    private void requestAdMobInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        com.google.android.gms.ads.interstitial.InterstitialAd.load(
                context, context.getString(R.string.main_interstitial_ad), adRequest, new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                        super.onAdLoaded(interstitialAd);
                        mAdMobInterstitial = interstitialAd;
                        Log.v("Ad", "AdMob Interstitial Ad Successfully Loaded!");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        mAdMobInterstitial = null;
                        Log.v("Ad", "AdMob Interstitial Ad Failed to Load!");
                    }
                }
        );
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        TextView callSignTV, depAirportCode, depAirportNameTV, arrAirportCode, arrAirportNameTV;
        Button planeDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            callSignTV = itemView.findViewById(R.id.callSign);
            depAirportCode = itemView.findViewById(R.id.depAirportCode);
            depAirportNameTV = itemView.findViewById(R.id.depAirportName);
            arrAirportCode = itemView.findViewById(R.id.arrAirportCode);
            arrAirportNameTV = itemView.findViewById(R.id.arrAirportName);
            planeDetails = itemView.findViewById(R.id.planeDetails);
        }
    }

    public static class AdMobNativeAdHolder extends RecyclerView.ViewHolder {

        public AdMobNativeAdHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}