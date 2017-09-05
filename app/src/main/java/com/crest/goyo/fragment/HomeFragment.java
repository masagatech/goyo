package com.crest.goyo.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crest.goyo.AdapterClasses.HomeViewPagerAdapter;
import com.crest.goyo.R;
import com.crest.goyo.school.MyKids;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;

import static com.crest.goyo.MainActivity.CURRENT_TAG;
import static com.crest.goyo.MainActivity.TAG_BOOK_YOUR_RIDE;
import static com.crest.goyo.MainActivity.TAG_MESSENGER_FOOD;
import static com.crest.goyo.MainActivity.TAG_MESSENGER_MEDICINE;
import static com.crest.goyo.MainActivity.TAG_MESSENGER_PARCEL;
import static com.crest.goyo.MainActivity.TAG_MY_KIDS;
import static com.crest.goyo.MainActivity.TAG_ORDER_GROCERY;
import static com.crest.goyo.MainActivity.navItemIndex;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private View view;
    private NavigationView navigationView;
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static final Integer[] XMEN= {R.drawable.imgbanner, R.drawable.groc, R.drawable.tiffinity, R.drawable.pizza_hut_logo, R.drawable.tiffinity};
    private ArrayList<Integer> XMENArray = new ArrayList<Integer>();
    private TextView TrackSchool, TrackEmploy, RideAuto, RideBike, RideTruck, OrderFood, OrderGlossery, MessengerParcel, MessengerMedicine, MessengerFood, actionbar_title;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        //initialize all buttons
        Init();

        //Viewpager
        Pager();
        return view;
    }

    private Void Init() {
        navigationView = (NavigationView)  getActivity().findViewById(R.id.nav_view);
        actionbar_title = (TextView) getActivity().findViewById(R.id.actionbar_title);
        TrackSchool = (TextView) view.findViewById(R.id.home_track_school);
        TrackEmploy = (TextView) view.findViewById(R.id.home_track_empl);
        RideAuto = (TextView) view.findViewById(R.id.home_ride_auto);
        RideBike = (TextView) view.findViewById(R.id.home_ride_bike);
        RideTruck = (TextView) view.findViewById(R.id.home_ride_truck);
        OrderFood = (TextView) view.findViewById(R.id.home_order_food);
        OrderGlossery = (TextView) view.findViewById(R.id.home_order_gloss);
        MessengerParcel = (TextView) view.findViewById(R.id.home_messenger_parcel);
        MessengerMedicine = (TextView) view.findViewById(R.id.home_messenger_medi);
        MessengerFood = (TextView) view.findViewById(R.id.home_messenger_food);

        TrackSchool.setOnClickListener(this);
        TrackEmploy.setOnClickListener(this);
        RideAuto.setOnClickListener(this);
        RideBike.setOnClickListener(this);
        RideTruck.setOnClickListener(this);
        OrderFood.setOnClickListener(this);
        OrderGlossery.setOnClickListener(this);
        MessengerParcel.setOnClickListener(this);
        MessengerMedicine.setOnClickListener(this);
        MessengerFood.setOnClickListener(this);
        return null;
    }

    private void Pager() {
        for(int i=0;i<XMEN.length;i++)
            XMENArray.add(XMEN[i]);

        mPager = (ViewPager) view.findViewById(R.id.pager);
        mPager.setAdapter(new HomeViewPagerAdapter(getActivity(),XMENArray));
        CircleIndicator indicator = (CircleIndicator) view.findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == XMEN.length) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 2500, 2500);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_track_school:

                navItemIndex = 3;
                actionbar_title.setText(R.string.nav_my_kids);
                CURRENT_TAG = TAG_MY_KIDS;
                SetCheckedNavItem(navItemIndex);
                MyKids MyKids = new MyKids();
                CallBack(MyKids);

                break;
            case R.id.home_track_empl:

                navItemIndex = 3;
                actionbar_title.setText(R.string.nav_my_kids);
                CURRENT_TAG = TAG_MY_KIDS;
                SetCheckedNavItem(navItemIndex);
                com.crest.goyo.school.MyKids MyKids2 = new MyKids();
                CallBack(MyKids2);

                break;
            case R.id.home_ride_auto:

                navItemIndex = 1;
                actionbar_title.setText(R.string.nav_book_my_ride);
                CURRENT_TAG = TAG_BOOK_YOUR_RIDE;
                SetCheckedNavItem(navItemIndex);
                BookYourRideFragment bookYourRideFragmentAuto = new BookYourRideFragment();
                CallBack(bookYourRideFragmentAuto);

                break;
            case R.id.home_ride_bike:

                navItemIndex = 1;
                actionbar_title.setText(R.string.nav_book_my_ride);
                CURRENT_TAG = TAG_BOOK_YOUR_RIDE;
                SetCheckedNavItem(navItemIndex);
                BookYourRideFragment bookYourRideFragmentBike = new BookYourRideFragment();
                CallBack(bookYourRideFragmentBike);

                break;

            case R.id.home_ride_truck:

                navItemIndex = 1;
                actionbar_title.setText(R.string.nav_book_my_ride);
                CURRENT_TAG = TAG_BOOK_YOUR_RIDE;
                SetCheckedNavItem(navItemIndex);
                BookYourRideFragment bookYourRideFragmentTruck = new BookYourRideFragment();
                CallBack(bookYourRideFragmentTruck);

                break;
            case R.id.home_order_food:

                break;
            case R.id.home_order_gloss:

                navItemIndex = 15;
                actionbar_title.setText(R.string.nav_order_grocery);
                CURRENT_TAG = TAG_ORDER_GROCERY;
                SetCheckedNavItem(navItemIndex);
                OrderGrocery orderGrocery = new OrderGrocery();
                CallBack(orderGrocery);

                break;
            case R.id.home_messenger_parcel:

                navItemIndex = 16;
                actionbar_title.setText(R.string.nav_messenger_parcel);
                CURRENT_TAG = TAG_MESSENGER_PARCEL;
                SetCheckedNavItem(navItemIndex);
                MessengerParcel messengerParcel = new MessengerParcel();
                CallBack(messengerParcel);

                break;
            case R.id.home_messenger_medi:

                navItemIndex = 14;
                actionbar_title.setText(R.string.nav_messenger_medicine);
                CURRENT_TAG = TAG_MESSENGER_MEDICINE;
                SetCheckedNavItem(navItemIndex);
                MessengerMedicine messengerMedicine = new MessengerMedicine();
                CallBack(messengerMedicine);

                break;
            case R.id.home_messenger_food:

                navItemIndex = 13;
                actionbar_title.setText(R.string.nav_messenger_food);
                CURRENT_TAG = TAG_MESSENGER_FOOD;
                SetCheckedNavItem(navItemIndex);
                MessengerFood messengerFood = new MessengerFood();
                CallBack(messengerFood);

                break;
        }
    }

    //opening new fragment
    private void CallBack(Fragment fragment){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }

    //highlighting selected nav item
    private void SetCheckedNavItem(int ItemCount){
        if (navigationView != null) {
            navigationView.getMenu().getItem(ItemCount).setChecked(true);
        }
    }
}
