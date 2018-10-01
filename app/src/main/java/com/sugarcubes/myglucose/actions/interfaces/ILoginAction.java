package com.sugarcubes.myglucose.actions.interfaces;

import android.content.Context;

import com.sugarcubes.myglucose.enums.ErrorCode;

import org.json.JSONException;

import java.net.MalformedURLException;

public interface ILoginAction
{
	ErrorCode attemptLogin( String username, String password, Context context ) throws MalformedURLException, JSONException;

} // interface
