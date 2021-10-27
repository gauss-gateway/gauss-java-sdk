package com.gauss.msg;

import com.gauss.common.EnvInstance;
import com.gauss.msg.utils.Message;
import com.gauss.msg.utils.type.MsgSendValue;
import com.gauss.types.Token;

import java.util.ArrayList;
import java.util.List;

public class MsgSend extends MsgBase {
    public Message produceSendMsg(String denom, String amountDenom, String to) {

        List<Token> amountList = new ArrayList<>();
        Token amount = new Token();
        amount.setDenom(denom);
        amount.setAmount(amountDenom);
        amountList.add(amount);

        MsgSendValue value = new MsgSendValue();
        value.setFromAddress(this.address);
        value.setToAddress(to);
        value.setAmount(amountList);

        Message<MsgSendValue> msg = new Message<>();
        msg.setType(msgType);
        msg.setValue(value);
        return msg;
    }

}
