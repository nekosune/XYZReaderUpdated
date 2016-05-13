package com.example.xyzreader;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xyzreader.data.ArticleLoader;
import com.squareup.picasso.Picasso;


/**
 * Created by Katrina on 11/05/2016.
 */
public class DetailsSingleFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String ARG_NEWSID = "newsID";
    private TextView mArticleTitle;
    private ImageView mImageView;
    private TextView mSubtitleView;
    private Toolbar mToolbar;
    private FloatingActionButton mButton;

    private TextView mTextView;
    // TODO: Rename and change types of parameters
    private int newsID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            newsID = getArguments().getInt(ARG_NEWSID);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getLoaderManager().initLoader(1, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_single_detail, container, false);
        mArticleTitle= (TextView) view.findViewById(R.id.detailsName);
        mImageView=(ImageView)view.findViewById(R.id.detailsImage);
        mSubtitleView=(TextView)view.findViewById(R.id.detail_subtitle_text);
        mTextView=(TextView)view.findViewById(R.id.detailText);
        mToolbar=(Toolbar)view.findViewById(R.id.detailsToolbar);
        mButton=(FloatingActionButton)view.findViewById(R.id.fab);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(getString(R.string.share_text))
                        .getIntent(), getString(R.string.action_share)));
            }
        });
        if(mToolbar!=null)
        {
            ((DetailsActivity)getActivity()).setSupportActionBar(mToolbar);
            ((DetailsActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((DetailsActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            ((DetailsActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
        }
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id==1)
        {
            return ArticleLoader.newInstanceForItemId(getContext(),getArguments().getInt(ARG_NEWSID));
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.moveToFirst()) {
            mArticleTitle.setText(data.getString(ArticleLoader.Query.TITLE));
            Picasso.with(getContext()).load(data.getString(ArticleLoader.Query.PHOTO_URL)).into(mImageView);
            mSubtitleView.setText(String.format(getResources().getString(R.string.byLine),DateUtils.getRelativeTimeSpanString(data.getLong(ArticleLoader.Query.PUBLISHED_DATE), System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL).toString(),data.getString(ArticleLoader.Query.AUTHOR))) ;
            mTextView.setText(Html.fromHtml(data.getString(ArticleLoader.Query.BODY)));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
