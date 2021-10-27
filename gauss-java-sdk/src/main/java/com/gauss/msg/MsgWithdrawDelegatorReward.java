package com.gauss.msg;

import com.gauss.common.EnvInstance;
import com.gauss.msg.utils.Message;
import com.gauss.msg.utils.type.MsgWithdrawDelegatorRewardValue;

public class MsgWithdrawDelegatorReward extends MsgBase {

    public Message produceMsg() {
        String validatorAddr = this.operAddress;
        MsgWithdrawDelegatorRewardValue value = new MsgWithdrawDelegatorRewardValue();
        value.setValidatorAddress(validatorAddr);
        value.setDelegatorAddress(this.address);

        Message<MsgWithdrawDelegatorRewardValue> msg = new Message<>();
        msg.setType(msgType);
        msg.setValue(value);
        return msg;
    }

}
