package com.unionman.filebrowser.localplayer.adapter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.provider.MediaStore.Video.Thumbnails;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.unionman.filebrowser.MyMediaActivity;
import com.unionman.filebrowser.R;
import com.unionman.filebrowser.localplayer.BaseActivity;
import com.unionman.filebrowser.localplayer.CustomApplication;
import com.unionman.filebrowser.localplayer.util.AsynImageLoader;
import com.unionman.filebrowser.localplayer.util.FileUtil;
import com.unionman.filebrowser.localplayer.util.ToastUtil;

/**
 * data adapter
 * 
 * @author liu_tianbao Provide data for the list or thumbnail
 */
public class FileAdapter extends BaseAdapter {
	private final static String TAG = "LocalPlayer--FileAdapter";
	private AsynImageLoader mAsynImageLoader;
	// filder
	private Bitmap folder_File;

	// music file
	private Bitmap music_File_mp3;

	// other file
	private Bitmap other_File;

	// vedio file
	private Bitmap video_File;

	// APK file
	private Bitmap apk_File;

	// picturn file
	private Bitmap img_File;

	// file list
	public List<File> list;

	// layout file ID
	private int layout = 0;

	public static final int IMAGE = 1;
	public static final int VIDEO = 2;

	/**
	 * @brief : Eliminating redundant code
	 */

	LayoutInflater inflater;

	BaseActivity context;

	String fileLength = null;

	boolean isListView = true;
	public static Boolean isChecBoxShow = false;

	public static Map<Integer, Boolean> isSelectedMap = new HashMap<Integer, Boolean>();
	

