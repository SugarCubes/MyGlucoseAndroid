package com.sugarcubes.myglucose.sensor.interfaces;

// Will listen to step alerts
public interface StepListener
{

	public void step( long timeNs );

}