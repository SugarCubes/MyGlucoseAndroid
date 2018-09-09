package com.sugarcubes.myglucose.entities;

public class MealItem
{
	private String id;
	private String mealId;
	private String name;
	private int carbs;
	private int servings;

	public MealItem()
	{
	}

	public MealItem( String id, String mealId, String name, int carbs, int servings )
	{
		this.id = id;
		this.name = name;
		this.carbs = carbs;
		this.servings = servings;
	}

	public String getId()
	{
		return id;
	}

	public void setId( String id )
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

} // class
