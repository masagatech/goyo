package com.crest.goyo.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crest.goyo.AdapterClasses.MainWalletViewPagerAdapter;
import com.crest.goyo.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainWalletFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainWalletFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TabLayout activity_tab_layout;
    private ViewPager activity_view_pager;
    private MainWalletViewPagerAdapter mainWalletViewPagerAdapter;
    private String[] tabTitle = {
            "My Wallet",
            "Coupon Wallet"
    };

    private OnFragmentInteractionListener mListener;

    public MainWalletFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MainWalletFragment newInstance(String param1, String param2) {
        MainWalletFragment fragment = new MainWalletFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_main_wallet, container, false);

        initView(mView);

        return mView;

    }


    private void initView(View mView){
        activity_tab_layout = (TabLayout) mView.findViewById(R.id.activity_tab_layout);
        activity_view_pager = (ViewPager) mView.findViewById(R.id.activity_view_pager);

        for (int i = 0; i < tabTitle.length; i++) {
            activity_tab_layout.addTab(activity_tab_layout.newTab().setText("" + tabTitle[i]));
        }
        mainWalletViewPagerAdapter = new MainWalletViewPagerAdapter(getActivity().getSupportFragmentManager(), activity_tab_layout.getTabCount());//Adding adapter to pager
        activity_view_pager.setAdapter(mainWalletViewPagerAdapter);
        activity_tab_layout.setTabGravity(TabLayout.GRAVITY_FILL);
        /*activity_tab_layout.setOnTabSelectedListener(this);
        activity_view_pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(activity_tab_layout));*/

        activity_tab_layout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(activity_view_pager));
        activity_view_pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(activity_tab_layout));
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
