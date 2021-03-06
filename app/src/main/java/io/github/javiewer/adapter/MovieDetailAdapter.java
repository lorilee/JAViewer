package io.github.javiewer.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.javiewer.R;
import io.github.javiewer.activity.GalleryActivity;
import io.github.javiewer.activity.MainActivity;
import io.github.javiewer.adapter.item.MovieDetail;
import io.github.javiewer.adapter.item.Screenshot;

/**
 * Project: JAViewer
 */
public class MovieDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Screenshot> screenshots;

    private Activity mParentActivity;

    private ScreenshotAdapter mScreenshotAdapter;

    private MovieDetail detailInfo = null;

    private boolean inited = false;

    public MovieDetailAdapter(List<Screenshot> screenshots, Activity mParentActivity) {
        this.screenshots = screenshots;
        this.mParentActivity = mParentActivity;
    }

    public void onInit(MovieDetail detailInfo) {
        this.detailInfo = detailInfo;
        inited = true;
        notifyItemRangeChanged(0, this.getItemCount());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0: {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_info, parent, false);
                return new InfoViewHolder(v);
            }

            case 1: {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_screenshots, parent, false);
                return new ScreenshotsViewHolder(v, screenshots, mParentActivity);
            }
        }

        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof InfoViewHolder) {
            if (detailInfo != null) {
                InfoViewHolder vh = (InfoViewHolder) holder;
                vh.mCodeText.setText(detailInfo.code);
                vh.mDateText.setText(detailInfo.date);
                vh.mDurationText.setText(detailInfo.duration);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (!inited) {
            return 0;
        }
        return (detailInfo == null ? 0 : 1) + //基本信息
                (screenshots == null ? 0 : 1); //截图
    }

    public class InfoViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.info_text_code)
        TextView mCodeText;

        @Bind(R.id.info_text_date)
        TextView mDateText;

        @Bind(R.id.info_text_duration)
        TextView mDurationText;

        public InfoViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);
        }
    }

    public class ScreenshotsViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.screenshots_recycler_view)
        RecyclerView mRecyclerView;

        @Bind(R.id.movie_icon_photo)
        ImageView mIcon;

        @Bind(R.id.screenshots_text)
        TextView mText;

        public ScreenshotsViewHolder(View view, List<Screenshot> screenshots, Activity mParentActivity) {
            super(view);

            ButterKnife.bind(this, view);

            mRecyclerView.setAdapter(mScreenshotAdapter = new ScreenshotAdapter(screenshots, mParentActivity, mIcon));
            mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
            mRecyclerView.setNestedScrollingEnabled(false);

            if (screenshots.isEmpty()) {
                mRecyclerView.setVisibility(View.GONE);
                mText.setVisibility(View.VISIBLE);
            }

            alignIconToView(mIcon, mText);

        }
    }

    private static void alignIconToView(final View icon, final View view) {
        Log.i("aligning", icon.toString() + " to " + view.toString());
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                icon.setPadding(
                        icon.getPaddingLeft(),
                        (view.getMeasuredHeight() - icon.getMeasuredHeight()) / 2,
                        icon.getPaddingRight(),
                        icon.getPaddingBottom()
                );

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    public class ScreenshotAdapter extends RecyclerView.Adapter<ScreenshotAdapter.ViewHolder> {

        private List<Screenshot> screenshots;

        private Activity mParentActivity;

        private ImageView mIcon;

        public ScreenshotAdapter(List<Screenshot> screenshots, Activity mParentActivity, ImageView mIcon) {
            this.screenshots = screenshots;
            this.mParentActivity = mParentActivity;
            this.mIcon = mIcon;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_screenshot, parent, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            Screenshot screenshot = screenshots.get(position);

            ImageLoader.getInstance().displayImage(screenshot.getThumbnailUrl(), holder.mImage, MainActivity.displayImageOptions);

            holder.mImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mParentActivity, GalleryActivity.class);
                    Bundle bundle = new Bundle();

                    String[] urls = new String[screenshots.size()];
                    for (int k = 0; k < screenshots.size(); k++) {
                        urls[k] = screenshots.get(k).getImageUrl();
                    }
                    bundle.putStringArray("urls", urls);
                    bundle.putInt("position", holder.getAdapterPosition());
                    i.putExtras(bundle);
                    mParentActivity.startActivity(i);

                }
            });

            if (position == 0) {
                alignIconToView(mIcon, holder.mImage);
            }
        }

        @Override
        public int getItemCount() {
            return screenshots == null ? 0 : screenshots.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.screenshot_image_view)
            public ImageView mImage;

            public ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }
}
