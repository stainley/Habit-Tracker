package ca.lambton.habittracker.common.fragment.graph.adapter;

import androidx.annotation.NonNull;

import com.antgroup.antv.f2.F2CanvasView;
import com.antgroup.antv.f2.F2Chart;
import com.antgroup.antv.f2.F2Util;

public class LinealProgressGraphAdapter implements F2CanvasView.Adapter {

    private F2Chart mChart;
    private final String jsonData;

    /**
     *
     * @param jsonData Contain progress percentage by period String for JSON data
     */
    public LinealProgressGraphAdapter(@NonNull String jsonData) {
        this.jsonData = jsonData;
    }

    @Override
    public void onCanvasDraw(F2CanvasView canvasView) {
        if (mChart == null) {
            mChart = F2Chart.create(canvasView.getContext(), "Graphical Analysis", canvasView.getWidth(), canvasView.getHeight());
        }

        mChart.setCanvas(canvasView);
        //set chart padding
        mChart.padding(8.0, 8.0, 8.0, 8.0);

        mChart.source(jsonData);

        mChart.setScale("percentage", new F2Chart.ScaleConfigBuilder()
                .precision(0)
                //.max(habitProgress.getHabit().getFrequency())
                .min(0));
        mChart.setScale("period", new F2Chart.ScaleConfigBuilder().precision(0).tickCount(4).range(new double[]{0.1, 0.9}));

        // Gradient
        mChart.area().position("period*percentage")
                //.fixedShape("smooth")
                .fixedColor(new F2Util.ColorLinearGradient()
                        .addColorStop(1.f, "orange")
                        .addColorStop(0.5f, "#FFE0E0")
                        .addColorStop(0.3f, "white")
                        .setPosition(0, 0, 0, canvasView.getHeight()));

        //Draw a polyline on the chart. The data mappings of the x-axis and y-axis of the polyline are genre and sold fields respectively.
        mChart.line()
                .color("type", new String[]{"ORANGE"})
                .fixedSize(2.f)
                //.fixedShape("smooth")
                .position("period*percentage");
        //Render and display on canvasView
        mChart.render();
    }

    @Override
    public void onDestroy() {
        if (mChart != null) {
            mChart.destroy();
        }
    }
}
