package com.airport.flightsschedule.flightstatus.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airport.flightsschedule.flightstatus.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomDrawerDialog extends BottomSheetDialogFragment {

    private Context context;
    private ViewGroup root;
    private ImageView rateUs, shareApp, privacyPolicy;
    private TextView rateUsTxt, shareAppTxt,  privacyPolicyTxt;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_drawer_layout, container, false);
        rateUs = view.findViewById(R.id.rate_us);
        shareApp = view.findViewById(R.id.share_app);
        privacyPolicy = view.findViewById(R.id.privacy_policy);
        rateUsTxt = view.findViewById(R.id.rateUsTxt);
        shareAppTxt = view.findViewById(R.id.shareAppTxt);
        privacyPolicyTxt = view.findViewById(R.id.privacyPolicyTxt);
        root = container;
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rateUs.setOnClickListener(v -> rateApp());
        rateUsTxt.setOnClickListener(v -> rateApp());
        shareApp.setOnClickListener(v -> shareApp());
        shareAppTxt.setOnClickListener(v -> shareApp());
        privacyPolicy.setOnClickListener(v -> privacyPolicy());
        privacyPolicyTxt.setOnClickListener(v -> privacyPolicy());
    }

    private void rateApp() {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        View view = LayoutInflater.from(context).inflate(R.layout.rate_us_layout, root, false);
        ImageView nope = view.findViewById(R.id.nope);
        ImageView yup = view.findViewById(R.id.yup);
        TextView no = view.findViewById(R.id.no);
        TextView yes = view.findViewById(R.id.yes);
        no.setOnClickListener(v -> alertDialog.dismiss());
        nope.setOnClickListener(v -> alertDialog.dismiss());
        yes.setOnClickListener(v -> {
            rateUs();
            alertDialog.dismiss();
        });
        yup.setOnClickListener(v -> {
            rateUs();
            alertDialog.dismiss();
        });
        alertDialog.setView(view);
        if (alertDialog.getWindow() != null)
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        dismiss();
    }

    private void rateUs() {
        String rate_url = "https://play.google.com/store/apps/details?id=com.airport.flightsschedule.flightstatus";
        Intent rate_us = new Intent(Intent.ACTION_VIEW);
        rate_us.setData(Uri.parse(rate_url));
        context.startActivity(rate_us);
    }

    private void shareApp() {
        Intent share = new Intent(Intent.ACTION_SEND);
        String url = "https://play.google.com/store/apps/details?id=com.airport.flightsschedule.flightstatus";
        String shareText = getString(R.string.try_flight_tracker);
        share.setType("text/plain");
        share.setAction(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        share.putExtra(Intent.EXTRA_TEXT, shareText.concat("\n\n").concat(Uri.parse(url).toString()));
        context.startActivity(Intent.createChooser(share, getString(R.string.app_name)));
        dismiss();
    }

    private void privacyPolicy() {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        View view = LayoutInflater.from(context).inflate(R.layout.privacy_policy_layout, root, false);
        RadioButton privacyLink = view.findViewById(R.id.privacyLink);
        Button accept = view.findViewById(R.id.accept);
        accept.setText(getString(R.string.accept));
        privacyLink.setOnClickListener(v -> {
            String privacy_link_url = "http://developer.12dtechnology.com/privacypolicy.htm";
            Intent privacy_link = new Intent(Intent.ACTION_VIEW);
            privacy_link.setData(Uri.parse(privacy_link_url));
            try {
                context.startActivity(privacy_link);
            } catch (RuntimeException e) {
                Toast.makeText(context, context.getString(R.string.no_browser), Toast.LENGTH_SHORT).show();
            }
        });
        accept.setOnClickListener(v -> dialog.dismiss());
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setView(view);
        dialog.show();
        dismiss();
    }
}
