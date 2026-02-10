package com.gcap.main.animations

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class VerticalSpaceItemDecoration(
    private val verticalSpace: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = 0
        outRect.right = 0

        val position = parent.getChildAdapterPosition(view)
        if (position >= 2) {
            outRect.top = verticalSpace
        }

        outRect.bottom = verticalSpace
    }
}
