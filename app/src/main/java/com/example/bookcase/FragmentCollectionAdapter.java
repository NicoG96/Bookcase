package com.example.bookcase;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import static com.example.bookcase.MainActivity.library;

public class FragmentCollectionAdapter extends FragmentStatePagerAdapter {
    private int bookSelected;

    public FragmentCollectionAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        //create an instance of a detail fragment, along with its respective bundle
        BookDetailsFragment bdf = new BookDetailsFragment();
        Bundle bundle = new Bundle();

        //send the index along with the bundle, accounting for array out of bounds exceptions
        bundle.putInt("index", (bookSelected++) % library.size());
        bdf.setArguments(bundle);

        //return the object of everything just created
        return bdf;
    }

    @Override
    public int getCount() {
        return 25;
    }

    public void setBookSelected(int bookSelected) {
        this.bookSelected = bookSelected;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }
}