	/**
	 * @param context
	 *            page
	 * @param list
	 *            collection of files
	 * @param fileString
	 *            file path
	 * @param layout
	 *            data layout
	 */
	public FileAdapter(BaseActivity context, List<File> list, int layout,
			Handler handler, Boolean isChecBoxShow) {
		/**
		 * Initialization parameters
		 */
		this.list = list;
		this.layout = layout;
		inflater = LayoutInflater.from(context);
		this.context = context;
		mAsynImageLoader = new AsynImageLoader(handler);
		this.isChecBoxShow = isChecBoxShow;
		initSelectedMapData();

		/*
		 * According to the different file types together with the different
		 * picture
		 */
		/* CNcomment: 根据不同的文件类型配以不同的图片显示 */

		other_File = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.file_other);
		apk_File = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.file_apk);
		video_File = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.file_vedio);
		music_File_mp3 = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.file_mp3);
		folder_File = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.file_folder);
		img_File = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.file_img);
	}

	/**
	 * Init the checkbox selected data map value to false.
	 */
	public void initSelectedMapData() {
		
		for (int len = 0; len < list.size(); len++) {
			isSelectedMap.put(len, false);
		}
	}

	/* the number of data containers */
	/* CNcomment: 获得容器中数据的数目 */
	public int getCount() {
		return list.size();
	}

	public void addFile(File file) {
		this.list.add(file);
	}

	public void addDir(int location, File file) {
		this.list.add(location, file);
	}

	/* For each option object container */
	/* CNcomment: 获得容器中每个选项对象 */

	public Object getItem(int position) {
		return position;
	}

	/* Access to each option in the container object */
	/* CNcomment: 获得容器中每个选项对象的ID */

	public long getItemId(int position) {
		return position;
	}

	public List<File> getFiles() {
		return list;
	}

	/* Assignment for each option object */
	/* CNcomment: 为每个选项对象赋值 */

	public View getView(final int position, View convertView, ViewGroup parent) {
		return getMyView(convertView, position);
	}

	public class ViewHolder {
		TextView tvFileName;
		TextView tvFileSize;
		ImageView ivFileIcon;
		public CheckBox iv_item_select;
	}

	public static Map<Integer, Boolean> getIsSelectedMap() {
		return isSelectedMap;
	}

	public static void setIsSelected(Map<Integer, Boolean> isSelectedMap) {
		FileAdapter.isSelectedMap = isSelectedMap;
	}

	private View getMyView(View convertView, final int position) {
		ViewHolder holder = null;
		if (layout == R.layout.gridview_item) {
			isListView = false;
		}
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(layout, null);
			holder.tvFileName = (TextView) convertView
					.findViewById(R.id.tv_filename);
			holder.tvFileSize = (TextView) convertView
					.findViewById(R.id.tv_filesize);
			holder.ivFileIcon = (ImageView) convertView
					.findViewById(R.id.iv_fileicon);
			holder.iv_item_select = (CheckBox) convertView
					.findViewById(R.id.iv_item_select);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.iv_item_select.setVisibility(this.isChecBoxShow ? View.VISIBLE
				: View.GONE);
		holder.iv_item_select.setChecked(isSelectedMap.get(position));


		// Control container
		/*
		 * According to the view whether there is to initialize the controls in
		 * each data container
		 */
		/* CNcomment: 根据视图是否存在 初始化每个数据容器中的控件 */
		final File file;
		file = list.get(position);
		String f_type = FileUtil.getMIMEType(file, context);
		// get the type of file
		if (file.isFile()) {
			if ("audio/*".equals(f_type)) {
				holder.ivFileIcon.setImageBitmap(music_File_mp3);
			} else if ("video/*".equals(f_type) || "video/iso".equals(f_type)) {
				if (isListView) {
					holder.ivFileIcon.setImageBitmap(video_File);
				} else {
					holder.ivFileIcon.setTag(file.getAbsolutePath());
					mAsynImageLoader.loadBitmap(holder.ivFileIcon, video_File,
							VIDEO, position);
				}
			} else if ("apk/*".equals(f_type)) {
				holder.ivFileIcon.setImageBitmap(apk_File);
			} else if (f_type.contains("image")) {
				if (isListView) {
					holder.ivFileIcon.setImageBitmap(img_File);
				} else {
					holder.ivFileIcon.setTag(file.getAbsolutePath());
					mAsynImageLoader.loadBitmap(holder.ivFileIcon, img_File,
							IMAGE, position);
				}
			} else if ("video/dvd".equals(f_type)) {
				holder.ivFileIcon.setImageBitmap(video_File);
			} else {
				holder.ivFileIcon.setImageBitmap(other_File);
			}
		} else {
			if ("video/bd".equals(f_type)) {
				holder.ivFileIcon.setImageBitmap(video_File);
			} else if ("video/dvd".equals(f_type)) {
				holder.ivFileIcon.setImageBitmap(video_File);
			} else {
				holder.ivFileIcon.setImageBitmap(folder_File);
			}
		}

		// Judge the thumbnail display format of the text
		// CNcomment:判断缩略图时文字的显示格式
		if (layout == R.layout.gridview_item) {
			if (holder.tvFileName.getPaint().measureText(file.getName()) >= holder.tvFileName
					.getLayoutParams().width) {
				holder.tvFileName.setEllipsize(TruncateAt.MARQUEE);
				holder.tvFileName
						.setMarqueeRepeatLimit(android.R.attr.marqueeRepeatLimit);
				holder.tvFileName.setHorizontallyScrolling(true);
			} else {
				holder.tvFileName.setGravity(Gravity.CENTER_HORIZONTAL);
			}
		} else {
			if (file.isFile()) {
				fileLength = FileUtil.fileSizeMsg(file);
				holder.tvFileSize.setText(fileLength);
			} else {
				holder.tvFileSize.setText("");
			}
		}
		
		String mName = file.getName();
		holder.tvFileName.setText(mName);

		return convertView;
	}

	private Bitmap getVideoBitmap(String filePath) {
		Bitmap bitmap = null;
		bitmap = ThumbnailUtils.createVideoThumbnail(filePath,
				Thumbnails.MINI_KIND);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, 120, 100);
		return bitmap;
	}

	public Bitmap getVideoThumbnail(String filePath) {
		Bitmap bitmap = null;
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		try {
			retriever.setDataSource(filePath);
			bitmap = retriever.getFrameAtTime();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			try {
				retriever.release();
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}

}
