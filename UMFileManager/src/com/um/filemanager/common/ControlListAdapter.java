package com.um.filemanager.common;

import java.io.File;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import com.um.filemanager.R;

/**
 * data adapter
 */
public class ControlListAdapter extends BaseAdapter {
    // file list
    private List<File> list;

    LayoutInflater inflater;

    CommonActivity context;

    public List<File> getList() {
        return list;
    }

    /**
     * @param context
     * @param list
     * @param fileString
     *            //file path
     * @param layout
     */
    public ControlListAdapter(CommonActivity context, List<File> list) {
        /**
         * initalization parameters
         */
        this.list = list;
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    /**
     * the number of data containers
     */

    public int getCount() {
        return list.size();
    }

    /**
     * for each option object container
     */

    public Object getItem(int position) {
        return list.get(position);
    }

    /**
     * access to each option in the container object ID
     */

    public long getItemId(int position) {
        return position;
    }

    /**
     * assignment for each option object
     */

    public View getView(final int position, View convertView, ViewGroup parent) {
        // control container
        final ViewHolder holder;
        
        Drawable dr_mp3file = context.getResources().getDrawable(R.drawable.hisil_mp3file);
        dr_mp3file.setBounds(0, 0, 60, 60);
        Drawable dr_vediofile = context.getResources().getDrawable(R.drawable.hisil_vediofile);
        dr_vediofile.setBounds(0, 0, 60, 60);
        Drawable dr_imgfile = context.getResources().getDrawable(R.drawable.hisil_imgfile);
        dr_imgfile.setBounds(0, 0, 60, 60);
        Drawable dr_otherfile = context.getResources().getDrawable(R.drawable.hisil_otherfile);
        dr_otherfile.setBounds(0, 0, 60, 60);
        Drawable dr_folder_file = context.getResources().getDrawable(R.drawable.hisil_folder_file);
        dr_folder_file.setBounds(0, 0, 60, 60);
        Drawable drawable = context.getResources().getDrawable(R.drawable.list);
        drawable.setBounds(0, 0, 60, 60);

        /*
         * According to the view whether there is to initialize the controls in
         * each data container
         */
        /* CNcomment: 根据视图是否存在 初始化每个数据容器中的控件 */

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.control_row, null);
            holder.checkedTxt = (CheckedTextView) convertView
                                .findViewById(R.id.check);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        final File f;
        f = list.get(position);
        String f_type = FileUtil.getMIMEType(f, context);

        // Adaptation different picture file types
        if (f.isFile()) {
            if ("audio/*".equals(f_type)) {
                //              holder.checkedTxt.setCompoundDrawablesWithIntrinsicBounds(
                //                      R.drawable.mp3file, 0, 0, 0);
                holder.checkedTxt.setCompoundDrawables(dr_mp3file,null,null,null);
            }
            else if ("video/*".equals(f_type)) {
                //              holder.checkedTxt.setCompoundDrawablesWithIntrinsicBounds(
                //                      R.drawable.vediofile, 0, 0, 0);
                holder.checkedTxt.setCompoundDrawables(dr_vediofile,null,null,null);
            }
            else if ("apk/*".equals(f_type)) {
                holder.checkedTxt.setCompoundDrawables(drawable,null,null,null);
            }
            else if ("image/*".equals(f_type)) {
                //              holder.checkedTxt.setCompoundDrawablesWithIntrinsicBounds(
                //                      R.drawable.imgfile, 0, 0, 0);
                holder.checkedTxt.setCompoundDrawables(dr_imgfile,null,null,null);
            }
            else {
                //jly 30140313
                //              holder.checkedTxt.setCompoundDrawablesWithIntrinsicBounds(
                //                      R.drawable.otherfile, 0, 0, 0);
                holder.checkedTxt.setCompoundDrawables(dr_otherfile,null,null,null);
                //jly 30140313
            }
        }
        // File type data icon
        else {
            //jly 20140313
            //          holder.checkedTxt.setCompoundDrawablesWithIntrinsicBounds(
            //                  R.drawable.folder_file, 0, 0, 0);
            holder.checkedTxt.setCompoundDrawables(dr_folder_file,null,null,null);
            //jly 20140313
        }

        holder.checkedTxt.setTextSize(22);

        /* The file name character length of more than 64, ticker display */
        /* CNcomment: 文件名字符长度超过64,跑马灯显示 */
        if (f.getName().length() > 66) {
            holder.checkedTxt.setText(f.getName().substring(0, 55) + "...");
        }
        else {
            holder.checkedTxt.setText(f.getName());
        }

        return convertView;
    }

    /**
     * the type of control container
     */
    private static class ViewHolder {
        private CheckedTextView checkedTxt;
    }

}
