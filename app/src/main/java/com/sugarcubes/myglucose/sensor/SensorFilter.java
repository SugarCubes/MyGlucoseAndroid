package com.sugarcubes.myglucose.sensor;

public class SensorFilter
{

	private SensorFilter()
	{
	}

	public static float sum( float[] array )
	{
		float retVal = 0;
		for( int i = 0; i < array.length; i++ )
		{
			retVal += array[ i ];
		}
		return retVal;

	} // sum


	public static float[] cross( float[] arrayA, float[] arrayB )
	{
		float[] retArray = new float[ 3 ];
		retArray[ 0 ] = arrayA[ 1 ] * arrayB[ 2 ] - arrayA[ 2 ] * arrayB[ 1 ];
		retArray[ 1 ] = arrayA[ 2 ] * arrayB[ 0 ] - arrayA[ 0 ] * arrayB[ 2 ];
		retArray[ 2 ] = arrayA[ 0 ] * arrayB[ 1 ] - arrayA[ 1 ] * arrayB[ 0 ];
		return retArray;

	} // cross


	public static float norm( float[] array )
	{
		float retval = 0;
		for( int i = 0; i < array.length; i++ )
		{
			retval += array[ i ] * array[ i ];
		}
		return (float) Math.sqrt( retval );

	} // norm


	public static float dot( float[] a, float[] b )
	{
		return a[ 0 ] * b[ 0 ] + a[ 1 ] * b[ 1 ] + a[ 2 ] * b[ 2 ];

	} // dot


	public static float[] normalize( float[] a )
	{
		float[] retval = new float[ a.length ];
		float norm = norm( a );
		for( int i = 0; i < a.length; i++ )
		{
			retval[ i ] = a[ i ] / norm;
		}
		return retval;

	} // normalize

} // class
