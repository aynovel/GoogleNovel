package life.forever.cf.publics.tool;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import life.forever.cf.R;


public class ManagerDialog extends Dialog implements View.OnClickListener {

	public ManagerDialog(final Context context) {
		super(context, R.style.Theme_Update_Dialog);
		this.setContentView(R.layout.manager_dialog);
		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		Resources resources = context.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		params.width = (int) (dm.widthPixels * 0.98);
		window.setAttributes(params);
		findViewById(R.id.cancel).setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		if (arg0.getId() == R.id.cancel) {
			this.dismiss();
		}
	}
}
