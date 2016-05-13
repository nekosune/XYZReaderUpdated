package com.example.xyzreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.data.UpdaterService;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewsList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewsList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsList extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;

    public NewsList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewsList.
     */
    // TODO: Rename and change types and number of parameters
    public static NewsList newInstance() {
        NewsList fragment = new NewsList();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_news_list, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_news);
        if (savedInstanceState == null) {
            onRefresh();
        }
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        getContext().registerReceiver(mRefreshingReceiver,
                new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(mRefreshingReceiver);
    }

    private boolean mIsRefreshing = false;

    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
                updateRefreshingUI();
            }
        }
    };

    private void updateRefreshingUI() {
        mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return ArticleLoader.newAllArticlesInstance(getContext());
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        NewsAdapter adapter = new NewsAdapter(data, new XYZAdapterOnClickHandler() {
            @Override
            public void onClick(int id, NewsAdapter.NewsHolder adapter) {
                mListener.onChoice(id);
            }
        });
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }


    @Override
    public void onRefresh() {
        getContext().startService(new Intent(getContext(), UpdaterService.class));
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onChoice(int id);
    }

    public static interface XYZAdapterOnClickHandler {
        void onClick(int id, NewsAdapter.NewsHolder adapter);
    }

    public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsHolder> {

        private Cursor mCursor;
        private XYZAdapterOnClickHandler mClickHandler;
        public NewsAdapter(Cursor cursor,XYZAdapterOnClickHandler mClickHandler) {
            mCursor = cursor;
            this.mClickHandler=mClickHandler;
        }


        @Override
        public long getItemId(int position) {
            mCursor.moveToPosition(position);
            return mCursor.getLong(ArticleLoader.Query._ID);
        }

        @Override
        public NewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.news_list_item, parent, false);
            final NewsHolder nh = new NewsHolder(view);

            return nh;
        }

        @Override
        public void onBindViewHolder(NewsHolder holder, int position) {
            mCursor.moveToPosition(position);
            holder.titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
            holder.subtitleView.setText(String.format(getResources().getString(R.string.byLine),DateUtils.getRelativeTimeSpanString(mCursor.getLong(ArticleLoader.Query.PUBLISHED_DATE), System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL).toString(),mCursor.getString(ArticleLoader.Query.AUTHOR))) ;
            Picasso.with(getContext()).load(mCursor.getString(ArticleLoader.Query.PHOTO_URL)).into(holder.imageView);

        }

        @Override
        public int getItemCount() {
            return mCursor.getCount();
        }

        public class NewsHolder extends RecyclerView.ViewHolder implements OnClickListener {

            public TextView titleView;
            public TextView subtitleView;
            public ImageView imageView;

            public NewsHolder(View itemView) {
                super(itemView);
                titleView = (TextView) itemView.findViewById(R.id.title_text);
                subtitleView = (TextView) itemView.findViewById(R.id.subtitle_text);
                imageView = (ImageView) itemView.findViewById(R.id.news_image);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int adapterPosition = getAdapterPosition();
                mCursor.moveToPosition(adapterPosition);
                int dateColumnIndex = ArticleLoader.Query._ID;
                mClickHandler.onClick(mCursor.getPosition(), this);
            }
        }

    }

}
