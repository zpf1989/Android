package mynet.signalr.android.chat;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/23.
 */

class MyAdapter extends BaseAdapter {
    private ArrayList<String> list;
    private Context context;

    public  MyAdapter(Context context,ArrayList<String> source){
        this.context=context;
        this.list=source;
    }

    public void setList(ArrayList<String> list){
        this.list=list;
    }

    @Override
    public int getCount(){
        return list.size();
    }

    @Override
    public Object getItem(int position){
        return list.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    //重要方法，用于展现该listview的item条目
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
		/*
		 * convertView为一个item条目对象，每个item滑过之后，创建的item对象有被重新销毁，为此安卓提供一种机制，
		 * 创建的条目不被销毁而是供后来展现的item重复使用，大大提高了效率
		 */
        TextView textView = null;
        if (convertView == null) {	//如果没有可供重复使用的item View对象
            textView = new TextView(context);
			/*
			 * 里面包含一种高效的处理机制，当每个item比较复杂，如果重复的执行类型转换、查找控件等操作，将会很大浪费，
			 * 可以定义一个类，专门用来存放控件对象的引用。后面的例子键会介绍到
			 */
        } else {
            textView = (TextView) convertView;	//如果已经加载将重复使用
        }
        textView.setText(list.get(position));
        textView.setPadding(20, 20, 20, 20);
        textView.setBackgroundColor(Color.GREEN);
        return textView;
    }
}
