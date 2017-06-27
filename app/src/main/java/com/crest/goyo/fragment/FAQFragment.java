package com.crest.goyo.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crest.goyo.ModelClasses.MyTicketsFaqModel;
import com.crest.goyo.R;
import com.crest.goyo.Utils.Constant;
import com.crest.goyo.Utils.FileUtils;
import com.crest.goyo.Utils.Preferences;
import com.crest.goyo.VolleyLibrary.RequestInterface;
import com.crest.goyo.VolleyLibrary.VolleyRequestClass;
import com.crest.goyo.VolleyLibrary.VolleyTAG;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.HttpUrl;

public class FAQFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ExpandableListView expandableListView;
    private ProgressBar mProgressBar;
    private TextView mLabel;

    List<String> stringsListHeader = new ArrayList<>();

    HashMap<String, List<MyTicketsFaqModel>> expandableListDetail = new HashMap<String, List<MyTicketsFaqModel>>();

    private OnFragmentInteractionListener mListener;

    public FAQFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FAQFragment newInstance(String param1, String param2) {
        FAQFragment fragment = new FAQFragment();
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
        View view = inflater.inflate(R.layout.fragment_faq, container, false);

        initView(view);

        return view;
    }

    private void initView(View mView){

        expandableListView = (ExpandableListView) mView.findViewById(R.id.expandableListView);
        mProgressBar = (ProgressBar) mView.findViewById(R.id.progress_bar);
        mLabel = (TextView) mView.findViewById(R.id.txt_lable);

        getFaqTypes();


        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGrp = -1;

            @Override
            public void onGroupExpand(int i) {
                if ((previousGrp != -1) && i != previousGrp) {
                    expandableListView.collapseGroup(previousGrp);
                }
                previousGrp = i;
            }
        });

    }


    private void getFaqTypes() {
        FileUtils.showProgressBar(getActivity(),mProgressBar);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.GET_FAQ_TYPES).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getContext(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token",Preferences.getValue_String(getContext(),Preferences.USER_AUTH_TOKEN));

        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();

        VolleyRequestClass.allRequest(getContext(), newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        FileUtils.hideProgressBar(getActivity(),mProgressBar);
                        mLabel.setVisibility(View.GONE);
                        JSONArray data = response.getJSONArray("data");

                        for(int i=0;i<data.length();i++){
                            stringsListHeader.add(data.getJSONObject(i).getString("j_title"));
                            ArrayList<MyTicketsFaqModel> stringsListItem = new ArrayList<>();
                            MyTicketsFaqModel myTicketsFaqModel = new MyTicketsFaqModel(data.getJSONObject(i).getString("id"),
                                    data.getJSONObject(i).getString("j_title"),
                                    data.getJSONObject(i).getString("j_text"),
                                    data.getJSONObject(i).getString("i_textbox"));
                            stringsListItem.add(myTicketsFaqModel);
                            expandableListDetail.put(stringsListHeader.get(i), stringsListItem);
                        }
                        ExpandableListViewAdapter expandableListViewAdapter = new ExpandableListViewAdapter(getActivity(), stringsListHeader, expandableListDetail);
                        expandableListView.setAdapter(expandableListViewAdapter);


                    }else {
                        FileUtils.hideProgressBar(getActivity(),mProgressBar);
                        mLabel.setText(message);
                        mLabel.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    FileUtils.hideProgressBar(getActivity(),mProgressBar);
                }
            }
        },false);
    }



    public class ExpandableListViewAdapter extends BaseExpandableListAdapter {

        private Context context;
        private List<String> expandableListTitle;
        private HashMap<String, List<MyTicketsFaqModel>> expandableListDetail;

        public ExpandableListViewAdapter(Context context, List<String> expandableListTitle,
                                         HashMap<String, List<MyTicketsFaqModel>> expandableListDetail) {
            this.context = context;
            this.expandableListTitle = expandableListTitle;
            this.expandableListDetail = expandableListDetail;
        }

        @Override
        public Object getChild(int listPosition, int expandedListPosition) {
            return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                    .get(expandedListPosition);
        }

        @Override
        public long getChildId(int listPosition, int expandedListPosition) {
            return expandedListPosition;
        }

        @Override
        public View getChildView(int listPosition, final int expandedListPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            final MyTicketsFaqModel expandedListText = (MyTicketsFaqModel) getChild(listPosition, expandedListPosition);
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.list_item, null);
            }
            TextView expandedListTextView = (TextView) convertView.findViewById(R.id.expandedListItem);
            final EditText mQuary = (EditText) convertView.findViewById(R.id.txt_quary);
            TextView mAddFaqTcket = (TextView) convertView.findViewById(R.id.txt_add_faq_ticket);
            LinearLayout mTextBox = (LinearLayout) convertView.findViewById(R.id.ll_text_box);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                expandedListTextView.setText(Html.fromHtml(expandedListText.getDetail(),Html.FROM_HTML_MODE_LEGACY));
            } else {
                expandedListTextView.setText(Html.fromHtml(expandedListText.getDetail()));
            }

            if(expandedListText.getItextbox().equalsIgnoreCase("0")){
                mTextBox.setVisibility(View.GONE);
            }else {
                mTextBox.setVisibility(View.VISIBLE);
            }

            mAddFaqTcket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String quary = mQuary.getText().toString().trim();
                    if(!quary.isEmpty() && quary != null){
                        createTicket(expandedListText.getId(),quary,mQuary);
                    }else {
                        mQuary.setError("Please enter quary");
                    }
                }
            });

            return convertView;
        }

        @Override
        public int getChildrenCount(int listPosition) {
            return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                    .size();
        }

        @Override
        public Object getGroup(int listPosition) {
            return this.expandableListTitle.get(listPosition);
        }

        @Override
        public int getGroupCount() {
            return this.expandableListTitle.size();
        }

        @Override
        public long getGroupId(int listPosition) {
            return listPosition;
        }

        @Override
        public View getGroupView(int listPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            String listTitle = (String) getGroup(listPosition);
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context.
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.list_group, null);
            }
            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageArrow);
            TextView listTitleTextView = (TextView) convertView
                    .findViewById(R.id.listTitle);
            listTitleTextView.setTypeface(null, Typeface.BOLD);
            listTitleTextView.setText(listTitle);

            if (isExpanded)
                imageView.setImageResource(R.drawable.ic_expand_more_black_24dp);
            else
                imageView.setImageResource(R.drawable.ic_chevron_right_black_24dp);
            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int listPosition, int expandedListPosition) {
            return true;
        }
    }


    private void createTicket(String i_type_id, String v_support_text, final EditText mQuary) {

        FileUtils.showProgressBar(getActivity(),mProgressBar);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.CREATE_TICKET).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getContext(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token",Preferences.getValue_String(getContext(),Preferences.USER_AUTH_TOKEN));
        urlBuilder.addQueryParameter("v_type","faq");
        urlBuilder.addQueryParameter("i_type_id",i_type_id);
        urlBuilder.addQueryParameter("v_support_text",v_support_text);

        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();

        VolleyRequestClass.allRequest(getContext(), newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        FileUtils.hideProgressBar(getActivity(),mProgressBar);
                        mQuary.setText("");
                        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
                    }else {
                        FileUtils.hideProgressBar(getActivity(),mProgressBar);
                        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    FileUtils.hideProgressBar(getActivity(),mProgressBar);
                    e.printStackTrace();
                }
            }
        },false);
    }







    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
     //   mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
