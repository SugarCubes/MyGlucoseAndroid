package com.sugarcubes.myglucose.entities;

import com.sugarcubes.myglucose.db.DB;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExerciseEntry
{
	private int    id;
	private String remoteId;
	private String userName;
	private String exerciseName;
	private int    minutes;
	private int    steps;
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
		steps = -1;
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

	public int getSteps()
	{
		return steps;
	}

	public void setSteps( int steps )
	{
		this.steps = steps;
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

	@Override
	public String toString()
	{
		try
		{
			return toJSONObject().toString();
		}
		catch( JSONException e )
		{
			e.printStackTrace();
			return "";
		}

	} // toString


	public JSONObject toJSONObject() throws JSONException
	{
		JSONObject exerciseEntry = new JSONObject();

		if( !remoteId.isEmpty() )
			exerciseEntry.put( DB.KEY_REMOTE_ID, remoteId );
		if( timestamp > 0 )
			exerciseEntry.put( DB.KEY_TIMESTAMP, timestamp );
		exerciseEntry.put( DB.KEY_EXERCISE_NAME, exerciseName );
		exerciseEntry.put( DB.KEY_USERNAME, userName );
		exerciseEntry.put( DB.KEY_EXERCISE_MINUTES, minutes );
		exerciseEntry.put( DB.KEY_EXERCISE_STEPS, steps );

		try
		{
			DateFormat df = new SimpleDateFormat( "MM/dd/yyyy HH:mm a", Locale.US );
			exerciseEntry.put( DB.KEY_CREATED_AT, df.format( createdAt ) );
			exerciseEntry.put( DB.KEY_UPDATED_AT, df.format( updatedAt ) );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}

		return exerciseEntry;

	} // toJSONObject

} // class
