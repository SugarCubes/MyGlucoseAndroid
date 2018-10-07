package com.sugarcubes.myglucose.entities;

public class MealItem
{
	private int id;
	private String remoteId;
	private String mealId;
	private String name;
	private int carbs;
	private int servings;

	public MealItem()
	{
		id			= -1;
		remoteId	= "";
		mealId		= "";
		name		= "";
		carbs		= 0;
		servings	= 0;

	} // default constructor

	public MealItem( int id, String remoteId, String mealId, String name, int carbs, int servings )
	{
		this.id = id;
		this.name = name;
		this.carbs = carbs;
		this.servings = servings;

	} // constructor

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
		return "MealItem{" +
				"id=" + id +
				", remoteId='" + remoteId + '\'' +
				", mealId='" + mealId + '\'' +
				", name='" + name + '\'' +
				", carbs=" + carbs +
				", servings=" + servings +
				'}';
	}

} // class
