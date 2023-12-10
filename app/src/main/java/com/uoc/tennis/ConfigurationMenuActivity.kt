package com.uoc.tennis

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.wear.widget.WearableLinearLayoutManager
import androidx.wear.widget.WearableRecyclerView
import com.uoc.tennis.databinding.ActivityConfigurationMenuBinding


class ConfigurationMenuActivity : Activity() {

    private var backhandConfiguration: String = MainActivity.NONE
    private var lateralityConfiguration: String = MainActivity.NONE
    private var ageConfiguration: String = MainActivity.NONE
    private var playerTypeConfiguration: String = MainActivity.NONE
    private var hitTypeConfiguration: String = MainActivity.NONE
    private var genderConfiguration: String = MainActivity.NONE
    private var _binding: ActivityConfigurationMenuBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityConfigurationMenuBinding.inflate(layoutInflater)
        setContentView(_binding!!.root)

        val menuItems = ArrayList<MenuItem>()
        menuItems.add(MenuItem(getString(R.string.age)))
        menuItems.add(MenuItem(getString(R.string.gender)))
        menuItems.add(MenuItem(getString(R.string.hitType)))
        menuItems.add(MenuItem(getString(R.string.playerType)))
        menuItems.add(MenuItem(getString(R.string.laterality)))
        menuItems.add(MenuItem(getString(R.string.BackhandType)))
        menuItems.add(MenuItem(getString(R.string.exitConfiguration)))

        val recyclerView = findViewById<WearableRecyclerView>(R.id.menu_container)
        recyclerView.isEdgeItemsCenteringEnabled = true
        recyclerView.layoutManager = WearableLinearLayoutManager(this)
        recyclerView.adapter = ConfigurationAdapter(this, menuItems, object: ConfigurationAdapter.AdapterCallback {
            override fun onItemClicked(menuPosition: Int?) {
                when(menuPosition) {
                    0 -> setAge()
                    1 -> action(getString(R.string.male), getString(R.string.female), getString(R.string.other), MainActivity.GENDER, MainActivity.MALE, MainActivity.FEMALE, MainActivity.OTHER)
                    2 -> action(getString(R.string.drive), getString(R.string.backhand), getString(R.string.ball), MainActivity.STROKE_TYPE, MainActivity.DRIVE, MainActivity.BACKHAND, MainActivity.BALL)
                    3 -> action(getString(R.string.casual), getString(R.string.amateur), getString(R.string.professional), MainActivity.PLAYER_TYPE, MainActivity.CASUAL, MainActivity.AMATEUR, MainActivity.PROFESSIONAL)
                    4 -> action(getString(R.string.Right), getString(R.string.Left), null, MainActivity.LATERALITY, MainActivity.RIGHT, MainActivity.LEFT, null)
                    5 -> action(getString(R.string.one), getString(R.string.two), null, MainActivity.BACKHAND, MainActivity.ONE, MainActivity.TWO, null)
                    else -> cancelMenu()
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (data!!.getStringExtra(MainActivity.OPTION_NAME)) {
            MainActivity.AGE -> ageConfiguration =
                data.getStringExtra(MainActivity.OPTION)!!

            MainActivity.BACKHAND -> backhandConfiguration =
                data.getStringExtra(MainActivity.OPTION)!!

            MainActivity.LATERALITY -> lateralityConfiguration =
                data.getStringExtra(MainActivity.OPTION)!!

            MainActivity.PLAYER_TYPE -> playerTypeConfiguration =
                data.getStringExtra(MainActivity.OPTION)!!

            MainActivity.STROKE_TYPE -> hitTypeConfiguration =
                data.getStringExtra(MainActivity.OPTION)!!

            MainActivity.GENDER -> genderConfiguration =
                data.getStringExtra(MainActivity.OPTION)!!
        }
    }

    fun action(firstText: String, secondText: String, thirdText: String?, option: String, firstOption: String, secondOption: String, thirdOption: String?) {
        val intent = Intent(this, ConfigurationActivity::class.java).apply {
            putExtra(MainActivity.FIRST_LABEL, firstText)
            putExtra(MainActivity.SECOND_LABEL, secondText)
            putExtra(MainActivity.THIRD_LABEL, thirdText)
            putExtra(MainActivity.OPTION_NAME, option)
            putExtra(MainActivity.FIRST_OPTION, firstOption)
            putExtra(MainActivity.SECOND_OPTION, secondOption)
            putExtra(MainActivity.THIRD_OPTION, thirdOption)
        }
        startActivityForResult(intent, MainActivity.CONFIGURATION_MENU)
    }

    fun setAge() {
        val intent = Intent(this, KeyboardActivity::class.java)
        startActivityForResult(intent, MainActivity.CONFIGURATION_MENU)
    }

    fun cancelMenu() {
        val intent = Intent().apply {
            putExtra(MainActivity.BACKHAND, backhandConfiguration)
            putExtra(MainActivity.LATERALITY, lateralityConfiguration)
            putExtra(MainActivity.AGE, ageConfiguration)
            putExtra(MainActivity.PLAYER_TYPE, playerTypeConfiguration)
            putExtra(MainActivity.STROKE_TYPE, hitTypeConfiguration)
            putExtra(MainActivity.GENDER, genderConfiguration)
        }
        this.setResult(RESULT_OK, intent)
        finish()
    }
}

