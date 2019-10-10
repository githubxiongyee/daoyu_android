package com.daoyu.chat.module.mine.activity;

import android.graphics.Canvas;

import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseTitleActivity;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnDrawListener;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import butterknife.BindView;

//了解潜言
public class UnderstandAct extends BaseTitleActivity implements OnPageChangeListener, OnLoadCompleteListener, OnDrawListener {
    @BindView(R.id.pdf)
    PDFView pdfView;

    @Override
    protected int getLayoutResId() {
        return R.layout.act_understand;
    }

    @Override
    protected void initEvent() {
        setCurrentTitle("了解潜言");

        displayFromAssets("product.pdf");
    }

    private void displayFromAssets(String assetfilename) {

        pdfView.fromAsset(assetfilename)//设置pdf文件地址

                .defaultPage(1)//设置默认显示第1页

                .onPageChange(this)//设置翻页监听

                .onLoad(this) //设置加载监听

                .onDraw(this) //绘图监听

                .showMinimap(false)//pdf放大的时候，是否在屏幕的右上角生成小地图

                .swipeVertical(true)//pdf文档翻页是否是垂直翻页，默认是左右滑动翻页

                .enableSwipe(true)//是否允许翻页，默认是允许翻页

                //   .pages() //把 5 过滤掉

                .load();

    }

    @Override
    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

    }

    @Override
    public void loadComplete(int nbPages) {

    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        //toast.toastShow("page= " + page + " ,pagecount= " + pageCount);
    }
}
