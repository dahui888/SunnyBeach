package cn.cqautotest.sunnybeach.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.aop.DebugLog
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.FishPondSettingActivityBinding
import cn.cqautotest.sunnybeach.model.FishPondTopicList
import cn.cqautotest.sunnybeach.ui.adapter.FishPondAdapter
import cn.cqautotest.sunnybeach.util.*
import cn.cqautotest.sunnybeach.viewmodel.fishpond.FishPondViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import com.blankj.utilcode.util.DeviceUtils
import com.chad.library.adapter.base.module.BaseDraggableModule
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import io.noties.markwon.syntax.*

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/7/10
 * desc   : 摸鱼设置界面
 */
class FishPondSettingActivity : AppActivity(), StatusAction, OnRefreshListener {

    private lateinit var mBinding: FishPondSettingActivityBinding
    private val mFishPondViewModel by viewModels<FishPondViewModel>()
    private lateinit var mStatusLayout: StatusLayout
    private lateinit var mRefreshLayout: SmartRefreshLayout
    private val mFishPondAdapter = FishPondAdapter()

    override fun getLayoutId(): Int = 0

    override fun onBindingView(): ViewBinding {
        mBinding = FishPondSettingActivityBinding.inflate(layoutInflater)
        return mBinding
    }

    override fun initObserver() {
        mFishPondViewModel.fishFishPondTopicList.observe(this) { fishTopic ->
            setupTopicUI(fishTopic)
        }
    }

    private fun setupTopicUI(fishFishPondTopicList: FishPondTopicList?) {
        mRefreshLayout.finishRefresh()
        if (fishFishPondTopicList == null) {
            showEmpty()
            return
        }
        showComplete()
        logByDebug(msg = "initObserver：===> topic is $fishFishPondTopicList")
        mFishPondAdapter.setList(fishFishPondTopicList)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        loadTopic()
    }

    override fun initEvent() {
        mRefreshLayout.setOnRefreshListener(this)
    }

    override fun initData() {
        loadTopic()
        showLoading()
    }

    private fun loadTopic() {
        mFishPondViewModel.loadTopicList()
    }

    override fun initView() {
        mStatusLayout = mBinding.hlFishPondHint
        mRefreshLayout = mBinding.rlStatusRefresh
        mBinding.rvFishPondLabelsList.apply {
            // 如果是平板则展示两列，否则展示一列
            val spanCount = if (DeviceUtils.isTablet()) 6 else 3
            layoutManager = GridLayoutManager(this@FishPondSettingActivity, spanCount)
            adapter = mFishPondAdapter
            val draggableModule = BaseDraggableModule(mFishPondAdapter)
            draggableModule.attachToRecyclerView(this)
            draggableModule.isDragEnabled = true
            addItemDecoration(object : RecyclerView.ItemDecoration() {

                // 单位间距（实际间距的一半）
                private val unit = 4.dp

                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    super.getItemOffsets(outRect, view, parent, state)
                    equilibriumAssignmentOfGrid(unit, outRect, view, parent)
                    view.setRoundRectBg(cornerRadius = 16.dp)
                }
            })
        }
    }

    override fun getStatusLayout(): StatusLayout {
        return mStatusLayout
    }

    override fun onLeftClick(view: View?) {
        finish()
    }

    companion object {

        @DebugLog
        fun start(context: Context) {
            val intent = Intent(context, FishPondSettingActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
}