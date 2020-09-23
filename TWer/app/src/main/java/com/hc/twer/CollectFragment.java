package com.hc.twer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class CollectFragment extends Fragment {

    private TextView title;
    private RecyclerView recyclerView;
    private FavoriteListAdapter favoriteListAdapter;
    private SharedPreference sharedPreference;
    private ArrayList<MyPlace> favoritePlaces;

    private MenuItem menu_pick;
    private MenuItem menu_delete;

    public static boolean PICK_MODE = false;

    public CollectFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collect, container, false);
        recyclerView = view.findViewById(R.id.favorite_list);

        init();
        setFavoriteList();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PICK_MODE = false;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu_pick = menu.findItem(R.id.collect_place_pick);
        menu_delete = menu.findItem(R.id.collect_place_delete);
        menu_pick.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.collect_place_pick:
                if (PICK_MODE)
                {
                    menu_delete.setVisible(false);
                }
                else
                {
                    menu_delete.setVisible(true);
                }
                PICK_MODE = !PICK_MODE;
                favoriteListAdapter.pickedList.clear();
                favoriteListAdapter.notifyDataSetChanged();
                return true;

            case R.id.collect_place_delete:
                ArrayList<MyPlace> favoritePlaces = sharedPreference.getFavoritePlaces(getActivity());
                for (int i = 0; i < favoriteListAdapter.pickedList.size(); i++)
                {
                    for (int j = 0; j < favoritePlaces.size(); j++)
                    {
                        if (favoritePlaces.get(j).getId().equals(favoriteListAdapter.pickedList.get(i).getId()))
                        {
                            favoritePlaces.remove(j);
                            break;
                        }
                    }
                }
                sharedPreference.saveFavoritePlaces(getActivity(), favoritePlaces);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CollectFragment(), null).commit();
                Toast.makeText(getActivity(), "已刪除收藏景點", Toast.LENGTH_LONG).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init()
    {
        setHasOptionsMenu(true);
        title = getActivity().findViewById(R.id.toolbar_title);
        title.setText("收藏景點");
    }
    // set up recyclerView
    private void setFavoriteList()
    {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        sharedPreference = new SharedPreference();
        favoritePlaces = sharedPreference.getFavoritePlaces(getActivity());
        if ( favoritePlaces == null )
        {
            favoritePlaces = new ArrayList<>();
        }
        favoriteListAdapter = new FavoriteListAdapter(getActivity(), favoritePlaces, -1, -1);
        recyclerView.setAdapter(favoriteListAdapter);
    }

}
