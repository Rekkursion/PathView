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

    // the height of this path-view
    private var mHeight: Int = 20
    val viewHeight get() = mHeight

    // the text of the separator
    private var mSeparatorStr: String = "/"
    val separator get() = mSeparatorStr

    // the color of the node string
    private var mNodeColor: Int = Color.BLACK
    val nodeTextColor get() = mNodeColor

    // the color of the separator
    private var mSeparatorColor: Int = Color.GRAY
    val separatorColor get() = mSeparatorColor

    // if the user'd like to put a single separator when there's nothing
    private var mPutSingleSeparatorWhenNothing: Boolean = true

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