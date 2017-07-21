package com.crest.goyo;


import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.multidex.MultiDex;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.crest.goyo.FCM.MyFirebaseMessagingService;
import com.crest.goyo.Utils.Constant;
import com.crest.goyo.Utils.Preferences;
import com.crest.goyo.VolleyLibrary.RequestInterface;
import com.crest.goyo.VolleyLibrary.VolleyRequestClass;
import com.crest.goyo.VolleyLibrary.VolleyTAG;
import com.crest.goyo.fragment.BookYourRideFragment;
import com.crest.goyo.fragment.FAQFragment;
import com.crest.goyo.fragment.FeedbackFragment;
import com.crest.goyo.fragment.MainWalletFragment;
import com.crest.goyo.fragment.MyRidesFragment;
import com.crest.goyo.fragment.MyTicketsFragment;
import com.crest.goyo.fragment.NotificationsFragment;
import com.crest.goyo.fragment.PromotionCodeFragment;
import com.crest.goyo.fragment.ReferralCodeFragment;
import com.crest.goyo.fragment.TariffCardFragment;
import com.crest.goyo.fragment.TermsAndConditionsFragment;
import com.crest.goyo.logger.Log;
import com.crest.goyo.other.CircleTransform;
import com.crest.goyo.school.MyKids;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import okhttp3.HttpUrl;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgProfile;
    private Toolbar toolbar;
    public static int navItemIndex = 0;
    private String[] activityTitles;
    private Handler mHandler;
    private View navHeader;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private BroadcastReceiver mReceiveMessageFromNotification;
    boolean doubleBackToExitPressedOnce = false;
    private TextView txtName, edit_profile, actionbar_title, logout;
    private static final String TAG_BOOK_YOUR_RIDE = "BOOK YOUR RIDE";
    private static final String TAG_MY_RIDES = "MY RIDES";
    private static final String TAG_TARIFF_CARD = "TARIFF CARD";
    private static final String TAG_PROMOTION_CODE = "PROMOTION CODE";
    private static final String TAG_REFERRAL_CODE = "REFERRAL CODE";
    private static final String TAG_MY_WALLET = "MY WALLET";
    private static final String TAG_NOTIFICATIONS = "NOTIFICATIONS";
    private static final String TAG_FEEDBACK = "FEEDBACK";
    private static final String TAG_TERMS_CONDITIONS = "TERMS AND CONDITIONS";
    private static final String TAG_MY_KIDS = "MY KID";
    private static final String TAG_MY_TICKET = "MY TICKET";
    private static final String TAG_FAQ = "FAQ";
    private AlertDialog.Builder builder;
    private String TAG = "MainActivity";
    public static String CURRENT_TAG = TAG_BOOK_YOUR_RIDE;
    NotificationManager mNotificationManager;
    private FragmentManager mFragmentManager;

    BookYourRideFragment bookYourRideFragment;
    TermsAndConditionsFragment termsAndConditionsFragment;
    String latestVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFragmentManager = getSupportFragmentManager();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mHandler = new Handler();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        actionbar_title = (TextView) findViewById(R.id.actionbar_title);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        edit_profile = (TextView) navHeader.findViewById(R.id.edit_profile);
        logout = (TextView) navHeader.findViewById(R.id.logout);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

        String menuFragment = getIntent().getStringExtra("from");
        String addMoney = getIntent().getStringExtra("addMoney");
        Log.d("#########", "addMoney : " + addMoney);

        if (menuFragment != null) {
            if (menuFragment.equals("notifServicePayment")) {
                setUpNavigationView();
                MainWalletFragment myWalletFragment = new MainWalletFragment();
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                FragmentTransaction replace = fragmentTransaction.replace(R.id.frame, myWalletFragment, CURRENT_TAG = TAG_MY_WALLET);
                navItemIndex = 5;
                activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
                actionbar_title.setText(R.string.nav_my_wallet);
                CURRENT_TAG = TAG_MY_WALLET;
                fragmentTransaction.commitAllowingStateLoss();
            } else if (menuFragment.equals("notifServiceRideCancelCharge")) {
                setUpNavigationView();
                MainWalletFragment myWalletFragment = new MainWalletFragment();
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                FragmentTransaction replace = fragmentTransaction.replace(R.id.frame, myWalletFragment, CURRENT_TAG = TAG_MY_WALLET);
                navItemIndex = 5;
                activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
                actionbar_title.setText(R.string.nav_my_wallet);
                CURRENT_TAG = TAG_MY_WALLET;
                fragmentTransaction.commitAllowingStateLoss();
            } else if (menuFragment.equals("notifyUser_Add_Money")) {
                setUpNavigationView();
                MainWalletFragment myWalletFragment = new MainWalletFragment();
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                FragmentTransaction replace = fragmentTransaction.replace(R.id.frame, myWalletFragment, CURRENT_TAG = TAG_MY_WALLET);
                navItemIndex = 5;
                activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
                actionbar_title.setText(R.string.nav_my_wallet);
                CURRENT_TAG = TAG_MY_WALLET;
                fragmentTransaction.commitAllowingStateLoss();
            } else {

            }
        } else {
            if (addMoney != null) {
                if (addMoney.equals("sucessAddMoney")) {
                    setUpNavigationView();
                    MainWalletFragment myWalletFragment = new MainWalletFragment();
                    Fragment fragment = getHomeFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                            android.R.anim.fade_out);
                    FragmentTransaction replace = fragmentTransaction.replace(R.id.frame, myWalletFragment, CURRENT_TAG = TAG_MY_WALLET);
                    navItemIndex = 5;
                    activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
                    actionbar_title.setText(R.string.nav_my_wallet);
                    CURRENT_TAG = TAG_MY_WALLET;
                    fragmentTransaction.commitAllowingStateLoss();
                }
            } else {
                activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
                actionbar_title.setText(R.string.nav_book_my_ride);
                setUpNavigationView();
                if (savedInstanceState == null) {
                    navItemIndex = 0;
                    CURRENT_TAG = TAG_BOOK_YOUR_RIDE;
                    loadHomeFragment();
                }
            }
        }

        setUpNavigationView();

        edit_profile.setOnClickListener(this);
        logout.setOnClickListener(this);

        getMessageFromNotification();
