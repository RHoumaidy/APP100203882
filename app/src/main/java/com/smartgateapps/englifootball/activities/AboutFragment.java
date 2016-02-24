package com.smartgateapps.englifootball.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smartgateapps.englifootball.R;

/**
 * Created by Raafat on 19/01/2016.
 */
public class AboutFragment extends Fragment implements View.OnClickListener {

    private ImageView facebookImgView;
    private ImageView webImgView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        facebookImgView = (ImageView) view.findViewById(R.id.facebookImgView);
        webImgView = (ImageView) view.findViewById(R.id.webImgView);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        facebookImgView.setOnClickListener(this);
        webImgView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.facebookImgView:
                Intent intent1 = getOpenFacebookIntent(getActivity(), "https://www.facebook.com/smartgateapps");
                startActivity(intent1);
                break;

            case R.id.webImgView:
                startActivity(getOpenWebPage("http://www.smartgateapps.com/"));
                break;


        }

    }

    public static Intent getOpenFacebookIntent(Context context, String url) {

        PackageManager pm = context.getPackageManager();
        Uri uri;
        try {
            pm.getPackageInfo("com.facebook.katana", 0);
            uri = Uri.parse("fb://facewebmodal/f?href=" + url);
        } catch (PackageManager.NameNotFoundException e) {
            uri = Uri.parse(url);
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static Intent getOpenWebPage(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static Intent getSendEmail(String to, String subject, String message) {
        Intent email = new Intent(Intent.ACTION_SENDTO);
        String uriText =
                "mailto:" +to+
                        "?subject=" + Uri.encode(subject) +
                        "&body=" + Uri.encode(message);
        email.setData(Uri.parse(uriText));
        return email;


    }

}
