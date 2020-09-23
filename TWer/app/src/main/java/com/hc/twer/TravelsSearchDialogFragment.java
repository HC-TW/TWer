package com.hc.twer;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class TravelsSearchDialogFragment extends DialogFragment {

    private AlphaAnimation alphaAnimation = new AlphaAnimation(1F, 0.8F);
    private EditText search_text;
    private Spinner spinner;
    private String[] list = {"搜尋遊記", "搜尋景點"};
    private ArrayAdapter<String> listAdapter;
    private ImageView search_confirm;

    public TravelsSearchDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_travels_search_dialog, container, false);
        search_text = view.findViewById(R.id.travels_third_search_text);
        search_confirm = view.findViewById(R.id.travels_third_search_confirm);
        spinner = view.findViewById(R.id.spinner);

        // set dialog's background transparent
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        setSpinner();
        setConfirm();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setGravity(Gravity.CENTER);
        getDialog().setCanceledOnTouchOutside(true);
    }



    private void setSpinner()
    {
        listAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, list);
        listAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(listAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(getActivity(), "已選擇" + listAdapter.getItem(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void setConfirm()
    {
        search_confirm.setOnClickListener(v -> {
            if (search_text.getText().length() > 0)
            {
                // get input keyword
                String text = search_text.getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString("keyword", text);

                int choice = spinner.getSelectedItemPosition();
                if (choice == 0) {
                    TravelsSecondFragment travelsSecondFragment = new TravelsSecondFragment();
                    travelsSecondFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left_full, R.anim.slide_in_left_full, R.anim.slide_out_right).replace(R.id.fragment_container, travelsSecondFragment).addToBackStack("travelsSearch").commit();
                } else if (choice == 1) {
                    SearchFragment searchFragment = new SearchFragment();
                    searchFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left_full, R.anim.slide_in_left_full, R.anim.slide_out_right).replace(R.id.fragment_container, searchFragment).addToBackStack("search").commit();
                }
                dismiss();
            }
            else
            {
                Toast.makeText(getActivity(), "請輸入景點名稱", Toast.LENGTH_LONG).show();
            }
        });
    }

}
