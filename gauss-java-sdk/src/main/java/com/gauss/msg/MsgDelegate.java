package com.gauss.msg;

import com.gauss.common.EnvInstance;
import com.gauss.msg.utils.Message;
import com.gauss.msg.utils.type.MsgDelegateValue;
import com.gauss.types.Token;


public class MsgDelegate extends MsgBase {



    protected Message produceDelegateMsg(String delegateDenom, String delegateAmount) {

        String validatorAddress = this.operAddress;
        MsgDelegateValue delegateValue = new MsgDelegateValue();
        delegateValue.setValidatorAddress(validatorAddress);
        delegateValue.setDelegatorAddress(address);
        //amount
        Token token = new Token();
        token.setDenom(delegateDenom);
        token.setAmount(delegateAmount);
        delegateValue.setAmount(token);
        Message<MsgDelegateValue> messageDelegateMulti = new Message<>();
        messageDelegateMulti.setType(msgType);
        messageDelegateMulti.setValue(delegateValue);
        return messageDelegateMulti;
    }

}