//        rideCancelByDriverNotify();
        if (Constant.isOnline(MainActivity.this)) {
            getUserProfileAPI();
        }
        GetLatestVersion task = new GetLatestVersion();
        task.execute();

        if (Preferences.getValue_String(this, Preferences.IS_RATED).equals("1")) {
            showRatingDialog();
        }
    }

    private void showRatingDialog() {
/*        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_rate_again);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);*/
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rate Ride");
        builder.setMessage("Do you want to rate your last ride ?");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(MainActivity.this, RateThisRide.class));
            }
        });
        builder.show();
    }

    private void rideCancelByDriverNotify() {
        mReceiveMessageFromNotification = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                android.util.Log.d(TAG, "data: " + "app open notif COMPLETE RIDE main activity 1");
                if (intent.getAction().equals(MyFirebaseMessagingService.MESSAGE_NOTIFICATION)) {
                    android.util.Log.d(TAG, "data: " + "app open notif COMPLETE RIDE main activity 2");
                    if (intent.getExtras() != null) {
                        android.util.Log.d(TAG, "data: " + "app open notif COMPLETE RIDE main activity 3");
                        String mRideid = intent.getStringExtra("i_ride_id");
                        Intent in = new Intent(MainActivity.this, CompleteRide.class);
                        in.putExtra("i_ride_id", mRideid);
                        startActivity(in);
                    }
                }
            }
        };
    }


    private void getUserProfileAPI() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_GET_USER_PROFILE).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getApplicationContext(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getApplicationContext(), Preferences.USER_AUTH_TOKEN));
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClass.allRequest(MainActivity.this, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        JSONObject jsonObject = response.getJSONObject("data");
                        txtName.setText(jsonObject.getString("v_name"));
                        if (response.getJSONObject("data").getString("v_image").equals("")) {
                            imgProfile.setImageResource(R.drawable.no_user_white);
                        } else {
                            Glide.with(MainActivity.this).load(response.getJSONObject("data").getString("v_image"))
                                    .crossFade()
                                    .thumbnail(0.5f)
                                    .bitmapTransform(new CircleTransform(MainActivity.this))
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(imgProfile);
                        }
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, true);
    }

    private void loadHomeFragment() {
        drawer.closeDrawers();
        setToolbarTitle();
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                FragmentTransaction replace = fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }
        //drawer.closeDrawers();
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                BookYourRideFragment bookYourRideFragment = new BookYourRideFragment();
//                Preferences.setValue(getApplicationContext(),"comeFromm","MyRides");
                return bookYourRideFragment;
            case 1:
                MyRidesFragment myRidesFragment = new MyRidesFragment();
                return myRidesFragment;
            case 2:
                TariffCardFragment tariffCardFragment = new TariffCardFragment();
                return tariffCardFragment;
            case 3:
                PromotionCodeFragment promotionCodeFragment = new PromotionCodeFragment();
                return promotionCodeFragment;
            case 4:
                ReferralCodeFragment referralCodeFragment = new ReferralCodeFragment();
                return referralCodeFragment;
            case 5:
                MainWalletFragment myWalletFragment = new MainWalletFragment();
                return myWalletFragment;
            case 6:
                NotificationsFragment notificationsFragment = new NotificationsFragment();
                return notificationsFragment;
            case 7:
                FeedbackFragment feedbackFragment = new FeedbackFragment();
                return feedbackFragment;
            case 8:
                MyTicketsFragment myTicketsFragment = new MyTicketsFragment();
                return myTicketsFragment;
            case 9:
                FAQFragment faqFragment = new FAQFragment();
                return faqFragment;
            case 10:
                TermsAndConditionsFragment termsAndConditionsFragment = new TermsAndConditionsFragment();
                return termsAndConditionsFragment;
            case 11:
                MyKids mykidsFragment = new MyKids();
                return mykidsFragment;
            default:
                return new BookYourRideFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }


    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_book_my_ride:
                        navItemIndex = 0;
                        actionbar_title.setText(R.string.nav_book_my_ride);
                        CURRENT_TAG = TAG_BOOK_YOUR_RIDE;
                        break;
                    case R.id.nav_my_rides:
                        navItemIndex = 1;
                        actionbar_title.setText(R.string.nav_my_rides);
                        CURRENT_TAG = TAG_MY_RIDES;
                        break;
                    case R.id.nav_tariff_card:
                        navItemIndex = 2;
                        actionbar_title.setText(R.string.nav_tariff_card);
                        CURRENT_TAG = TAG_TARIFF_CARD;
                        break;
                    case R.id.nav_promotion_code:
                        navItemIndex = 3;
                        actionbar_title.setText(R.string.nav_promotion_code);
                        CURRENT_TAG = TAG_PROMOTION_CODE;
                        break;
                    case R.id.nav_referral_code:
                        navItemIndex = 4;
                        actionbar_title.setText(R.string.nav_referral_code);
                        CURRENT_TAG = TAG_REFERRAL_CODE;
                        break;
                    case R.id.nav_my_wallet:
                        navItemIndex = 5;
                        actionbar_title.setText(R.string.nav_my_wallet);
                        CURRENT_TAG = TAG_MY_WALLET;
                        break;
                    case R.id.nav_notifications:
                        navItemIndex = 6;
                        actionbar_title.setText(R.string.nav_notifications);
                        CURRENT_TAG = TAG_NOTIFICATIONS;
                        break;
                    case R.id.nav_feedback:
                        navItemIndex = 7;
                        actionbar_title.setText(R.string.nav_Feedback);
                        CURRENT_TAG = TAG_FEEDBACK;
                        break;
                    case R.id.nav_mytickets:
                        navItemIndex = 8;
                        actionbar_title.setText(R.string.nav_mytickets);
                        CURRENT_TAG = TAG_MY_TICKET;
                        break;
                    case R.id.nav_faq:
                        navItemIndex = 9;
                        actionbar_title.setText(R.string.nav_faq);
                        CURRENT_TAG = TAG_FAQ;
                        break;
                    case R.id.nav_terms_conditions:
                        navItemIndex = 10;
                        actionbar_title.setText(R.string.nav_terms);
                        CURRENT_TAG = TAG_TERMS_CONDITIONS;
                        break;
                    case R.id.nav_my_kids:
                        navItemIndex = 11;
                        actionbar_title.setText(R.string.nav_my_kids);
                        CURRENT_TAG = TAG_MY_KIDS;
                        break;
                    default:
                        navItemIndex = 0;
                        actionbar_title.setText(R.string.nav_book_my_ride);
                }

                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }

                menuItem.setChecked(true);
                loadHomeFragment();
                return true;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        if (navItemIndex != 0) {
            BookYourRideFragment bookYourRideFragment = new BookYourRideFragment();
            Fragment fragment = getHomeFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out);
            FragmentTransaction replace = fragmentTransaction.replace(R.id.frame, bookYourRideFragment, CURRENT_TAG = TAG_BOOK_YOUR_RIDE);
            navItemIndex = 0;
            actionbar_title.setText(R.string.nav_book_my_ride);
            CURRENT_TAG = TAG_BOOK_YOUR_RIDE;
            fragmentTransaction.commitAllowingStateLoss();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                finishAffinity();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click back again to exit", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_profile:
                Intent mIntent = new Intent(getApplicationContext(), EditProfile.class);
                startActivity(mIntent);
                break;
            case R.id.logout:
                if (Constant.isOnline(MainActivity.this)) {
                    user_logout();
                }
                break;
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void user_logout() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_LOGOUT).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getApplicationContext(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getApplicationContext(), Preferences.USER_AUTH_TOKEN));
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClass.allRequest(MainActivity.this, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        Preferences.setValue(getApplicationContext(), Preferences.USER_ID, "");
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        startActivity(intent);
                    } else {
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, true);
    }

    private void getMessageFromNotification() {
        mReceiveMessageFromNotification = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                android.util.Log.d(TAG, "data: " + "app open notif START RIDE main activity 1");
                if (intent.getAction().equals(MyFirebaseMessagingService.MESSAGE_SUCCESS)) {
                    android.util.Log.d(TAG, "data: " + "app open notif START RIDE main activity 2");
                    if (intent.getExtras() != null) {
                        android.util.Log.d(TAG, "data: " + "app open notif START RIDE main activity 3");
                        String mRideid = intent.getStringExtra("i_ride_id");
                        Intent in = new Intent(MainActivity.this, StartRideActivity.class);
                        in.putExtra("i_ride_id", mRideid);
                        startActivity(in);
                        finish();
                    }
                } else if (intent.getAction().equals(MyFirebaseMessagingService.RIDE_CANCEL_BY_DRIVER)) {
                    android.util.Log.d(TAG, "data: " + "app open notif main activity");
                    if (intent.getExtras() != null) {
                        android.util.Log.d(TAG, "data: " + "app open notif main activity");
                        String mTitle = intent.getStringExtra("mTitle");
                        String mBody = intent.getStringExtra("mBody");
                        builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
                        builder.setTitle(mTitle);
                        builder.setMessage(mBody);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                        });
                        builder.show();
                    }

                } else if (intent.getAction().equals(MyFirebaseMessagingService.COMPLETE_RIDE)) {
                    android.util.Log.d(TAG, "data: " + "app open notif main activity");
                    if (intent.getExtras() != null) {
                        android.util.Log.d(TAG, "data: " + "app open notif main activity");
                        String mRideid = intent.getStringExtra("i_ride_id");
                        Intent in = new Intent(MainActivity.this, CompleteRide.class);
                        in.putExtra("i_ride_id", mRideid);
                        startActivity(in);
                    }
                }

            }
        };
    }

