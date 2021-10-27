package com.gauss.msg;

import com.gauss.common.EnvInstance;
import com.gauss.msg.utils.Message;
import com.gauss.msg.utils.type.MsgSetWithdrawAddrValue;


public class MsgSetWithdrawAddress extends MsgBase {
    public Message produceMsg() {
        String withdrawAddr = this.address;
        MsgSetWithdrawAddrValue value = new MsgSetWithdrawAddrValue();
        value.setWithdrawAddress(withdrawAddr);
        value.setDelegatorAddress(this.address);

        Message<MsgSetWithdrawAddrValue> msg = new Message<>();
        msg.setType(msgType);
        msg.setValue(value);
        return msg;
    }

}
