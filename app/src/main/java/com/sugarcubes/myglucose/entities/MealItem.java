package com.sugarcubes.myglucose.entities;

import com.sugarcubes.myglucose.db.DB;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MealItem
{
	private int    id;
	private String remoteId;
	private String mealId;
	private String name;
	private int    carbs;
	private int    servings;
	private Date   updatedAt;

	public MealItem()
	{
		id = -1;
		remoteId = "";
		mealId = "";
		name = "";
		carbs = 0;
		servings = 0;
		updatedAt = new Date();

	} // default constructor

	public String getRemoteId()
	{
		return remoteId;
	}

	public void setRemoteId( String remoteId )
	{
		this.remoteId = remoteId;
	}

	public int getId()
	{
		return id;
	}

	public void setId( int id )
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public int getCarbs()
	{
		return carbs;
	}

	public void setCarbs( int carbs )
	{
		this.carbs = carbs;
	}

	public int getServings()
	{
		return servings;
	}

	public void setServings( int servings )
	{
		this.servings = servings;
	}

	public String getMealId()
	{
		return mealId;
	}

	public void setMealId( String mealId )
	{
		this.mealId = mealId;
	}

	public Date getUpdatedAt()
	{
		return updatedAt;
	}

	public void setUpdatedAt( Date updatedAt )
	{
		this.updatedAt = updatedAt;
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
		JSONObject mealItem = new JSONObject();

		mealItem.put( DB.KEY_REMOTE_ID, remoteId );
		mealItem.put( DB.KEY_MEAL_ID, mealId );
		mealItem.put( DB.KEY_MEAL_ITEM_NAME, name );
		mealItem.put( DB.KEY_MEAL_ITEM_CARBS, carbs );
		mealItem.put( DB.KEY_MEAL_ITEM_SERVINGS, servings );

		try
		{
			DateFormat df = new SimpleDateFormat( "MM/dd/yyyy HH:mm a", Locale.US );
			if( updatedAt != null )
				mealItem.put( DB.KEY_UPDATED_AT, df.format( updatedAt ) );
			else
			{
				updatedAt = new Date();
				mealItem.put( DB.KEY_UPDATED_AT, df.format( updatedAt ) );
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}

		return mealItem;

	} // toJSONObject

} // class
