package com.crest.goyo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crest.goyo.R;
import com.wajahatkarim3.easyflipview.EasyFlipView;

public class MessengerFood extends Fragment {
   private EasyFlipView PizzaHut,Fasoos,PunjabKing,Kabir,Toritos,Kfc,Momoman,TwistedMazzo,PizzaVilla,Tiffinity,Applewoods,BlueOven;
    private View view;

    public MessengerFood() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      view = inflater.inflate(R.layout.fragment_messenger_food, container, false);

        //initialize all buttons
        Init();

        return view;
    }
    private Void Init() {

        PizzaHut = (EasyFlipView) view.findViewById(R.id.pizzhut);
        Fasoos = (EasyFlipView) view.findViewById(R.id.fasoos);
        PunjabKing = (EasyFlipView) view.findViewById(R.id.punjabking);
        Kabir = (EasyFlipView) view.findViewById(R.id.kabir);
        Toritos = (EasyFlipView) view.findViewById(R.id.toritos);
        Kfc = (EasyFlipView) view.findViewById(R.id.kfc);
        Momoman = (EasyFlipView) view.findViewById(R.id.momon);
        TwistedMazzo = (EasyFlipView) view.findViewById(R.id.twistedmazo);
        PizzaVilla = (EasyFlipView) view.findViewById(R.id.pizavilla);
        Tiffinity = (EasyFlipView) view.findViewById(R.id.tiffity);
        Applewoods = (EasyFlipView) view.findViewById(R.id.applewood);
        BlueOven = (EasyFlipView) view.findViewById(R.id.blueoven);



        return null;
    }

}
