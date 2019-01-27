package com.sugarcubes.myglucose.sensor;

import android.hardware.SensorEventListener;

import com.sugarcubes.myglucose.sensor.interfaces.StepListener;

/**
 * Adapted from http://www.gadgetsaint.com/android/create-pedometer-step-counter-android/
 */
public class StepDetector
{
	// This acts as the sample size, constantly rotating accelerations in
	//		and out. The higher the sample size, the more accurate the
	//		acceleration change detection should be:
	private static final int VEL_RING_SIZE   = 16;         // Default: 10
	private static final int ACCEL_RING_SIZE = 30;         // Default: 50

	// change this threshold according to your sensitivity preferences
	private static final float STEP_THRESHOLD = 12f;       // Default: 50f
	private static final int   STEP_DELAY_NS  = 220000000; // Default: 250000000

	private int     accelRingCounter    = 0;
	private float[] accelRingX          = new float[ ACCEL_RING_SIZE ];
	private float[] accelRingY          = new float[ ACCEL_RING_SIZE ];
	private float[] accelRingZ          = new float[ ACCEL_RING_SIZE ];
	private int     velRingCounter      = 0;
	private float[] velRing             = new float[ VEL_RING_SIZE ];
	private long    lastStepTimeNs      = 0;
	private float   oldVelocityEstimate = 0;

	// This is the interface we created to act as a contract. We only accept the listener if
	//		it implements the interface. That way, we can call step() when a step is detected.
	private StepListener listener;


	public void registerListener( StepListener listener )
	{
		this.listener = listener;

	}


	/**
	 * updateAccel - This is called every time the sensor is updated (many times per second).
	 * It only updates the listener if a movement significant enough to be considered
	 * a step is detected.
	 *
	 * @param timeNs - time
	 * @param x      - x value
	 * @param y      - y value
	 * @param z      - z value
	 */
	public void updateAccel( long timeNs, float x, float y, float z )
	{
		// Create a structure to hold the current vector:
		float[] currentAccel = new float[ 3 ];
		currentAccel[ 0 ] = x;
		currentAccel[ 1 ] = y;
		currentAccel[ 2 ] = z;

		// First step is to update our guess of where the global z vector is.
		// First, we fill up each slot in the x, y, and z arrays:
		accelRingCounter++;
		accelRingX[ accelRingCounter % ACCEL_RING_SIZE ] = currentAccel[ 0 ];
		accelRingY[ accelRingCounter % ACCEL_RING_SIZE ] = currentAccel[ 1 ];
		accelRingZ[ accelRingCounter % ACCEL_RING_SIZE ] = currentAccel[ 2 ];

		// Get an average value for each axis (x, y, z):
		float[] worldZ = new float[ 3 ];
		worldZ[ 0 ] =
				SensorFilter.sum( accelRingX ) / Math.min( accelRingCounter, ACCEL_RING_SIZE );
		worldZ[ 1 ] =
				SensorFilter.sum( accelRingY ) / Math.min( accelRingCounter, ACCEL_RING_SIZE );
		worldZ[ 2 ] =
				SensorFilter.sum( accelRingZ ) / Math.min( accelRingCounter, ACCEL_RING_SIZE );

		float normalization_factor = SensorFilter.norm( worldZ );

		worldZ[ 0 ] = worldZ[ 0 ] / normalization_factor;
		worldZ[ 1 ] = worldZ[ 1 ] / normalization_factor;
		worldZ[ 2 ] = worldZ[ 2 ] / normalization_factor;

		float currentZ = SensorFilter.dot( worldZ, currentAccel ) - normalization_factor;
		velRingCounter++;
		velRing[ velRingCounter % VEL_RING_SIZE ] = currentZ;

		// This should keep the accuracy, but only check for step a percentage of the
		//		time, thus saving the device from so much work, and saving battery...
		if( velRingCounter % 4 == 0 )
		{
			float velocityEstimate = SensorFilter.sum( velRing );
			//		float velocityEstimate = SensorFilter.norm( velRing ); // not as sensitive

			if( velocityEstimate > STEP_THRESHOLD && oldVelocityEstimate <= STEP_THRESHOLD
					&& ( timeNs - lastStepTimeNs > STEP_DELAY_NS ) )
			{
				listener.step( timeNs );
				lastStepTimeNs = timeNs;
			}
			oldVelocityEstimate = velocityEstimate;

		} // if

	} // updateAccel

} // class