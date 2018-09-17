package com.sugarcubes.myglucose.entities;

import java.util.Date;

public class ExerciseEntry
{
	private int id;
	private String remoteId;
	private String userName;
	private String exerciseName;
	private int minutes;
	private Date date;
	private long timestamp;


	public ExerciseEntry()
	{

	}

	public int getId()
	{
		return id;
	}

	public void setId( int id )
	{
		this.id = id;
	}

	public String getRemoteId()
	{
		return remoteId;
	}

	public void setRemoteId( String remoteId )
	{
		this.remoteId = remoteId;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName( String userName )
	{
		this.userName = userName;
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
