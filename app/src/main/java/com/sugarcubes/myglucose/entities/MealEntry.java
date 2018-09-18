package com.sugarcubes.myglucose.entities;

import java.util.ArrayList;
import java.util.Date;

public class MealEntry
{
	private int id;
	private String remoteId;
	private String userName;
	private int totalCarbs;
	private Date date;
	private long timestamp;

	private ArrayList<MealItem> mealItems;


	public MealEntry()
	{
		mealItems = new ArrayList<>();

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

	public ArrayList<MealItem> getMealItems()
	{
		return mealItems;
	}

	public void setMealItems( ArrayList<MealItem> mealItems )
	{
		this.mealItems = mealItems;
	}

} // class
