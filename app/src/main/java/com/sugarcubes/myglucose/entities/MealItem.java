package com.sugarcubes.myglucose.entities;

import com.sugarcubes.myglucose.db.DB;

import org.json.JSONException;
import org.json.JSONObject;

public class MealItem
{
	private int    id;
	private String remoteId;
	private String mealId;
	private String name;
	private int    carbs;
	private int    servings;

	public MealItem()
	{
		id = -1;
		remoteId = "";
		mealId = "";
		name = "";
		carbs = 0;
		servings = 0;

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

		return mealItem;

	} // toJSONObject

} // class
