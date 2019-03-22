package com.bigkoo.alabombackstage.activity;

import android.text.TextUtils;
import android.view.View;

import com.bigkoo.alabombackstage.R;
import com.bigkoo.alabombackstage.TokenBean;
import com.bigkoo.alabombackstage.presenter.LoginPresenter;
import com.bigkoo.alabombackstage.utils.ValidateUtil;
import com.bigkoo.katafoundation.activity.BaseDetailActivity;
import com.bigkoo.kataframework.mvpview.BaseDetailView;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends BaseDetailActivity<LoginPresenter> implements BaseDetailView {
    private TextInputEditText etAccount, etPassWord;
    @Override
    protected int getLayoutResID() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        etAccount = findViewById(R.id.etAccount);
        etPassWord = findViewById(R.id.etPassWord);
    }

    public void onSubmit(View v){

        String account = etAccount.getText().toString();
        //先检验输入数据的正确性
        String validateTips = ValidateUtil.account(account);
        if (validateToast(validateTips)) return;
        String passWord = etPassWord.getText().toString();
        //先检验输入数据的正确性
        validateTips = ValidateUtil.passWord(passWord);
        if (validateToast(validateTips)) return;

        getPresenter().onLogin(account,passWord);

    }

    /**
     * 提示验证信息
     *
     * @param validateTips
     * @return true为不通过，弹提示，false为通过继续下一步
     */
    public boolean validateToast(String validateTips) {
        if (!TextUtils.isEmpty(validateTips)) {
            showToast(validateTips);
            return true;
        }

        return false;
    }

    @Override
    public void onRefreshing(boolean refreshing) {

    }

    @Override
    public void onStatusEmpty(boolean statusEmpty) {

    }

    @Override
    public void onStatusLoading(boolean statusLoading) {

    }

    @Override
    public void onStatusError(boolean statusError, int code, String msg) {

    }

    @Override
    public void onStatusNetworkError(boolean statusNetworkError, String msg) {

    }

    @Override
    public void onDataSetChange(Object data) {
        TokenBean tokenBean = (TokenBean) data;

    }

    @Override
    public void onLoadComplete() {

    }
}
