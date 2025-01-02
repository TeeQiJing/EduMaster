package com.practical.edumasters.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.MaterialToolbar;
import com.practical.edumasters.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DefaultBadgeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DefaultBadgeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DefaultBadgeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DefaultBadgeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DefaultBadgeFragment newInstance(String param1, String param2) {
        DefaultBadgeFragment fragment = new DefaultBadgeFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_default_badge, container, false);

        // Initialize the toolbar
        MaterialToolbar toolbar = rootView.findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        // Additional UI setup (if required)
        // For example, setting text or configuring other views
        // TextView message = rootView.findViewById(R.id.noBadgeMessage);
        // message.setText("You don't have any badges yet!");

        return rootView;
    }


}