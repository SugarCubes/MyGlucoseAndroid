package com.sugarcubes.myglucose.services;
import android.content.Context;
import android.os.AsyncTask;

public class AsyncTaskImplement extends AsyncTask<Void, Integer, String> {

    private Context context;

    public AsyncTaskImplement(Context context)
    {
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params)
    {
        int i = 0;
        synchronized (this)
        {
            while (i<10)
            {
                try {
                    wait(1500);
                    i++;
                }catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String string){
        super.onPostExecute(string);
    }

    @Override
    protected void onProgressUpdate(Integer... values)
    {
        int progress = values[0];
    }

}
