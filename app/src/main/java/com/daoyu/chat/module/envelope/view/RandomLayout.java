package com.daoyu.chat.module.envelope.view;
import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.daoyu.chat.module.envelope.bean.TextItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Squirrelhuan on 2017/7/21.
 */

public class RandomLayout extends FrameLayout implements ViewTreeObserver.OnGlobalLayoutListener {

    private int[] colors = {android.R.color.black};
    private int width, height;
    Random random = new Random();

    public RandomLayout(Context context) {
        super(context);
    }

    public RandomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RandomLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private boolean isFirst = true;

    public void onGlobalLayout() {
        if (isFirst) {
            height = getHeight();
            isFirst = false;
        }
    }

    //@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    int count;

    public void setData(List<TextItem> items, final Context context) {
        count = items.size();

        //1.优先级排序
        items = sortTextItem(items);
        //2.字体大小排序
        int[] frontSizes = generateFrontSize(items.size());
        for (int i = 0; i < count; i++) {
            TextItem temp = items.get(i);
            temp.setFrontSize(frontSizes[i]);
            temp.setFrontColor(colors[0]);
            items.set(i, temp);
        }

        for (int i = count - 1; i >= 0; i--) {
            final TextView textView = new TextView(context);
            textView.setText(items.get(i).getValue());
            textView.setTextSize(items.get(i).getFrontSize());
            textView.setTextColor(context.getResources().getColor(items.get(i).getFrontColor()));
            int b = random.nextInt(2);
            if (b == 1) {
                textView.setSingleLine(true);
            } else {
                textView.setEms(1);
            }
            textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
            textView.setTag(items.get(i));
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener == null) {
                        return;
                    }
                    TextItem textItem = ((TextItem) view.getTag());
                    listener.onItemViewClickListenerView(textItem.getTag(), textView);

                }
            });
            addView(textView);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            FrameLayout.MarginLayoutParams marginParams = null;
            //获取view的margin设置参数
            if (params instanceof ViewGroup.MarginLayoutParams) {
                marginParams = (ViewGroup.MarginLayoutParams) params;
            } else {
                //不存在时创建一个新的参数
                //基于View本身原有的布局参数对象
                marginParams = new ViewGroup.MarginLayoutParams(params);
            }
            int height_px = getHeight();
            int width_px = getWidth();
            int left = random.nextInt(width_px);
            int top = random.nextInt(height_px);
            left = left > (width_px / 2) ? left - width_px / 10 - getCharacterWidth(items.get(i).getValue(), items.get(i).getFrontSize()) : left;
            top = top > (height_px / 2) ? top - height_px / 10 - ((b == 0) ? getCharacterWidth(items.get(i).getValue(), items.get(i).getFrontSize()) : items.get(i).getFrontSize()) : top;
            marginParams.setMargins(left, top, 0, 0);
            textView.setLayoutParams(marginParams);
        }
        TextView tv = null;

    }


    private int[] generateFrontSize(int count) {
        int[] sizes = new int[count];
        for (int i = 0; i < count; i++) {
            random = new Random();
            //sizes[i] = (random.nextInt(6) * 5 + 12);
            sizes[i] = (30);
        }
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < count; j++) {
                if (sizes[i] > sizes[j]) {
                    int c = sizes[i];
                    sizes[i] = sizes[j];
                    sizes[j] = c;
                }
            }
        }
        return sizes;
    }

    /**
     * 排序
     */
    List<TextItem> sortTextItem(List<TextItem> items) {
        int count = items.size();
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < count; j++) {
                if (items.get(i).getIndex() > items.get(j).getIndex()) {
                    TextItem item_c = items.get(i);
                    items.add(i, items.get(j));
                    items.add(j, item_c);
                }
            }
        }
        return items;
    }

    public int getCharacterWidth(String text, float size) {
        if (null == text || "".equals(text)) {
            return 0;

        }
        Paint paint = new Paint();
        paint.setTextSize(size);
        int text_width = (int) paint.measureText(text);
        return text_width;
    }

    public void setTextData(final String[] texts, final Context context) {
        post(new Runnable() {
            @Override
            public void run() {
                List<TextItem> textItems = new ArrayList<TextItem>();
                for (int i = 0; i < texts.length; i++) {
                    TextItem item = new TextItem();
                    item.setIndex(texts.length);
                    item.setTag(i);
                    item.setValue(texts[i % texts.length]);
                    textItems.add(item);
                }
                setData(textItems, context);
            }
        });
    }

    public void setTextData(final List<String> texts, final Context context) {
        post(new Runnable() {
            @Override
            public void run() {
                List<TextItem> textItems = new ArrayList<TextItem>();
                for (int i = 0; i < texts.size(); i++) {
                    TextItem item = new TextItem();
                    item.setIndex(texts.size());
                    item.setTag(i);
                    item.setValue(texts.get(i % texts.size()));
                    textItems.add(item);
                }
                setData(textItems, context);
            }
        });
    }

    private OnItemViewClickListener listener;

    public void setListener(OnItemViewClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemViewClickListener {
        void onItemViewClickListenerView(int tag, TextView textView);
    }

    public void clearLayout(){
        removeAllViewsInLayout();
    }

}