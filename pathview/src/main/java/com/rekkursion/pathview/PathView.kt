package com.rekkursion.pathview

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.children
import kotlin.math.max

class PathView(context: Context, attrs: AttributeSet? = null): LinearLayoutCompat(context, attrs) {
    // the container for putting path-node-views
    private val mLlyNodesContainer: LinearLayoutCompat

    // is indicator or not
    private var mIsIndicator: Boolean = false
    var isIndicator get() = mIsIndicator; set(value) { mIsIndicator = value }

    // the height of this path-view
    private var mHeight: Int = 20
    var viewHeight get() = mHeight; set(value) { mHeight = value }

    // the text of the separator
    private var mSeparatorStr: String = "/"
    var separator get() = mSeparatorStr; set(value) { mSeparatorStr = value }

    // the color of the node string
    private var mNodeColor: Int = Color.BLACK
    var nodeTextColor get() = mNodeColor; set(value) { mNodeColor = value }

    // the color of the separator
    private var mSeparatorColor: Int = Color.GRAY
    var separatorColor get() = mSeparatorColor; set(value) { mSeparatorColor = value }

    // if the user'd like to put a single separator when there's nothing
    private var mPutSingleSeparatorWhenNothing: Boolean = true
    var putSingleSeparatorWhenNothing get() = mPutSingleSeparatorWhenNothing; set(value) { mPutSingleSeparatorWhenNothing = value }

    // the listener when clicking the path node string
    private var mOnPathNodeClickListener: OnPathNodeClickListener? = null

    /* =================================================================== */

    // primary constructor
    init {
        // inflate the layout
        LayoutInflater.from(context).inflate(R.layout.view_path, this)

        // get views
        mLlyNodesContainer = findViewById(R.id.lly_path_nodes_container)

        // initialize attributes
        attrs?.let {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.PathView)

            mIsIndicator = ta.getBoolean(R.styleable.PathView_is_indicator, false)
            mHeight = max(8, ta.getInteger(R.styleable.PathView_text_height, 20))
            mSeparatorStr = ta.getString(R.styleable.PathView_separator) ?: "/"
            mNodeColor = ta.getColor(R.styleable.PathView_node_text_color, Color.BLACK)
            mSeparatorColor = ta.getColor(R.styleable.PathView_separator_color, Color.GRAY)
            mPutSingleSeparatorWhenNothing = ta.getBoolean(R.styleable.PathView_put_single_separator_when_nothing, true)

            ta.recycle()
        }

        // put single separator if the user'd like to
        if (mPutSingleSeparatorWhenNothing) push("")
    }

    /* =================================================================== */

    // push a path node to the tail
    fun push(pathNodeString: String) {
        // the passed string is empty -> return
        if (pathNodeString.isEmpty() && mLlyNodesContainer.childCount > 0) return

        // if there's a single separator w/o the node string
        if (mLlyNodesContainer.childCount == 1 && (mLlyNodesContainer.getChildAt(0) as PathNodeView).getNodeString().isEmpty())
            (mLlyNodesContainer.getChildAt(0) as PathNodeView).setNodeString(pathNodeString)

        // general case
        else {
            val childCnt = mLlyNodesContainer.childCount

            val nodeView = PathNodeView.Builder(context)
                .setNode(pathNodeString, mNodeColor)
                .setSeparator(mSeparatorStr, mSeparatorColor)
                .setHeight(mHeight)
                .setOnPathNodeClickListener(object: OnDefaultPathClickListener {
                    override fun onPathNodeClick() {
                        // do the default things
                        if (!mIsIndicator)
                            while (mLlyNodesContainer.childCount > childCnt + 1)
                                mLlyNodesContainer.removeViewAt(mLlyNodesContainer.childCount - 1)

                        // do the things user'd like to do
                        mOnPathNodeClickListener?.onPathNodeClick(this@PathView, childCnt)
                    }
                })
                .create()

            mLlyNodesContainer.addView(nodeView)
        }
    }

    // push all path nodes to the tail
    fun pushAll(pathNodeStrings: ArrayList<String>) {
        // push all
        pathNodeStrings.forEach { pathNodeString -> push(pathNodeString) }
    }

    // pop a path node from the tail
    fun pop(): String? {
        if (mLlyNodesContainer.childCount > 0) {
            val last = mLlyNodesContainer.children.last()

            last as PathNodeView
            if (last.getNodeString().isEmpty()) return null

            val retStr = last.getNodeString()
            if (mLlyNodesContainer.childCount == 1 && mPutSingleSeparatorWhenNothing)
                last.setNodeString("")
            else
                mLlyNodesContainer.removeView(last)

            return retStr
        }
        return null
    }

    // clear all path nodes
    fun clear() {
        if (mPutSingleSeparatorWhenNothing) {
            mLlyNodesContainer.removeViews(1, mLlyNodesContainer.childCount - 1)
            (mLlyNodesContainer.getChildAt(0) as PathNodeView).setNodeString("")
        }
        else
            mLlyNodesContainer.removeAllViews()
    }

    // set the listener when clicking the path node string
    fun setOnPathNodeClickListener(onPathNodeClickListener: OnPathNodeClickListener) {
        mOnPathNodeClickListener = onPathNodeClickListener
    }

    // get all current path nodes
    fun getAllPathNodes(): ArrayList<String> = mLlyNodesContainer.children.map {
        it as PathNodeView
        it.getNodeString()
    }.toCollection(ArrayList())
}