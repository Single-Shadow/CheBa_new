/**
 * 
 */
package com.aou.cheba.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.aou.cheba.R;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kince
 * @category 模仿最美应用底部tagview
 * 
 */
public class TagListView extends FlowLayout implements OnClickListener {

	private OnTagCheckedChangedListener mOnTagCheckedChangedListener;
	private OnTagClickListener mOnTagClickListener;
	private OnDelClickListener mOnDelClickListener;

	private int mTagViewBackgroundResId = 0;
	private int mTagViewTextColorResId = 0;
	private final List<Tag> mTags = new ArrayList<Tag>();

	/**
	 * @param context
	 */
	public TagListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	/**
	 * @param context
	 * @param attributeSet
	 */
	public TagListView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		// TODO Auto-generated constructor stub
		init();
	}

	/**
	 * @param context
	 * @param attributeSet
	 * @param defStyle
	 */
	public TagListView(Context context, AttributeSet attributeSet, int defStyle) {
		super(context, attributeSet, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	@Override
	public void onClick(View v) {
		if ((v instanceof TagView)) {
			Tag localTag = (Tag) v.getTag();
			if (this.mOnTagClickListener != null) {
				this.mOnTagClickListener.onTagClick((TagView) v, localTag);
			}
		} else if ((v instanceof TextView)) {
			Tag localTag = (Tag) v.getTag();
			if (this.mOnDelClickListener != null) {
				this.mOnDelClickListener.onDelClick((TextView) v, localTag);
			}
		}
	}

	private void init() {

	}

	private void inflateTagView(final Tag t, boolean b) {
		View view = View.inflate(getContext(), R.layout.tag, null);

		TextView localTextView = (TextView) view.findViewById(R.id.tv_del);
		if (t.isShowDel()) {
			localTextView.setTag(t);
			localTextView.setOnClickListener(this);
			localTextView.setVisibility(View.VISIBLE);
		} else {
			localTextView.setVisibility(View.GONE);
		}

		TagView localTagView = (TagView) view.findViewById(R.id.tagview);
		localTagView.setText(t.getTitle());
		localTagView.setTag(t);

		localTagView.setChecked(t.isChecked());
		if (t.isChecked()) {
			localTagView.setTextColor(Color.parseColor("#ff33b5e5"));
			view.setBackgroundResource(R.drawable.tag_checked_pressed);
		} else {
			localTagView.setTextColor(Color.parseColor("#ff999999"));
			view.setBackgroundResource(R.drawable.tag_checked_normal);
		}

		if (mTagViewTextColorResId != 0) {
			localTagView.setTextColor(mTagViewTextColorResId);
		}

		if (mTagViewBackgroundResId != 0) {
			view.setBackgroundResource(mTagViewBackgroundResId);
		}

		localTagView.setCheckEnable(b);

		if ((t.getLeftDrawableResId() > 0) || (t.getRightDrawableResId() > 0)) {
			localTagView.setCompoundDrawablesWithIntrinsicBounds(
					t.getLeftDrawableResId(), 0, t.getRightDrawableResId(), 0);
		}
		localTagView.setOnClickListener(this);
		localTagView
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					public void onCheckedChanged(
							CompoundButton paramAnonymousCompoundButton,
							boolean paramAnonymousBoolean) {
						t.setChecked(paramAnonymousBoolean);
						if (TagListView.this.mOnTagCheckedChangedListener != null) {
							TagListView.this.mOnTagCheckedChangedListener
									.onTagCheckedChanged(
											(TagView) paramAnonymousCompoundButton,
											t);
						}
					}
				});

		addView(view);
	}

	public void addTag(int i, String s) {
		addTag(i, s, false);
	}

	public void addTag(int i, String s, boolean b) {
		addTag(new Tag(i, s), b);
	}

	public void addTag(Tag tag) {
		addTag(tag, false);
	}

	public void addTag(Tag tag, boolean b) {
		mTags.add(tag);
		inflateTagView(tag, b);
	}

	public void addTags(List<Tag> lists) {
		addTags(lists, false);
	}

	public void addTags(List<Tag> lists, boolean b) {
		for (int i = 0; i < lists.size(); i++) {
			addTag((Tag) lists.get(i), b);
		}
	}

	public List<Tag> getTags() {
		return mTags;
	}

	public View getViewByTag(Tag tag) {
		return findViewWithTag(tag);
	}

	public void removeTag(Tag tag) {
		mTags.remove(tag);
		removeView(getViewByTag(tag));
	}

	public void setOnTagCheckedChangedListener(
			OnTagCheckedChangedListener onTagCheckedChangedListener) {
		mOnTagCheckedChangedListener = onTagCheckedChangedListener;
	}

	public void setOnTagClickListener(OnTagClickListener onTagClickListener) {
		mOnTagClickListener = onTagClickListener;
	}

	public void setOnDelClickListener(OnDelClickListener onDelClickListener) {
		mOnDelClickListener = onDelClickListener;
	}

	public void setTagViewBackgroundRes(int res) {
		mTagViewBackgroundResId = res;
	}

	public void setTagViewTextColorRes(int res) {
		mTagViewTextColorResId = res;
	}

	public void setTags(List<? extends Tag> lists) {
		setTags(lists, false);
	}

	public void setTags(List<? extends Tag> lists, boolean b) {
		removeAllViews();
		mTags.clear();
		for (int i = 0; i < lists.size(); i++) {
			addTag((Tag) lists.get(i), b);
		}
	}

	public static abstract interface OnTagCheckedChangedListener {
		public abstract void onTagCheckedChanged(TagView tagView, Tag tag);
	}

	public static abstract interface OnTagClickListener {
		public abstract void onTagClick(TagView tagView, Tag tag);
	}

	public static abstract interface OnDelClickListener {
		public abstract void onDelClick(TextView textView, Tag tag);
	}
}
