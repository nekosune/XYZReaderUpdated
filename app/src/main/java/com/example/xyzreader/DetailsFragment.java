package com.example.xyzreader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xyzreader.data.ArticleLoader;


/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class DetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{


    public static final String ARG_POSITION = "position";
    private ViewPager mViewPager;
    private CursorPagerAdapter<DetailsSingleFragment> mAdapter;
    public int mPosition=0;
    public DetailsFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments().containsKey(ARG_POSITION))
        {
            mPosition=getArguments().getInt(ARG_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_details, container, false);

        mViewPager= (ViewPager) view.findViewById(R.id.pager);
        mAdapter=new CursorPagerAdapter<DetailsSingleFragment>(getActivity().getSupportFragmentManager(),DetailsSingleFragment.class,null);

        mViewPager.setAdapter(mAdapter);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return ArticleLoader.newAllArticlesInstance(getContext());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        mViewPager.setCurrentItem(mPosition);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
       // mViewPager.setAdapter(null);
    }


    public class CursorPagerAdapter<F extends Fragment> extends FragmentStatePagerAdapter {
        private final Class<F> fragmentClass;
        private Cursor cursor;

        public CursorPagerAdapter(FragmentManager fm, Class<F> fragmentClass, Cursor cursor) {
            super(fm);
            this.fragmentClass = fragmentClass;
            this.cursor = cursor;
        }

        @Override
        public F getItem(int position) {
            if (cursor == null) // shouldn't happen
                return null;

            cursor.moveToPosition(position);
            F frag;
            try {
                frag = fragmentClass.newInstance();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            Bundle args = new Bundle();
            args.putInt(DetailsSingleFragment.ARG_NEWSID,cursor.getInt(ArticleLoader.Query._ID));
            frag.setArguments(args);
            return frag;
        }

        @Override
        public int getCount() {
            if (cursor == null)
                return 0;
            else
                return cursor.getCount();
        }

        public void swapCursor(Cursor c) {
            if (cursor == c)
                return;

            this.cursor = c;
            notifyDataSetChanged();
        }

        public Cursor getCursor() {
            return cursor;
        }
    }
}
