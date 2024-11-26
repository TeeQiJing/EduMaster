package com.practical.edumasters.fragments;



import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.practical.edumasters.R;
import com.practical.edumasters.adapters.LeaderboardAdapter;
import com.practical.edumasters.models.LeaderboardCard;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LeaderboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LeaderboardFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LeaderboardFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LeaderBoard.
     */
    // TODO: Rename and change types and number of parameters
    public static LeaderboardFragment newInstance(String param1, String param2) {
        LeaderboardFragment fragment = new LeaderboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    ArrayList<LeaderboardCard> testingModelArrayList=new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }

    }
//    private void setUpTestingmodel(){
//        String[] testing_name=getResources().getStringArray(R.array.testing_name);
//        String[] testing_mark=getResources().getStringArray(R.array.testing_mark);
//        for(int i=0;i<testing_name.length;i++){
//            testingModelArrayList.add(new LeaderboardCard(testing_name[i],testing_mark[i]));
//        }
//    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_leaderboard, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
//        setUpTestingmodel();
        TextView firstName=view.findViewById(R.id.firstName);
        TextView firstMark=view.findViewById(R.id.firstMark);
        TextView secondName=view.findViewById(R.id.secondName);
        TextView secondMark=view.findViewById(R.id.secondMark);
        TextView thirdName=view.findViewById(R.id.thirdName);
        TextView thirdMark=view.findViewById(R.id.thirdMark);
        TextView []name={firstName,secondName,thirdName};
        TextView []mark={firstMark,secondMark,thirdMark};
        for(int i=0;i<testingModelArrayList.size()&&i<3;i++) {
            name[i].setText(testingModelArrayList.get(i).getTesting_name());
            mark[i].setText(testingModelArrayList.get(i).getTesting_mark()+" pts");
        }
        RecyclerView recyclerView=view.findViewById(R.id.test_recycler);
        LeaderboardAdapter adapter=new LeaderboardAdapter(requireContext(),testingModelArrayList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }
}
