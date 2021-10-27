package com.gauss.msg;

import com.gauss.common.EnvInstance;
import com.gauss.msg.utils.Message;
import com.gauss.msg.utils.type.MsgWithdrawValidatorCommissionValue;

public class MsgWithdrawValidatorCommission extends MsgBase {

    public Message produceMsg() {
        String validatorAddr = this.operAddress;
        MsgWithdrawValidatorCommissionValue value = new MsgWithdrawValidatorCommissionValue();
        value.setValidatorAddress(validatorAddr);

        Message<MsgWithdrawValidatorCommissionValue> msg = new Message<>();
        msg.setType(msgType);
        msg.setValue(value);
        return msg;
    }

}
