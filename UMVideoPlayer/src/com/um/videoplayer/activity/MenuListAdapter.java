package com.um.videoplayer.activity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.um.videoplayer.R;

public class MenuListAdapter extends BaseAdapter {
    public static int CONTROL_MENU_ITEMTYPE;
    private Activity activity;
    private String[] menuItems;
    public int isAllEnable = 1;
    public int imageType = -1;
    int subType = 0;
    private static boolean TYPE_DIALOG = false;

    static class Holder {
        ImageView image;
        TextView text;
        TextView textbro;
    }

    public MenuListAdapter(Activity a, String[] items, int isAllEnable, int subType, boolean istypedialog) {
        this.activity    = a;
        this.menuItems   = items;
        this.isAllEnable = isAllEnable;
        this.subType = subType;
        this.TYPE_DIALOG = istypedialog;
    }

    public MenuListAdapter(Activity a, String[] items, int isAllEnable, int subType, boolean istypedialog, int imageType) {
        this.activity    = a;
        this.menuItems   = items;
        this.isAllEnable = isAllEnable;
        this.subType = subType;
        this.TYPE_DIALOG = istypedialog;
        this.imageType = imageType;
    }

    public int getCount() {
        return menuItems.length;
    }

    public Object getItem(int position) {
        return menuItems[position];
    }

    public long getItemId(int position) {
        return position;
    }
    public void setImageType(int type) {
        imageType = type;
    }
    public int getImageType() {
        return imageType;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        Holder holder = null;

        if (convertView == null) {
        	convertView = inflater.inflate(R.layout.menu_item_option, null);
            //convertView = inflater.inflate(R.layout.menu_list_item, null);
            holder = new Holder();
           // holder.image = (ImageView)convertView.findViewById(R.id.menu_icon);//菜单选项图标
           // holder.text = (TextView)convertView.findViewById(R.id.menu_item);//选项文字
            
            holder.image = (ImageView)convertView.findViewById(R.id.menu_item_icon);//菜单选项图标
            holder.text = (TextView)convertView.findViewById(R.id.setting_option_item_txt);//选项文字
            holder.textbro = (TextView)convertView.findViewById(R.id.setting_option_item_txt2);//选项文字
            convertView.setTag(holder);
        }
        else {
            holder = (Holder)convertView.getTag();
        }

        if (TYPE_DIALOG) {
            if (isAllEnable == 0) {
                if (position != 0) {
                    holder.text.setTextColor(Color.GRAY);
                }
                else {
                    holder.text.setTextColor(Color.WHITE);
                }
            }
            else if (isAllEnable != 0) {
                switch (subType) {
                    case 1:
                        if (position == 0 || position == 1 || position == 2 || position == 3 || position == 4 || position == 5
                            || position == 6 || position == 7 || position == 8 || position == 9 || position == 10) {
                            holder.text.setTextColor(Color.WHITE);
                        }
                        else
                        { holder.text.setTextColor(Color.GRAY); }

                        break;

                    case 2:
                        if (position == 0) {
                            holder.text.setTextColor(Color.WHITE);
                        }
                        else
                        { holder.text.setTextColor(Color.GRAY); }

                        break;

                    case 3:
                        if (position == 0 || position == 1 || position == 2 || position == 3 || position == 4 || position == 5
                            || position == 6 || position == 7 || position == 8 || position == 9 || position == 10) {
                            holder.text.setTextColor(Color.WHITE);
                        }
                        else
                        { holder.text.setTextColor(Color.GRAY); }

                        break;

                    case 4:
                        if (position == 0) {
                            holder.text.setTextColor(Color.WHITE);
                        }
                        else
                        { holder.text.setTextColor(Color.GRAY); }

                        break;

                    case 5:
                        if (position == 1) {
                            holder.text.setTextColor(Color.WHITE);
                        }
                        else
                        { holder.text.setTextColor(Color.GRAY); }

                        break;

                    case 7:
                        if (position == 0) {
                            holder.text.setTextColor(Color.WHITE);
                        }
                        else
                        { holder.text.setTextColor(Color.GRAY); }

                        break;

                    case 8:
                        if (position == 1 || position == 2 || position == 3 || position == 4) {
                            holder.text.setTextColor(Color.WHITE);
                        }
                        else
                        { holder.text.setTextColor(Color.GRAY); }

                        break;

                    case 9:
                        if (position == 0 || position == 1 || position == 3) {
                            holder.text.setTextColor(Color.WHITE);
                        }
                        else
                        { holder.text.setTextColor(Color.GRAY); }

                        break;

                    default:
                        holder.text.setTextColor(Color.WHITE);
                        break;
                }
            }
        }

        setImage(position, holder.image);
        String item = menuItems[position];
        holder.text.setText(item);
        holder.textbro.setText(item);
        return convertView;
    }

