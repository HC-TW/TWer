package com.hc.twer;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class TravelsFirstFragment extends Fragment implements View.OnClickListener {

    private TextView title;
    private EditText search_text;
    private ImageView search_clear;
    private ImageView search_confirm;
    private RecyclerView recyclerView;
    private CountyListAdapter countyListAdapter;
    private List<String> countyNames;
    private List<Integer> countyImageIds;

    public TravelsFirstFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_travels_first, container, false);
        search_text = view.findViewById(R.id.travels_search_text);
        search_clear = view.findViewById(R.id.travels_search_clear);
        search_confirm = view.findViewById(R.id.travels_search_confirm);
        recyclerView = view.findViewById(R.id.county_list);

        init();
        displayCountyList();

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.travels_search_clear:
                search_text.setText("");
                break;

            case R.id.travels_search_confirm:
                // get input keyword
                if (search_text.getText().length() > 0)
                {
                    hideKeyboard(v);
                    String text = search_text.getText().toString();
                    Bundle bundle = new Bundle();
                    bundle.putString("keyword", text);
                    TravelsSecondFragment travelsSecondFragment = new TravelsSecondFragment();
                    travelsSecondFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left_full, R.anim.slide_in_left_full, R.anim.slide_out_right).replace(R.id.fragment_container, travelsSecondFragment).addToBackStack("travelsFirst").commit();
                }
                else
                {
                    Toast.makeText(getActivity(), "請輸入景點地區或名稱", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void init()
    {
        title = getActivity().findViewById(R.id.toolbar_title);
        title.setText("遊記");

        search_clear.setOnClickListener(this);
        search_confirm.setOnClickListener(this);

        search_text.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
            {
                hideKeyboard(v);
            }
        });
    }

    private void displayCountyList()
    {
        String[] countyStr = new String[] { "基隆", "新北", "台北", "宜蘭", "桃園", "新竹", "苗栗", "台中", "南投",
                                            "彰化", "雲林", "嘉義", "台南", "高雄", "屏東", "花蓮", "台東"};
        Integer[] imageId = new Integer[] { R.drawable.keelung, R.drawable.newtaipeicity, R.drawable.taipei, R.drawable.ilan, R.drawable.taoyuan,
                                            R.drawable.hsinchu, R.drawable.miaoli, R.drawable.taichung, R.drawable.nantou, R.drawable.changhua,
                                            R.drawable.yunlin, R.drawable.chiayi, R.drawable.tainan, R.drawable.kaohsiung, R.drawable.pingtung,
                                            R.drawable.hualien, R.drawable.taitung};

        countyNames = new ArrayList<>();
        countyNames = java.util.Arrays.asList(countyStr);
        countyImageIds = new ArrayList<>();
        countyImageIds = java.util.Arrays.asList(imageId);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        countyListAdapter = new CountyListAdapter(getActivity(), countyNames, countyImageIds);
        recyclerView.setAdapter(countyListAdapter);

        recyclerView.setOnTouchListener((v, event) -> {
            hideKeyboard(v);
            return false;
        });
    }

    // hide keyboard
    private void hideKeyboard(View view)
    {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
