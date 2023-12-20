package com.uoc.tennis

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.uoc.tennis.databinding.ActivityMainBinding
import android.hardware.SensorEventListener
import android.util.Log
import android.view.View.OnClickListener
import android.widget.TextView
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception
import java.time.Instant
import java.time.format.DateTimeFormatter

class MainActivity : Activity(), SensorEventListener, OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private val dataArray: ArrayList<SensorData> = java.util.ArrayList()
    private var mSensorManager: SensorManager? = null
    private var mAccelerometer: Sensor? = null
    private var mGyroscope: Sensor? = null
    //private var mMagnetic: Sensor? = null
    private var button: Button? = null
    private var title: TextView? = null
    private var text: TextView? = null
    private var sessionInfo: TextView? = null
    private var clicks: Int = INITIAL_CLICK
    private var sessionID: Int? = null
    private var numberDatum: Int = INITIAL_DATUM
    private var playerType: String = NONE
    private var hitType: String = NONE
    private var gender: String = NONE
    private var age: String = NONE
    private var backhand: String = NONE
    private var laterality: String = NONE

    private lateinit var firebase: FirebaseFirestore
    private val jacksonMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

    data class SensorData(
        val x: Double, val y: Double, val z: Double,
        val timestamp: String, val sensor: String, val sessionID: Int)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionID = (SESSION_ID_MIN..SESSION_ID_MAX).shuffled().first()

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mGyroscope = mSensorManager!!.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        //mMagnetic = mSensorManager!!.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        firebase = FirebaseFirestore.getInstance()

        button = findViewById(R.id.button)
        title = findViewById(R.id.textTitle)
        text = findViewById(R.id.text)
        sessionInfo = findViewById(R.id.sessionInfo)

        button!!.setOnClickListener(this)
        sessionInfo!!.setOnClickListener {
            val intent = Intent(this, ConfigurationMenuActivity::class.java)
            startActivityForResult(intent, CONFIGURATION)
        }
    }

    override fun onClick(v: View){
        clicks += 1
        when (clicks) {
            FIRST_CLICK -> {
                sessionInfo!!.visibility = View.GONE
                //sessionInfo!!.text = EMPTY
                button!!.setText(R.string.buttonStop)
                text!!.setText(R.string.stopText)
                title!!.setText(R.string.stopTitle)
                mSensorManager!!.registerListener( this, mAccelerometer!!, SensorManager.SENSOR_DELAY_GAME)
                mSensorManager!!.registerListener(this, mGyroscope!!, SensorManager.SENSOR_DELAY_GAME)
                //mSensorManager!!.registerListener(this, mMagnetic!!, SensorManager.SENSOR_DELAY_GAME)
            }
            SECOND_CLICK -> {
                button!!.setText(R.string.stoppedButton)
                text!!.setText(R.string.stoppedText)
                title!!.setText(R.string.stoppedTitle)
                mSensorManager!!.unregisterListener(this)
                saveInDatabase()
            }
            else -> {
                finish()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent) {
        val datum = SensorData(event.values[X_COORDINATE].toDouble(), event.values[Y_COORDINATE].toDouble(),
            event.values[Z_COORDINATE].toDouble(), DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
            event.sensor.name, sessionID!!)
        dataArray.add(datum)
        numberDatum++
        if ( numberDatum % MAX_DATUM_TO_SEND == ZERO) {
            saveInDatabase()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        playerType =  data!!.getStringExtra(PLAYER_TYPE)!!
        hitType =  data.getStringExtra(STROKE_TYPE)!!
        gender = data.getStringExtra(GENDER)!!
        age = data.getStringExtra(AGE)!!
        laterality = data.getStringExtra(LATERALITY)!!
        backhand = data.getStringExtra(BACKHAND)!!
        Log.i(getString(R.string.config), getString(R.string.data))
    }

    /**
     * Saves in FireStore all the data present in the list of SensorData and the attributes of the session.
     */
    private fun saveInDatabase() {
        try {
            val hashMap = hashMapOf(
                ENTRIES to dataArray.map { jacksonMapper.convertValue(it, Map::class.java) },
                PLAYER_TYPE to playerType,
                STROKE_TYPE to hitType,
                GENDER to gender,
                NUM_ENTRIES to numberDatum,
                LATERALITY to laterality,
                BACKHAND to backhand,
                AGE to age
            )
            dataArray.clear()
            numberDatum = INITIAL_DATUM
            firebase.collection(COLLECTION).add(hashMap)
            Log.i(getString(R.string.firebase), getString(R.string.data))
        } catch (e: Exception){
            Log.e(getString(R.string.error), getString(R.string.data_error) + e.message)
        }

    }

    companion object Constants {
        const val COLLECTION = "sensorData2"
        const val ENTRIES = "entries"
        const val NUM_ENTRIES = "numEntries"
        const val FIRST_LABEL = "first"
        const val SECOND_LABEL = "second"
        const val THIRD_LABEL = "third"
        const val FIRST_OPTION = "first_option"
        const val SECOND_OPTION = "second_option"
        const val THIRD_OPTION = "third_option"
        const val LATERALITY = "laterality"
        const val BACKHAND = "backhand"
        const val AGE = "age"
        const val PLAYER_TYPE = "playerType"
        const val STROKE_TYPE = "strokeType"
        const val GENDER = "gender"
        const val NONE = "none"
        const val OPTION = "option"
        const val OPTION_NAME = "optionName"
        const val MALE = "male"
        const val FEMALE = "female"
        const val OTHER = "other"
        const val DRIVE = "drive"
        const val BALL = "ball"
        const val CASUAL = "casual"
        const val AMATEUR = "amateur"
        const val PROFESSIONAL = "professional"
        const val RIGHT = "right"
        const val LEFT = "left"
        const val ONE = "one"
        const val TWO = "two"
        const val CONFIGURATION = 1
        const val CONFIGURATION_MENU = 2
        const val INITIAL_DATUM = 0
        const val INITIAL_CLICK = 0
        const val MAX_DATUM_TO_SEND = 10000
        const val SESSION_ID_MIN = 100000
        const val SESSION_ID_MAX = 999999
        const val FIRST_CLICK = 1
        const val SECOND_CLICK = 2
        const val X_COORDINATE = 0
        const val Y_COORDINATE = 1
        const val Z_COORDINATE = 2
        const val ZERO = 0
    }

}