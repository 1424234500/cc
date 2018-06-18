package util.view;
 

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;


public class ImageView extends android.widget.ImageView implements View.OnClickListener {
	Bitmap bitmap;

	public ImageView(Context con) {
		super(con);
		this.setOnClickListener(this);
	}
	public ImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnClickListener(this);
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		this.bitmap = bm;
	}

	@Override
	public void onClick(View v) {
		if(bitmap == null)return;
		DialogImageShow dis = new DialogImageShow(getContext(), bitmap);
		dis.show();
		dis.setCancelable(true);
	}
}