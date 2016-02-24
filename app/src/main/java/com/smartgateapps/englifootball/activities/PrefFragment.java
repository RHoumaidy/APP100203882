package com.smartgateapps.englifootball.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import com.smartgateapps.englifootball.R;
import com.smartgateapps.englifootball.model.Legue;
import com.smartgateapps.englifootball.engli.MyApplication;

import java.util.List;

/**
 * Created by Raafat on 12/01/2016.
 */
public class PrefFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private List<Legue> allLegues;

    private SwitchPreference primiumLeaguePref,
            expertsCupPref,
            unionCupPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);


        primiumLeaguePref = (SwitchPreference) findPreference(getString(R.string.premium_league_notificatin_pref_key));
        expertsCupPref = (SwitchPreference) findPreference(getString(R.string.experts_cup_notification_pref_key));
        unionCupPref = (SwitchPreference) findPreference(getString(R.string.union_cup_notification_pref_key));

        boolean premiumLeagueNotification = MyApplication.pref.getBoolean(getString(R.string.premium_league_notificatin_pref_key), false);
        boolean expertsCupNotification = MyApplication.pref.getBoolean(getString(R.string.experts_cup_notification_pref_key), false);
        boolean unionCupNotification = MyApplication.pref.getBoolean(getString(R.string.union_cup_notification_pref_key), false);

        primiumLeaguePref.setChecked(premiumLeagueNotification);
        expertsCupPref.setChecked(expertsCupNotification);
        unionCupPref.setChecked(unionCupNotification);

    }

    @Override
    public void onResume() {
        super.onResume();

        primiumLeaguePref.setOnPreferenceChangeListener(this);
        expertsCupPref.setOnPreferenceChangeListener(this);
        unionCupPref.setOnPreferenceChangeListener(this);

        MyApplication.pref.registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        MyApplication.pref.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        String key = preference.getKey();
        if (preference instanceof SwitchPreference) {
            boolean value = (boolean) newValue;

            if (key.equalsIgnoreCase(getString(R.string.premium_league_notificatin_pref_key))) {
                MyApplication
                        .pref.
                        edit()
                        .putBoolean(getString(R.string.premium_league_notificatin_pref_key), value)
                        .apply();

            } else if (key.equalsIgnoreCase(getString(R.string.experts_cup_notification_pref_key))) {
                MyApplication
                        .pref.
                        edit()
                        .putBoolean(getString(R.string.experts_cup_notification_pref_key), value)
                        .apply();

            }else if(key.equalsIgnoreCase(getString(R.string.union_cup_notification_pref_key))){
                MyApplication.pref
                        .edit()
                        .putBoolean(getString(R.string.union_cup_notification_pref_key),value)
                        .apply();
            }
            return true;

        }
        return false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        boolean premiumLeagueNotification = MyApplication.pref.getBoolean(getString(R.string.premium_league_notificatin_pref_key), false);
        boolean expertsCupNotification = MyApplication.pref.getBoolean(getString(R.string.experts_cup_notification_pref_key), false);
        boolean unionCupNotification = MyApplication.pref.getBoolean(getString(R.string.union_cup_notification_pref_key), false);

        if (key.equalsIgnoreCase(getString(R.string.premium_league_notificatin_pref_key))) {
            primiumLeaguePref.setChecked(premiumLeagueNotification);

        } else if (key.equalsIgnoreCase(getString(R.string.experts_cup_notification_pref_key))) {
            expertsCupPref.setChecked(expertsCupNotification);

        }else if(key.equalsIgnoreCase(getString(R.string.union_cup_notification_pref_key))){
            unionCupPref.setChecked(unionCupNotification);
        }
    }
}


