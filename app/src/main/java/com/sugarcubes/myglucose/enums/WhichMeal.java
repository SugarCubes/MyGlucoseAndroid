package com.sugarcubes.myglucose.enums;

public enum WhichMeal
{
	BREAKFAST(0),
	LUNCH(1),
	DINNER(2),
	SNACK(3),
	OTHER(4);

	private int _value;

	WhichMeal( int Value )
	{
		this._value = Value;

	} // constructor


	public int getValue()
	{
		return _value;

	} // getValue


	public static WhichMeal fromInt( int i )
	{
		for( WhichMeal whichMeal : WhichMeal.values() )
		{
			if( whichMeal.getValue() == i )
			{
				return whichMeal;
			}
		}
		return null;

	} // fromInt

} // enum
