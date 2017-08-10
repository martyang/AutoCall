package com.gionee.autocall.action;


import com.gionee.autocall.bean.CallLogBean;

import java.util.ArrayList;

interface IAutoCallAction {
    void writeResult(int successFled, int failed, WriteResultListener writeResultListener);

    void exportExcel(ArrayList<CallLogBean> mList);

    void updateViews();
}
