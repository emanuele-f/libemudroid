package com.androidemu.wrapper;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;

public abstract class TabbedHelpViewFactory
{
	public static View newTabbedHelpView(Activity a, String[][] urls)
	{
		if (Wrapper.SDK_INT < 11)
		{
			return new TabsConnectorBase(a, urls);
		}
		else
		{
			return new TabsConnectorHoneycomb(a, urls);
		}
	}

	private static class TabsConnectorBase extends TabHost
	{
		private final WebView wv;

		@SuppressWarnings("deprecation")
		public TabsConnectorBase(Context a, String[][] tabs)
		{
			super(a);

			wv = new WebView(a);
			wv.getSettings().setNeedInitialFocus(false);

			TabContentFactory cf = new TabContentFactory()
			{
				@Override
				public View createTabContent(String tag)
				{
					return wv;
				}
			};

			TabHost.OnTabChangeListener tl = new TabHost.OnTabChangeListener()
			{
				@Override
				public void onTabChanged(String tabId)
				{
					wv.loadUrl(tabId);
				}
			};

			LinearLayout ll = new LinearLayout(a);
			ll.setOrientation(LinearLayout.VERTICAL);

			TabWidget widget = new TabWidget(a);
			widget.setId(android.R.id.tabs);
			ll.addView(widget, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));

			FrameLayout frame = new FrameLayout(a);
			frame.setId(android.R.id.tabcontent);
			ll.addView(frame, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT));

			addView(ll);
			setOnTabChangedListener(tl);
			setup();

			for (String[] tab : tabs)
			{
				addTab(newTabSpec(tab[0]).setIndicator(tab[1]).setContent(cf));
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private static class TabsConnectorHoneycomb extends WebView
	{
		private ActionBar ab;

		public TabsConnectorHoneycomb(Activity a, String[][] tabs)
		{
			super(a);
			
			getSettings().setNeedInitialFocus(false);

			ActionBar.TabListener tl = new ActionBar.TabListener()
			{
				@Override
				public void onTabSelected(Tab tab, FragmentTransaction ft)
				{
					loadUrl((String) tab.getTag());
				}

				public void onTabUnselected(Tab tab, FragmentTransaction ft)
				{
				}

				public void onTabReselected(Tab tab, FragmentTransaction ft)
				{
				}
			};

			ab = a.getActionBar();
			ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			for (String[] tab : tabs)
			{
				ab.addTab(ab.newTab().setTag(tab[0]).setText(tab[1]).setTabListener(tl));
			}
		}
	}
}
