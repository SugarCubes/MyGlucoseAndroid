package com.sugarcubes.myglucose.entities;

import com.sugarcubes.myglucose.db.DB;
import com.sugarcubes.myglucose.enums.WhichMeal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class MealEntry
{
	private int       id;
	private String    remoteId;
	private String    userName;
	private int       totalCarbs;
	private Date      createdAt;
	private Date	  updatedAt;
	private long      timestamp;
	private WhichMeal whichMeal;

	private ArrayList<MealItem> mealItems;


	public MealEntry()
	{
		mealItems = new ArrayList<>();
		id = -1;
		remoteId = "";
		userName = "";
		totalCarbs = 0;
		timestamp = 0;
		whichMeal = WhichMeal.OTHER;
		Date date = new Date();
		this.createdAt = date;
		updatedAt = createdAt;
		timestamp = date.getTime();

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

	public int getTotalCarbs()
	{
		return totalCarbs;
	}

	public void setTotalCarbs( int totalCarbs )
	{
		this.totalCarbs = totalCarbs;
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

	public ArrayList<MealItem> getMealItems()
	{
		return mealItems;
	}

	public void setMealItems( ArrayList<MealItem> mealItems )
	{
		this.mealItems = mealItems;
	}

	public WhichMeal getWhichMeal()
	{
		return whichMeal;
	}

	public void setWhichMeal( WhichMeal whichMeal )
	{
		this.whichMeal = whichMeal;
	}


	public JSONObject toJSONObject() throws JSONException
	{
		JSONObject mealEntry = new JSONObject();

		// TODO

		if( mealItems.size() > 0 )
		{
			JSONArray mItems = new JSONArray();

			for( MealItem mealItem : mealItems )
			{
				mItems.put( mealItem.toJSONObject() );
			}

			mealEntry.put( DB.KEY_MEAL_ITEMS, mItems );
		}

		return mealEntry;

	} // toJSONObject

} // class
