/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.um.gallery3d.app;

import java.util.Stack;


import android.app.Activity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.um.gallery3d.R;
import com.um.gallery3d.common.Utils;
import com.um.gallery3d.util.CustomToast;
import com.um.gallery3d.util.FileUtil;

public class StateManager {
    @SuppressWarnings("unused")
    private static final String TAG = "StateManager";

    private boolean mIsResumed = false;

    private static final String KEY_MAIN = "activity-state";

    private static final String KEY_DATA = "data";

    private static final String KEY_STATE = "bundle";

    private static final String KEY_CLASS = "class";

    private static final String KEY_LAUNCH_GALLERY_ON_TOP = "launch-gallery-on-top";

    private GalleryActivity mContext;
    

    private Stack<StateEntry> mStack = new Stack<StateEntry>();

    private ActivityState.ResultEntry mResult;

    private boolean mLaunchGalleryOnTop = false;

    public StateManager(GalleryActivity context) {
        mContext = context;
    }

    public void startState(Class<? extends ActivityState> klass, Bundle data) {
        Log.v(TAG, "startState " + klass);
        ActivityState state = null;

        try {
            state = klass.newInstance();
        } catch (Exception e) {
            throw new AssertionError(e);
        }

        if (!mStack.isEmpty()) {
            ActivityState top = getTopState();

            if (mIsResumed) {
                top.onPause();
            }
        }

        state.initialize(mContext, data);
        mStack.push(new StateEntry(data, state));
        state.onCreate(data, null);

        if (mIsResumed) {
            state.resume();
        }
    }

    public void setLaunchGalleryOnTop(boolean enabled) {
        mLaunchGalleryOnTop = enabled;
    }

    public void startStateForResult(Class<? extends ActivityState> klass, int requestCode,
            Bundle data) {
        Log.v(TAG, "startStateForResult " + klass + ", " + requestCode);
        ActivityState state = null;

        try {
            state = klass.newInstance();
        } catch (Exception e) {
            throw new AssertionError(e);
        }

        state.initialize(mContext, data);
        state.mResult = new ActivityState.ResultEntry();
        state.mResult.requestCode = requestCode;

        if (!mStack.isEmpty()) {
            ActivityState as = getTopState();
            as.mReceivedResults = state.mResult;

            if (mIsResumed) {
                as.onPause();
            }
        } else {
            mResult = state.mResult;
        }

        mStack.push(new StateEntry(data, state));
        state.onCreate(data, null);

        if (mIsResumed) {
            state.resume();
        }
    }

    public boolean createOptionsMenu(Menu menu) {
        if (!mStack.isEmpty()) {
            ((Activity) mContext).setProgressBarIndeterminateVisibility(false);
            return getTopState().onCreateActionBar(menu);
        } else {
            return false;
        }
    }

    public void onConfigurationChange(Configuration config) {
        for (StateEntry entry : mStack) {
            entry.activityState.onConfigurationChanged(config);
        }
    }

    public void resume() {
        if (mIsResumed) {
            return;
        }

        mIsResumed = true;

        if (!mStack.isEmpty()) {
            getTopState().resume();
        }
    }

    public void pause() {
        if (!mIsResumed) {
            return;
        }

        mIsResumed = false;

        if (!mStack.isEmpty()) {
            getTopState().onPause();
        }
    }

    public void notifyActivityResult(int requestCode, int resultCode, Intent data) {
        getTopState().onStateResult(requestCode, resultCode, data);
    }

    public int getStateCount() {
        return mStack.size();
    }

    public boolean itemSelected(MenuItem item) {
        if (!mStack.isEmpty()) {
            if (item.getItemId() == android.R.id.home) {
                getTopState().onBackPressed();
                // if (mStack.size() > 1) {
                // getTopState().onBackPressed();
                // } else if (mLaunchGalleryOnTop) {
                // Activity activity = (Activity) mContext;
                // Intent intent = new Intent(activity, Gallery.class)
                // .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // ((Activity) mContext).startActivity(intent);
                // }
                // return true;
            } else {
                return getTopState().onItemSelected(item);
            }
        }

        return false;
    }

    public void onBackPressed() {
        if (!mStack.isEmpty()) {
            getTopState().onBackPressed();
        }
    }
   private int count = 0;
   	Handler tmpHander = new Handler() {
   		public void handleMessage(Message msg) {
   			Log.i(TAG, "=======tmpHander========");
   			count = 0;
            super.handleMessage(msg);
        }
   	};
   	
