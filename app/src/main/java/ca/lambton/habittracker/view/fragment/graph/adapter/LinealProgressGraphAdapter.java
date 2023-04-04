package ca.lambton.habittracker.view.fragment.graph.adapter;

import com.antgroup.antv.f2.F2CanvasView;
import com.antgroup.antv.f2.F2Chart;
import com.antgroup.antv.f2.F2Util;

import ca.lambton.habittracker.habit.model.HabitProgress;

public class LinealProgressGraphAdapter implements F2CanvasView.Adapter {

    private F2Chart mChart;
    private final String jsonData;
    private final HabitProgress habitProgress;
    public LinealProgressGraphAdapter(String jsonData, HabitProgress habitProgress) {
        this.jsonData = jsonData;
        this.habitProgress = habitProgress;
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

        mChart.setScale("progress", new F2Chart.ScaleConfigBuilder()
                .precision(0)
                .max(habitProgress.getHabit().getFrequency())
                .min(0));
        mChart.setScale("date", new F2Chart.ScaleConfigBuilder().precision(0).tickCount(4).range(new double[]{0.1, 0.9}));

        //Draw a polyline on the chart. The data mappings of the x-axis and y-axis of the polyline are genre and sold fields respectively.
        mChart.area().position("date*progress")
                //.fixedShape("smooth")
                .fixedColor(new F2Util.ColorLinearGradient()
                        .addColorStop(1.f, "orange")
                        .addColorStop(0.5f, "#FFE0E0")
                        .addColorStop(0.3f, "white")
                        .setPosition(0, 0, 0, canvasView.getHeight()));

        mChart.line()

                .color("type", new String[]{"ORANGE"})
                .fixedSize(2.f)
                //.fixedShape("smooth")
                .position("date*progress");
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
