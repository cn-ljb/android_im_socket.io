package com.ljb.socket.android.act

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.github.chrisbanes.photoview.PhotoView
import com.ljb.socket.android.R
import com.ljb.socket.android.img.ImageLoader
import kotlinx.android.synthetic.main.activity_photo_list.*
import java.util.*

class PhotoListActivity : Activity(), ViewPager.OnPageChangeListener {

    companion object {
        const val KEY_CUR_PHOTO_INDEX = "cur_index"
        const val KEY_PHOTO_LIST = "data"

        fun statrtActivity(activity: Activity, data: ArrayList<String>, index: Int = 0) {
            val intent = Intent(activity, PhotoListActivity::class.java)
            intent.putExtra(KEY_CUR_PHOTO_INDEX, index)
            intent.putExtra(KEY_PHOTO_LIST, data)
            activity.startActivity(intent)
        }
    }

    private lateinit var mAdapter: PhotoListAdapter
    private var mCurIndex = 0
    private var mData: ArrayList<String>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        init()
        initView()
    }

    private fun getLayoutId() = R.layout.activity_photo_list

    private fun init() {
        mCurIndex = intent.getIntExtra(KEY_CUR_PHOTO_INDEX, 0)
        mData = intent.getStringArrayListExtra(KEY_PHOTO_LIST)
        if (mData == null || mData!!.size == 0) {
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        vp_group.apply {
            mAdapter = PhotoListAdapter(this@PhotoListActivity, mData!!)
            adapter = mAdapter
            offscreenPageLimit = 3
            addOnPageChangeListener(this@PhotoListActivity)
            currentItem = mCurIndex
        }
        tv_page.text = "${mCurIndex + 1}/${mAdapter.count}"
        iv_back.setOnClickListener {
            onBackPressed()
        }
    }


    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    @SuppressLint("SetTextI18n")
    override fun onPageSelected(position: Int) {
        tv_page.text = "${position + 1}/${mAdapter.count}"
    }


    inner class PhotoListAdapter(private val mContext: Context, val mData: MutableList<String> = mutableListOf()) : PagerAdapter() {

        //优化Item缓存
        private var mCacheViews = LinkedList<View>()

        override fun instantiateItem(container: ViewGroup, position: Int): View {
            val view: View = if (mCacheViews.size > 0) {
                mCacheViews.removeAt(0)
            } else {
                val photoView = LayoutInflater.from(mContext).inflate(R.layout.item_photo, container, false) as PhotoView
                photoView.setOnViewTapListener { _, _, _ ->

                    if (mContext is Activity) {
                        mContext.finish()
                    }
                }
                photoView
            }
            val imgView = view.findViewById<ImageView>(R.id.iv_img)
            ImageLoader.load(mContext, mData[position], imgView)
            container.addView(view)
            return view
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun getCount() = mData.size

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val view = `object` as View
            container.removeView(view)
            mCacheViews.add(view)
        }
    }
}