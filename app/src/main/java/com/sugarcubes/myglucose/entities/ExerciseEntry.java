package com.sugarcubes.myglucose.entities;

import org.json.JSONObject;

import java.util.Date;

public class ExerciseEntry
{
	private int    id;
	private String remoteId;
	private String userName;
	private String exerciseName;
	private int    minutes;
	private Date   updatedAt;
	private Date   createdAt;
	private long   timestamp;


	public ExerciseEntry()
	{
		id = -1;
		remoteId = "";
		userName = "";
		exerciseName = "";
		minutes = -1;
		createdAt = new Date();
		updatedAt = createdAt;
		timestamp = createdAt.getTime();

	} // constructor

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

	public Date getCreatedAt()
	{
		return createdAt;
	}

	public void setCreatedAt( Date createdAt )
	{
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt()
	{
		return updatedAt;
	}

	public void setUpdatedAt( Date updatedAt )
	{
		this.updatedAt = updatedAt;
	}

	public long getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp( long timestamp )
	{
		this.timestamp = timestamp;
	}

	public JSONObject toJSONObject()
	{
		// TODO

		return null;

	} // toJSONObject

} // class
