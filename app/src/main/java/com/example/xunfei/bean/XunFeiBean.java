package com.example.xunfei.bean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/2.
 */

public class XunFeiBean {

    public ArrayList<WS> ws;
    public class WS {
        public ArrayList<CW> cw;
    }
    public class CW {
        public String w;
    }

}
