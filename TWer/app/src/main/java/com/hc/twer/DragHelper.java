package com.hc.twer;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

public class DragHelper extends ItemTouchHelper.Callback {

    private ActionCompletionContract contract;
    private int dragFrom = -1;
    private int dragTo = -1;

    public DragHelper(ActionCompletionContract contract)
    {
        this.contract = contract;
    }

    public interface ActionCompletionContract
    {
        void onViewMoved(int oldPosition, int newPosition);
        void reallyMoved(int oldPosition, int newPosition);
        void drop();
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof DateTabListAdapter.DateTabViewHolder)
        {
            return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        }
        return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.UP | ItemTouchHelper.DOWN);
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {

        int fromPosition = viewHolder.getAdapterPosition();
        int toPosition = viewHolder1.getAdapterPosition();

        if (dragFrom == -1)
        {
            dragFrom = fromPosition;
        }
        dragTo = toPosition;
        contract.onViewMoved(fromPosition, toPosition);

        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        contract.drop();
        if(dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
            contract.reallyMoved(dragFrom, dragTo);
        }

        dragFrom = dragTo = -1;
    }
}
