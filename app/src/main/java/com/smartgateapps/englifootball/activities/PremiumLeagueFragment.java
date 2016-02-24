package com.smartgateapps.englifootball.activities;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseAnalytics;
import com.smartgateapps.englifootball.Adapter.ViewPagerAdapter;
import com.smartgateapps.englifootball.R;
import com.smartgateapps.englifootball.model.Legue;
import com.smartgateapps.englifootball.engli.MyApplication;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Raafat on 03/12/2015.
 */
public class PremiumLeagueFragment extends Fragment {

    private Fragment newsListFragment;
    private Fragment teamsFragment;
    private Fragment playerGoalerFragment;
    private Fragment placesFragmend;
    private Fragment matchFragment;

    private ViewPagerAdapter adapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private int leagueId = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {

            newsListFragment = new NewsListFragment();
            teamsFragment = new TeamListFragment();
            playerGoalerFragment = new PlayerGoalerFragment();
            placesFragmend = new PlacedListFragment();
            matchFragment = new MatchFragment();

            Bundle args = new Bundle();
            args.putString("URL_EXT", MyApplication.PRIMIUM_LEAGUE_NEWS_EXT);
            args.putInt("RES", R.layout.news_card_layout_2);
            args.putInt("LEAGUE_ID", leagueId);
            newsListFragment.setArguments(args);

            Bundle args2 = new Bundle();
            args2.putString("URL_EXT", MyApplication.PRIMIUM_LEAGUE_EXT + MyApplication.SCORERS_CM);
            playerGoalerFragment.setArguments(args2);

            Bundle args3 = new Bundle();
            args3.putString("URL_EXT", MyApplication.PRIMIUM_LEAGUE_EXT + MyApplication.POSES_CM);
            placesFragmend.setArguments(args3);

            Bundle args4 = new Bundle();
            args4.putString("URL_EXT", MyApplication.PRIMIUM_LEAGUE_EXT + MyApplication.MATCHES_CM);
            args4.putInt("LEAGUE_ID", leagueId);
            matchFragment.setArguments(args4);

            Bundle args5 = new Bundle();
            args5.putString("URL_EXT", MyApplication.PRIMIUM_LEAGUE_EXT + MyApplication.TEAMS_CM);
            args5.putInt("LEAGUE_ID", leagueId);
            teamsFragment.setArguments(args5);

            adapter = new ViewPagerAdapter(getChildFragmentManager()); // not the parent fragmetmanager

            adapter.addFrag(newsListFragment, "أخبار");
            adapter.addFrag(playerGoalerFragment, "الهدافين");
            adapter.addFrag(teamsFragment, "الفرق");
            adapter.addFrag(matchFragment, "المباريات");
            adapter.addFrag(placesFragmend, "المراكز");

        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tabs_layout, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 3) {
                    Map<String, String> dimensions = new HashMap<>();
                    dimensions.put("category", "استعراض مباريات : " + Legue.load((long) leagueId).get(0).getName());
                    ParseAnalytics.trackEventInBackground("open_match", dimensions);
                    dimensions.put("category", "استعراض مباريات");
                    ParseAnalytics.trackEventInBackground("open_match", dimensions);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Map<String, String> dimensions = new HashMap<>();
        dimensions.put("category", "الدوري الإنكليزي الممتاز");
        dimensions.put("dayType", "weekday");
        ParseAnalytics.trackEventInBackground("open_league", dimensions);
        MyApplication.changeTabsFont(tabLayout);

        return view;

    }

}
