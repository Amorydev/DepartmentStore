package com.amory.departmentstore.adapter

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.model.OnLoadMoreListener

class RvLoadMoreScroll(layoutManager: GridLayoutManager) :
    RecyclerView.OnScrollListener() {

    private var visibleItem = 0
    private lateinit var mOnLoadMoreListener: OnLoadMoreListener
    private var isLoading: Boolean = false
    private var firstVisibleItem = 0
    private var totalItemCount = 0
    private val visibleThreshold = 0// Số lượng item còn lại trước khi load thêm
    private var mLayoutManager: RecyclerView.LayoutManager = layoutManager

    fun setLoaded() {
        isLoading = false
    }

    fun getLoaded(): Boolean {
        return isLoading
    }

    fun setOnLoadMoreListener(mOnLoadMoreListener: OnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener
    }

    init {
        visibleItem *= layoutManager.spanCount
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        if (dy <= 0) return
        visibleItem = mLayoutManager.childCount
        totalItemCount = mLayoutManager.itemCount

        if (mLayoutManager is GridLayoutManager) {
            firstVisibleItem = (mLayoutManager as GridLayoutManager).findFirstVisibleItemPosition()
        }

        if (!isLoading &&  (totalItemCount - visibleItem) <= (firstVisibleItem + visibleThreshold)) {
            mOnLoadMoreListener.onLoadMore()
            isLoading = true
        }

    }

}
