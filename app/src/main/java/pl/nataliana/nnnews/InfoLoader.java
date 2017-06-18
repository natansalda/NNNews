package pl.nataliana.nnnews;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class InfoLoader extends AsyncTaskLoader<List<Info>> {

    //Global variable for the URL
    private String mUrl;

    public InfoLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Info> loadInBackground() {

        //If the URL is empty, don't do anything
        if (mUrl == null){
            return null;
        }

        //If there is a request URL, get the data from it and return
        List<Info> infos = Utils.fetchNewsData(mUrl);
        return infos;
    }
}