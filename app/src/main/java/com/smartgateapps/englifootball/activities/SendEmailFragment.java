package com.smartgateapps.englifootball.activities;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.smartgateapps.englifootball.R;


public class SendEmailFragment extends android.support.v4.app.Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_send_email, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button sendButton = (Button) view.findViewById(R.id.sendBtn);
        final EditText titleEdTx = (EditText) view.findViewById(R.id.titleEdTxt);
        final EditText messgeEdTx = (EditText) view.findViewById(R.id.bodyEdTxt);
        final String toEmail = "apps@smartgateapps.com";

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageInfo pInfo = null;
                try {
                    pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                String version = pInfo.versionName;
                startActivity(AboutFragment.getSendEmail(toEmail,titleEdTx.getText().toString()+"  ("+getString(R.string.app_name)+" - "+ version+")",
                        messgeEdTx.getText().toString()));
            }
        });

    }
}
