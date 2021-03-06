package io.github.javiewer.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.LinkedHashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.javiewer.R;
import io.github.javiewer.adapter.ViewPagerAdapter;
import io.github.javiewer.adapter.item.Genre;
import io.github.javiewer.network.AVMO;
import io.github.javiewer.network.provider.AVMOProvider;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GenreFragment extends Fragment implements ToolbarNoElevationFragment {

    @Bind(R.id.genre_tabs)
    public TabLayout mTabLayout;

    @Bind(R.id.genre_view_pager)
    public ViewPager mViewPager;

    @Bind(R.id.genre_progress_bar)
    public ProgressBar mProgressBar;

    public ViewPagerAdapter mAdapter;

    public GenreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AVMO.BASE_URL)
                .build();
        AVMO avmo = retrofit.create(AVMO.class);
        Call<ResponseBody> call = avmo.getGenre();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressBar.setVisibility(View.INVISIBLE);
                try {
                    LinkedHashMap<String, List<Genre>> genres = AVMOProvider.parseGenres(response.body().string());

                    GenreListFragment fragment;
                    for (String title : genres.keySet()) {
                        fragment = new GenreListFragment();
                        fragment.getGenres().addAll(genres.get(title));
                        mAdapter.addFragment(fragment, title);
                    }

                    mAdapter.notifyDataSetChanged();

                    mTabLayout.setVisibility(View.VISIBLE);
                } catch (Throwable e) {
                    onFailure(call, e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_genre, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
