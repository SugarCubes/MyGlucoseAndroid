package com.sugarcubes.myglucose.entities;

import java.util.ArrayList;
import java.util.Date;

public class MealEntry
{
	private String id;
	private int totalCarbs;
	private Date date;
	private long timestamp;

	private ArrayList<MealItem> mealItems;


	public String getId()
	{
		return id;
	}

	public void setId( String id )
	{
		this.id = id;
	}

	public MealEntry()
	{
		mealItems = new ArrayList<>();

	} // constructor


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