//    private void rideCancelByDriverNotify() {
//        mReceiveMessageFromNotification = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, final Intent intent) {
//                if (intent.getAction().equals(MyFirebaseMessagingService.RIDE_CANCEL_BY_DRIVER)) {
//                    if (intent.getExtras() != null) {
//                        android.util.Log.d(TAG, "data: " + "app open notif main activity");
//                        String mTitle = intent.getStringExtra("mTitle");
//                        String mBody = intent.getStringExtra("mBody");
//                        builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
//                        builder.setTitle(mTitle);
//                        builder.setMessage(mBody);
//                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int whichButton) {
//                                dialog.dismiss();
//                               Intent intent=new Intent(getApplicationContext(),MainActivity.class);
//                                startActivity(intent);
//                            }
//                        });
//                        builder.show();
//
//                    }
//                }
//            }
//        };
//    }


    @Override
    public void onPause() {
        super.onPause();
//        App.activityPaused();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mReceiveMessageFromNotification);
    }

    @Override
    public void onResume() {
        super.onResume();
//        App.activityResumed();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mReceiveMessageFromNotification,
                new IntentFilter(MyFirebaseMessagingService.MESSAGE_SUCCESS));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mReceiveMessageFromNotification,
                new IntentFilter(MyFirebaseMessagingService.MESAGE_ERROR));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mReceiveMessageFromNotification,
                new IntentFilter(MyFirebaseMessagingService.MESSAGE_NOTIFICATION));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mReceiveMessageFromNotification,
                new IntentFilter(MyFirebaseMessagingService.RIDE_CANCEL_BY_DRIVER));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mReceiveMessageFromNotification,
                new IntentFilter(MyFirebaseMessagingService.COMPLETE_RIDE));
        Log.e("#########", "Receiver : ");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }



    /*private void isDroverOpen()
    {
        if(drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);

        }else {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    public void initBookYourRideFragment()
    {
        bookYourRideFragment = BookYourRideFragment.newInstance();

        if(!bookYourRideFragment.isAdded())
        {
            mFragmentManager.beginTransaction().replace(R.id.frame,bookYourRideFragment).commit();
        }
    }



    public void initTwoFragment()
    {
        termsAndConditionsFragment = TermsAndConditionsFragment.newInstance();

        if(!termsAndConditionsFragment.isAdded())
        {
            mFragmentManager.beginTransaction().add(R.id.frame,termsAndConditionsFragment).addToBackStack(termsAndConditionsFragment.getClass().getName()).commit();
        }

    }*/

    private class GetLatestVersion extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String urlOfAppFromPlayStore = "https://play.google.com/store/apps/details?id=com.crest.goyo&hl=en";
                //It retrieves the latest version by scraping the content of current version from play store at runtime
                Document doc = Jsoup.connect(urlOfAppFromPlayStore).get();
                latestVersion = doc.getElementsByAttributeValue("itemprop", "softwareVersion").first().text();
                android.util.Log.e("latestVersion", "latestVersion playstore: " + latestVersion);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new JSONObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if (!GetThisVersion().equals(latestVersion)) {
                ShowUpdateDialog();
            }
        }
    }

    String GetThisVersion() {
        PackageManager pm = this.getPackageManager();
        PackageInfo pInfo = null;

        try {
            pInfo = pm.getPackageInfo(this.getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        assert pInfo != null;
        android.util.Log.e("latestVersion", "latestVersion system" + pInfo.versionName);
        return pInfo.versionName;
    }

    void ShowUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Available");
        builder.setMessage("Are you sure to update to new version?");
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.crest.goyo" + getPackageName() + "&hl=en")));
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}