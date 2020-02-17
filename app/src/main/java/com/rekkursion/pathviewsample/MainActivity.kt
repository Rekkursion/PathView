package com.rekkursion.pathviewsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.rekkursion.pathview.OnPathNodeClickListener
import com.rekkursion.pathview.PathView
import java.util.*

class MainActivity: AppCompatActivity(), OnPathNodeClickListener {
    // click on a certain path node
    override fun onPathNodeClick(pathView: PathView, index: Int) {
        AlertDialog.Builder(this)
            .setMessage("$index: " + pathView.getAllPathNodes().joinToString(pathView.separator))
            .create()
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get the path view for demo
        val pv = findViewById<PathView>(R.id.path_view)

        // set the listener when clicking on a certain path node
        pv.setOnPathNodeClickListener(this)

        // push a node
        findViewById<Button>(R.id.btn_push_node).setOnClickListener {
            pv.push(UUID.randomUUID().toString().substring(0, 5))
        }
        // pop a node
        findViewById<Button>(R.id.btn_pop_node).setOnClickListener {
            pv.pop()
        }
        // clear all nodes
        findViewById<Button>(R.id.btn_clear_nodes).setOnClickListener {
            pv.clear()
        }
    }
}
