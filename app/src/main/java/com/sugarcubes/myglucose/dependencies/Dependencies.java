package com.sugarcubes.myglucose.dependencies;

import android.app.Application;

// Adapted from:
// https://softwareengineering.stackexchange.com/questions/354465/pure-dependency-injection-how-to-implement-it
public class Dependencies extends Application
{
	private static ObjectGraph objectGraph;


	@Override
	public void onCreate()
	{
		super.onCreate();

		objectGraph = new ObjectGraph( getApplicationContext() );

	} // onCreate


	// This is where your code accesses its dependencies
	public static <T> T get( Class<T> s )
	{
//		Affirm.notNull(objectGraph);
		return objectGraph.get( s );

	} // get


	// This is how you inject mock dependencies when running tests
	public <T> void injectMockObject( Class<T> clazz, T object )
	{
		objectGraph.putMock( clazz, object );

	} // injectMockObject

} // class
