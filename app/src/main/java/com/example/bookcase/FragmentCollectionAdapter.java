package com.example.bookcase;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class FragmentCollectionAdapter extends FragmentStatePagerAdapter {

    public FragmentCollectionAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        //create an instance of a detail fragment, along with its respective bundle
        BookDetailsFragment bdf = new BookDetailsFragment();
        Bundle bundle = new Bundle();
        i++;

        //send the index along with the bundle
        bundle.putInt("index", i);
        bdf.setArguments(bundle);

        //return the object of everything just created
        return bdf;
    }

    @Override
    public int getCount() {
        return 10;
    }
}
