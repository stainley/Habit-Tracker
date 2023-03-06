package ca.lambton.habittracker.view.fragment.habit.stack;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CardStackLayoutManager extends RecyclerView.LayoutManager {
    private static final int STACK_COUNT = 2;
    private static final float STACK_SCALE_FACTOR = 0.03f;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }


    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.getItemCount() == 0) {
            removeAndRecycleAllViews(recycler);
            return;
        }

        detachAndScrapAttachedViews(recycler);

        int itemCount = getItemCount();
        int bottomPosition = Math.max(itemCount - STACK_COUNT, 0);

        for (int i = bottomPosition; i < itemCount; i++) {
            View view = recycler.getViewForPosition(i);
            measureChild(view, 0, 0);

            addView(view);



            int width = getWidth();
            //int height = getHeight();
            int height = getHeight();

            float scale = 1 - STACK_SCALE_FACTOR * (i - bottomPosition);
            view.setScaleX(scale);
            view.setScaleY(scale);

            int top = (int) (height - view.getMeasuredHeight() * scale) / 2;
            int left = (int) (width - view.getMeasuredWidth() * scale) / 2;

            layoutDecoratedWithMargins(view, left, top, left + view.getMeasuredWidth(), top + view.getMeasuredHeight());
        }
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

/*


    @Override
    public boolean canScrollVertically() {
        return false;
    }*/
}
