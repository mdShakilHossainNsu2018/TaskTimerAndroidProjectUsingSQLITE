package com.example.tasktimer

import android.content.ContentValues
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), AddEditFragment.OnSaveClicked {

    private var mTwoPane = false

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        mTwoPane = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        var fragment = supportFragmentManager.findFragmentById(R.id.task_details_container)

        if (fragment != null){
            showEditPane()
        } else {
            findViewById<FrameLayout>(R.id.task_details_container)
                .visibility = if (mTwoPane) View.INVISIBLE else View.GONE


            supportFragmentManager.
            findFragmentById(R.id.mainFragment)?.
            view?.visibility = View.VISIBLE
        }
    }

    private fun showEditPane(){
        findViewById<FrameLayout>(R.id.task_details_container)
            .visibility = View.VISIBLE

        supportFragmentManager.
        findFragmentById(R.id.mainFragment)?.
        view?.visibility = if (mTwoPane) View.VISIBLE else View.GONE
    }

    private fun removeEditPane(fragment: Fragment? = null){
        if (fragment != null){
            supportFragmentManager.beginTransaction()
                .remove(fragment)
                .commit()
        }

        // Set the visibility

        findViewById<FrameLayout>(R.id.task_details_container).visibility = if (mTwoPane) View.INVISIBLE else View.GONE

        supportFragmentManager.findFragmentById(R.id.mainFragment)?.view?.visibility = View.VISIBLE


    }

    override fun onSaveClicked(){
        Log.d(TAG, "onSaveClicked called")
        var fragment = supportFragmentManager.findFragmentById(R.id.task_details_container)
        removeEditPane(fragment)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.menumain_addTask -> taskEditRequest(null)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun taskEditRequest(task: Task?) {
        val newFragment = AddEditFragment.newInstance(task)

        supportFragmentManager.beginTransaction()
            .replace(R.id.task_details_container, newFragment)
            .commit()

        showEditPane()
    }
}