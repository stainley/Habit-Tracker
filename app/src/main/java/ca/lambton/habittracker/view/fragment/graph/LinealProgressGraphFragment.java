package ca.lambton.habittracker.view.fragment.graph;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.antgroup.antv.f2.F2CanvasView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

import ca.lambton.habittracker.databinding.FragmentGraphAnalysisBinding;
import ca.lambton.habittracker.habit.view.GraphData;
import ca.lambton.habittracker.view.fragment.graph.adapter.LinealProgressGraphAdapter;

public class LinealProgressGraphFragment extends Fragment {
    FragmentGraphAnalysisBinding binding;
    private String source;
    private F2CanvasView linealGraphView;
    private List<GraphData> graphDataList;

    private LinealProgressGraphFragment() {
    }

    public static LinealProgressGraphFragment newInstance(List<GraphData> graphData) {
        LinealProgressGraphFragment INSTANCE = new LinealProgressGraphFragment();

        Bundle args = new Bundle();
        args.putSerializable("habit_progress", (Serializable) graphData);
        INSTANCE.setArguments(args);
        return INSTANCE;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentGraphAnalysisBinding.inflate(LayoutInflater.from(requireContext()));
        linealGraphView = binding.linealGraphView;

        assert getArguments() != null;
        graphDataList = (List<GraphData>) getArguments().getSerializable("habit_progress");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        JSONArray jsonArray = new JSONArray();

        graphDataList.forEach(graphData -> {

            JSONObject jsonObject = new JSONObject();
            try {

                jsonObject.put("percentage", graphData.getPercentage());
                jsonObject.put("period", graphData.getPeriod());

                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            //set data to chart
            source = jsonArray.toString();
            System.out.println(source);
        });

        linealGraphView.setAdapter(new LinealProgressGraphAdapter(source));

    }
}
