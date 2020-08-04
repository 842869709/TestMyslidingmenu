# Myslidingmenu
侧滑菜单布局，仿QQ，SlidingMenu,侧滑出菜单效果

示例图片

![](https://github.com/842869709/TestMyslidingmenu/blob/master/2020.07.31.15.49.20_2.gif)

## 1.用法
使用前，对于Android Studio的用户，可以选择添加:

```gradle
	allprojects {
		repositories {
			maven { url 'https://jitpack.io' }
		}
	}
	dependencies {
	        implementation 'com.github.842869709:TestMyslidingmenu:Tag'
	}
```
## 2.功能参数与含义
配置参数|参数含义|参数类型|默认值
-|-|-|-
setRightDown|	设置右边布局是否可以上下滑动|	boolean|	true
setOnStateChangeListening|	设置展开状态监听，以及返回滑动百分比|	无| 	无

## 3.代码参考
布局文件
将菜单布局与主布局嵌套在MySlidingMenu内
右边的布局即主布局放在上
左边的布局即菜单布局放在下
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.yangxuydong.myslidingmenu.MySlidingMenu
        android:id="@+id/msm"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <include layout="@layout/menu_right"/>
        <include layout="@layout/menu_left"/>

    </com.yangxuydong.myslidingmenu.MySlidingMenu>

</LinearLayout>
```
示例右布局即菜单布局
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        android:background="@android:color/holo_green_dark">

        <Button
            android:id="@+id/bt_open"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="关闭或者打开"/>
        <TextView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:text="我是右边我是右边我是右边"
            android:textSize="35sp"/>
    </LinearLayout>

</LinearLayout>
```
示例左布局即菜单布局
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" android:layout_width="200dp"
    android:layout_height="match_parent"
    android:background="@android:color/holo_blue_bright">

    <TextView
        android:layout_width="200dp"
        android:layout_height="wrap_content"
        android:textSize="30sp"
        android:text="我是左边"/>


</LinearLayout>
```

配置及初始化
```
package com.yangxuydong.testmybannergithub;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.yangxuydong.myslidingmenu.MySlidingMenu;

/**
 * 创建时间：2020/8/4
 * 编写人：czy_yangxudong
 * 功能描述：测试MySlidingMenu
 */
public class TestSlidingMenuActivity extends Activity {

    private MySlidingMenu msm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_slidingmenu);

        initView();
    }

    private void initView() {
        msm = findViewById(R.id.msm);
        msm.setRightDown(true);
        msm.setOnStateChangeListening(new MySlidingMenu.OnStateChangeListening() {
            @Override
            public void OnStateChange(boolean isExpand) {
                Log.i("test",isExpand?"打开":"关闭");
            }

            @Override
            public void OnScroll(int precent) {
                Log.i("test","precent="+precent);
            }
        });

        findViewById(R.id.bt_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msm.setOpen();
            }
        });

    }
}

```
## v1.0.0初始化提交
