package com.sugarcubes.myglucose.entities;

import java.util.Date;

public class ExerciseEntry
{
	private String id;
	private String userId;
	private String exerciseName;
	private int minutes;
	private Date date;
	private long timestamp;





	public String getId()
	{
		return id;
	}

	public void setId( String id )
	{
		this.id = id;
	}

	public String getUserId()
	{
		return userId;
	}

	public void setUserId( String userId )
	{
		this.userId = userId;
	}

	public String getExerciseName()
	{
		return exerciseName;
	}

	public void setExerciseName( String exerciseName )
	{
		this.exerciseName = exerciseName;
	}

	public int getMinutes()
	{
		return minutes;
	}

	public void setMinutes( int minutes )
	{
		this.minutes = minutes;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate( Date date )
	{
		this.date = date;
	}

	public long getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp( long timestamp )
	{
		this.timestamp = timestamp;
	}
}