    private int seccount = 0;
   	Handler tmpSecHander = new Handler() {
   		public void handleMessage(Message msg) {
   			Log.i(TAG, "=======tmpHander========");
   			seccount = 0;
            super.handleMessage(msg);
        }
   	};
   	
    void finishState(final ActivityState state) {
    	

        Log.i(TAG, "finishState toString" + state.getClass().toString());
        
       if ( state.getClass().toString().equals("class com.um.gallery3d.app.PhotoPage")) {
  
    	   seccount++;
	          Log.i(TAG, "=======count========"+count);
	          if(seccount<2)
	          {
	        	  
	        	  //Toast.makeText(mContext.getAndroidContext(),mContext.getAndroidContext().getResources().getString(R.string.toast_photoexit), Toast.LENGTH_SHORT).show();  

	        	  final CustomToast myToast=new CustomToast(mContext.getAndroidContext());
	        	  myToast.getmDialog().setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss(DialogInterface arg0) {
						// TODO Auto-generated method stub
						if(myToast.isBackDismiss()==true){

				        	
				  	        if (state != mStack.peek().activityState) {
				  	            if (state.isDestroyed()) {
				  	                Log.d(TAG, "The state is already destroyed");
				  	                return;
				  	            } else {
				  	                throw new IllegalArgumentException("The stateview to be finished"
				  	                        + " is not at the top of the stack: " + state + ", "
				  	                        + mStack.peek().activityState);
				  	            }
				  	        }
				  	
				  	        // Remove the top state.
				  	        mStack.pop();
				  	
				  	        if (mIsResumed) {
				  	            state.onPause();
				  	        }
				  	
				  	        mContext.getGLRoot().setContentPane(null);
				  	        state.onDestroy();
				  	
				  	        if (mStack.isEmpty()) {
				  	            Log.v(TAG, "no more state, finish activity");
				  	            Activity activity = (Activity) mContext.getAndroidContext();
				  	
				  	            if (mResult != null) {
				  	                activity.setResult(mResult.resultCode, mResult.resultData);
				  	            }
				  	
				  	            activity.finish();
				  	
				  	            // The finish() request is rejected (only happens under Monkey),
				  	            // so we start the default page instead.
				  	            if (!activity.isFinishing()) {
				  	                Log.v(TAG, "finish() failed, start default page");
				  	                ((Gallery) mContext).startDefaultPage();
				  	            }
				  	        } else {
				  	            // Restore the immediately previous state
				  	            ActivityState top = mStack.peek().activityState;
				  	
				  	            if (mIsResumed) {
				  	                top.resume();
				  	            }
				  	        }
				      	
				          
						}
						
					}
				});
	        	  myToast.setMessage(R.string.toast_photoexit);
	        	  myToast.showTime(2000);
	        	  myToast.show();
	        	  
	        	  Log.i(TAG, "=======finishState========");
	              tmpSecHander.sendEmptyMessageDelayed(0, 2000);
	          }
	          else
	          {
	        	  seccount = 0;
	  	        if (state != mStack.peek().activityState) {
	  	            if (state.isDestroyed()) {
	  	                Log.d(TAG, "The state is already destroyed");
	  	                return;
	  	            } else {
	  	                throw new IllegalArgumentException("The stateview to be finished"
	  	                        + " is not at the top of the stack: " + state + ", "
	  	                        + mStack.peek().activityState);
	  	            }
	  	        }
	  	
	  	        // Remove the top state.
	  	        mStack.pop();
	  	
	  	        if (mIsResumed) {
	  	            state.onPause();
	  	        }
	  	
	  	        mContext.getGLRoot().setContentPane(null);
	  	        state.onDestroy();
	  	
	  	        if (mStack.isEmpty()) {
	  	            Log.v(TAG, "no more state, finish activity");
	  	            Activity activity = (Activity) mContext.getAndroidContext();
	  	
	  	            if (mResult != null) {
	  	                activity.setResult(mResult.resultCode, mResult.resultData);
	  	            }
	  	
	  	            activity.finish();
	  	
	  	            // The finish() request is rejected (only happens under Monkey),
	  	            // so we start the default page instead.
	  	            if (!activity.isFinishing()) {
	  	                Log.v(TAG, "finish() failed, start default page");
	  	                ((Gallery) mContext).startDefaultPage();
	  	            }
	  	        } else {
	  	            // Restore the immediately previous state
	  	            ActivityState top = mStack.peek().activityState;
	  	
	  	            if (mIsResumed) {
	  	                top.resume();
	  	            }
	  	        }
	      	
	          }

        }
         
       else if ( state.getClass().toString().equals("class com.um.gallery3d.app.SlideshowPage")) {
  

	          count++;
	          Log.i(TAG, "=======count========"+count);
	          if(count<3)
	          {
	        	  if(count == 1){
	        		  //Toast.makeText(mContext.getAndroidContext(),mContext.getAndroidContext().getResources().getString(R.string.toast_slideexit), Toast.LENGTH_SHORT).show(); 
	        		  final CustomToast mySlideToast=new CustomToast(mContext.getAndroidContext());
	        		  mySlideToast.getmDialog().setOnDismissListener(new OnDismissListener() {
						
						@Override
						public void onDismiss(DialogInterface arg0) {
							// TODO Auto-generated method stub
							if(mySlideToast.isBackDismiss() == true){

					  	        if (state != mStack.peek().activityState) {
					  	            if (state.isDestroyed()) {
					  	                Log.d(TAG, "The state is already destroyed");
					  	                return;
					  	            } else {
					  	                throw new IllegalArgumentException("The stateview to be finished"
					  	                        + " is not at the top of the stack: " + state + ", "
					  	                        + mStack.peek().activityState);
					  	            }
					  	        }
					  	
					  	        // Remove the top state.
					  	        mStack.pop();
					  	
					  	        if (mIsResumed) {
					  	            state.onPause();
					  	        }
					  	
					  	        mContext.getGLRoot().setContentPane(null);
					  	        state.onDestroy();
					  	
					  	        if (mStack.isEmpty()) {
					  	            Log.v(TAG, "no more state, finish activity");
					  	            Activity activity = (Activity) mContext.getAndroidContext();
					  	
					  	            if (mResult != null) {
					  	                activity.setResult(mResult.resultCode, mResult.resultData);
					  	            }
					  	
					  	            activity.finish();
					  	
					  	            // The finish() request is rejected (only happens under Monkey),
					  	            // so we start the default page instead.
					  	            if (!activity.isFinishing()) {
					  	                Log.v(TAG, "finish() failed, start default page");
					  	                ((Gallery) mContext).startDefaultPage();
					  	            }
					  	        } else {
					  	            // Restore the immediately previous state
					  	            ActivityState top = mStack.peek().activityState;
					  	
					  	            if (mIsResumed) {
					  	                top.resume();
					  	            }
					  	        }
					      	
					          
							}
						}
					});
	        		  
	        		  mySlideToast.setMessage(R.string.toast_slideexit);
	        		  mySlideToast.showTime(2000);
	        		  mySlideToast.show();
	        	  
	        	  }
	             
	              Log.i(TAG, "=======finishState========");
	              tmpHander.sendEmptyMessageDelayed(0, 2000);
	          }
	          else
	          {
	        	  count = 0;
	  	        if (state != mStack.peek().activityState) {
	  	            if (state.isDestroyed()) {
	  	                Log.d(TAG, "The state is already destroyed");
	  	                return;
	  	            } else {
	  	                throw new IllegalArgumentException("The stateview to be finished"
	  	                        + " is not at the top of the stack: " + state + ", "
	  	                        + mStack.peek().activityState);
	  	            }
	  	        }
	  	
	  	        // Remove the top state.
	  	        mStack.pop();
	  	
	  	        if (mIsResumed) {
	  	            state.onPause();
	  	        }
	  	
	  	        mContext.getGLRoot().setContentPane(null);
	  	        state.onDestroy();
	  	
	  	        if (mStack.isEmpty()) {
	  	            Log.v(TAG, "no more state, finish activity");
	  	            Activity activity = (Activity) mContext.getAndroidContext();
	  	
	  	            if (mResult != null) {
	  	                activity.setResult(mResult.resultCode, mResult.resultData);
	  	            }
	  	
	  	            activity.finish();
	  	
	  	            // The finish() request is rejected (only happens under Monkey),
	  	            // so we start the default page instead.
	  	            if (!activity.isFinishing()) {
	  	                Log.v(TAG, "finish() failed, start default page");
	  	                ((Gallery) mContext).startDefaultPage();
	  	            }
	  	        } else {
	  	            // Restore the immediately previous state
	  	            ActivityState top = mStack.peek().activityState;
	  	
	  	            if (mIsResumed) {
	  	                top.resume();
	  	            }
	  	        }
	      	
	          }

        }
       else{
	
	        if (state != mStack.peek().activityState) {
	            if (state.isDestroyed()) {
	                Log.d(TAG, "The state is already destroyed");
	                return;
	            } else {
	                throw new IllegalArgumentException("The stateview to be finished"
	                        + " is not at the top of the stack: " + state + ", "
	                        + mStack.peek().activityState);
	            }
	        }
	
	        // Remove the top state.
	        mStack.pop();
	
	        if (mIsResumed) {
	            state.onPause();
	        }
	
	        mContext.getGLRoot().setContentPane(null);
	        state.onDestroy();
	
	        if (mStack.isEmpty()) {
	            Log.v(TAG, "no more state, finish activity");
	            Activity activity = (Activity) mContext.getAndroidContext();
	
	            if (mResult != null) {
	                activity.setResult(mResult.resultCode, mResult.resultData);
	            }
	
	            activity.finish();
	
	            // The finish() request is rejected (only happens under Monkey),
	            // so we start the default page instead.
	            if (!activity.isFinishing()) {
	                Log.v(TAG, "finish() failed, start default page");
	                ((Gallery) mContext).startDefaultPage();
	            }
	        } else {
	            // Restore the immediately previous state
	            ActivityState top = mStack.peek().activityState;
	
	            if (mIsResumed) {
	                top.resume();
	            }
	        }
    	}
    }

    void switchState(ActivityState oldState, Class<? extends ActivityState> klass, Bundle data) {
        Log.v(TAG, "switchState " + oldState + ", " + klass);

        if (oldState != mStack.peek().activityState) {
            throw new IllegalArgumentException("The stateview to be finished"
                    + " is not at the top of the stack: " + oldState + ", "
                    + mStack.peek().activityState);
        }

        // Remove the top state.
        mStack.pop();

        if (mIsResumed) {
            oldState.onPause();
        }

        oldState.onDestroy();
        // Create new state.
        ActivityState state = null;

        try {
            state = klass.newInstance();
        } catch (Exception e) {
            throw new AssertionError(e);
        }

        state.initialize(mContext, data);
        mStack.push(new StateEntry(data, state));
        state.onCreate(data, null);

        if (mIsResumed) {
            state.resume();
        }
    }

    public void destroy() {
        Log.v(TAG, "destroy");

        while (!mStack.isEmpty()) {
            mStack.pop().activityState.onDestroy();
        }

        mStack.clear();
    }

    @SuppressWarnings("unchecked")
    public void restoreFromState(Bundle inState) {
        Log.v(TAG, "restoreFromState");
        mLaunchGalleryOnTop = inState.getBoolean(KEY_LAUNCH_GALLERY_ON_TOP, false);
        Parcelable list[] = inState.getParcelableArray(KEY_MAIN);

        for (Parcelable parcelable : list) {
            Bundle bundle = (Bundle) parcelable;
            Class<? extends ActivityState> klass = (Class<? extends ActivityState>) bundle
                    .getSerializable(KEY_CLASS);
            Bundle data = bundle.getBundle(KEY_DATA);
            Bundle state = bundle.getBundle(KEY_STATE);
            ActivityState activityState;

            try {
                Log.v(TAG, "restoreFromState " + klass);
                activityState = klass.newInstance();
            } catch (Exception e) {
                throw new AssertionError(e);
            }

            activityState.initialize(mContext, data);
            activityState.onCreate(data, state);
            mStack.push(new StateEntry(data, activityState));
        }
    }

    public void saveState(Bundle outState) {
        Log.v(TAG, "saveState");
        outState.putBoolean(KEY_LAUNCH_GALLERY_ON_TOP, mLaunchGalleryOnTop);
        Parcelable list[] = new Parcelable[mStack.size()];
        int i = 0;

        for (StateEntry entry : mStack) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(KEY_CLASS, entry.activityState.getClass());
            bundle.putBundle(KEY_DATA, entry.data);
            Bundle state = new Bundle();
            entry.activityState.onSaveState(state);
            bundle.putBundle(KEY_STATE, state);
            Log.v(TAG, "saveState " + entry.activityState.getClass());
            list[i++] = bundle;
        }

        outState.putParcelableArray(KEY_MAIN, list);
    }

    public boolean hasStateClass(Class<? extends ActivityState> klass) {
        for (StateEntry entry : mStack) {
            if (klass.isInstance(entry.activityState)) {
                return true;
            }
        }

        return false;
    }

    public ActivityState getTopState() {
        Utils.assertTrue(!mStack.isEmpty());
        return mStack.peek().activityState;
    }

    private static class StateEntry {
        public Bundle data;

        public ActivityState activityState;

        public StateEntry(Bundle data, ActivityState state) {
            this.data = data;
            this.activityState = state;
        }
    }
}
