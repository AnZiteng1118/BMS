package com.AmoSmartRF.bluetooth.le;

import com.lin.bluetooth.le.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class HistogramView extends View {

	private Paint xLinePaint;// 坐标轴 轴线 画笔：
	private Paint hLinePaint;// 坐标轴水平内部 虚线画笔
	private Paint titlePaint;// 绘制文本的画笔
	private Paint paint;// 矩形画笔 柱状图的样式信息
	private double[] progress;// 
	private double[] aniProgress;// 实现动画的值
	private final int TRUE = 1;// 在柱状图上显示数字
	private double[] text; 	// 设置点击事件，显示哪一条柱状的信息
	private String[] ySteps; // 坐标轴左侧的数标
	private String[] xWeeks; // 坐标轴底部的节数
	private HistogramAnimation ani;

	public HistogramView(Context context) {
		super(context);
		init(context, null);
	}

	public HistogramView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {

		ySteps = new String[] { "5", "4", "3","2","1","0" };
		xWeeks = new String[] { "1", "2", "3", "4", "5", "6", "7","8","9","10","11","12","13","14","15","16" };
		text = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,0,0,0};
		aniProgress = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,0,0,0,0,0 };
		ani = new HistogramAnimation();
		ani.setDuration(1000);

		xLinePaint = new Paint();
		hLinePaint = new Paint();
		titlePaint = new Paint();
		paint = new Paint();
		
		// 给画笔设置颜色
		xLinePaint.setColor(Color.BLACK);
		hLinePaint.setColor(Color.RED);
		titlePaint.setColor(Color.BLACK);
	}

	public void setText(double[] text) {

		this.text = text;

		this.postInvalidate();
	}

	public void setProgress(double[] progress) {
		this.progress = progress;
		// this.invalidate(); 
		// this.postInvalidate(); 
		this.startAnimation(ani);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(event);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		int width = getWidth();
		int height = getHeight() - 50;

		// startX, startY, stopX, stopY, paint
		int startX = dip2px(getContext(), 50);
		int startY = dip2px(getContext(), 10);
		int stopX = dip2px(getContext(), 50);
		int stopY = dip2px(getContext(), 320);
		canvas.drawLine(10, 10, 10, height, xLinePaint);

		canvas.drawLine(10, height, width - 10, height, xLinePaint);

		int leftHeight = height-20;// 减去一个底边

		int hPerHeight = leftHeight / 5;// 横向分成5份

		//画出5条横线
		hLinePaint.setTextAlign(Align.CENTER);
		for (int i = 0; i < 5; i++) {
			canvas.drawLine(10, 20 + i * hPerHeight, width - 10, 20 + i
					* hPerHeight, hLinePaint);
		}

		
		//加上纵坐标的值
		titlePaint.setTextAlign(Align.RIGHT);
		titlePaint.setTextSize(20);
		titlePaint.setAntiAlias(true);
		titlePaint.setStyle(Paint.Style.FILL);
		
		//设置左部的数字
		for (int i = 0; i < ySteps.length; i++) {
			canvas.drawText(ySteps[i], 10, 20 + i * hPerHeight, titlePaint);
		}

		// 加上x坐标的值
		int xAxisLength = width - 10;
		int columCount = xWeeks.length + 1;
		int step = xAxisLength / columCount;//宽度除以个数，相当于矩形个数

		//设置底部的数字
		for (int i = 0; i < columCount - 1; i++) {
			// text, baseX, baseY, textPaint
			canvas.drawText(xWeeks[i], 25 + step * (i + 1), height+30, titlePaint);
		}

		//绘制矩形
		if (aniProgress != null && aniProgress.length > 0) {
			for (int i = 0; i < aniProgress.length; i++) {// 寰幆閬嶅巻灏�7鏉℃煴鐘跺浘褰㈢敾鍑烘潵
				double value = aniProgress[i];//要显示的矩形高度，也就是值
				paint.setAntiAlias(true);// 抗锯齿功能，使图像边缘相对清晰
				paint.setStyle(Paint.Style.FILL);
				paint.setTextSize(15);// // 字体大小
				paint.setColor(Color.parseColor("#6DCAEC"));// 字体颜色 
				Rect rect = new Rect();// 绘制矩形

				rect.left = 20 + step * (i + 1) - 20;//矩形左右边的位置
				rect.right = 20 + step * (i + 1) + 20;
				int rh = (int) (leftHeight - leftHeight * (value/5.0));
				rect.top = rh + 20;
				rect.bottom = height;

				Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.column);

				canvas.drawBitmap(bitmap, null, rect, paint);
				// 显示柱状图上方的数字
				canvas.drawText(value + "", 20 + step * (i + 1) - 20,
						rh + 10, paint);
				/* // 是否显示柱状图上方的数字
				if (this.text[i] == TRUE) {
					canvas.drawText(value + "", 30 + step * (i + 1) - 30,
							rh + 10, paint);
				}*/
			}
		}
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	private class HistogramAnimation extends Animation {
		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {
			super.applyTransformation(interpolatedTime, t);
			if (interpolatedTime < 1.0f) {
				for (int i = 0; i < aniProgress.length; i++) {
					aniProgress[i] = (int) (progress[i] * interpolatedTime);
				}
			} else {
				for (int i = 0; i < aniProgress.length; i++) {
					aniProgress[i] = progress[i];
				}
			}
			postInvalidate();
		}
	}

}