    public boolean areAllItemsEnabled() {
        if (isAllEnable == 0) {
            return false;
        }

        return true;
    }

    public boolean isEnabled(int position) {
        if ((isAllEnable == 0) && (subType == 0)) {
            return (position == 0);
        }
        else if ((isAllEnable != 0) && (subType != 0)) {
            if (subType == 1) {
                return true;
            }
            else if (subType == 2) {
                return (position == 0);
            }
            else if (subType == 3) {
                return true;
            }
            else if (subType == 4) {
                return (position == 0);
            }
            else if (subType == 5) {
                return (position == 1);
            }
            else if (subType == 6) {
                return (position == 2 || position == 1 || position == 0);
            }
            else if (subType == 7) {
                return (position == 0);
            }
            else if (subType == 8) {
                return (position == 1 || position == 2 || position == 3 || position == 4);
            }
            else if (subType == 9) {
                return (position == 0 || position == 1 || position == 3);
            }
            else {
                return (position == 0);
            }
        }

        return super.isEnabled(position);
    }

    protected void setImage(int position, ImageView imageView) {
        if (imageType == 0) {
            switch (position) {
                case 0:
                    imageView.setImageResource(R.drawable.hisil_iconf);
                    break;

                case 1:
                    imageView.setImageResource(R.drawable.hisil_iconn);
                    break;

                case 2:
                    imageView.setImageResource(R.drawable.iconk);
                    break;

                case 3:
                    imageView.setImageResource(R.drawable.hisil_iconj);
                    break;

                case 4:
                    imageView.setImageResource(R.drawable.hisil_iconc);
                    break;
            }
        }
        else if (imageType == 1) {
            switch (position) {
                case 0:
                    imageView.setImageResource(R.drawable.hisil_iconf);
                    break;

                case 1:
                    imageView.setImageResource(R.drawable.hisil_iconn);
                    break;

                case 2:
                    imageView.setImageResource(R.drawable.hisil_iconj);
                    break;

                case 3:
                    imageView.setImageResource(R.drawable.hisil_iconc);
                    break;
            }
        }
        else if (imageType == 2) {
            switch (position) {
                case 0:
                    imageView.setImageResource(R.drawable.hisil_iconp);
                    break;

                case 1:
                    imageView.setImageResource(R.drawable.hisil_iconq);
                    break;

                case 2:
                    imageView.setImageResource(R.drawable.hisil_icono);
                    break;
            }
        }
        else if (imageType == 3) {
            switch (position) {
              /*  case 0:
                    imageView.setImageResource(R.drawable.hisil_icons);
                    break;
*/
              /*  case 1:
                    imageView.setImageResource(R.drawable.hisil_iconr);
                    break;*/

                case 0:
                    imageView.setImageResource(R.drawable.hisil_icont);
                    break;

                case 1:
                    imageView.setImageResource(R.drawable.hisil_iconu);
                    break;

                case 2:
                    imageView.setImageResource(R.drawable.hisil_iconv);
                    break;

                case 3:
                    imageView.setImageResource(R.drawable.hisil_iconw);
                    break;

                case 4:
                    imageView.setImageResource(R.drawable.hisil_iconx);
                    break;

                case 5:
                    imageView.setImageResource(R.drawable.icony);
                    break;
            }
        }
        else if (imageType == 4) {
            switch (position) {
                case 0:
                    imageView.setImageResource(R.drawable.hisil_iconn);
                    break;

                case 1:
                    imageView.setImageResource(R.drawable.icong);
                    break;
            }
        }
    }
}
