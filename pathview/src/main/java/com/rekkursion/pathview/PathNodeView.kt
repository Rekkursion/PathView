package com.rekkursion.pathview

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat

internal class PathNodeView private constructor(context: Context, attrs: AttributeSet? = null):
        LinearLayoutCompat(context, attrs) {

    // the builder class
    internal class Builder(context: Context, attrs: AttributeSet? = null) {
        // the unique instance
        private val mInstance = PathNodeView(context, attrs)

        // return the instance
        internal fun create(): PathNodeView {
            if (mInstance.mTxtvSeparator.visibility == View.GONE &&
                    mInstance.mImgvSeparator.visibility == View.GONE) {
                mInstance.mTxtvSeparator.visibility = View.VISIBLE
                mInstance.mTxtvSeparator.text = mInstance.context.getString(R.string.str_default_separator)
                mInstance.removeView(mInstance.mImgvSeparator)
            }
            else if (mInstance.mImgvSeparator.visibility == View.GONE)
                mInstance.removeView(mInstance.mImgvSeparator)
            else if (mInstance.mTxtvSeparator.visibility == View.GONE)
                mInstance.removeView(mInstance.mTxtvSeparator)
            else
                mInstance.removeView(mInstance.mImgvSeparator)
            return mInstance
        }

        // set the node string
        internal fun setNode(pathNodeString: String, textColor: Int? = null): Builder {
            mInstance.mTxtvNodeStr.text = pathNodeString
            if (textColor != null)
                mInstance.mTxtvNodeStr.setTextColor(textColor)
            return this
        }

        // set the text-separator
        internal fun setSeparator(separator: String, color: Int? = null): Builder {
            mInstance.mImgvSeparator.visibility = View.GONE
            mInstance.mTxtvSeparator.visibility = View.VISIBLE
            mInstance.mTxtvSeparator.text = separator
            if (color != null)
                mInstance.mTxtvSeparator.setTextColor(color)
            return this
        }

        // set the drawable-separator
        internal fun setSeparator(separator: Drawable): Builder {
            mInstance.mImgvSeparator.visibility = View.VISIBLE
            mInstance.mTxtvSeparator.visibility = View.GONE
            mInstance.mImgvSeparator.setImageDrawable(separator)
            return this
        }

        // set the height
        internal fun setHeight(height: Int): Builder {
            mInstance.mTxtvNodeStr.textSize = height.toFloat()
            mInstance.mTxtvSeparator.textSize = height.toFloat()
            mInstance.mImgvSeparator.layoutParams.height = height
            return this
        }

        // set the listener when clicking the path node string
        internal fun setOnPathNodeClickListener(onDefaultPathClickListener: OnDefaultPathClickListener): Builder {
            mInstance.mTxtvNodeStr.setOnClickListener { onDefaultPathClickListener.onPathNodeClick() }
            return this
        }
    }

    /* =================================================================== */

    // for placing the string of a certain path node
    private val mTxtvNodeStr: TextView
    var nodeString: String get() = mTxtvNodeStr.text.toString(); set(value) { mTxtvNodeStr.text = value }

    // for showing the text-separator
    private val mTxtvSeparator: TextView
    var separator: String get() = mTxtvSeparator.text.toString(); set(value) { mTxtvSeparator.text = value }

    // for showing the drawable-separator
    private val mImgvSeparator: ImageView

    /* =================================================================== */

    // primary constructor
    init {
        // inflate the layout
        LayoutInflater.from(context).inflate(R.layout.view_path_node, this)

        // get views
        mTxtvNodeStr = findViewById(R.id.txtv_node_string)
        mTxtvSeparator = findViewById(R.id.txtv_separator)
        mImgvSeparator = findViewById(R.id.imgv_separator)

        // request layout for the image-view
        mImgvSeparator.requestLayout()
    }

    /* =================================================================== */

    // set the height
    internal fun setViewHeight(height: Int) {
        mTxtvNodeStr.textSize = height.toFloat()
        mTxtvSeparator.textSize = height.toFloat()
        mImgvSeparator.layoutParams.height = height
    }

    // set the node string color
    internal fun setNodeColor(color: Int) {
        mTxtvNodeStr.setTextColor(color)
    }

    // set the separator color
    internal fun setSeparatorColor(color: Int) {
        mTxtvSeparator.setTextColor(color)
    }
}