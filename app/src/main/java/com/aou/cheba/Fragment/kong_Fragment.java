package com.aou.cheba.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aou.cheba.R;


/**
 * Created by Administrator on 2016/8/25.
 */
public class kong_Fragment extends Fragment {

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.xiyou_fragment, container, false);
        }

        return rootView;

    }

}
