package com.wheel;




import com.pcstar.people.activity.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.Button;

public class ImageTextButton extends Button 
{
	private final String namespace = "http://www.javaeye.com/custom";
	private int resourceId = 0;
	private Bitmap bitmap;

	public ImageTextButton(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		setClickable(true);
		// 默锟斤拷使锟斤拷R.drawable.icon锟斤拷锟斤拷图片锟斤拷通锟斤拷icon锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟饺⊥计�
		resourceId = attrs.getAttributeResourceValue(namespace, "icon",R.drawable.appicon);
		bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
		this.setTextSize(12);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		// 图片锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷示
		int x = (this.getMeasuredWidth() - bitmap.getWidth()) >> 1;
		int y = 0;
		canvas.drawBitmap(bitmap, x, y, null);
		// 锟斤拷锟斤拷锟揭拷锟斤拷锟斤拷锟轿拷锟斤拷锟斤拷锟斤拷Button锟叫碉拷锟斤拷锟街撅拷锟斤拷锟斤拷示
		// 锟斤拷锟斤拷锟斤拷要锟斤拷锟斤拷锟斤拷锟节底诧拷锟斤拷示
		canvas.translate(0,(this.getMeasuredHeight() >> 1) - (int) this.getTextSize());
		super.onDraw(canvas);
	}

	public void setIcon(Bitmap bitmap) 
	{
		this.bitmap = bitmap;
		invalidate();
	}

	public void setIcon(int resourceId) 
	{
		this.bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
		invalidate();
	}

}
