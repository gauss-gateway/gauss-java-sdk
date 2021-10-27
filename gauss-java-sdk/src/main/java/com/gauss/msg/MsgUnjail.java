package com.gauss.msg;

import com.gauss.common.EnvInstance;
import com.gauss.msg.utils.Message;
//import io.cosmos.msg.utils.Message35;
import com.gauss.msg.utils.type.MsgUnjailValue;

public class MsgUnjail  extends MsgBase {
    public Message produceMsg() {
        MsgUnjailValue value = new MsgUnjailValue();
        value.setAddress(this.operAddress);

        Message<MsgUnjailValue> msg = new Message<>();
        msg.setType(msgType);
        msg.setValue(value);
        return msg;
    }
}

